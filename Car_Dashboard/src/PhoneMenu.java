
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.animation.FadeTransition;
import javafx.geometry.Insets;

public class PhoneMenu {

    private StackPane centerPane; // Ensure this is a member variable

    public PhoneMenu(StackPane centerPane) {
        this.centerPane = centerPane; // Initialize member variable

        // Clear any existing content in the centerPane
        centerPane.getChildren().clear();

        // Load the background image
        Image backgroundImage = new Image(getClass().getResourceAsStream("images/phonewall.png"));

        // Check if the image was loaded successfully
        if (backgroundImage.isError()) {
            // Show an error message and exit
            showErrorAlert("Error", "Could not load background image.");
            return;
        }

        // Create an ImageView for the background image
        ImageView backgroundImageView = new ImageView(backgroundImage);
        backgroundImageView.setPreserveRatio(false); // Disable preserving the aspect ratio
        backgroundImageView.fitWidthProperty().bind(centerPane.widthProperty());  // Bind width to centerPane's width
        backgroundImageView.fitHeightProperty().bind(centerPane.heightProperty());  // Bind height to centerPane's height

        // Create the VBox to hold the buttons and move it to the middle right
        VBox phoneOptions = new VBox(20);
        phoneOptions.setAlignment(Pos.CENTER_RIGHT); // Align to the right side
        phoneOptions.setPadding(new Insets(0, 30, 0, 0)); // Add padding to move the buttons away from the edge

        // Create the Contact List button with styles
        Button contactListButton = new Button("Contact List");
        contactListButton.setStyle(
                "-fx-background-color: rgba(64, 115, 158, 0.8); "
                + "-fx-text-fill: #FFFFFF; "
                + "-fx-font-size: 16px; "
                + "-fx-font-weight: bold; "
                + "-fx-background-radius: 25; "
                + "-fx-padding: 10 20; "
                + "-fx-border-width: 0;"
        );
        applyButtonAnimation(contactListButton);
        contactListButton.setOnAction(e -> showContactList());

        // Create the Insert New Number button with styles
        Button insertNumberButton = new Button("New Number");
        insertNumberButton.setStyle(
                "-fx-background-color: rgba(64, 115, 158, 0.8); "
                + "-fx-text-fill: #FFFFFF; "
                + "-fx-font-size: 16px; "
                + "-fx-font-weight: bold; "
                + "-fx-background-radius: 25; "
                + "-fx-padding: 10 20; "
                + "-fx-border-width: 0;"
        );
        applyButtonAnimation(insertNumberButton);
        insertNumberButton.setOnAction(e -> showKeypad());

        // Add buttons to the VBox
        phoneOptions.getChildren().addAll(contactListButton, insertNumberButton);

        // Create a StackPane to layer the background image and content
        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(backgroundImageView, phoneOptions);

        // Adjust the alignment of the VBox to position it in the middle right
        StackPane.setAlignment(phoneOptions, Pos.CENTER_RIGHT);

        // Set the StackPane as the center content
        centerPane.getChildren().setAll(stackPane);
    }

    private void showContactList() {
        new ContactList(centerPane); // Instantiate and display the ContactList
    }

    private void showKeypad() {
        new Keypad(centerPane); // Instantiate and display the Keypad
    }

    private void showErrorAlert(String title, String message) {
        // Implement error alert display
    }

    private void applyButtonAnimation(Button button) {
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(300), button);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.7);
        fadeTransition.setCycleCount(FadeTransition.INDEFINITE);
        fadeTransition.setAutoReverse(true);
        button.setOnMouseEntered(e -> fadeTransition.play());
        button.setOnMouseExited(e -> fadeTransition.stop());
    }
}
