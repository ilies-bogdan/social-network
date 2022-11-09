import repository.factory.FriendshipRepositoryFactory;
import repository.factory.RepositoryStrategy;
import repository.factory.UserRepositoryFactory;
import service.NetworkService;
import domain.validators.UserValidator;
import view.CLI;

public class Main {
    public static void main(String[] args) {
        String userFileName = "data/test/users_test.csv";
        String friendshipsFileName = "data/test/friendships_test.csv";

        String url = "jdbc:postgresql://localhost:5432/social-network";
        String username = "postgres";
        String password = "postgres";

        NetworkService networkService = NetworkService.getInstance();
        networkService.initialize(UserRepositoryFactory.getInstance().createRepository(RepositoryStrategy.file, userFileName, null, null, null),
                new UserValidator(),
                FriendshipRepositoryFactory.getInstance().createRepository(RepositoryStrategy.file, friendshipsFileName, null, null, null));

        CLI cli = new CLI(networkService);
        cli.run();
    }
}