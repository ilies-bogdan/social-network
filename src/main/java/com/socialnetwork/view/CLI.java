package com.socialnetwork.view;

import com.socialnetwork.domain.FriendshipStatus;
import com.socialnetwork.domain.User;
import com.socialnetwork.exceptions.RepositoryException;
import com.socialnetwork.exceptions.ValidationException;
import com.socialnetwork.service.NetworkService;

import java.util.List;
import java.util.Scanner;

public class CLI {
    private NetworkService networkService;

    public CLI(NetworkService networkService) {
        this.networkService = networkService;
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
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();

        try {
            networkService.addUser(username, password, email);
            System.out.println("\nUser added.\n");
        } catch (RepositoryException | ValidationException exception) {
            exception.printStackTrace();
        }
    }

    private void removeUser(Scanner scanner) {
        System.out.print("Username: ");
        String username = scanner.nextLine();

        System.out.println("WARNING: Removing the user will remove all of its related friendships!");
        System.out.println("Are you sure you want to continue? Y/N");
        String answer = scanner.nextLine();

        if (answer.equals("Y") || answer.equals("y")) {
            try {
                networkService.removeUser(username);
                System.out.println("\nUser deleted.\n");
            } catch (RepositoryException exception) {
                exception.printStackTrace();
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
            networkService.updateUser(username, newPassword, newEmail);
            System.out.println("\nUser updated!\n");
        } catch (ValidationException | RepositoryException exception) {
            exception.printStackTrace();
        }
    }

    private void addFriendship(Scanner scanner) {
        System.out.print("First User: ");
        String username1 = scanner.nextLine();
        System.out.print("Second User: ");
        String username2 = scanner.nextLine();

        try {
            networkService.addFriendship(username1, username2, FriendshipStatus.accepted);
            System.out.println("\nFriendship established!\n");
        } catch (RepositoryException exception) {
            exception.printStackTrace();
        }
    }

    private void removeFriendship(Scanner scanner) {
        System.out.print("First User: ");
        String username1 = scanner.nextLine();
        System.out.print("Second User: ");
        String username2 = scanner.nextLine();

        try {
            networkService.removeFriendship(username1, username2);
            System.out.println("\nFriendship over!:(\n");
        } catch (RepositoryException exception) {
            exception.printStackTrace();
        }
    }

    private void countCommunities() {
        int communitiesCount = networkService.getNumberOfCommunities();
        String be = "are ";
        String count = " communities.\n";
        if (communitiesCount == 1) {
            be = "is ";
            count = " community.\n";
        }
        System.out.println("\nThere " + be + communitiesCount + count);
    }

    private void mostSociableCommunity() {
        List<User> community = networkService.mostSociableCommunity();
        System.out.println("\nMost sociable community is:");
        for (int i = 0; i < community.size() - 1; i++) {
            System.out.print(community.get(i).toString() + " -> ");
        }
        System.out.println(community.get(community.size() - 1));
    }

    private void printUsers() {
        networkService.getAllUsers().forEach(System.out::println);
    }

    private void printFriendships() {
       networkService.getAllFriendships().forEach(System.out::println);
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
        System.out.println("\n1. Show friends");
        System.out.println("2. Show friend requests");
        System.out.println("3. Add friend");
        System.out.println("4. Remove friend");
        System.out.println("\nPress X to exit User Menu");
    }

    private void showFriends(User user) {
        networkService.getFriends(user).forEach(System.out::println);
    }

    private void showFriendRequests(User user) {
        networkService.getFriendRequests(user).forEach(System.out::println);
    }

    private void addFriend(User user, Scanner scanner) {
        System.out.print("Friend username: ");
        String username = scanner.nextLine();
        try {
            networkService.addFriend(user, username);
            System.out.println("Friend request sent!");
        } catch(RepositoryException exception) {
            exception.printStackTrace();
        }
    }

    private void removeFriend(User user, Scanner scanner) {
        System.out.print("Friend username: ");
        String username = scanner.nextLine();
        try {
            networkService.removeFriend(user, username);
            System.out.println("Friend removed / Friend request unsent!");
        } catch(RepositoryException exception) {
            exception.printStackTrace();
        }
    }

    private void runUser(Scanner scanner) {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        User user = null;
        try {
            user = networkService.handleLogInRequest(username, password);
        } catch (RepositoryException exception) {
            System.out.println("User does not exist.");
            return;
        }
        if (user == null) {
            System.out.println("Invalid credentials!");
            return;
        }

        while (true) {
            showUserMenu();
            String input = scanner.nextLine();
            switch (input) {
                case "1" -> showFriends(user);
                case "2" -> showFriendRequests(user);
                case "3" -> addFriend(user, scanner);
                case "4" -> removeFriend(user, scanner);
                case "x", "X" -> {
                    System.out.println("\nExiting User Menu...");
                    return;
                }
                default -> System.out.println("\nInvalid command...");
            }
        }
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            showMainMenu();
            String input = scanner.nextLine();
            switch (input) {
                case "1" -> runAdmin(scanner);
                case "2" -> runUser(scanner);
                case "x", "X" -> {
                    System.out.println("Exiting...");
                    return;
                }
                default -> System.out.println("Invalid command...");
            }
        }
    }
}
