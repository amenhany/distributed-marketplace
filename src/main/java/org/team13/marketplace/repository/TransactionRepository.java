package org.team13.marketplace.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.team13.marketplace.model.Transaction;

import java.util.List;

public interface TransactionRepository extends MongoRepository<Transaction, String> {
    List<Transaction> findByBuyerId(String buyerId);
}
