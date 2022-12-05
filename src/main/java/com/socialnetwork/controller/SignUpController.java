package com.socialnetwork.controller;

import com.socialnetwork.exceptions.RepositoryException;
import com.socialnetwork.exceptions.ValidationException;
import com.socialnetwork.service.NetworkService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SignUpController {
    private NetworkService networkService;

    @FXML
    TextField textFieldUsername;

    @FXML
    TextField textFieldEmail;

    @FXML
    TextField textFieldPassword;

    public void setNetworkService(NetworkService networkService) {
        this.networkService = networkService;
    }

    public void handleSignUp(ActionEvent event) {
        String username = textFieldUsername.getText();
        String email = textFieldEmail.getText();
        String password = textFieldPassword.getText();

        try {
            networkService.addUser(username, password, email);
            textFieldPassword.clear();
            PopupMessage.showInformationMessage("Sign up successful!");
            Stage signUpStage = (Stage) textFieldUsername.getScene().getWindow();
            signUpStage.close();
        } catch (ValidationException | RepositoryException exception) {
            PopupMessage.showErrorMessage(exception.getMessage());
        }
    }
}
