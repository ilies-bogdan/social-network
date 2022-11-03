import repository.file.FriendshipFileRepository;
import repository.file.UserFileRepository;
import repository.memory.InMemoryRepository;
import service.Network;
import testing.TestRunner;
import domain.validators.UserValidator;
import view.CLI;

public class Main {
    public static void main(String[] args) {
        TestRunner testRunner = new TestRunner();
        testRunner.runTests();

        Network applicationService = new Network(new UserFileRepository("data/users_test.csv"),
                new UserValidator(),
                new FriendshipFileRepository("data/friendships.csv"));

        CLI cli = new CLI(applicationService);
        cli.run();
    }
}