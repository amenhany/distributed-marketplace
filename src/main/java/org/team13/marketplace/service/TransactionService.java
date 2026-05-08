package org.team13.marketplace.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.team13.marketplace.model.Item;
import org.team13.marketplace.model.Transaction;
import org.team13.marketplace.model.User;
import org.team13.marketplace.repository.ItemRepository;
import org.team13.marketplace.repository.TransactionRepository;
import org.team13.marketplace.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private AuthService authService;

    // Requirement VI: Purchase Item
    @Transactional
    public Integer purchaseItem(String token, Item item) {
        // 1. Authenticate Buyer
        Optional<User> buyerAuth = authService.getUserByToken(token);
        if (buyerAuth.isEmpty()) return 403;

        // 2. Update Buyer Balance
        User buyer = buyerAuth.get();
        buyer.setBalance(buyer.getBalance() - item.getPrice());
        userRepository.save(buyer);

        // 3. Update Seller Balance
        User seller = userRepository.findById(item.getOwnerId()).orElseThrow();
        seller.setBalance(seller.getBalance() + item.getPrice());
        userRepository.save(seller);

        // 4. Create Transaction Record (The Partitioned Data)
        Transaction tx = new Transaction();
        tx.setBuyerId(buyer.getId());
        tx.setSellerId(seller.getId());
        tx.setItemId(item.getId());
        tx.setAmount(item.getPrice());
        tx.setCreatedAt(LocalDateTime.now());
        transactionRepository.save(tx);

        // 5. Update new Item owner
        item.setOwnerId(buyer.getId());
        itemRepository.save(item);

        return 200;
    }

    // Requirement VII: View Purchased Items
    public List<Transaction> getPurchaseHistory(String userId) {
        // We query the transaction collection directly by the buyerId index
        return transactionRepository.findByBuyerId(userId);
    }
}
