package ro.iss.service;

import ro.iss.domain.User;
import ro.iss.repository.UserRepository;

import java.util.List;

public class UserService {
    private final UserRepository userRepository = new UserRepository();

    public void registerUser(String username, String password, String type) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setType(type);

        userRepository.save(user);
    }

    public User login(String username, String password) {
        System.out.println("Attempting login with username: [" + username + "] and password: [" + password + "]");
        List<User> users = userRepository.findAll();
        System.out.println("Found " + users.size() + " users in the database.");
        
        for (User u : users) {
            System.out.println("Checking DB user: [" + u.getUsername() + "] / [" + u.getPassword() + "]");
        }

        return users.stream()
                .filter(u -> {
                    boolean usernameMatch = u.getUsername().equals(username);
                    boolean passwordMatch = u.getPassword().equals(password);
                    System.out.println("Comparing: DB(\'" + u.getUsername() + "\', \'" + u.getPassword() + "\') vs Input(\'" + username + "\', \'" + password + "\') -> UsernameMatch=" + usernameMatch + ", PasswordMatch=" + passwordMatch);
                    return usernameMatch && passwordMatch;
                })
                .findFirst()
                .orElse(null);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
