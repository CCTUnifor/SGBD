package front.controller;

import entidades.GerenciadorArquivo;
import entidades.GerenciadorArquivoService;
import entidades.GerenciadorDeIO;
import entidades.arvoreBMais.ArvoreBPlus;
import entidades.arvoreBMais.Key;
import entidades.arvoreBMais.Node;
import entidades.blocos.*;
import entidades.index.IndexContainer;
import entidades.index.IndexFileManager;
import entidades.index.TreeBPlus;
import entidades.index.inner.*;
import exceptions.ContainerNoExistent;
import exceptions.IncorrectTypeToPushPointerException;
import exceptions.innerBlock.*;
import factories.ContainerId;
import interfaces.IFileManager;
import interfaces.IIndexFileManager;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.CollumnService;
import services.TableService;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;


public class MainController implements Initializable {
    private CollumnService _collumnService;
    private TableService __tableService;

    private IFileManager _ga;
    private GerenciadorArquivoService _gaService;
    IIndexFileManager indexFileManager;

    private ContainerId[] containerIds;

    private ObservableList<TableViewIndice> tableViewIndiceValues;

    @FXML
    TableView<TableViewIndice> tableViewIndices;
    @FXML
    TableColumn<TableViewIndice, String> tableViewIndices_id;
    @FXML
    TableColumn<TableViewIndice, String> tableViewIndices_tabela;
    @FXML
    TableColumn<TableViewIndice, String> tableViewIndices_colunas;
    @FXML
    TableColumn<TableViewIndice, String> tableViewIndices_ordem;

    @FXML
    private ComboBox<String> tablesComboBox;
    @FXML
    private ListView<String> collumnsListView;
    @FXML
    private TextField ordemDoIndice;
    @FXML
    TextField nomeIndiceTextField;

    @FXML
    TableView<ObservableList<String>> tableViewBancoValores;

    @FXML
    TextField findKeyText;
    @FXML
    TextField findKeyResultado;
    @FXML
    ListView<String> findKeyResultadoCompleto;

    private int tableSelected() {
        return tablesComboBox.getSelectionModel().getSelectedIndex();
    }

