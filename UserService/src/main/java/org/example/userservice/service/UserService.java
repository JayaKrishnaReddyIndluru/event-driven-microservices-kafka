package org.example.userservice.service;

import lombok.extern.slf4j.Slf4j;
import org.example.userservice.dto.CreateUserRequest;
import org.example.userservice.enums.UserType;
import org.example.userservice.mapper.UserMapper;
import org.example.userservice.model.User;
import org.example.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import static org.example.userservice.constants.KafkaConstants.USER_CREATION_TOPIC;
import static org.example.userservice.constants.UserCreationTopicConstants.*;

@Service
@Slf4j
public class UserService implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public User loadUserByUsername(String phoneNo) throws UsernameNotFoundException {
        User user = userRepository.findByPhoneNo(phoneNo);
        if (user == null) {
            throw new UsernameNotFoundException("User does not exist");
        }
        return user;
    }

    public User createUser(CreateUserRequest userRequest) {
        User user = UserMapper.mapToUser(userRequest);
        user.setUserType(UserType.USER);
        user.setAuthorities("USER");
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));

        log.info("User created: {}", user);
        userRepository.save(user);

        log.info("User saved: {}", user);

        //Publish the data to Kafka

        //notification service -> username, email
        //wallet service -> phoneno, userstatus

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put(EMAIL, user.getEmail());
        objectNode.put(PHONENO, user.getPhoneNo());
        objectNode.put(NAME, user.getName());
        objectNode.put(USERID, user.getId());

        String kafkaMessage = objectNode.toString();
        kafkaTemplate.send(USER_CREATION_TOPIC, kafkaMessage);

        log.info("Message published to Kafka: {}", kafkaMessage);

        return  user;
    }

    public User getUserByPhoneNo(String phoneNo) {
        return userRepository.findByPhoneNo(phoneNo);
    }
}
