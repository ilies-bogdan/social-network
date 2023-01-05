package com.socialnetwork.controller;

import com.socialnetwork.exceptions.RepositoryException;
import com.socialnetwork.service.NetworkService;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.time.LocalDateTime;

public class SendMessageController {
    private NetworkService networkService;
    private String username;

    @FXML
    TextField textFieldTo;

    @FXML
    TextField textFieldSubject;

    @FXML
    TextArea textAreaMessage;

    public void setData(NetworkService networkService, String username, String to) {
        this.networkService = networkService;
        this.username = username;
        textFieldTo.setText(to);
    }

    @FXML
    protected void handleSendOneMessage() {
        String to = textFieldTo.getText();
        String subject = textFieldSubject.getText();
        String message = textAreaMessage.getText();

        try {
            networkService.addMessage(LocalDateTime.now(), subject, message, username, to);
            PopupMessage.showInformationMessage("Message sent!");
        } catch (RepositoryException exception) {
            PopupMessage.showErrorMessage(exception.getMessage());
        }
    }
}
