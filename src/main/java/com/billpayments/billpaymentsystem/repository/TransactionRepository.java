package com.billpayments.billpaymentsystem.repository;

import com.billpayments.billpaymentsystem.enums.TransactionType;
import com.billpayments.billpaymentsystem.models.Transaction;
import com.billpayments.billpaymentsystem.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>{
    List<Transaction> findByUserOrderByCreatedAtDesc(User user);
    List<Transaction> findByUserAndTypeOrderByCreatedAtDesc(User user, TransactionType type);
    Optional<Transaction> findByReferenceId(String referenceId);
}


//FindByUser -> find all transactions belonging to this user
//OrderByCreatedAt -> sort them by the date they were created
//Desc -> newest first(descending order)