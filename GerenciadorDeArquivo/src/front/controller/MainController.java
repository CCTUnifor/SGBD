package front.controller;

import entidades.GerenciadorArquivo;
import entidades.GerenciadorArquivoService;
import entidades.arvoreBMais.ArvoreBPlus;
import entidades.arvoreBMais.Key;
import entidades.arvoreBMais.Node;
import entidades.blocos.*;
import factories.ContainerId;
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class MainController implements Initializable {
    private CollumnService _collumnService;
    private TableService __tableService;

    private GerenciadorArquivo _ga;
    private GerenciadorArquivoService _gaService;


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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        __tableService = new TableService();
        _collumnService = new CollumnService();

        _ga = new GerenciadorArquivo();
        _gaService = new GerenciadorArquivoService(_ga);

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
    }

    private void loadCollumnsListView(int tableSelecionada) {
        ContainerId containerIdSelecionado = __tableService.getContainerIdBySelected(containerIds, tableSelecionada);
        List<String> descritors = _ga.getDescritores(containerIdSelecionado);

        if (descritors == null)
            return;

        collumnsListView.getItems().clear();
        collumnsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        collumnsListView.getItems().addAll(descritors);
    }

    public void onAdicionarIndiceClick() {
        System.out.println("onAdicionarIndiceClick");
        if (Integer.parseInt(this.ordemDoIndice.getText()) < 2) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ordem da Arvore");
            alert.setHeaderText("");
            alert.setContentText("A árvore não pode ter ordem menor que 2!");

            alert.showAndWait();
            return;
        }

        TableViewIndice x = new TableViewIndice();
        x.index = (this.tableViewIndiceValues.size() + 1) + "";
        x.arvore = new ArvoreBPlus(Integer.parseInt(this.ordemDoIndice.getText()));
        x.colunas = this.colunasSelecionadasConcatenadas();
        x.colunasId = this.colunasSelecionadasIds();
        x.tabela = this.tablesComboBox.getSelectionModel().getSelectedItem();
        x.tabelaId = __tableService.getContainerIdBySelected(containerIds, this.tableSelected()).getValue();
        x.ordem = this.ordemDoIndice.getText();

        this.tableViewIndiceValues.add(x);
        this.ordemDoIndice.setText("");
        this.collumnsListView.getSelectionModel().clearSelection();
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
            BlocoContainer container = this._ga.lerContainer(indice.tabelaId);
            for (BlocoDado bd : container.getBlocosDados()) {
                for (Linha tuple : bd.getTuples()) {
                    StringBuilder c = new StringBuilder();
                    for (int i = 0; i < colunas.length; i++) {
                        c.append(tuple.getColunas().get(Integer.parseInt(colunas[i])).getInformacao());
                        c.append(";");
                    }
                    indice.arvore.insert(c.toString(), bd.getRowId());
                }
            }
        } catch (FileNotFoundException e) {
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
        ArvoreBPlus arvore = this.tableViewIndices.getSelectionModel().getSelectedItem().arvore;

        try {
            FXMLLoader loader;

            loader = new FXMLLoader(getClass().getResource("../view/GraphViewWindow.fxml"));
            GraphViewController controller = new GraphViewController();
            controller.setArvoreBMais(arvore);
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
        }
        else {
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
