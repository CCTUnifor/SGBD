package front;

import front.view.GraphView;
import javafx.application.Application;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import prefuse.data.Graph;

import javax.swing.*;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("view/Main.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 757, 472));

        /**/
//        final SwingNode swingNode = new SwingNode();
//        createAndSetSwingContent(swingNode);
//
//        Pane pane = new Pane();
//        pane.getChildren().add(swingNode); // Adding swing node
//        /**/
//
//        primaryStage.setScene(new Scene(pane, 100, 50));

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
//                panel.add(new JButton("Click me!"));
                swingNode.setContent(panel);
            }
        });
    }


    public static void main(String[] args) {
        launch(args);
    }
}
