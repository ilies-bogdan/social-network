import domain.User;
import exceptions.RepositoryException;
import exceptions.ValidationException;
import repository.InMemoryRepository;
import service.UsersService;
import testing.TestRunner;
import validators.UserValidator;
import view.CLI;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        TestRunner testRunner = new TestRunner();
        testRunner.runTests();

        UsersService usersService = new UsersService(new InMemoryRepository<>(), new UserValidator());
        CLI cli = new CLI(usersService);
        cli.run();
    }
}