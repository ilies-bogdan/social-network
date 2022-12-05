package com.socialnetwork.controller;

import com.socialnetwork.domain.User;
import com.socialnetwork.exceptions.RepositoryException;
import com.socialnetwork.service.NetworkService;
import com.socialnetwork.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AddFriendController implements Observer {
    private NetworkService networkService;
    private User user;
    private ObservableList<User> modelUsers = FXCollections.observableArrayList();

    @FXML
    private TableView<User> tableViewUsers;

    @FXML
    private TableColumn<User, String> tableColumnUsersUsername;

    @FXML
    private TableColumn<User, String> tableColumnUsersEmail;

    @FXML
    private TextField textFieldFriendUsername;

    public void setData(NetworkService networkService, User user) {
        this.networkService = networkService;
        this.user = user;
        networkService.addObserver(this);
        initModel();
    }

    @Override
    public void update() {
        initModel();
    }

    @FXML
    private void initModel() {
        List<User> userList = new ArrayList<>();
        for (User u : networkService.getAllUsers()) {
            if (!u.equals(user)) {
                userList.add(u);
            }
        }
        modelUsers.setAll(userList);
    }

    @FXML
    public void initialize() {
        tableColumnUsersUsername.setCellValueFactory(new PropertyValueFactory<User, String>("username"));
        tableColumnUsersEmail.setCellValueFactory(new PropertyValueFactory<User, String>("email"));
        tableViewUsers.setItems(modelUsers);

        textFieldFriendUsername.textProperty().addListener(o -> handleFilter());
    }

    private void handleFilter() {
        Predicate<User> byUsername = x -> x.getUsername().toLowerCase().startsWith(textFieldFriendUsername.getText());

        List<User> userList = new ArrayList<>();
        for (User u : networkService.getAllUsers()) {
            if (!u.equals(user)) {
                userList.add(u);
            }
        }
        modelUsers.setAll(userList.stream().filter(byUsername).collect(Collectors.toList()));
    }

    @FXML
    protected void handleAddFriend(ActionEvent event) {
        User friend = tableViewUsers.getSelectionModel().getSelectedItem();

        try {
            networkService.addFriend(user, friend.getUsername());
            PopupMessage.showInformationMessage("Friend request sent!");
            textFieldFriendUsername.clear();
        } catch (RepositoryException exception) {
            PopupMessage.showErrorMessage(exception.getMessage());
        }
    }
}
