package org.team13.marketplace.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.team13.marketplace.dto.item.ItemDto;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountInfoResponse {

    private String userId;
    private String username;
    private String email;
    private double balance;
    // Items this user currently has
    private List<ItemDto> ownedItems;

    // Items this user bought from others
    private List<ItemDto> purchasedItems;

    // Items this user has successfully sold
    private List<ItemDto> soldItems;
}
