package org.team13.marketplace.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.team13.marketplace.model.User;
import org.team13.marketplace.repository.UserRepository;

import java.util.ArrayList;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional // Ensures both documents are created or neither is
    public User createNewAccount(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setBalance(0.0);
        user = userRepository.save(user); // Generates the ID

//        UserDetails details = new UserDetails();
//        details.setUserId(user.getId());
//        details.setPurchasedItemIds(new ArrayList<>());
//        detailsRepository.save(details);
        return user;
    }

    public Double getBalance(String userId) {
        return userRepository.findById(userId)
                .map(User::getBalance)
                .orElse(0.0);
    }
}
