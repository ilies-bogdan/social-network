package com.socialnetwork.controller;

import com.socialnetwork.SocialNetwork;
import com.socialnetwork.domain.User;
import com.socialnetwork.exceptions.RepositoryException;
import com.socialnetwork.service.NetworkService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LogInController {
    private NetworkService networkService;

    @FXML
    private TextField textFieldUsername;

    @FXML
    private TextField textFieldPassword;

    public void setNetworkService(NetworkService networkService) {
        this.networkService = networkService;
    }

    @FXML
    protected void handleLogIn(ActionEvent event) {
        try {
            User user = null;
            while (user == null) {
                String username = textFieldUsername.getText();
                String password = textFieldPassword.getText();
                user = networkService.handleLogInRequest(username, password);
                textFieldPassword.setText("");
                if (user != null) {
                    Stage loginStage = (Stage) textFieldUsername.getScene().getWindow();
                    loginStage.hide();
                    startUserSession(user, loginStage);
                }
            }
        } catch (RepositoryException exception) {
            PopupMessage.showErrorMessage("Incorrect log in data!");
        }
    }

    private void startUserSession(User user, Stage loginStage) {
        try {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(SocialNetwork.class.getResource("views/user-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            scene.getStylesheets().add(SocialNetwork.class.getResource("css/style.css").toExternalForm());

            stage.setTitle("Social Network");
            stage.setScene(scene);

            UserController userController = fxmlLoader.getController();
            userController.setData(networkService, user, loginStage);

            stage.show();
        } catch (IOException exception) {
            PopupMessage.showErrorMessage("Session start error!");
        }
    }

    @FXML
    public void handleSignUp(ActionEvent event) {
        try {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(SocialNetwork.class.getResource("views/signup-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            scene.getStylesheets().add(SocialNetwork.class.getResource("css/login.css").toExternalForm());

            stage.setTitle("Sign Up");
            stage.setScene(scene);

            SignUpController signUpController = fxmlLoader.getController();
            signUpController.setNetworkService(networkService);

            stage.show();
        } catch (IOException exception) {
            PopupMessage.showErrorMessage(exception.getMessage());
        }
    }
}
