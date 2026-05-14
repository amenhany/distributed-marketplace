package org.team13.marketplace.client.Forms;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.team13.marketplace.client.socket.MarketplaceClient;
import org.team13.marketplace.dto.item.AddItemRequest;
import org.team13.marketplace.dto.item.ItemDto;
import org.team13.marketplace.socket.SocketResponse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BulkAddItemsForm {

    private final MarketplaceClient client;
    private File selectedFile;

    public BulkAddItemsForm(MarketplaceClient client) {
        this.client = client;
    }

    public void show() {
        Stage stage = new Stage();

        Label titleLabel = new Label("Bulk Add Items");
        Label fileLabel = new Label("No CSV selected.");
        Label statusLabel = new Label();

        TextArea logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setPrefRowCount(10);
        logArea.setPromptText("CSV validation and import results appear here.");

        Button chooseButton = new Button("Choose CSV");
        Button importButton = new Button("Import CSV");

        chooseButton.setOnAction(event -> chooseFile(stage, fileLabel, statusLabel, logArea));
        importButton.setOnAction(event -> importCsv(importButton, statusLabel, logArea));

        HBox buttons = new HBox(10, chooseButton, importButton);

        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.getChildren().addAll(
                titleLabel,
                fileLabel,
                buttons,
                statusLabel,
                logArea
        );

        Scene scene = new Scene(root, 520, 380);

        stage.setTitle("Bulk Add Items");
        stage.setScene(scene);
        stage.show();
    }

    private void chooseFile(Stage stage, Label fileLabel, Label statusLabel, TextArea logArea) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose Items CSV");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files", "*.csv"));

        File file = chooser.showOpenDialog(stage);
        if (file == null) {
            return;
        }

        selectedFile = file;
        fileLabel.setText(file.getAbsolutePath());
        statusLabel.setText("");
        logArea.clear();
    }

    private void importCsv(Button importButton, Label statusLabel, TextArea logArea) {
        if (selectedFile == null) {
            showError(statusLabel, "Choose a CSV file first.");
            return;
        }

        List<AddItemRequest> requests;
        try {
            requests = parseCsv(selectedFile.toPath());
        } catch (Exception e) {
            showError(statusLabel, "CSV validation failed.");
            logArea.setText(e.getMessage());
            return;
        }

        importButton.setDisable(true);
        statusLabel.setStyle("-fx-text-fill: black;");
        statusLabel.setText("Importing...");
        logArea.clear();

        Thread importThread = new Thread(() -> {
            int successCount = 0;
            List<String> failures = new ArrayList<>();

            for (int i = 0; i < requests.size(); i++) {
                AddItemRequest request = requests.get(i);
                try {
                    SocketResponse response = client.send("ADD_ITEM", request, ItemDto.class);
                    if ("OK".equalsIgnoreCase(response.getStatus())) {
                        successCount++;
                    } else {
                        failures.add("Row " + (i + 2) + ": " + response.getMessage());
                    }
                } catch (Exception e) {
                    failures.add("Row " + (i + 2) + ": " + e.getMessage());
                }
            }

            int finalSuccessCount = successCount;
            Platform.runLater(() -> {
                importButton.setDisable(false);

                String summary;
                if (failures.isEmpty()) {
                    summary = "Imported " + finalSuccessCount + " items.";
                    statusLabel.setStyle("-fx-text-fill: green;");
                    statusLabel.setText("Import completed successfully.");
                } else {
                    summary = "Imported " + finalSuccessCount + " items. Failed: " + failures.size();
                    statusLabel.setStyle("-fx-text-fill: red;");
                    statusLabel.setText("Import completed with errors.");
                }

                List<String> results = new ArrayList<>();
                results.add(summary);
                results.addAll(failures);
                logArea.setText(String.join(System.lineSeparator(), results));
            });
        });

        importThread.setDaemon(true);
        importThread.start();
    }

    static List<AddItemRequest> parseCsv(Path path) throws IOException {
        List<String> lines = Files.readAllLines(path);
        if (lines.isEmpty()) {
            throw new IllegalArgumentException("CSV file is empty.");
        }

        Map<String, Integer> columns = headerIndexes(parseLine(lines.get(0)));
        requireColumn(columns, "name");
        requireColumn(columns, "brand");
        requireColumn(columns, "description");
        requireColumn(columns, "price");
        requireColumn(columns, "quantity");

        List<AddItemRequest> requests = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isBlank()) {
                continue;
            }

            List<String> values = parseLine(line);
            requests.add(buildRequest(values, columns, i + 1));
        }

        if (requests.isEmpty()) {
            throw new IllegalArgumentException("CSV has no item rows.");
        }

        return requests;
    }

    private static Map<String, Integer> headerIndexes(List<String> headers) {
        Map<String, Integer> columns = new HashMap<>();
        for (int i = 0; i < headers.size(); i++) {
            columns.put(headers.get(i).trim().toLowerCase(), i);
        }
        return columns;
    }

    private static void requireColumn(Map<String, Integer> columns, String column) {
        if (!columns.containsKey(column)) {
            throw new IllegalArgumentException("Missing required column: " + column);
        }
    }

    private static AddItemRequest buildRequest(List<String> values, Map<String, Integer> columns, int rowNumber) {
        String name = getValue(values, columns, "name").trim();
        String brand = getValue(values, columns, "brand").trim();
        String description = getValue(values, columns, "description").trim();
        String priceText = getValue(values, columns, "price").trim();
        String quantityText = getValue(values, columns, "quantity").trim();

        if (name.isEmpty()) {
            throw new IllegalArgumentException("Row " + rowNumber + ": name is required.");
        }

        double price;
        try {
            price = Double.parseDouble(priceText);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Row " + rowNumber + ": price must be a valid number.");
        }

        if (price <= 0) {
            throw new IllegalArgumentException("Row " + rowNumber + ": price must be positive.");
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityText);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Row " + rowNumber + ": quantity must be a valid whole number.");
        }

        if (quantity < 1) {
            throw new IllegalArgumentException("Row " + rowNumber + ": quantity must be at least 1.");
        }

        AddItemRequest request = new AddItemRequest();
        request.setName(name);
        request.setBrand(brand);
        request.setDescription(description);
        request.setPrice(price);
        request.setQuantity(quantity);
        return request;
    }

    private static String getValue(List<String> values, Map<String, Integer> columns, String column) {
        int index = columns.get(column);
        if (index >= values.size()) {
            return "";
        }
        return values.get(index);
    }

    private static List<String> parseLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                values.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }

        values.add(current.toString());
        return values;
    }

    private void showError(Label statusLabel, String message) {
        statusLabel.setStyle("-fx-text-fill: red;");
        statusLabel.setText(message);
    }
}
