package org.example.notificationservice.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import static org.example.notificationservice.constants.KafkaConstants.USER_CREATION_TOPIC;
import static org.example.notificationservice.constants.UserCreationTopicConstants.EMAIL;
import static org.example.notificationservice.constants.UserCreationTopicConstants.NAME;

@Service
@Slf4j
public class UserCreationConsumer {
    @Autowired
    ObjectMapper mapper;

    @Autowired
    JavaMailSender javaMailSender;

    @KafkaListener(topics = USER_CREATION_TOPIC, groupId = "notification-group")
    public void userCreated(String message) throws JsonProcessingException {
        log.info("User created message received: {}", message);

        ObjectNode node = mapper.readValue(message, ObjectNode.class);

        String name = node.get(NAME).textValue();
        String email = node.get(EMAIL).textValue();

        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setFrom("wallet-service@gmail.com");
        mailMessage.setTo(email);
        mailMessage.setSubject("Welcome to E-Wallet");
        mailMessage.setText("Hey, " + name+ ". Welcome to E-Wallet");
        //FROM
        //TO
        //SUBJECT
        //BODY
        //CC, BCC

        javaMailSender.send(mailMessage);

        log.info("User creation mail sent");
    }

}
