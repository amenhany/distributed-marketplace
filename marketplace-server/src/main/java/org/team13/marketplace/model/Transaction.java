package org.team13.marketplace.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "transactions")
//@Sharded(shardKey = "buyerId")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    private String id;
    private String buyerId;
    private String sellerId;
    private PurchasedItem purchasedItem;
    private Integer quantity;
    private LocalDateTime createdAt;
}
