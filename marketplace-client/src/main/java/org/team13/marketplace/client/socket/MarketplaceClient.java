package org.team13.marketplace.client.socket;

import org.team13.marketplace.socket.SocketRequest;
import org.team13.marketplace.socket.SocketResponse;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.io.*;
import java.net.Socket;
import java.util.Map;

public class MarketplaceClient {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private final JsonMapper mapper = new JsonMapper();

    public void connect(String host, int port) throws IOException {
        socket = new Socket(host, port);
        in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
        in.readLine(); // welcome message
    }

    public <T> SocketResponse send(String command, Object payload, Class<T> dataType)
            throws IOException {
        Map<String, Object> payloadMap = mapper.convertValue(payload, new TypeReference<>() {});
        SocketRequest req = new SocketRequest(command, payloadMap);
        out.println(mapper.writeValueAsString(req));

        String line = in.readLine();
        JsonNode node = mapper.readTree(line);
        T data = mapper.treeToValue(node.get("data"), dataType);
        return new SocketResponse(
                node.get("status").asString("ERROR"),
                node.get("message").asString(""),
                data);
    }

    public void disconnect() throws IOException {
        send("DISCONNECT", Map.of(), Void.class);
        socket.close();
    }
}
