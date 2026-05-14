package org.team13.marketplace.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.application.Platform;
import org.team13.marketplace.client.Forms.RegisterForms;
import org.team13.marketplace.client.socket.MarketplaceClient;

public class RegisterController {

    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label statusLabel;

    private MarketplaceClient client;
    private PanelSwitcher panelSwitcher;

    public void setClient(MarketplaceClient client) {
        this.client = client;
    }

    public void setPanelSwitcher(PanelSwitcher panelSwitcher) {
        this.panelSwitcher = panelSwitcher;
    }

    @FXML
    private void handleRegister() {
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirm = confirmPasswordField.getText();

        // 1. Validation
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please fill in all fields");
            return;
        }

        if (!password.equals(confirm)) {
            statusLabel.setText("Passwords do not match!");
            return;
        }

        statusLabel.setText("Registering...");

        // 2. Network Task
        new Thread(() -> {
            try {
                RegisterForms regLogic = new RegisterForms(client);
                regLogic.register(username, password, email);

                Platform.runLater(() -> {
                    // Navigate back to login on success
                    panelSwitcher.switchToLoginPanel();
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    statusLabel.setText("Registration Failed: " + e.getMessage());
                });
            }
        }).start();
    }

    @FXML
    private void handleBackToLogin() {
        panelSwitcher.switchToLoginPanel();
    }
}
