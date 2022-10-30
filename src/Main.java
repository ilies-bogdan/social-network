import exceptions.RepositoryException;
import exceptions.ValidationException;
import repository.InMemoryRepository;
import service.Network;
import testing.TestRunner;
import domain.validators.UserValidator;
import view.CLI;

public class Main {
    public static void main(String[] args) {
        TestRunner testRunner = new TestRunner();
        testRunner.runTests();

        Network usersService = new Network(new InMemoryRepository<>(), new UserValidator(), new InMemoryRepository<>());
        try {
            usersService.addUser("1", "1", "1");
            usersService.addUser("2", "2", "2");
            usersService.addUser("3", "3", "3");
            usersService.addUser("4", "4", "4");
            usersService.addUser("5", "5", "5");
            usersService.addUser("6", "6", "6");
            usersService.addUser("7", "7", "7");
            usersService.addUser("8", "8", "8");
            usersService.addUser("9", "9", "9");

            usersService.addFriendship("1", "2");
            usersService.addFriendship("1", "3");
            usersService.addFriendship("1", "4");
            usersService.addFriendship("1", "5");
            usersService.addFriendship("1", "6");
            usersService.addFriendship("7", "8");
            usersService.addFriendship("8", "9");
        } catch (RepositoryException | ValidationException e) {}
        CLI cli = new CLI(usersService);
        cli.run();
    }
}