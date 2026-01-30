package org.example.walletservice.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.example.walletservice.model.Wallet;
import org.example.walletservice.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static org.example.walletservice.constants.KafkaConstants.TRANSACTION_INITIATED_TOPIC;
import static org.example.walletservice.constants.KafkaConstants.TRANSACTION_UPDATED_TOPIC;
import static org.example.walletservice.constants.TransactionInitiatedConstants.*;
import static org.example.walletservice.constants.TransactionUpdatedConstants.STATUS;
import static org.example.walletservice.constants.TransactionUpdatedConstants.STATUSMESSAGE;

@Service
@Slf4j
public class TransactionInitiatedConsumer {

  @Autowired ObjectMapper mapper;

  @Autowired WalletRepository walletRepository;

  @Autowired KafkaTemplate kafkaTemplate;

  @KafkaListener(topics = TRANSACTION_INITIATED_TOPIC, groupId = "wallet-group")
  public void transactionInitiated(String message) throws JsonProcessingException {
    log.info("Transaction initiated message received: {}", message);
    ObjectNode node = mapper.readValue(message, ObjectNode.class);

    String senderPhoneNo = node.get(SENDERPHONENO).textValue();
    String receiverPhoneNo = node.get(RECEIVERPHONENO).textValue();
    String transactionId = node.get(TRANSACTIONID).textValue();
    Double amount = node.get(AMOUNT).doubleValue();

    // fetching the wallets of the users

    Wallet senderWallet = walletRepository.findByPhoneNo(senderPhoneNo);
    Wallet receiverWallet = walletRepository.findByPhoneNo(receiverPhoneNo);

    String status;
    String statusMessage;

    if (senderWallet == null) {
      log.info("sender wallet is not present");
      status = "FAILED";
      statusMessage = "Sender wallet does not exist in our system";
    } else if (receiverWallet == null) {
      log.info("receiver wallet is not present");
      status = "FAILED";
      statusMessage = "Receiver wallet does not exist in our system";
    } else if (amount > senderWallet.getBalance()) {
      log.info("Amount sent is greater");
      status = "FAILED";
      statusMessage = "Amount sent is greater than the amount in the sender's wallet";
    } else {
      // successful transaction
      log.info("Successful");
      status = "SUCCESSFUL";
      statusMessage = "Transaction is successful";
      updateWallets(senderWallet, receiverWallet, amount);
      log.info("Wallet updated");
    }
    // publish the message back to Kafka

    sendMessageToKafka(transactionId, status, statusMessage);

    log.info("Message sent to kafka");
  }

  private void sendMessageToKafka(String transactionId, String status, String statusMessage) {
    ObjectNode node = mapper.createObjectNode();
    node.put(STATUS, status);
    node.put(STATUSMESSAGE, statusMessage);
    node.put(TRANSACTIONID, transactionId);

    kafkaTemplate.send(TRANSACTION_UPDATED_TOPIC, node.toString());
  }

  @Transactional
  public void updateWallets(Wallet senderWallet, Wallet receiverWallet, Double amount) {
    walletRepository.updateWallet(senderWallet.getPhoneNo(), -amount);
    walletRepository.updateWallet(receiverWallet.getPhoneNo(), amount);
  }
}
