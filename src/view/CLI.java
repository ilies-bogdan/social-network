package view;

import domain.Friendship;
import domain.User;
import exceptions.RepositoryException;
import exceptions.ValidationException;
import service.ApplicationService;

import java.util.List;
import java.util.Scanner;

public class CLI {
    private ApplicationService appSrv;

    public CLI(ApplicationService usersSrv) {
        this.appSrv = usersSrv;
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
        System.out.println("3. Add friendship");
        System.out.println("4. Remove friendship");
        System.out.println("5. Show users");
        System.out.println("6. Show friendships");
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
            appSrv.addUser(username, password, email);
            System.out.println("User added.\n");
        } catch (RepositoryException | ValidationException exception) {
            System.out.println(exception.getMessage());
        }
    }

    private void removeUser(Scanner scanner) {
        System.out.println("Username: ");
        String username = scanner.nextLine();
        try {
            appSrv.removeUser(username);
            System.out.println("User deleted.\n");
        } catch (RepositoryException exception) {
            System.out.println(exception.getMessage());
        }
    }

    private void addFriendship(Scanner scanner) {
        System.out.println("First User: ");
        String username1 = scanner.nextLine();
        System.out.println("Second User: ");
        String username2 = scanner.nextLine();
        try {
            appSrv.addFriendship(username1, username2);
            System.out.println("Friendship established!\n");
        } catch (RepositoryException exception) {
            System.out.println(exception.getMessage());
        }
    }

    private void removeFriendship(Scanner scanner) {
        System.out.println("First User: ");
        String username1 = scanner.nextLine();
        System.out.println("Second User: ");
        String username2 = scanner.nextLine();
        try {
            appSrv.removeFriendship(username1, username2);
            System.out.println("Friendship disbanded!\n");
        } catch (RepositoryException exception) {
            System.out.println(exception.getMessage());
        }
    }

    private void printUsers() {
        List<User> users = appSrv.getAllUsers();
        for (User user : users) {
            System.out.println(user.toString());
        }
    }

    private void printFriendships() {
        List<Friendship> friendships = appSrv.getAllFriendships();
        for (Friendship friendship : friendships) {
            System.out.println(friendship.toString());
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
                case "3":
                    addFriendship(scanner);
                    break;
                case "4":
                    removeFriendship(scanner);
                    break;
                case "5":
                    printUsers();
                    break;
                case "6":
                    printFriendships();
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
                    // showUserMenu();
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
