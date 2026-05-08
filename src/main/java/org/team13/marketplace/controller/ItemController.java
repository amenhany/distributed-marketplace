package org.team13.marketplace.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.team13.marketplace.model.Item;
import org.team13.marketplace.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @GetMapping
    public List<Item> getAll() {
        return itemService.getAllItems();
    }

//    @PostMapping
//    public Item create(@RequestBody Item item) {
//        return itemService.addItem(item);
//    }

    @GetMapping("/search")
    public List<Item> search(@RequestParam String q) {
        return itemService.searchItems(q);
    }
}
