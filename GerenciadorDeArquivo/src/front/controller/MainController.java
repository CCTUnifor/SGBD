package front.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import services.CollumnService;
import services.TableService;

import java.net.URL;
import java.util.ResourceBundle;


public class MainController implements Initializable {
    private CollumnService _collumnService;
    private TableService __tableService;

    @FXML private ComboBox<String> tablesComboBox;
    @FXML private ListView<String> collumnsListView;
    @FXML private TextField chaveUm;
    @FXML private TextField chaveDois;

    private String tableSelected() {
        return tablesComboBox.getSelectionModel().getSelectedItem();
    }

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

    public void onSelecionarTable() {
        if (tableSelected() == null)
            return;

        System.out.println("Table selecionada: " + tableSelected());
        collumnsListView.setDisable(false);
    }

    public void onAdicionarChavesClick() {
        System.out.println("Chave um: " + chaveUm.getText());
        System.out.println("Chave dois: " + chaveDois.getText());
    }
}
