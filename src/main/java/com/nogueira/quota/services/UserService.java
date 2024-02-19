package com.nogueira.quota.services;

import com.nogueira.quota.models.User;
import com.nogueira.quota.repositories.UserRepositoryFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepositoryFactory userRepositoryFactory;

    public UserService(UserRepositoryFactory userRepositoryFactory) {
        this.userRepositoryFactory = userRepositoryFactory;
    }

    public User createUser(User user) {
        return userRepositoryFactory.getUserRepository().save(user);
    }

    public List<User> getAllUsers() {
        return userRepositoryFactory.getUserRepository().findAll();
    }

    public User getUserById(Long id) {
        Optional<User> user = userRepositoryFactory.getUserRepository().findById(id);
        return user.orElse(null);
    }

    public User updateUser(Long id, User user) {
        Optional<User> existingUser = userRepositoryFactory.getUserRepository().findById(id);
        if (existingUser.isPresent()) {
            User updatedUser = existingUser.get();
            updatedUser.setFirstName(user.getFirstName());
            updatedUser.setLastName(user.getLastName());
            updatedUser.setLastLoginTimeUtc(user. getLastLoginTimeUtc());
            return userRepositoryFactory.getUserRepository().save(updatedUser);
        }
        return null;
    }

    public void deleteUser(Long id) {
        userRepositoryFactory.getUserRepository().deleteById(id);
    }
}