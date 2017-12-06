package front;

import front.modelos.GraphView;
import javafx.application.Application;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import prefuse.data.Graph;

import javax.swing.*;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("view/Main.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 757, 472));

        primaryStage.show();
    }

    private void createAndSetSwingContent(final SwingNode swingNode) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Graph graph = new Graph();
                graph.addNode();
                graph.addNode();
                graph.addEdge(0, 1);

                JPanel panel = new GraphView(graph, "teste");
                swingNode.setContent(panel);
            }
        });
    }

    public void caller(String[] args) {
        launch(args);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
