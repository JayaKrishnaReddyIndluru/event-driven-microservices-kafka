package org.example.transactionservice.repository;

import jakarta.transaction.Transactional;
import org.example.transactionservice.enums.TransactionStatus;
import org.example.transactionservice.model.Transaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

  @Transactional
  @Modifying
  @Query(
      "update Transaction t set t.status = :status, t.transactionStatusMessage= :statusMessage where t.transactionId= :transactionId")
  void updateTransactionStatus(
      TransactionStatus status, String statusMessage, String transactionId);

  List<Transaction> findBySenderPhoneNo(String senderPhoneNo, Pageable pageable);
}
