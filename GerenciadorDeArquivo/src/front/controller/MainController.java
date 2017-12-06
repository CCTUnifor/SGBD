package front.controller;

import entidades.GerenciadorArquivo;
import entidades.GerenciadorArquivoService;
import factories.ContainerId;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
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

    @FXML
    private ComboBox<String> tablesComboBox;
    @FXML
    private ListView<String> collumnsListView;
    @FXML
    private TextField chaveUm;
    @FXML
    private TextField chaveDois;

    private int tableSelected() {
        return tablesComboBox.getSelectionModel().getSelectedIndex();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        __tableService = new TableService();
        _collumnService = new CollumnService();

        _ga = new GerenciadorArquivo();
        _gaService = new GerenciadorArquivoService(_ga);

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

    public void onAdicionarChavesClick() {
        System.out.println("Chave um: " + chaveUm.getText());
        System.out.println("Chave dois: " + chaveDois.getText());
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
}
