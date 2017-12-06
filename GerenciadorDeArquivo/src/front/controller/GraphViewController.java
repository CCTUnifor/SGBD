package front.controller;

import front.modelos.GraphView;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
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

    public static final double HEIGHT = 700;
    public static final double WIDTH = 1200;

    private Graph _graph;
    private String _label;
    static GraphicsContext gc;

    public void setGraph(Graph graph) {
        this._graph = graph;
    }

    public void setLabel(String label) {
        this._label= label;
    }

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

            Poss[] roots = mock();

            int quantidadeDeRoots = roots.length;
            double xInicial = ((WIDTH / quantidadeDeRoots) / 2);
            double yInicial = 20;

            Queue<Poss> q = new LinkedList<Poss>();

            for (int i = 0; i < quantidadeDeRoots; i++) {
                Poss p = roots[i];

                if (p == null)
                    continue;;

                gc.strokeRoundRect(xInicial, yInicial, 100, 30, 10, 10);
                gc.fillText(p.value, xInicial + 8, yInicial + 18, 85);

//                while (ro)
            }

        }
    }

    private Poss[] mock() {
        Poss[] p = new Poss[2];
        p[0] = new Poss(0, 0, "5", new Poss(0, 0,  "1", null, null), new Poss(0, 0, "2", null, null));
        return p;
    }
}

class Poss {
    public double x;
    public double y;
    public String value;

    public Poss left;
    public Poss rigth;

    Poss(double _x, double _y, String _value, Poss _left, Poss _rigth) {
        x = _x;
        y = _y;
        value = _value;
        left = _left;
        rigth = _rigth;
    }
}
