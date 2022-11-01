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

        Network usersService = new Network(new UserFileRepository("data/users.txt"), new UserValidator(), new FriendshipFileRepository("data/friendships.txt"));

        CLI cli = new CLI(usersService);
        cli.run();
    }
}