    private ContainerId containerIdSelected() {
        return containerIds[tableSelected()];
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.

                __tableService = new TableService();
        _collumnService = new CollumnService();

        _ga = new GerenciadorArquivo();
        _gaService = new GerenciadorArquivoService(_ga);
        indexFileManager = new IndexFileManager();

        this.tableViewIndiceValues = FXCollections.observableArrayList();

        this.tableViewIndices_id.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().index));
        this.tableViewIndices_tabela.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().tabela));
        this.tableViewIndices_colunas.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().colunas));
        this.tableViewIndices_ordem.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().ordem));

        this.tableViewIndices.setItems(this.tableViewIndiceValues);
        this.tableViewIndices.setEditable(true);

        loadTablesComboBox();

        tableViewIndices.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            this.onVizualizarClick();
        });
    }

    private void loadTablesComboBox() {
        HashMap<ContainerId, String> containers = _gaService.getTables();

        containerIds = __tableService.convertContainerIds(containers);
        tablesComboBox.getItems().addAll(__tableService.convertContainerNames(containers));
    }

    public void onSelecionarTable() {
        System.out.println("Table selecionada: " + tableSelected());
        collumnsListView.setDisable(false);

        loadCollumnsListView(tableSelected());
        loadIndexTable(tableSelected());
    }

    private void loadCollumnsListView(int tableSelecionada) {
        this.collumnsListView.getItems().removeAll(this.collumnsListView.getItems());
        ContainerId containerIdSelecionado = __tableService.getContainerIdBySelected(containerIds, tableSelecionada);
        List<String> columns = null;
        try {
            columns = _ga.getColumns(containerIdSelecionado);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (columns == null)
            return;

        collumnsListView.getItems().clear();
        collumnsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        collumnsListView.getItems().addAll(columns);
    }


    public void onAdicionarIndiceClick() throws InnerIndexBlockPointerToChildIsFullException, InnerIndexBlockFullCollumnValueException {
        System.out.println("onAdicionarIndiceClick");
        if (this.indexFileManager.existIndice(containerIdSelected(), nomeIndiceTextField.getText())) {
            this.alert(Alert.AlertType.ERROR, "Index", "Index já existe!");
            return;
        }

        try {
            TreeBPlus tree = new TreeBPlus(containerIdSelected(), nomeIndiceTextField.getText(), this.columnsSelectedIndices());

            String tablePath = GerenciadorArquivo.getDiretorio(containerIdSelected());

            int blockLength = BlocoControle.getBlockLengthFile(tablePath);
            int lastBlockFree = BlocoControle.getControllerBlock(containerIdSelected()).getHeader().getProximoBlocoLivre();
            int currentBlockCounter = blockLength;

            while (currentBlockCounter <= lastBlockFree) {
                BlocoDado dataBlock = new BlocoDado(GerenciadorDeIO.getBytes(tablePath, currentBlockCounter, blockLength));
                for (Linha tuple : dataBlock.getTuples()) {

                    List<Integer> collumns = IndexContainer.getIndexDescritorsByType(containerIdSelected(), TipoDado.COLLUMN).stream()
                            .map(x -> Integer.parseInt(x.getNome())).collect(Collectors.toList());


                    StringBuilder c = new StringBuilder();
                    for (int i = 0; i < collumns.size(); i++) {
                        c.append(tuple.getColunas().get(collumns.get(i)).getInformacao());
                        c.append(";");
                    }
                    CollumnValue col = new CollumnValue(c.toString());
                    tree.insert(col, dataBlock.getRowId());
                }

                currentBlockCounter += blockLength;
            }



            InnerIndexBlock root = (InnerIndexBlock) IndexContainer.loadInnerIndexBlock(RowId.create(containerIdSelected().getValue(), 1));

//            /** CREATE THE INDEX_INNER **/
//            indexFileManager.createIndex(containerIdSelected(), nomeIndiceTextField.getText());
//            /** CREATE THE INDEX_INNER **/
//
//            /* GET JUST INDEX_INNER CONTAINER WITHOUT DATA */
//            IndexContainer ic = IndexContainer.getJustContainer(containerIdSelected());
//            /* GET JUST INDEX_INNER CONTAINER WITHOUT DATA */
//
//            /* CREATE A NEW INNER INDEX_INNER BLOCK */
//            int numberOfChildrens = 5; // TODO number of children that we can have in each InnerIndexBlock
//            InnerIndexBlock block = new InnerIndexBlock(numberOfChildrens); // somente instancia um bloco
//            indexFileManager.createBlock(containerIdSelected(), block); // create the index file and put the index ref to the table => gonna be generate a blockId 1
//            indexFileManager.createBlock(containerIdSelected(), block); // create the index file and put the index ref to the table => gonna be generate a blockId 2
//            /* CREATE A NEW INNER INDEX_INNER BLOCK */
//
//            /* CARREGAR INDEX_INNER BLOCK */
//            block = (InnerIndexBlock) IndexContainer.loadInnerIndexBlock(RowId.create(containerIdSelected().getValue(), 1)); // load a index block, inner or leaf
//            block = (InnerIndexBlock) IndexContainer.loadInnerIndexBlock(RowId.create(containerIdSelected().getValue(), 2)); // load a index block, inner or leaf
//            // TODO block = (LeafIndexBlock) IndexContainer.loadInnerIndexBlock(containerIdSelected().getValue(), 2);
//            /* CARREGAR INDEX_INNER BLOCK */
//
//            /* ADICIONAR VALORES DE COLUNAS */
//            block.pushColumnValue("thiago; victor"); // push collumn value to the index block;
//            block.pushColumnValue("thiago1; victor1"); // push collumn value to the index block;
//            /* ADICIONAR VALORES DE COLUNAS */
//
//
//            CollumnValue col = block.loadCollumnValue(0); // load a CollumnValue putted in this block
//            col = block.loadCollumnValue(1); // load a CollumnValue putted in this block
//
//            block.pushPointerToChild(BlocoId.create(1));
//            IndexBlock childBlock = block.getChildren(0);

            adicionarIndiceNaTableView(IndexFileManager.getDiretorio(containerIdSelected(), nomeIndiceTextField.getText()));
            this.alert(Alert.AlertType.INFORMATION, "Index", "Index " + nomeIndiceTextField.getText() + " criado com sucesso!");

        } catch (IOException | ContainerNoExistent | IndexBlockNotFoundException | IndexLeafBlockCannotPushPointerChildException | IncorrectTypeToPushPointerException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<Integer> columnsSelectedIndices() {
        ArrayList<Integer> x = new ArrayList<Integer>();
        x.addAll(this.collumnsListView.getSelectionModel().getSelectedIndices());

        return x;
    }

    private void loadIndexTable(int tableSelecionada) {
        this.tableViewIndices.getItems().removeAll(this.tableViewIndices.getItems());
        ContainerId containerIdSelecionado = __tableService.getContainerIdBySelected(containerIds, tableSelecionada);
        List<String> indicesPath = null;
        try {
            indicesPath = indexFileManager.getIndicesPath(containerIdSelecionado);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (indicesPath == null)
            return;

        for (String anIndicesPath : indicesPath) {
            this.adicionarIndiceNaTableView(anIndicesPath);
        }
    }

    private void adicionarIndiceNaTableView(String indicePath) {
//        int ordem = Integer.parseInt(this.ordemDoIndice.getText());
        int ordem = 10;

        TableViewIndice x = new TableViewIndice();
        x.index = (this.tableViewIndiceValues.size() + 1) + "";
        x.arvore = new ArvoreBPlus(ordem);
        x.colunas = this.colunasSelecionadasConcatenadas();
        x.colunasId = this.colunasSelecionadasIds();
        x.tabela = indicePath;
        x.tabelaId = containerIdSelected().getValue();
        x.ordem = ordem + "";

        this.tableViewIndiceValues.add(x);
    }

    private void alert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText("");
        alert.setContentText(content);
        alert.showAndWait();
    }

    private String colunasSelecionadasConcatenadas() {
        StringBuilder x = new StringBuilder();
        for (String c : this.collumnsListView.getSelectionModel().getSelectedItems()) {
            x.append(c).append("; ");
        }

        return x.toString();
    }

    private String colunasSelecionadasIds() {
        StringBuilder x = new StringBuilder();
        for (int i = 0; i < this.collumnsListView.getSelectionModel().getSelectedIndices().size(); i++) {
            int c = this.collumnsListView.getSelectionModel().getSelectedIndices().get(i);
            x.append(c);
            if (i + 1 < this.collumnsListView.getSelectionModel().getSelectedIndices().size())
                x.append(";");

        }

        return x.toString();
    }

    public void onVizualizarClick() {
        System.out.println("onVizualizarClick");
        TableViewIndice indice = this.tableViewIndices.getSelectionModel().getSelectedItem();
        indice.arvore = new ArvoreBPlus(Integer.parseInt(indice.ordem));
        this.carregarIndice(indice);
        this.carregarTableView(indice);
    }

    private void carregarIndice(TableViewIndice indice) {
        String[] colunas = indice.colunasId.split(";");

        try {
            BlocoContainer container = this._ga.selectAllFrom(indice.tabelaId);
            for (BlocoDado bd : container.getBlocosDados()) {
                for (Linha tuple : bd.getTuples()) {
                    StringBuilder c = new StringBuilder();
                    for (String coluna : colunas) {
                        c.append(tuple.getColunas().get(Integer.parseInt(coluna)).getInformacao());
                        c.append(";");
                    }
                    indice.arvore.insert(c.toString(), bd.getRowId());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void carregarTableView(TableViewIndice indice) {
        this.tableViewBancoValores.getColumns().removeAll(this.tableViewBancoValores.getColumns());
        this.tableViewBancoValores.getItems().removeAll(this.tableViewBancoValores.getItems());

        TableColumn<ObservableList<String>, String> tableColumn = new TableColumn<>("#");
        tableColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().get(0)));
        this.tableViewBancoValores.getColumns().add(tableColumn);

        tableColumn = new TableColumn<>("RowId");
        tableColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().get(1)));
        this.tableViewBancoValores.getColumns().add(tableColumn);

        for (int i = 0; i < indice.colunasId.split(";").length; i++) {
            String c = indice.colunas.split(";")[i];

            final int indexColuna = i + 2;
            tableColumn = new TableColumn<>(c);
            tableColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().get(indexColuna)));
            this.tableViewBancoValores.getColumns().add(tableColumn);
        }

        ArvoreBPlus arv = indice.arvore;
        if (!arv.isEmpty()) {
            Queue<Node> queue = new LinkedList<Node>();
            queue.add(arv.getRoot());
            Node tempNode = null;
            int count = 0;

            while (!queue.isEmpty()) {
                tempNode = queue.remove();
                for (int i = 0; i < tempNode.getIndexInsertionKeys(); i++) {
                    if (tempNode.getKey(i) != null) {
                        this.add((++count + ";") + (tempNode.getKey(i).getRowId() + ";") + tempNode.getKey(i).getValue());
                        if (!tempNode.isLeaf()) {
                            for (int j = 0; j < tempNode.getChildrens().length; j++) {
                                if (tempNode.getChildren(j) != null)
                                    queue.add(tempNode.getChildren(j));
                            }
                        }
                    }
                }
            }
        }
    }

    private void add(String value) {
        this.tableViewBancoValores.getItems().add(FXCollections.observableArrayList(value.split(";")));
    }

    public void onBrowserClick() {
        System.out.println("onBrowserClick");

        try {
            FXMLLoader loader;

            loader = new FXMLLoader(getClass().getResource("../view/GraphViewWindow.fxml"));
            GraphViewController controller = new GraphViewController();
            controller.setIndex(this.containerIdSelected());
            loader.setController(controller);

            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Graph");
            stage.setScene(new Scene(root, GraphViewController.WIDTH, GraphViewController.HEIGHT));

            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(GraphViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void onFindKey() {
        if (this.tableViewIndices.getSelectionModel().getSelectedIndices().size() == 0)
            return;

        String findKey = findKeyText.getText();
        TableViewIndice indice = this.tableViewIndices.getSelectionModel().getSelectedItem();
        ArvoreBPlus arv = indice.arvore;

        Key keyToSearch = new Key(findKey);
        RowId keyback = arv.findKey(keyToSearch);

        this.findKeyResultadoCompleto.getItems().removeAll(this.findKeyResultadoCompleto.getItems());
        if (keyback == null) {
            this.findKeyResultado.setText("Não achou!");
            this.findKeyResultadoCompleto.getItems().add("Não achou!");
        } else {
            this.findKeyResultado.setText("Row Id: " + keyback.toString());
            try {
                ArrayList<Linha> tuplas = this._ga.lerBloco(keyback).getTuples();
                List<String> cc = new ArrayList<String>();

                for (Linha t : tuplas) {
                    StringBuilder tupleString = new StringBuilder();
                    for (Coluna c : t.getColunas()) {
                        tupleString.append(c.getInformacao()).append("; ");
                    }
                    cc.add(tupleString.toString());
                }
                this.findKeyResultadoCompleto.getItems().addAll(cc);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.findKeyText.setText("");
    }
}
