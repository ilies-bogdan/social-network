package com.socialnetwork;

import com.socialnetwork.repository.database.MessageDBRepository;
import com.socialnetwork.repository.factory.FriendshipRepositoryFactory;
import com.socialnetwork.repository.factory.RepositoryStrategy;
import com.socialnetwork.repository.factory.UserRepositoryFactory;
import com.socialnetwork.service.NetworkService;
import com.socialnetwork.domain.validators.UserValidator;
import com.socialnetwork.view.CLI;

public class Main {
    public static void main(String[] args) {
        String userFileName = "data/test/users_test.csv";
        String friendshipsFileName = "data/test/friendships_test.csv";

        String url = "jdbc:postgresql://localhost:5432/social-network";
        String username = "postgres";
        String password = "postgres";

        NetworkService networkService = NetworkService.getInstance();
        networkService.initialize(UserRepositoryFactory.getInstance().createRepository(RepositoryStrategy.database, null, url, username, password),
                new UserValidator(),
                FriendshipRepositoryFactory.getInstance().createRepository(RepositoryStrategy.database, null, url, username, password),
                new MessageDBRepository(url, username, password));

        CLI cli = new CLI(networkService);
        cli.run();
    }
}