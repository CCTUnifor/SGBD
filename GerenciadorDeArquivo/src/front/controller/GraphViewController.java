package front.controller;

import front.modelos.GraphView;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import prefuse.data.Graph;

import java.net.URL;
import java.util.ResourceBundle;

public class GraphViewController implements Initializable {
    @FXML
    private SwingNode swingNode;
    private Graph _graph;
    private String _label;

    public void setGraph(Graph graph) {
        this._graph = graph;
    }

    public void setLabel(String label) {
        this._label= label;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        GraphView jPanel = new GraphView(_graph, _label);
        swingNode.setContent(jPanel);
    }
}
