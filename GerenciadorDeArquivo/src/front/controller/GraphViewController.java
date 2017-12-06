package front.controller;

import entidades.arvoreBMais.ArvoreBPlus;
import front.modelos.GraphView;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import prefuse.data.Graph;

import java.net.URL;
import java.util.ResourceBundle;

import static utils.GlobalVariables.MOSTRAR_PREFUSE;

public class GraphViewController implements Initializable {
    @FXML private SwingNode swingNode;
    @FXML private Canvas myCanvas;

    public static final double HEIGHT = 900;
    public static final double WIDTH = 1700;

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
        GraphView jPanel = new GraphView(_graph, _label);
        if (MOSTRAR_PREFUSE)
            swingNode.setContent(jPanel);
        else {
            gc = this.myCanvas.getGraphicsContext2D();
            this.myCanvas.setHeight(HEIGHT);
            this.myCanvas.setWidth(WIDTH);
            gc.setFill(Color.BLACK);


            int quantidadeDeRoots = 1;
            int countNivel = 0;



            double tamanhoNo = arvore.root.numberOfValidKeys() * 80;
            double tamanhoTexto = arvore.root.numberOfValidKeys() * 70;

            double xInicial = ((WIDTH / quantidadeDeRoots) / 2) - (tamanhoNo / 2);
            double yInicial = 20;

            gc.strokeRoundRect(xInicial, yInicial, tamanhoNo, 30, 10, 10);
            gc.fillText(arvore.root.getValues(), xInicial + 8, yInicial + 18, tamanhoTexto);

            xInicial = xInicial/2;
            yInicial += 20;


        }
    }


}
