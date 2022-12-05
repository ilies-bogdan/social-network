package com.socialnetwork.controller;

import com.socialnetwork.SocialNetwork;
import com.socialnetwork.domain.User;
import com.socialnetwork.domain.dto.FriendshipDto;
import com.socialnetwork.exceptions.RepositoryException;
import com.socialnetwork.service.NetworkService;
import com.socialnetwork.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;

public class UserController implements Observer {
    private NetworkService networkService;
    private User user;
    private Stage loginStage;
    private ObservableList<FriendshipDto> modelFriends = FXCollections.observableArrayList();
    private ObservableList<FriendshipDto> modelFriendRequests = FXCollections.observableArrayList();

    @FXML
    private TableView<FriendshipDto> tableViewFriends;

    @FXML
    private TableColumn<FriendshipDto, String> tableColumnFriendsUsername;

    @FXML
    private TableColumn<FriendshipDto, String> tableColumnFriendsFrom;

    @FXML
    private TableColumn<FriendshipDto, String> tableColumnStatus;

    @FXML
    private Label labelFriendRequestCount;

    @FXML
    private TableView<FriendshipDto> tableViewFriendRequests;

    @FXML
    private TableColumn<FriendshipDto, String> tableColumnFriendRequestsUsername;

    @FXML
    private TableColumn<FriendshipDto, String> tableColumnSentAt;

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
    }

    @FXML
    protected void handleRemoveFriend(ActionEvent event) {
        FriendshipDto friendshipDto = tableViewFriends.getSelectionModel().getSelectedItem();

        try {
            networkService.removeFriend(user, friendshipDto.getFriendUsername());
            PopupMessage.showInformationMessage("Friend removed!");
        } catch (RepositoryException exception) {
            PopupMessage.showErrorMessage(exception.getMessage());
        }
    }

    @FXML
    protected void handleAcceptFriendRequest() {
        FriendshipDto friendshipDto = tableViewFriendRequests.getSelectionModel().getSelectedItem();

        try {
            networkService.acceptFriendRequest(user, friendshipDto.getFriendUsername());
            PopupMessage.showInformationMessage("Friend request accepted!");
        } catch (RepositoryException exception) {
            PopupMessage.showErrorMessage(exception.getMessage());
        }
    }

    @FXML
    protected void handleRejectFriendRequest() {
        FriendshipDto friendshipDto = tableViewFriendRequests.getSelectionModel().getSelectedItem();

        try {
            networkService.rejectFriendRequest(user, friendshipDto.getFriendUsername());
            PopupMessage.showInformationMessage("Friend request rejected!");
        } catch (RepositoryException exception) {
            PopupMessage.showErrorMessage(exception.getMessage());
        }
    }

    @FXML
    protected void handleLogOut() {
        Stage thisStage = (Stage) tableViewFriends.getScene().getWindow();
        thisStage.close();
        loginStage.show();
    }

    @FXML
    public void handleOpenAddFriend(ActionEvent event) {
        try {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(SocialNetwork.class.getResource("views/addfriend-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            scene.getStylesheets().add(SocialNetwork.class.getResource("css/style.css").toExternalForm());

            stage.setTitle("Add friend");
            stage.setScene(scene);

            AddFriendController addFriendController = fxmlLoader.getController();
            addFriendController.setData(networkService, user);

            stage.show();
        } catch (IOException exception) {
            PopupMessage.showErrorMessage(exception.getMessage());
        }
    }
}
