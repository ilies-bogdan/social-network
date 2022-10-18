package view;

import exceptions.RepositoryException;
import exceptions.ValidationException;
import service.UsersService;

import java.util.Scanner;

public class CLI {
    private UsersService usersSrv;

    public CLI(UsersService usersSrv) {
        this.usersSrv = usersSrv;
    }

    private void showMainMenu() {
        System.out.println("1. Log in as Admin");
        System.out.println("2. Log in as User");
        System.out.println();
        System.out.println("Press X to exit...");
        System.out.println();
    }

    private void showAdminMenu() {
        System.out.println("1. Add user");
        System.out.println("2. Remove user");
        System.out.println();
        System.out.println("Press X to exit Admin Menu");
        System.out.println();
    }

    private void addUser(Scanner scanner) {
        System.out.println("Username: ");
        String username = scanner.nextLine();
        System.out.println("Password: ");
        String password = scanner.nextLine();
        System.out.println("Email: ");
        String email = scanner.nextLine();
        try {
            usersSrv.addUser(username, password, email);
            System.out.println("User added.\n");
        } catch (RepositoryException | ValidationException exception) {
            System.out.println(exception.getMessage());
        }
    }

    private void removeUser(Scanner scanner) {
        System.out.println("Username: ");
        String username = scanner.nextLine();
        try {
            usersSrv.removeUser(username);
            System.out.println("User deleted.\n");
        } catch (RepositoryException exception) {
            System.out.println(exception.getMessage());
        }
    }

    private void runAdmin(Scanner scanner) {
        while (true) {
            showAdminMenu();
            String input = scanner.nextLine();
            switch (input) {
                case "1":
                    addUser(scanner);
                    break;
                case "2":
                    removeUser(scanner);
                    break;
                case "x":
                case "X":
                    System.out.println("Exiting Admin Menu...");
                    return;
                default:
                    break;
            }
        }
    }

    private void showUserMenu() {
        System.out.println("In user");
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            showMainMenu();
            String input = scanner.nextLine();
            switch (input) {
                case "1":
                    runAdmin(scanner);
                    break;
                case "2":
                    showUserMenu();
                    break;
                case "x":
                case "X":
                    System.out.println("Exiting...");
                    return;
                default:
                    break;
            }
        }
    }
}
