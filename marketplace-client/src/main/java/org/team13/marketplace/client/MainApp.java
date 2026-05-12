package org.team13.marketplace.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.team13.marketplace.client.socket.MarketplaceClient;

public class MainApp extends Application {

    private MarketplaceClient socketClient;
    private Label statusLabel = new Label("Connecting...");

    @Override
    public void start(Stage stage) {
        // Build UI first
        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(new Label("Marketplace System"), statusLabel);
        
        Scene scene = new Scene(root, 400, 200);
        stage.setTitle("Client Terminal");
        stage.setScene(scene);
        stage.show(); // Show window IMMEDIATELY

        // Connect in a separate thread so the GUI doesn't freeze
        Thread connectionThread = new Thread(() -> {
            try {
                socketClient = new MarketplaceClient();
                socketClient.connect("localhost", 9090);
                
                // Update UI on the JavaFX Thread
                Platform.runLater(() -> {
                    statusLabel.setText("Connected Successfully!");
                    statusLabel.setStyle("-fx-text-fill: green;");
                    System.out.println("[SUCCESS] GUI Updated.");
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    statusLabel.setText("Connection Failed: " + e.getMessage());
                    statusLabel.setStyle("-fx-text-fill: red;");
                });
                e.printStackTrace();
            }
        });
        
        connectionThread.setDaemon(true);
        connectionThread.start();
    }

    @Override
    public void stop() throws Exception {
        if (socketClient != null) {
            socketClient.disconnect();
        }
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}