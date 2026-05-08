package org.team13.marketplace.socket;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SocketResponse {
    /**
     * "OK" for success, class="str">"ERROR" for failures.
     */
    private String status;

    /**
     * Human-readable message (used for error details or confirmations).
     */
    private String message;

    /**
     * The response payload serialized to JSON.
     * Can be a User, Item, Transaction, List, etc.
     */
    private Object data;
}

