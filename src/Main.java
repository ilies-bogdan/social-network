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

        NetworkService networkService = NetworkService.getInstance();
        networkService.initialize(new UserFileRepository("data/users.csv"),
                new UserValidator(),
                new FriendshipFileRepository("data/friendships.csv"));

        CLI cli = new CLI(networkService);
        cli.run();
    }
}