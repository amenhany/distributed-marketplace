package org.team13.marketplace.client.Forms;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.team13.marketplace.client.socket.MarketplaceClient;
import org.team13.marketplace.dto.item.AddItemRequest;
import org.team13.marketplace.dto.item.ItemDto;
import org.team13.marketplace.dto.item.ProductEnrichmentRequest;
import org.team13.marketplace.socket.SocketResponse;

public class AddItemForm {

    private final MarketplaceClient client;

    public AddItemForm(MarketplaceClient client) {
        this.client = client;
    }

    public void show() {
        Stage stage = new Stage();

        Label titleLabel = new Label("Add Item");

        TextField nameField = new TextField();
        nameField.setPromptText("Name");

        TextField brandField = new TextField();
        brandField.setPromptText("Brand");

        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Description");
        descriptionArea.setPrefRowCount(4);

        Button generateDescriptionButton = new Button("Generate Description");

        TextField priceField = new TextField();
        priceField.setPromptText("Price");

        TextField quantityField = new TextField();
        quantityField.setPromptText("Quantity");

        Button submitButton = new Button("Add Item");
        Label statusLabel = new Label();

        submitButton.setOnAction(event -> submitItem(
                nameField,
                brandField,
                descriptionArea,
                priceField,
                quantityField,
                submitButton,
                statusLabel
        ));

        generateDescriptionButton.setOnAction(event -> generateDescription(
                nameField,
                brandField,
                descriptionArea,
                generateDescriptionButton,
                statusLabel
        ));

        HBox descriptionActions = new HBox(10, generateDescriptionButton);

        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.getChildren().addAll(
                titleLabel,
                nameField,
                brandField,
                descriptionArea,
                descriptionActions,
                priceField,
                quantityField,
                submitButton,
                statusLabel
        );

        Scene scene = new Scene(root, 400, 430);

        stage.setTitle("Add Item");
        stage.setScene(scene);
        stage.show();
    }

    private void generateDescription(
            TextField nameField,
            TextField brandField,
            TextArea descriptionArea,
            Button generateDescriptionButton,
            Label statusLabel
    ) {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Name is required before generating a description.");
            return;
        }

        ProductEnrichmentRequest request = new ProductEnrichmentRequest();
        request.setName(name);
        request.setBrand(brandField.getText().trim());

        generateDescriptionButton.setDisable(true);
        statusLabel.setStyle("-fx-text-fill: black;");
        statusLabel.setText("Generating description...");

        Thread requestThread = new Thread(() -> {
            try {
                SocketResponse response = client.send("AI_ENRICH", request, String.class);

                Platform.runLater(() -> {
                    generateDescriptionButton.setDisable(false);

                    if ("OK".equalsIgnoreCase(response.getStatus())) {
                        descriptionArea.setText((String) response.getData());
                        statusLabel.setStyle("-fx-text-fill: green;");
                        statusLabel.setText("Description generated.");
                    } else {
                        statusLabel.setStyle("-fx-text-fill: red;");
                        statusLabel.setText(response.getMessage());
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    generateDescriptionButton.setDisable(false);
                    statusLabel.setStyle("-fx-text-fill: red;");
                    statusLabel.setText("Error: " + e.getMessage());
                });
            }
        });

        requestThread.setDaemon(true);
        requestThread.start();
    }

    private void submitItem(
            TextField nameField,
            TextField brandField,
            TextArea descriptionArea,
            TextField priceField,
            TextField quantityField,
            Button submitButton,
            Label statusLabel
    ) {
        AddItemRequest request;

        try {
            request = buildRequest(nameField, brandField, descriptionArea, priceField, quantityField);
        } catch (IllegalArgumentException e) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText(e.getMessage());
            return;
        }

        submitButton.setDisable(true);
        statusLabel.setStyle("-fx-text-fill: black;");
        statusLabel.setText("Adding item...");

        Thread requestThread = new Thread(() -> {
            try {
                SocketResponse response = client.send("ADD_ITEM", request, ItemDto.class);

                Platform.runLater(() -> {
                    submitButton.setDisable(false);

                    if ("OK".equalsIgnoreCase(response.getStatus())) {
                        statusLabel.setStyle("-fx-text-fill: green;");
                        statusLabel.setText("Item added successfully.");
                        clearFields(nameField, brandField, descriptionArea, priceField, quantityField);
                    } else {
                        statusLabel.setStyle("-fx-text-fill: red;");
                        statusLabel.setText(response.getMessage());
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    submitButton.setDisable(false);
                    statusLabel.setStyle("-fx-text-fill: red;");
                    statusLabel.setText("Error: " + e.getMessage());
                });
            }
        });

        requestThread.setDaemon(true);
        requestThread.start();
    }

    private AddItemRequest buildRequest(
            TextField nameField,
            TextField brandField,
            TextArea descriptionArea,
            TextField priceField,
            TextField quantityField
    ) {
        String name = nameField.getText().trim();
        String priceText = priceField.getText().trim();
        String quantityText = quantityField.getText().trim();

        if (name.isEmpty()) {
            throw new IllegalArgumentException("Name is required.");
        }

        double price;
        try {
            price = Double.parseDouble(priceText);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Price must be a valid number.");
        }

        if (price <= 0) {
            throw new IllegalArgumentException("Price must be positive.");
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityText);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Quantity must be a valid whole number.");
        }

        if (quantity < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1.");
        }

        AddItemRequest request = new AddItemRequest();
        request.setName(name);
        request.setBrand(brandField.getText().trim());
        request.setDescription(descriptionArea.getText().trim());
        request.setPrice(price);
        request.setQuantity(quantity);
        return request;
    }

    private void clearFields(
            TextField nameField,
            TextField brandField,
            TextArea descriptionArea,
            TextField priceField,
            TextField quantityField
    ) {
        nameField.clear();
        brandField.clear();
        descriptionArea.clear();
        priceField.clear();
        quantityField.clear();
    }
}
