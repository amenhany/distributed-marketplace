package org.team13.marketplace.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.team13.marketplace.model.Item;

import java.util.List;

public interface ItemRepository extends MongoRepository<Item, String> {
    List<Item> findByNameContainingIgnoreCaseOrBrandContainingIgnoreCase(String name, String brand);
}
