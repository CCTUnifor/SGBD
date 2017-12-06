package front.controller;

import entidades.GerenciadorArquivo;
import entidades.GerenciadorArquivoService;
import entidades.arvoreBMais.ArvoreBPlus;
import factories.ContainerId;
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
import prefuse.data.*;
import services.CollumnService;
import services.TableService;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import static utils.GlobalVariables.TREEVIEW_VISUALIZATION;


public class MainController implements Initializable {
    private CollumnService _collumnService;
    private TableService __tableService;

    GerenciadorArquivo _ga;
    GerenciadorArquivoService _gaService;

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
        if (Integer.parseInt(this.ordemDoIndice.getText()) <= 2) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ordem da Arvore");
            alert.setHeaderText("");
            alert.setContentText("A árvore não pode ter ordem menor que 3!");

            alert.showAndWait();
            return;
        }

        System.out.println("onAdicionarIndiceClick");
        TableViewIndice x = new TableViewIndice();
        x.index = "" + this.tableViewIndiceValues.size();
        x.arvore = new ArvoreBPlus(Integer.parseInt(this.ordemDoIndice.getText()));
        x.colunas = this.colunasSelecionadas();
        x.tabela = this.tablesComboBox.getSelectionModel().getSelectedItem();
        x.ordem = this.ordemDoIndice.getText();

        this.tableViewIndiceValues.add(x);
        this.ordemDoIndice.setText("");
    }

    private String colunasSelecionadas() {
        StringBuilder x = new StringBuilder();
        for (String c : this.collumnsListView.getSelectionModel().getSelectedItems()) {
            x.append(c).append("; ");
        }

        return x.toString();
    }

    public void onBrowserClick() {
        System.out.println("onBrowserClick");


        try {
            FXMLLoader loader;

            Table table = new Table();
            table.addColumn("name", String.class);
            table.addColumn("gender", String.class);

            Table edges = new Table();
            edges.addColumn("id1", int.class);
            edges.addColumn("id2", int.class);

            if (TREEVIEW_VISUALIZATION) {
                Tree tree = new Tree(table, edges, "id1", "id2");

                Node root = tree.addRoot();
                root.set("name", "Thiago");
                root.set("gender", "M");

                Node n1 = tree.addChild(root);
                n1.set("name", "Vitor");
                n1.set("gender", "M");

                Edge ed1 = tree.addEdge(root, n1);

                loader = new FXMLLoader(getClass().getResource("../view/TreeViewWindow.fxml"));
                TreeViewController controller = new TreeViewController();
                controller.setTree(tree);
                loader.setController(controller);
            } else {
                Graph g = new Graph(table, edges, true, "id1", "id2");

                Node n1 = g.addNode();
                n1.set("name", "0002");
                n1.set("gender", "M");

                Node n2 = g.addNode();
                n2.set("name", "0001");
                n2.set("gender", "M");

                Node n3 = g.addNode();
                n3.set("name", "0002    0003");
                n3.set("gender", "M");

                Node n4 = g.addNode();
                n3.set("name", "0002    0003");
                n3.set("gender", "M");

                Node n5 = g.addNode();
                n3.set("name", "0002    0003");
                n3.set("gender", "M");

                Edge e1 = g.addEdge(n1, n2);
                Edge e2 = g.addEdge(n1, n3);
                Edge e3 = g.addEdge(n2, n3);

                Edge e4 = g.addEdge(n4, n5);


                loader = new FXMLLoader(getClass().getResource("../view/GraphViewWindow.fxml"));
                GraphViewController controller = new GraphViewController();
                controller.setGraph(g);
                controller.setLabel("name");
                controller.setArvoreBMais(this.mock());

                loader.setController(controller);
            }


            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Graph");
            stage.setScene(new Scene(root, GraphViewController.WIDTH, GraphViewController.HEIGHT));

            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(GraphViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private ArvoreBPlus mock() {
        ArvoreBPlus p = new ArvoreBPlus(7);
        p.insert("Thiago;16");
//        p.insert("Victor;18");
        p.printTreeBPlus();
        return p;
    }

}
