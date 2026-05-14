package org.team13.marketplace.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.team13.marketplace.client.controllers.PanelSwitcher;
import org.team13.marketplace.client.socket.MarketplaceClient;

public class MainApp extends Application {

    private MarketplaceClient socketClient;
    private Label statusLabel = new Label("Connecting to Server...");
    private PanelSwitcher panelSwitcher;
    @Override
    public void start(Stage stage) {
        // 1. Build Splash/Connection UI
        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(new Label("Marketplace System"), statusLabel);
        
        Scene scene = new Scene(root, 400, 200);
        stage.setTitle("Client Terminal");
        stage.setScene(scene);
        stage.show(); 

        // 2. Connect in a background thread
        Thread connectionThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socketClient = new MarketplaceClient();
                    // Match the port used in your previous snippet (9090)
                    socketClient.connect("localhost", 9090);

                    Platform.runLater(() -> {
                        statusLabel.setText("Connected Successfully!");
                        statusLabel.setStyle("-fx-text-fill: green;");
                        System.out.println("[SUCCESS] GUI Updated.");

                        // Initialize panel switcher with the established connection
                        panelSwitcher = new PanelSwitcher(stage, socketClient);
                        panelSwitcher.switchToLoginPanel();
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        statusLabel.setText("Connection Failed: " + e.getMessage());
                        statusLabel.setStyle("-fx-text-fill: red;");
                    });
                    e.printStackTrace();
                }
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
        // Only call launch(args) here. 
        // The startup logic belongs in the start() method.
        launch(args);
    }
}