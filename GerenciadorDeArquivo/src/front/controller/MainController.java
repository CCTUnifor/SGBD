package front.controller;

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
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;


public class MainController implements Initializable {
    private CollumnService _collumnService;
    private TableService __tableService;

    @FXML
    private ComboBox<String> tablesComboBox;
    @FXML
    private ListView<String> collumnsListView;
    @FXML
    private TextField chaveUm;
    @FXML
    private TextField chaveDois;

    private String tableSelected() {
        return tablesComboBox.getSelectionModel().getSelectedItem();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        __tableService = new TableService();
        _collumnService = new CollumnService();

        loadTablesComboBox();
        loadCollumnsListView();
    }

    private void loadTablesComboBox() {
        tablesComboBox.getItems().addAll(__tableService.mockTables());
    }

    private void loadCollumnsListView() {
        collumnsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        collumnsListView.getItems().addAll(_collumnService.mockColunas());
    }

    public void onSelecionarTable() {
        if (tableSelected() == null)
            return;

        System.out.println("Table selecionada: " + tableSelected());
        collumnsListView.setDisable(false);
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

            boolean treeVisualization = false;

            if (treeVisualization) {
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
            } else {
                Graph g = new Graph(table, edges, true, "id1", "id2");

                Node n1 = g.addNode();
                n1.set("name", "Thiago");
                n1.set("gender", "M");

                Node n2 = g.addNode();
                n2.set("name", "Vitor");
                n2.set("gender", "M");

                Node n3 = g.addNode();
                n3.set("name", "teste");
                n3.set("gender", "M");

                Edge e1 = g.addEdge(n1, n2);
//                Edge e2 = g.addEdge(n2, n3);
                Edge e3 = g.addEdge(n3, n1);

                loader = new FXMLLoader(getClass().getResource("../view/GraphViewWindow.fxml"));
                GraphViewController controller = new GraphViewController();
                controller.setGraph(g);
                controller.setLabel("name");
                loader.setController(controller);
            }


            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Graph");
            stage.setScene(new Scene(root, 1020, 700));

            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(GraphViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
