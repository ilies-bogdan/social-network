package com.socialnetwork.controller;

import com.socialnetwork.domain.User;
import com.socialnetwork.domain.dto.FriendshipDto;
import com.socialnetwork.exceptions.RepositoryException;
import com.socialnetwork.service.NetworkService;
import com.socialnetwork.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class UserController implements Observer {
    private NetworkService networkService;
    private User user;
    private Stage loginStage;
    private ObservableList<FriendshipDto> modelFriends = FXCollections.observableArrayList();
    private ObservableList<FriendshipDto> modelFriendRequests = FXCollections.observableArrayList();
    private ObservableList<User> modelUsers = FXCollections.observableArrayList();

    @FXML
    private TableView<FriendshipDto> tableViewFriends;

    @FXML
    private TableColumn<FriendshipDto, String> tableColumnFriendsUsername;

    @FXML
    private TableColumn<FriendshipDto, String> tableColumnFriendsFrom;

    @FXML
    private TableColumn<FriendshipDto, String> tableColumnStatus;

    @FXML
    private TextField textFieldFriendUsername;

    @FXML
    private Label labelFriendRequestCount;

    @FXML
    private TableView<FriendshipDto> tableViewFriendRequests;

    @FXML
    private TableColumn<FriendshipDto, String> tableColumnFriendRequestsUsername;

    @FXML
    private TableColumn<FriendshipDto, String> tableColumnSentAt;

    @FXML
    private TableView<User> tableViewUsers;

    @FXML
    private TableColumn<User, String> tableColumnUsersUsername;

    @FXML
    private TableColumn<User, String> tableColumnUsersEmail;

    public void setData(NetworkService networkService, User user, Stage loginStage) {
        this.networkService = networkService;
        this.user = user;
        this.loginStage = loginStage;
        networkService.addObserver(this);
        initModel();
    }

    @Override
    public void update() {
        initModel();
    }

    @FXML
    private void initModel() {
        modelFriends.setAll(networkService.getFriendships(user));
        modelFriendRequests.setAll(networkService.getFriendRequests(user));

        List<User> userList = new ArrayList<>();
        for (User u : networkService.getAllUsers()) {
            if (!u.equals(user)) {
                userList.add(u);
            }
        }
        modelUsers.setAll(userList);
       // modelUsers.setAll(networkService.getAllUsers());

        int friendRequestCount = networkService.getFriendRequests(user).size();
        labelFriendRequestCount.setText("You have " + friendRequestCount + " friend request(s)!");
    }

    @FXML
    public void initialize() {
        tableColumnFriendsUsername.setCellValueFactory(new PropertyValueFactory<FriendshipDto, String>("friendUsername"));
        tableColumnFriendsFrom.setCellValueFactory(new PropertyValueFactory<FriendshipDto, String>("friendsFrom"));
        tableColumnStatus.setCellValueFactory(new PropertyValueFactory<FriendshipDto, String>("status"));
        tableViewFriends.setItems(modelFriends);

        tableColumnFriendRequestsUsername.setCellValueFactory(new PropertyValueFactory<FriendshipDto, String>("friendUsername"));
        tableColumnSentAt.setCellValueFactory(new PropertyValueFactory<FriendshipDto, String>("friendsFrom"));
        tableViewFriendRequests.setItems(modelFriendRequests);

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
            PopupMessage.showErrorMessage("User not found!");
        }
    }

    @FXML
    protected void handleRemoveFriend(ActionEvent event) {
        FriendshipDto friendshipDto = tableViewFriends.getSelectionModel().getSelectedItem();

        try {
            networkService.removeFriend(user, friendshipDto.getFriendUsername());
            PopupMessage.showInformationMessage("Friend removed!");
        } catch (RepositoryException exception) {
            PopupMessage.showErrorMessage("User not found!");
        }
    }

    @FXML
    protected void handleAcceptFriendRequest() {
        FriendshipDto friendshipDto = tableViewFriendRequests.getSelectionModel().getSelectedItem();

        try {
            networkService.acceptFriendRequest(user, friendshipDto.getFriendUsername());
            PopupMessage.showInformationMessage("Friend request accepted!");
        } catch (RepositoryException exception) {
            PopupMessage.showErrorMessage("User not found!");
        }
    }

    @FXML
    protected void handleRejectFriendRequest() {
        FriendshipDto friendshipDto = tableViewFriendRequests.getSelectionModel().getSelectedItem();

        try {
            networkService.rejectFriendRequest(user, friendshipDto.getFriendUsername());
            PopupMessage.showInformationMessage("Friend request rejected!");
        } catch (RepositoryException exception) {
            PopupMessage.showErrorMessage("User not found!");
        }
    }

    @FXML
    protected void handleLogOut() {
        Stage thisStage = (Stage) textFieldFriendUsername.getScene().getWindow();
        thisStage.close();
        loginStage.show();
    }
}
