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
    @FXML
    private SwingNode swingNode;
    @FXML
    private Canvas myCanvas;
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
        this._label = label;
    }

    public void setArvoreBMais(ArvoreBPlus _arvore) {
        this.arvore = _arvore;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (MOSTRAR_PREFUSE) {
            GraphView jPanel = new GraphView(_graph, _label);
            swingNode.setContent(jPanel);
        } else {
            gc = this.myCanvas.getGraphicsContext2D();
            this.myCanvas.setHeight(HEIGHT);
            this.myCanvas.setWidth(WIDTH);
            gc.setFill(Color.BLACK);


            int quantidadeDeRoots = 1;
            int countNivel = 0;
            scrollPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);


//
//            xInicial = xInicial/2;
//            yInicial += 20;

            ArvoreBPlus arv = this.arvore;

            if (!arv.isEmpty()) {
                Queue<Node> queue = new LinkedList<Node>();
                queue.add(arv.getRoot());
                Node tempNode = null;
                int yCount = 0;
                double xCount = 0;

                while (!queue.isEmpty()) {
                    tempNode = queue.remove();
                    xCount = 10;
                    yCount++;
                    int j = 0;
                    for (int i = 0; i < tempNode.getIndexInsertionKeys(); i++) {
                        if (tempNode.getKey(i) != null) {
                            Key keyTemp = tempNode.getKey(i);

                            /* */
                            StringBuilder value = new StringBuilder();
                            for (int k = 0; k < 2; k++) {
                                if (keyTemp.getValueColumn(k) != null){
                                    value.append(keyTemp.getValueColumn(k)).append(";");
                                }
                            }


                            double tamanhoNo = value.length() * 6 + 8;
                            double tamanhoTexto = tamanhoNo;
//
//                            double xInicial = (xCount * tamanhoNo);
                            double yInicial = yCount * 30 + 50;
//
                            gc.strokeRoundRect(xCount, yInicial, tamanhoNo, 30, 0, 10);
                            gc.fillText(value.toString(), xCount + 8, yInicial + 18, tamanhoTexto);

                            xCount += tamanhoNo;

                            /**/
                            if (!tempNode.isLeaf()) {
                                while (j < tempNode.getNumberNodeInsertion()) {
                                    queue.add(tempNode.getChildren(j));
                                    j++;
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
