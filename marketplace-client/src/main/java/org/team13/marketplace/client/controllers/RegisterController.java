package org.team13.marketplace.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.team13.marketplace.client.Forms.RegisterForms;
import org.team13.marketplace.client.socket.MarketplaceClient;

/**
 * GUI Controller for the Registration screen.
 * 
 * FORM MODIFICATION GUIDE:
 * When modifying RegisterForms for GUI integration:
 * 1. Remove all Scanner/console input logic
 * 2. The form should accept username, email, and password as parameters or through setter methods
 * 3. The form should handle password confirmation validation before sending the request
 * 4. The form should return a success message or throw an exception on failure
 * 5. GUI updates (status labels, panel switching) should remain in this controller
 * 6. The form should only handle the business logic of sending the registration request
 * 
 * Expected form signature:
 * public String register(String username, String email, String password, String confirmPassword) throws Exception
 */
public class RegisterController {
    
    @FXML
    private TextField usernameField;
    
    @FXML
    private TextField emailField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private PasswordField confirmPasswordField;
    
    @FXML
    private Label statusLabel;
    
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
        String confirmPassword = confirmPasswordField.getText();
        
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            statusLabel.setText("Please fill in all fields");
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            statusLabel.setText("Passwords do not match");
            return;
        }
        
        // Create a custom RegisterForms that uses the GUI input
        RegisterForms registerForms = new RegisterForms(client) {
            @Override
            public void show() {
                // Override to use GUI input instead of Scanner
                org.team13.marketplace.dto.auth.RegisterRequest registerReq = new org.team13.marketplace.dto.auth.RegisterRequest();
                registerReq.setUsername(username);
                registerReq.setEmail(email);
                registerReq.setPassword(password);
                
                try {
                    org.team13.marketplace.socket.SocketResponse response = client.send("REGISTER", registerReq, String.class);
                    
                    if ("OK".equalsIgnoreCase(response.getStatus())) {
                        javafx.application.Platform.runLater(() -> {
                            statusLabel.setText("Registration Successful!");
                            statusLabel.setStyle("-fx-text-fill: green;");
                            // Switch back to login panel after successful registration
                            panelSwitcher.switchToLoginPanel();
                        });
                    } else {
                        javafx.application.Platform.runLater(() -> {
                            statusLabel.setText("Registration Failed: " + response.getMessage());
                        });
                    }
                } catch (Exception e) {
                    javafx.application.Platform.runLater(() -> {
                        statusLabel.setText("Error: " + e.getMessage());
                    });
                }
            }
        };
        
        registerForms.show();
    }
    
    @FXML
    private void handleBackToLogin() {
        panelSwitcher.switchToLoginPanel();
    }
}
