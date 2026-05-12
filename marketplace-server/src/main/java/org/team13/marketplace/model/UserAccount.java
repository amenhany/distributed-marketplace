package org.team13.marketplace.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "user_accounts")
//@Sharded(shardKey = "_id")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAccount {
    @Id
    private String id;
    @Builder.Default
    private double balance = 0.0;
}
