package org.team13.marketplace.client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.team13.marketplace.client.socket.MarketplaceClient;

public class MainApp extends Application {

    private MarketplaceClient socketClient;

    @Override
    public void start(Stage stage) throws Exception {
        // 1. Initialize the socket connection
        socketClient = new MarketplaceClient();
        socketClient.connect("localhost", 9090);

        // 2. Simple UI to show it's running
        Label label = new Label("Marketplace Client Running - Connected to 9090");
        StackPane root = new StackPane(label);
        Scene scene = new Scene(root, 400, 200);

        stage.setTitle("Distributed Marketplace");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        // Good practice: close connections when closing the window
        System.exit(0); 
    }

    public static void main(String[] args) {
        launch(args);
    }
}