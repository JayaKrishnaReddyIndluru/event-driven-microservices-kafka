package org.example.transactionservice.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.transactionservice.enums.TransactionStatus;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Transaction {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  int id;

  String senderPhoneNo;
  String receiverPhoneNo;
  String transactionId; // we would return to the user
  Double amount;

  String purpose;

  String transactionStatusMessage;

  @Enumerated(EnumType.STRING)
  TransactionStatus status;

  @CreationTimestamp Date createdOn;

  @UpdateTimestamp Date updatedOn;
}
