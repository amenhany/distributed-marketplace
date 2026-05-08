package org.team13.marketplace.socket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.team13.marketplace.model.Item;
import org.team13.marketplace.service.AuthService;
import org.team13.marketplace.service.ItemService;
import org.team13.marketplace.service.UserService;
import tools.jackson.databind.json.JsonMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClientHandler {
    private final ItemService itemService;
    private final UserService userService;
    private final AuthService authService;
    private final JsonMapper mapper;

    public void handleClient(Socket socket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            String input = in.readLine();
            while (!input.equals("EXIT")) {
                // Simple ALP: "COMMAND|JSON_DATA"
                String[] parts = input.split("\\|", 3);
                if (parts.length < 3) {
                    out.println("INVALID");
                    input = in.readLine();
                    continue;
                }

                String command = parts[0];
                String token = parts[1];
                String payload = parts[2];

                if (command.equals("REGISTER")) {
                    String[] creds = payload.split(",");
                    send(out, "OK", "Registered", userService.register(creds[0], creds[1]));
                } else if (command.equals("LOGIN")) {
                    // For LOGIN, payload is "username,password"
                    String[] creds = payload.split(",");
                    String resultToken = authService.login(creds[0], creds[1]);
                    out.println("TOKEN|" + resultToken);
                } else if (command.equals("ADD_ITEM")) {
                    Item item = mapper.readValue(payload, Item.class);
                    Item saved = itemService.addItem(token, item);
                    if (saved == null) out.println("FAIL");
                    else out.println("SUCCESS|" + mapper.writeValueAsString(saved));
                } else if (command.equals("GET_ALL")) {
                    out.println("LIST|" + mapper.writeValueAsString(itemService.getAllItems()));
                }

                input = in.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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