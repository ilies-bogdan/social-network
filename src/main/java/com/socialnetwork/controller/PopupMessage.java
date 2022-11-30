package com.socialnetwork.controller;

import javafx.scene.control.Alert;

public class PopupMessage {
    static void showInformationMessage(String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Info");
        alert.setContentText(content);
        alert.showAndWait();
    }

    static void showErrorMessage(String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Error");
        alert.setContentText(content);
        alert.showAndWait();
    }
}
