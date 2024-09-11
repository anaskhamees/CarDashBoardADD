import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class Keypad {

    public Keypad(StackPane centerPane) {
        // Create a GridPane for the keypad layout
        GridPane keypad = new GridPane();
        keypad.setHgap(10);
        keypad.setVgap(10);
        keypad.setAlignment(Pos.CENTER);
        keypad.setStyle("-fx-background-color: #1F1F1F;");

        // Create and style the "Back" button
        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #7289DA; -fx-text-fill: #FFFFFF; -fx-font-size: 16px;");
        backButton.setOnAction(e -> displayPhoneOptions(centerPane));

        // Adding number buttons to the keypad
        for (int i = 1; i <= 9; i++) {
            Button numberButton = new Button(String.valueOf(i));
            numberButton.setStyle("-fx-background-color: #7289DA; -fx-text-fill: #FFFFFF; -fx-font-size: 16px;");
            keypad.add(numberButton, (i - 1) % 3, (i - 1) / 3);
        }

        // Create and style the "0" button
        Button zeroButton = new Button("0");
        zeroButton.setStyle("-fx-background-color: #7289DA; -fx-text-fill: #FFFFFF; -fx-font-size: 16px;");
        keypad.add(zeroButton, 1, 3);

        // Add the "Back" button to the GridPane
        keypad.add(backButton, 0, 4, 3, 1);

        // Add the GridPane to the centerPane
        centerPane.getChildren().setAll(keypad);
    }

    // Method to handle displaying phone options
    private void displayPhoneOptions(StackPane centerPane) {
        new PhoneMenu(centerPane); 
    }
}
