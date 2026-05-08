package org.team13.marketplace.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.team13.marketplace.model.Item;
import org.team13.marketplace.model.User;
import org.team13.marketplace.repository.ItemRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ItemService {
    @Autowired private ItemRepository itemRepository;
    @Autowired private AuthService authService;

    public Item addItem(String token, Item item) {
        Optional<User> buyerAuth = authService.getUserByToken(token);
        if (buyerAuth.isEmpty()) return null;
        return itemRepository.save(item);
    }

    public List<Item> getAllItems() { return itemRepository.findAll(); }

    public List<Item> searchItems(String query) {
        return itemRepository.findByNameContainingIgnoreCaseOrBrandContainingIgnoreCase(query, query);
    }

    public void deleteItem(String id) { itemRepository.deleteById(id); }
}
