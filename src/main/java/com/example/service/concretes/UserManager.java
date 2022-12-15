package com.example.service.concretes;
import com.example.model.User;
import com.example.repository.UserRepository;
import com.example.service.abstracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@Component
public class UserManager implements UserService {


    private UserRepository userRepository;
    @Autowired
    public UserManager(UserRepository userRepository) {
        super();
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getAllUsers() {
        return null;
    }

    @Override
    public User getUser(String email) {
        User user = this.userRepository.getUserByEmail(email);

        if (user != null){
            return user;
        }
        return null;
    }

    @Override
    public User createUser(User user) {
        String email = user.getEmail();
        User searchUser = this.userRepository.findFirstByEmail(email);
        if (searchUser == null){
            this.userRepository.save(user);
            return user;
        }
        return null;
    }

}
