package com.socialnetwork;

import com.socialnetwork.controller.LogInController;
import com.socialnetwork.domain.validators.UserValidator;
import com.socialnetwork.repository.database.FriendshipDBRepository;
import com.socialnetwork.repository.database.UserDBRepository;
import com.socialnetwork.service.NetworkService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SocialNetwork extends Application {
    private NetworkService networkService;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        String userFileName = "data/test/users_test.csv";
        String friendshipsFileName = "data/test/friendships_test.csv";

        String url = "jdbc:postgresql://localhost:5432/social-network";
        String username = "postgres";
        String password = "postgres";
        networkService = NetworkService.getInstance();
        networkService.initialize(new UserDBRepository(url, username, password),
                new UserValidator(),
               new FriendshipDBRepository(url, username, password));

        initView(primaryStage);
        primaryStage.show();
    }

    private void initView(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("views/login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 420, 260);
        scene.getStylesheets().add(SocialNetwork.class.getResource("css/login.css").toExternalForm());
        primaryStage.setTitle("Log In");
        primaryStage.setScene(scene);

        LogInController logInController = fxmlLoader.getController();
        logInController.setNetworkService(networkService);
    }
}
