package org.team13.marketplace.socket;

import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.team13.marketplace.service.ItemService;
import org.team13.marketplace.service.TransactionService;
import org.team13.marketplace.service.UserService;
import tools.jackson.databind.json.JsonMapper;

import java.net.Socket;

@Component
@RequiredArgsConstructor
public class ClientHandlerFactory {

    private final ItemService itemService;
    private final UserService userService;
    private final TransactionService transactionService;
    private final JsonMapper mapper;
    private final Validator validator;

    public ClientHandler create(Socket socket) {
        return new ClientHandler(
                socket, itemService, userService,
                transactionService, mapper, validator
        );
    }
}
