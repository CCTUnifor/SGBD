package front.controller;

import front.modelos.GraphView;
import front.modelos.TreeView;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import prefuse.data.Tree;

import javax.swing.*;
import java.net.URL;
import java.util.ResourceBundle;

public class TreeViewController implements Initializable {
    @FXML private SwingNode swingNode;
    private Tree _tree;

    public void setTree(Tree tree) {
        this._tree = tree;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        JPanel jPanel = new JPanel();
        TreeView display = new TreeView(_tree, "teste");
        jPanel.add(display);

        swingNode.setContent(jPanel);
    }
}
