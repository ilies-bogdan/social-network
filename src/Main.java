import repository.InMemoryRepository;
import service.ApplicationService;
import testing.TestRunner;
import validators.UserValidator;
import view.CLI;

public class Main {
    public static void main(String[] args) {
        TestRunner testRunner = new TestRunner();
        testRunner.runTests();

        ApplicationService usersService = new ApplicationService(new InMemoryRepository<>(), new UserValidator(), new InMemoryRepository<>());
        CLI cli = new CLI(usersService);
        cli.run();
    }
}