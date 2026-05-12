package org.team13.marketplace.socket;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.team13.marketplace.dto.auth.LoginRequest;
import org.team13.marketplace.dto.auth.RegisterRequest;
import org.team13.marketplace.dto.item.AddItemRequest;
import org.team13.marketplace.dto.item.ProductEnrichmentRequest;
import org.team13.marketplace.dto.item.UpdateItemRequest;
import org.team13.marketplace.dto.transaction.PurchaseRequest;
import org.team13.marketplace.dto.user.ProfileRequest;
import org.team13.marketplace.dto.user.UserDto;
import org.team13.marketplace.exception.MarketplaceException;
import org.team13.marketplace.service.AIService;
import org.team13.marketplace.service.ItemService;
import org.team13.marketplace.service.TransactionService;
import org.team13.marketplace.service.UserService;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
public class ClientHandler implements Runnable {

    private final Socket socket;

    private final ItemService itemService;
    private final UserService userService;
    private final TransactionService transactionService;
    private final AIService aiService;
    private final JsonMapper mapper;
    private final Validator validator;

    private String authenticatedUserId = null;

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            out.println("WELCOME_TO_MARKETPLACE");
            String input;
            while ((input = in.readLine()) != null) {
                try {
                    SocketRequest req = mapper.readValue(input, SocketRequest.class);
                    handleCommand(req, out);

                    if ("DISCONNECT".equals(req.getCommand()))
                        break;

                } catch (JacksonException e) {
                    log.warn("Received malformed JSON: {}", input);
                    send(out, "ERROR", "Malformed JSON request. Please check your syntax.", null);
                }
            }
        } catch (Exception e) {
            log.error("Client handler error", e);
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }

    private void handleCommand(SocketRequest req, PrintWriter out) {
        if (req == null || req.getCommand() == null) {
            send(out, "ERROR", "Invalid request format", null);
            return;
        }

        try {
            guardAuth(req.getCommand());
            Map<String, Object> p = req.getPayload();

            switch (req.getCommand()) {

                case "REGISTER" -> {
                    RegisterRequest r = cast(p, RegisterRequest.class);
                    validate(r);
                    send(out, "OK", "Registered successfully. Please LOGIN.", userService.register(r));
                }

                case "LOGIN" -> {
                    LoginRequest r = cast(p, LoginRequest.class);
                    validate(r);
                    UserDto u = userService.login(r);
                    authenticatedUserId = u.getId();
                    send(out, "OK", "Logged in", u);
                }

                case "DEPOSIT" -> {
                    Object amt = p.get("amount");
                    if (!(amt instanceof Number)) {
                        throw new MarketplaceException("Amount must be a number and cannot be null");
                    }

                    double amount = ((Number) amt).doubleValue();
                    send(out, "OK", "Deposited", userService.deposit(authenticatedUserId, amount));
                }

                case "ADD_ITEM" -> {
                    AddItemRequest r = cast(p, AddItemRequest.class);
                    validate(r);
                    send(out, "OK", "Item added", itemService.addItem(authenticatedUserId, r));
                }

                case "EDIT_ITEM" -> {
                    String id = (String) p.get("itemId");
                    UpdateItemRequest r = cast(p, UpdateItemRequest.class);
                    validate(r);
                    send(out, "OK", "Updated", itemService.editItem(id, authenticatedUserId, r));
                }

                case "REMOVE_ITEM" -> {
                    itemService.removeItem((String) p.get("itemId"), authenticatedUserId);
                    send(out, "OK", "Removed", null);
                }

                case "AI_ENRICH" -> {
                    ProductEnrichmentRequest r = cast(p, ProductEnrichmentRequest.class);
                    validate(r);
                    send(out, "OK", "Enriched Description", aiService.enrichProduct(r));
                }

                case "SEARCH" -> send(out, "OK", null,
                        itemService.searchItems((String) p.get("query")));

                case "PURCHASE" -> {
                    PurchaseRequest r = cast(p, PurchaseRequest.class);
                    validate(r);
                    send(out, "OK", "Purchased",
                            transactionService.purchaseItem(
                                    authenticatedUserId, r.getItemId(), r.getQuantity()));
                }

                case "ACCOUNT" -> send(out, "OK", null,
                        userService.getAccountInfo(authenticatedUserId));

                case "GET_PROFILE" -> {
                    ProfileRequest r = cast(p, ProfileRequest.class);
                    validate(r);
                    send(out, "OK", null, userService.getProfile(r.getUserId()));
                }

                case "GET_NAME" -> {
                    ProfileRequest r = cast(p, ProfileRequest.class);
                    validate(r);
                    send(out, "OK", null, userService.getUser(r.getUserId()));
                }

                case "LOGOUT" -> {
                    authenticatedUserId = null;
                    send(out, "OK", "Logged out", null);
                }

                case "DISCONNECT" -> {
                    authenticatedUserId = null;
                    send(out, "OK", "Bye", null);
                }

                default -> send(out, "ERROR", "Unknown command: " + req.getCommand(), null);

            }
        } catch (Exception e) {
            send(out, "ERROR", e.getMessage(), null);
        }
    }

    private <T> void validate(T object) {
        Set<ConstraintViolation<T>> violations = validator.validate(object);
        if (!violations.isEmpty()) {
            String errorMessage = violations.iterator().next().getMessage();
            throw new MarketplaceException(errorMessage);
        }
    }

    private void guardAuth(String command) {
        boolean isPublic = "REGISTER".equals(command)
                || "LOGIN".equals(command)
                || "DISCONNECT".equals(command);

        if (!isPublic && authenticatedUserId == null)
            throw new SecurityException("Not authenticated. Please LOGIN first.");
    }

    private <T> T cast(Map<String, Object> payload, Class<T> type) {
        if (payload == null) {
            throw new MarketplaceException("Request payload is missing");
        }
        return mapper.convertValue(payload, type);
    }

    private void send(PrintWriter out, String status, String message, Object data) {
        try {
            out.println(mapper.writeValueAsString(
                    new SocketResponse(status, message, data)));
        } catch (Exception e) {
            log.error("Failed to serialize response", e);
        }
    }
}