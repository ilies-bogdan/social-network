import repository.database.FriendshipDBRepository;
import repository.database.UserDBRepository;
import repository.file.FriendshipFileRepository;
import repository.file.UserFileRepository;
import service.NetworkService;
import testing.TestRunner;
import domain.validators.UserValidator;
import view.CLI;

public class Main {
    public static void main(String[] args) {
        TestRunner testRunner = new TestRunner();
        testRunner.runTests();

        String url = "jdbc:postgresql://localhost:5432/social-network";
        NetworkService networkService = NetworkService.getInstance();
        networkService.initialize(new UserFileRepository("data/test/users_test.csv"),
                new UserValidator(),
                new FriendshipFileRepository("data/test/friendships_test.csv"));

        CLI cli = new CLI(networkService);
        cli.run();
    }
}