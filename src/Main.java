import exceptions.RepositoryException;
import exceptions.ValidationException;
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

        Network usersService = new Network(new UserFileRepository("data/users_test.txt"), new UserValidator(), new InMemoryRepository<>());
//        try {
//            usersService.addUser("1", "12345678", "1@gmail.com");
//            usersService.addUser("2", "12345678", "2@gmail.com");
//            usersService.addUser("3", "12345678", "3@gmail.com");
//            usersService.addUser("4", "12345678", "4@gmail.com");
//            usersService.addUser("5", "12345678", "5@gmail.com");
//            usersService.addUser("6", "12345678", "6@gmail.com");
//            usersService.addUser("7", "12345678", "7@gmail.com");
//            usersService.addUser("8", "12345678", "8@gmail.com");
//            usersService.addUser("9", "12345678", "9@gmail.com");
//
//            usersService.addFriendship("1", "2");
//            usersService.addFriendship("1", "3");
//            usersService.addFriendship("1", "4");
//            usersService.addFriendship("1", "5");
//            usersService.addFriendship("1", "6");
//            usersService.addFriendship("7", "8");
//            usersService.addFriendship("8", "9");
//        } catch (RepositoryException | ValidationException e) {}

        CLI cli = new CLI(usersService);
        cli.run();
    }
}