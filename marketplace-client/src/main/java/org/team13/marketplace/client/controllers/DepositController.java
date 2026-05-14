package org.team13.marketplace.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import org.team13.marketplace.client.Forms.DepositForms;
import org.team13.marketplace.client.socket.MarketplaceClient;
import org.team13.marketplace.dto.auth.AccountInfoResponse;

public class DepositController {

    @FXML
    private TextField amountField;

    @FXML
    private Label statusLabel;

    private PanelSwitcher panelSwitcher;
    private DepositForms depositForms;
    private AccountInfoResponse currentUser;

    public void setClient(MarketplaceClient client) {
        this.depositForms = new DepositForms(client);
    }

    public void setPanelSwitcher(PanelSwitcher panelSwitcher) {
        this.panelSwitcher = panelSwitcher;
    }

    public void setCurrentUser(AccountInfoResponse currentUser) {
        this.currentUser = currentUser;
    }

    @FXML
    private void handleDeposit() {
        String amountText = amountField.getText();
        if (amountText == null || amountText.isEmpty()) {
            setStatus("Please enter an amount", Color.RED);
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);
            if (amount <= 0) {
                setStatus("Amount must be positive", Color.RED);
                return;
            }

            double newBalance = depositForms.deposit(amount);
            currentUser.setBalance(newBalance);
            setStatus("Successfully deposited $" + String.format("%.2f", amount), Color.GREEN);
            amountField.clear();

        } catch (NumberFormatException e) {
            setStatus("Invalid amount format", Color.RED);
        } catch (Exception e) {
            setStatus("Error: " + e.getMessage(), Color.RED);
        }
    }

    @FXML
    private void handleBack() {
        panelSwitcher.switchToAccountInfoPanel(currentUser);
    }

    private void setStatus(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setTextFill(color);
    }
}
