package com.socialnetwork.controller;

import com.socialnetwork.domain.Message;
import com.socialnetwork.service.NetworkService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class MessagesController {
    private NetworkService networkService;
    private String username;
    private ObservableList<Message> modelMessage = FXCollections.observableArrayList();

    @FXML
    private TableView<Message> tableViewMessages;

    @FXML
    private TableColumn<Message, String> tableColumnTime;

    @FXML
    private TableColumn<Message, String> tableColumnFrom;

    @FXML
    private TableColumn<Message, String> tableColumnSubject;

    @FXML
    private Label labelText;

    public void setData(NetworkService networkService, String username) {
        this.networkService = networkService;
        this.username = username;
        initModel();
    }

    @FXML
    private void initModel() {
        modelMessage.setAll(networkService.getAllMessagesForSomeone(username));
    }

    @FXML
    public void initialize() {
        tableColumnTime.setCellValueFactory(new PropertyValueFactory<>("sentAt"));
        tableColumnFrom.setCellValueFactory(new PropertyValueFactory<>("sender"));
        tableColumnSubject.setCellValueFactory(new PropertyValueFactory<>("subject"));
        tableViewMessages.setItems(modelMessage);

        tableViewMessages.getSelectionModel().selectedItemProperty().addListener((x) ->
                labelText.setText(tableViewMessages.getSelectionModel().getSelectedItem().getText()));
    }
}
