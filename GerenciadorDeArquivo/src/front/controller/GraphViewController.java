package front.controller;

import entidades.arvoreBMais.ArvoreBPlus;
import entidades.arvoreBMais.Key;
import entidades.arvoreBMais.Node;
import front.modelos.GraphView;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.paint.Color;
import prefuse.data.Graph;

import java.net.URL;
import java.util.LinkedList;
import java.util.Queue;
import java.util.ResourceBundle;

import static utils.GlobalVariables.MOSTRAR_PREFUSE;

public class GraphViewController implements Initializable {
    @FXML private SwingNode swingNode;
    @FXML private Canvas myCanvas;
    @FXML
    ScrollPane scrollPane;

    public static final double HEIGHT = 700;
    public static final double WIDTH = 1300;

    private Graph _graph;
    private String _label;
    static GraphicsContext gc;
    private ArvoreBPlus arvore;

    public void setGraph(Graph graph) {
        this._graph = graph;
    }
    public void setLabel(String label) {
        this._label= label;
    }

    public void setArvoreBMais(ArvoreBPlus _arvore) {this.arvore = _arvore;}

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (MOSTRAR_PREFUSE){
            GraphView jPanel = new GraphView(_graph, _label);
            swingNode.setContent(jPanel);
        }
        else {
            gc = this.myCanvas.getGraphicsContext2D();
            this.myCanvas.setHeight(HEIGHT);
            this.myCanvas.setWidth(WIDTH);
            gc.setFill(Color.BLACK);


            int quantidadeDeRoots = 1;
            int countNivel = 0;
            scrollPane.setMaxHeight(Double.MAX_VALUE);
            scrollPane.setMaxWidth(Double.MAX_VALUE);


//
//            xInicial = xInicial/2;
//            yInicial += 20;

            ArvoreBPlus arv = this.arvore;

            if (!arv.isEmpty()) {
                Queue<Node> queue = new LinkedList<Node>();
                queue.add(arv.getRoot());
                Node tempNode = null;
                int yCount = 0;
                int xCount = 0;

                while (!queue.isEmpty()) {
                    tempNode = queue.remove();
                    xCount = 0;
                    yCount++;

                    for (int i = 0; i < tempNode.getIndexInsertionKeys(); i++) {
                        if (tempNode.getKey(i) != null) {
                            Key keyTemp = tempNode.getKey(i);

                            /* */

                            double tamanhoNo = tempNode.getIndexInsertionKeys() * 2;
                            double tamanhoTexto = tempNode.getIndexInsertionKeys() * 2;
                            if (tamanhoNo == 0)
                                tamanhoNo = 80;
                            if (tamanhoTexto == 0)
                                tamanhoTexto = 70;
//
                            double xInicial = (xCount * tamanhoNo) * 5;
                            double yInicial = yCount * 30 + 10;
//
                            gc.strokeRoundRect(xInicial, yInicial, tamanhoNo, 30, 10, 10);
                            gc.fillText(keyTemp.getValueColumn(0), xInicial + 8, yInicial + 18, tamanhoTexto);
                            xCount++;

                            /**/
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
    }

    private void print(int nivel, String value) {

    }
}
