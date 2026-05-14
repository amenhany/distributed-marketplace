package org.team13.marketplace.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.team13.marketplace.client.Forms.LoginForms;
import org.team13.marketplace.client.socket.MarketplaceClient;

/**
 * GUI Controller for the Login screen.
 * 
 * FORM MODIFICATION GUIDE:
 * When modifying LoginForms for GUI integration:
 * 1. Remove all Scanner/console input logic
 * 2. The form should accept username and password as parameters or through setter methods
 * 3. The form should return the AccountInfoResponse on success or throw an exception on failure
 * 4. GUI updates (status labels, panel switching) should remain in this controller
 * 5. The form should only handle the business logic of sending the login request
 * 
 * Expected form signature:
 * public AccountInfoResponse login(String username, String password) throws Exception
 */
public class LoginController {
    
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;
    
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
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        
        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please fill in all fields");
            return;
        }
        
        // Create a custom LoginForms that uses the GUI input
        LoginForms loginForms = new LoginForms(client) {
            @Override
            public void show() {
                // Override to use GUI input instead of Scanner
                org.team13.marketplace.dto.auth.LoginRequest loginReq = new org.team13.marketplace.dto.auth.LoginRequest();
                loginReq.setUsername(username);
                loginReq.setPassword(password);
                
                try {
                    org.team13.marketplace.socket.SocketResponse response = client.send("LOGIN", loginReq, org.team13.marketplace.dto.auth.AccountInfoResponse.class);
                    
                    if ("OK".equalsIgnoreCase(response.getStatus())) {
                        org.team13.marketplace.dto.auth.AccountInfoResponse info = (org.team13.marketplace.dto.auth.AccountInfoResponse) response.getData();
                        // Switch to home panel on successful login
                        panelSwitcher.switchToHomePanel(info);
                    } else {
                        javafx.application.Platform.runLater(() -> {
                            statusLabel.setText("Login Failed: " + response.getMessage());
                        });
                    }
                } catch (Exception e) {
                    javafx.application.Platform.runLater(() -> {
                        statusLabel.setText("Error: " + e.getMessage());
                    });
                }
            }
        };
        
        loginForms.show();
    }
    
    @FXML
    private void handleRegister() {
        panelSwitcher.switchToRegisterPanel();
    }
}
