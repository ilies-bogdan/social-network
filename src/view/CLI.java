package view;

import domain.Friendship;
import domain.User;
import exceptions.RepositoryException;
import exceptions.ValidationException;
import service.Network;

import java.util.List;
import java.util.Scanner;

public class CLI {
    private Network appSrv;

    public CLI(Network usersSrv) {
        this.appSrv = usersSrv;
    }

    private void showMainMenu() {
        System.out.println("\n1. Log in as Admin");
        System.out.println("2. Log in as User");
        System.out.println();
        System.out.println("Press X to exit...");
        System.out.println();
    }

    private void showAdminMenu() {
        System.out.println("\n1. Add user");
        System.out.println("2. Remove user");
        System.out.println("3. Update user");
        System.out.println("4. Add friendship");
        System.out.println("5. Remove friendship");
        System.out.println("6. Communities count");
        System.out.println("7. Most sociable community");
        System.out.println("8. Show users");
        System.out.println("9. Show friendships");
        System.out.println("\nPress X to exit Admin Menu\n");
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
            System.out.println("\nUser added.\n");
        } catch (RepositoryException | ValidationException exception) {
            System.out.println(exception.getMessage());
        }
    }

    private void removeUser(Scanner scanner) {
        System.out.println("Username: ");
        String username = scanner.nextLine();

        System.out.println("WARNING: Removing the user will remove all of its related friendships!");
        System.out.println("Are you sure you want to continue? Y/N");
        String answer = scanner.nextLine();

        if (answer.equals("Y") || answer.equals("y")) {
            try {
                appSrv.removeUser(username);
                System.out.println("\nUser deleted.\n");
            } catch (RepositoryException exception) {
                System.out.println(exception.getMessage());
            }
        }
        else if (answer.equals("N") || answer.equals("n")) {
            System.out.println("Aborted.");
        }
        else {
            System.out.println("Invalid answer!");
        }
    }

    private void updateUser(Scanner scanner) {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("New password: ");
        String newPassword = scanner.nextLine();
        System.out.print("New email: ");
        String newEmail = scanner.nextLine();

        try {
            appSrv.updateUser(username, newPassword, newEmail);
            System.out.println("\nUser updated!\n");
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
            System.out.println("\nFriendship established!\n");
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
            System.out.println("\nFriendship disbanded!\n");
        } catch (RepositoryException exception) {
            System.out.println(exception.getMessage());
        }
    }

    private void countCommunities() {
        int communitiesCount = appSrv.getNumberOfCommunities();
        String be = "are ";
        String count = " communities.\n";
        if (communitiesCount == 1) {
            be = "is ";
            count = " community.\n";
        }
        System.out.println("\nThere " + be + communitiesCount + count);
    }

    private void mostSociableCommunity() {
        List<User> commmunity = appSrv.mostSociableCommunity();
        System.out.println("\nMost sociable community is:");
        for (int i = 0; i < commmunity.size() - 1; i++) {
            System.out.print(commmunity.get(0).toString() + " -> ");
        }
        System.out.println(commmunity.get(commmunity.size() - 1));
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
                case "1" -> addUser(scanner);
                case "2" -> removeUser(scanner);
                case "3" -> updateUser(scanner);
                case "4" -> addFriendship(scanner);
                case "5" -> removeFriendship(scanner);
                case "6" -> countCommunities();
                case "7" -> mostSociableCommunity();
                case "8" -> printUsers();
                case "9" -> printFriendships();
                case "x", "X" -> {
                    System.out.println("\nExiting Admin Menu...");
                    return;
                }
                default -> System.out.println("\nInvalid command...");
            }
        }
    }

    private void showUserMenu() {
        System.out.println("\nIn user");
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
                    System.out.println("Invalid command...");
                    break;
            }
        }
    }
}
