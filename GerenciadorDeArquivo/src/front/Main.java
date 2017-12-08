package front;

import front.controller.GraphViewController;
import front.modelos.GraphView;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utils.GlobalVariables;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("view/Main.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setMaximized(true);
        primaryStage.setScene(new Scene(root, GraphViewController.WIDTH, GraphViewController.HEIGHT));

        primaryStage.show();
    }

    public void caller(String[] args) {
        launch(args);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
