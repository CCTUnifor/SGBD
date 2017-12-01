package front.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import services.CollumnService;
import services.TableService;

import java.net.URL;
import java.util.ResourceBundle;


public class MainController implements Initializable {
    private CollumnService _collumnService;

    @FXML private ComboBox<String> tablesComboBox;
    @FXML private ListView<String> collumnsListView;
    private TableService __tableService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        __tableService = new TableService();
        _collumnService = new CollumnService();

        loadTablesComboBox();
        loadCollumnsListView();
    }

    private void loadTablesComboBox() {
        tablesComboBox.getItems().addAll(__tableService.mockTables());
    }

    private void loadCollumnsListView() {
        collumnsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        collumnsListView.getItems().addAll(_collumnService.mockColunas());
    }
}
