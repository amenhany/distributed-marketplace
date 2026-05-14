package org.team13.marketplace.client.Forms;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.team13.marketplace.client.socket.MarketplaceClient;
import org.team13.marketplace.dto.item.ItemDto;
import org.team13.marketplace.dto.item.ProductEnrichmentRequest;
import org.team13.marketplace.model.ItemStatus;
import org.team13.marketplace.socket.SocketResponse;

import java.util.HashMap;
import java.util.Map;

public class EditItemForm {

    private final MarketplaceClient client;
    private final ItemDto selectedItem;

    public EditItemForm(MarketplaceClient client, ItemDto selectedItem) {
        if (selectedItem == null) {
            throw new IllegalArgumentException("Selected item is required.");
        }
        this.client = client;
        this.selectedItem = selectedItem;
    }

    public void show() {
        Stage stage = new Stage();

        Label titleLabel = new Label("Edit Item");
        Label itemLabel = new Label("Editing: " + selectedItem.getName());
        Label itemIdLabel = new Label("Item ID: " + selectedItem.getId());

        TextField nameField = new TextField();
        nameField.setPromptText("New name (optional)");

        TextField brandField = new TextField();
        brandField.setPromptText("New brand (optional)");

        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("New description (optional)");
        descriptionArea.setPrefRowCount(4);

        Button generateDescriptionButton = new Button("Generate Description");

        TextField priceField = new TextField();
        priceField.setPromptText("New price (optional)");

        TextField quantityField = new TextField();
        quantityField.setPromptText("New quantity (optional)");

        ComboBox<ItemStatus> statusComboBox = new ComboBox<>();
        statusComboBox.getItems().addAll(ItemStatus.AVAILABLE, ItemStatus.SOLD, ItemStatus.UNLISTED);
        statusComboBox.setPromptText("New status (optional)");

        prefillFields(nameField, brandField, descriptionArea, priceField, quantityField, statusComboBox);

        Button submitButton = new Button("Save Changes");
        Label statusLabel = new Label();

        submitButton.setOnAction(event -> submitEdit(
                nameField,
                brandField,
                descriptionArea,
                priceField,
                quantityField,
                statusComboBox,
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
                itemLabel,
                itemIdLabel
        );
        root.getChildren().addAll(
                nameField,
                brandField,
                descriptionArea,
                descriptionActions,
                priceField,
                quantityField,
                statusComboBox,
                submitButton,
                statusLabel
        );

        Scene scene = new Scene(root, 420, 520);

        stage.setTitle("Edit Item");
        stage.setScene(scene);
        stage.show();
    }

    private void prefillFields(
            TextField nameField,
            TextField brandField,
            TextArea descriptionArea,
            TextField priceField,
            TextField quantityField,
            ComboBox<ItemStatus> statusComboBox
    ) {
        setTextIfPresent(nameField, selectedItem.getName());
        setTextIfPresent(brandField, selectedItem.getBrand());
        setTextIfPresent(descriptionArea, selectedItem.getDescription());

        if (selectedItem.getPrice() != null) {
            priceField.setText(String.valueOf(selectedItem.getPrice()));
        }

        if (selectedItem.getQuantity() != null) {
            quantityField.setText(String.valueOf(selectedItem.getQuantity()));
        }

        if (selectedItem.getStatus() != null) {
            try {
                statusComboBox.setValue(ItemStatus.valueOf(selectedItem.getStatus()));
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    private void setTextIfPresent(TextField field, String value) {
        if (value != null) {
            field.setText(value);
        }
    }

    private void setTextIfPresent(TextArea field, String value) {
        if (value != null) {
            field.setText(value);
        }
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

    private void submitEdit(
            TextField nameField,
            TextField brandField,
            TextArea descriptionArea,
            TextField priceField,
            TextField quantityField,
            ComboBox<ItemStatus> statusComboBox,
            Button submitButton,
            Label statusLabel
    ) {
        Map<String, Object> payload;

        try {
            payload = buildPayload(
                    nameField,
                    brandField,
                    descriptionArea,
                    priceField,
                    quantityField,
                    statusComboBox
            );
        } catch (IllegalArgumentException e) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText(e.getMessage());
            return;
        }

        submitButton.setDisable(true);
        statusLabel.setStyle("-fx-text-fill: black;");
        statusLabel.setText("Saving changes...");

        Thread requestThread = new Thread(() -> {
            try {
                SocketResponse response = client.send("EDIT_ITEM", payload, ItemDto.class);

                Platform.runLater(() -> {
                    submitButton.setDisable(false);

                    if ("OK".equalsIgnoreCase(response.getStatus())) {
                        statusLabel.setStyle("-fx-text-fill: green;");
                        statusLabel.setText("Item updated successfully.");
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

    private Map<String, Object> buildPayload(
            TextField nameField,
            TextField brandField,
            TextArea descriptionArea,
            TextField priceField,
            TextField quantityField,
            ComboBox<ItemStatus> statusComboBox
    ) {
        String itemId = selectedItem.getId();
        if (itemId.isEmpty()) {
            throw new IllegalArgumentException("Item ID is required.");
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("itemId", itemId);

        putIfChanged(payload, "name", nameField.getText(), selectedItem.getName());
        putIfChanged(payload, "brand", brandField.getText(), selectedItem.getBrand());
        putIfChanged(payload, "description", descriptionArea.getText(), selectedItem.getDescription());

        String priceText = priceField.getText().trim();
        if (!priceText.isEmpty()) {
            double price;
            try {
                price = Double.parseDouble(priceText);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Price must be a valid number.");
            }

            if (price <= 0) {
                throw new IllegalArgumentException("Price must be positive.");
            }

            if (selectedItem.getPrice() == null || Double.compare(price, selectedItem.getPrice()) != 0) {
                payload.put("price", price);
            }
        }

        String quantityText = quantityField.getText().trim();
        if (!quantityText.isEmpty()) {
            int quantity;
            try {
                quantity = Integer.parseInt(quantityText);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Quantity must be a valid whole number.");
            }

            if (quantity < 0) {
                throw new IllegalArgumentException("Quantity cannot be negative.");
            }

            if (selectedItem.getQuantity() == null || quantity != selectedItem.getQuantity()) {
                payload.put("quantity", quantity);
            }
        }

        ItemStatus selectedStatus = statusComboBox.getValue();
        String originalStatus = selectedItem.getStatus();
        if (selectedStatus != null && !selectedStatus.name().equals(originalStatus)) {
            payload.put("status", selectedStatus);
        }

        if (payload.size() == 1) {
            throw new IllegalArgumentException("Enter at least one field to update.");
        }

        return payload;
    }

    private void putIfChanged(Map<String, Object> payload, String key, String newValue, String originalValue) {
        String trimmedNewValue = newValue.trim();
        String safeOriginalValue = originalValue == null ? "" : originalValue.trim();

        if (!trimmedNewValue.equals(safeOriginalValue)) {
            payload.put(key, trimmedNewValue);
        }
    }
}
