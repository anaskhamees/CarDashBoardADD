import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class Settings {

    private StackPane centerPane;

    public Settings(StackPane centerPane) {
        this.centerPane = centerPane;

        // Load the background image
        Image backgroundImage = null;
        try {
            backgroundImage = new Image(getClass().getResourceAsStream("images/set.png"));
        } catch (Exception e) {
            System.err.println("Error loading background image: " + e.getMessage());
        }

        if (backgroundImage != null) {
            ImageView backgroundImageView = new ImageView(backgroundImage);
            backgroundImageView.setFitWidth(centerPane.getWidth());
            backgroundImageView.setFitHeight(centerPane.getHeight());
            backgroundImageView.setPreserveRatio(true);

            VBox settingsBox = new VBox(20);
            settingsBox.setAlignment(Pos.CENTER);
            settingsBox.setStyle("-fx-background-color: rgba(240, 240, 240, 0.8); -fx-padding: 20;");

            Label settingsLabel = new Label("Settings Menu");
            settingsLabel.setFont(new Font("Arial", 24));
            settingsLabel.setTextFill(Color.BLACK);

            HBox buttonBox = new HBox(30);
            buttonBox.setAlignment(Pos.CENTER);
            buttonBox.setPadding(new Insets(20));

            Button wifiButton = createIconButton("images/wifi.png", "WiFi Icon");
            wifiButton.setOnAction(event -> showWiFiSettings());

            Button bluetoothButton = createIconButton("images/blutooth.png", "Bluetooth Icon");
            bluetoothButton.setOnAction(event -> showBluetoothSettings());

            buttonBox.getChildren().addAll(wifiButton, bluetoothButton);

            Label themeLabel = new Label("Theme Color:");
            ColorPicker colorPicker = new ColorPicker();
            colorPicker.setValue(Color.WHITE);
            colorPicker.setOnAction(event -> changeThemeColor(colorPicker.getValue()));

            settingsBox.getChildren().addAll(settingsLabel, buttonBox, themeLabel, colorPicker);

            centerPane.getChildren().clear();
            centerPane.getChildren().addAll(backgroundImageView, settingsBox);
        } else {
            System.err.println("Background image not found.");
        }
    }

    private Button createIconButton(String imagePath, String imageName) {
        try {
            Image image = new Image(imagePath);
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(50);
            imageView.setFitHeight(50);

            Button button = new Button();
            button.setGraphic(imageView);
            button.setStyle("-fx-background-color: transparent;"); // Set background to transparent

            return button;
        } catch (IllegalArgumentException e) {
            javafx.application.Platform.runLater(() -> showErrorAlert("Image Not Found", imageName + " not found at " + imagePath));
            return new Button();
        }
    }

    private void showWiFiSettings() {
        centerPane.getChildren().clear();

        ImageView wifiGif = new ImageView(new Image(getClass().getResourceAsStream("/images/wifisearch.gif")));
        wifiGif.setFitWidth(150); // Reduced size of the GIF
        wifiGif.setFitHeight(150);

        Label wifiLabel = new Label("Searching for network...");
        wifiLabel.setFont(new Font("Arial", 18));
        wifiLabel.setTextFill(Color.BLACK);

        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #7289DA; -fx-text-fill: white; -fx-font-size: 14px;");
        backButton.setPrefSize(80, 30); // Reduced size for the button
        backButton.setOnAction(e -> showSettingsMenu());

        VBox contentBox = new VBox(20);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setStyle("-fx-background-color: white; -fx-padding: 20;");
        contentBox.getChildren().addAll(wifiGif, wifiLabel, backButton);

        centerPane.getChildren().add(contentBox);
    }

    private void showBluetoothSettings() {
        centerPane.getChildren().clear();

        ImageView bluetoothGif = new ImageView(new Image(getClass().getResourceAsStream("/images/blutoothgif.gif")));
        bluetoothGif.setFitWidth(150); // Reduced size of the GIF
        bluetoothGif.setFitHeight(150);

        Label bluetoothLabel = new Label("Connect to Bluetooth device...");
        bluetoothLabel.setFont(new Font("Arial", 18));
        bluetoothLabel.setTextFill(Color.BLACK);

        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #7289DA; -fx-text-fill: white; -fx-font-size: 14px;");
        backButton.setPrefSize(80, 30); // Reduced size for the button
        backButton.setOnAction(e -> showSettingsMenu());

        VBox contentBox = new VBox(20);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setStyle("-fx-background-color: white; -fx-padding: 20;");
        contentBox.getChildren().addAll(bluetoothGif, bluetoothLabel, backButton);

        centerPane.getChildren().add(contentBox);
    }

    private void changeThemeColor(Color color) {
        centerPane.setStyle("-fx-background-color: " + toRgbaString(color) + ";");

        VBox settingsBox = (VBox) centerPane.getChildren().get(1);
        settingsBox.setStyle("-fx-background-color: " + toRgbaString(color) + ";");
    }

    private String toRgbaString(Color color) {
        int red = (int) (color.getRed() * 255);
        int green = (int) (color.getGreen() * 255);
        int blue = (int) (color.getBlue() * 255);
        double alpha = color.getOpacity();
        return "rgba(" + red + "," + green + "," + blue + "," + alpha + ")";
    }

    private void showErrorAlert(String title, String message) {
        System.err.println(title + ": " + message);
    }

    private void showSettingsMenu() {
        new Settings(centerPane); // Navigate back to settings menu
    }
}
