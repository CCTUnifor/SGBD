package front.controller;

import front.view.GraphView;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import prefuse.data.Graph;

import java.net.URL;
import java.util.ResourceBundle;

public class GraphViewController implements Initializable {
    @FXML
    private SwingNode swingNode;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Graph graph = new Graph();
        graph.addNode();
        graph.addNode();
        graph.addEdge(0, 1);
        GraphView jPanel = new GraphView(graph, "teste");
        swingNode.setContent(jPanel);
    }
}
