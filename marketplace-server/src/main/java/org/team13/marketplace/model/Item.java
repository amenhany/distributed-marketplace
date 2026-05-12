package org.team13.marketplace.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "items")
//@Sharded(shardKey = "brand")
@Builder(toBuilder = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    @Id
    private String id;
    @TextIndexed(weight = 3)
    private String name;
    @TextIndexed(weight = 2)
    private String brand;
    private Double price;
    private String description;
    private String ownerId;
    private Integer quantity;
    @Builder.Default
    private ItemStatus status = ItemStatus.AVAILABLE;
}