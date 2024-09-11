
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.paint.Color;

public class ContactList {

    public ContactList(StackPane centerPane) {
        // Clear existing content
        centerPane.getChildren().clear();

        // Create a VBox to hold all elements
        VBox mainVBox = new VBox(10);
        mainVBox.setAlignment(Pos.CENTER);
        mainVBox.setStyle("-fx-background-color: #F0F0F0; -fx-padding: 20;");

        // Create the "Back" button
        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #7289DA; -fx-text-fill: #FFFFFF; -fx-font-size: 16px;");
        backButton.setOnAction(e -> new PhoneMenu(centerPane));

        // Create a VBox to hold the contact list
        VBox contactListBox = new VBox(10);
        contactListBox.setAlignment(Pos.CENTER);

        // Create a Label for the contact list
        Label contactListLabel = new Label("Contact List");
        contactListLabel.setFont(new Font("Arial", 24));
        contactListLabel.setTextFill(Color.BLACK);

        // Create a ListView to display contacts
        ListView<Contact> contactListView = new ListView<>();
        contactListView.setPrefWidth(400);

        // Sample contacts with images and phone numbers (replace with actual data)
        try {
            contactListView.getItems().addAll(
                    new Contact("Farouk Ehab", "01068555223", new Image(getClass().getResourceAsStream("contacts/farouk.png"))),
                    new Contact("Anas Khames", "01025805968", new Image(getClass().getResourceAsStream("contacts/anas.png"))),
                    new Contact("Ahmed Elnahas", "01024797847", new Image(getClass().getResourceAsStream("contacts/nahas.png"))),
                    new Contact("Ahmed Abdallah", "01557090844", new Image(getClass().getResourceAsStream("contacts/abdallah.png")))
            );
        } catch (Exception e) {
            System.err.println("Error loading images: " + e.getMessage());
        }

        // Set cell factory for ListView to display contact information
        contactListView.setCellFactory(lv -> new ListCell<Contact>() {
            private final HBox hbox = new HBox(10);
            private final ImageView imageView = new ImageView();
            private final VBox textBox = new VBox();
            private final Label nameLabel = new Label();
            private final Label phoneLabel = new Label();

            {
                imageView.setFitWidth(50);
                imageView.setFitHeight(50);
                textBox.getChildren().addAll(nameLabel, phoneLabel);
                hbox.getChildren().addAll(imageView, textBox);
                hbox.setAlignment(Pos.CENTER_LEFT);
                setGraphic(hbox);
            }

            @Override
            protected void updateItem(Contact contact, boolean empty) {
                super.updateItem(contact, empty);
                if (empty || contact == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    nameLabel.setText(contact.getName());
                    phoneLabel.setText(contact.getPhoneNumber());
                    imageView.setImage(contact.getPhoto());
                    setGraphic(hbox);
                }
            }
        });

        // Add event handler to handle contact selection
        contactListView.setOnMouseClicked(event -> {
            Contact selectedContact = contactListView.getSelectionModel().getSelectedItem();
            if (selectedContact != null) {
                showCallingDialog(selectedContact);
            }
        });

        // Add components to the VBox
        contactListBox.getChildren().addAll(contactListLabel, contactListView);

        // Add the Back button and contact list VBox to the main VBox
        mainVBox.getChildren().addAll(backButton, contactListBox);

        // Add the VBox to centerPane
        centerPane.getChildren().add(mainVBox);
    }

    ContactList(VBox centerPane) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void showCallingDialog(Contact contact) {
        Stage callStage = new Stage();
        callStage.initModality(Modality.APPLICATION_MODAL);
        callStage.initOwner(callStage.getOwner());
        callStage.setTitle("Calling");

        VBox contentBox = new VBox(10);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new Insets(10));
        contentBox.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 10; -fx-background-radius: 10;");

        ImageView contactImageView = new ImageView(contact.getPhoto());
        contactImageView.setFitWidth(80);
        contactImageView.setFitHeight(80);
        contactImageView.setClip(new Circle(40, 40, 40));

        Label contactNameLabel = new Label(contact.getName());
        contactNameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        Label phoneNumberLabel = new Label(contact.getPhoneNumber());
        phoneNumberLabel.setFont(Font.font("Arial", 12));

        Label callingLabel = new Label("Calling...");
        callingLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(7), callingLabel);
        translateTransition.setFromX(-140);
        translateTransition.setToX(160);
        translateTransition.setCycleCount(TranslateTransition.INDEFINITE);
        translateTransition.play();

        ImageView gifImageView = new ImageView();
        try {
            gifImageView.setImage(new Image(getClass().getResourceAsStream("/images/calling.gif")));
            gifImageView.setFitWidth(40);
            gifImageView.setFitHeight(40);
        } catch (Exception e) {
            System.err.println("Error: Calling GIF not found.");
        }

        VBox speakerIcon = createIconWithLabel("Speaker", "/images/speaker.png");
        VBox muteIcon = createIconWithLabel("Mute", "/images/mute.png");
        VBox keyboardIcon = createIconWithLabel("Keyboard", "/images/keyboard.png");

        Button endCallButton = new Button("End Call");
        endCallButton.setStyle(
                "-fx-background-color: #FF3B30; "
                + "-fx-text-fill: white; "
                + "-fx-font-size: 14px; "
                + "-fx-background-radius: 20;"
        );

        endCallButton.setOnAction(e -> {
            translateTransition.stop();
            callStage.close();
        });

        HBox controlBox = new HBox(15);
        controlBox.setAlignment(Pos.CENTER);
        controlBox.getChildren().addAll(speakerIcon, muteIcon, keyboardIcon);

        contentBox.getChildren().addAll(contactImageView, contactNameLabel, phoneNumberLabel, gifImageView, callingLabel, controlBox, endCallButton);

        Scene scene = new Scene(contentBox);

        callStage.setScene(scene);
        callStage.setWidth(250);
        callStage.setHeight(350);
        callStage.show();
    }

    private VBox createIconWithLabel(String label, String imagePath) {
        VBox vbox = new VBox();
        ImageView icon = new ImageView(new Image(getClass().getResourceAsStream(imagePath)));
        icon.setFitWidth(30);
        icon.setFitHeight(30);
        Label iconLabel = new Label(label);
        vbox.getChildren().addAll(icon, iconLabel);
        return vbox;
    }
}
