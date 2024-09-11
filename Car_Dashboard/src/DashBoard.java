
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.application.Platform;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.animation.TranslateTransition;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;

public class DashBoard extends Application {

    private BorderPane root;
    private VBox centralContent;
    private Label timeLabel;
    private Label dateLabel;
    private Label temperatureValueLabel;
    private Canvas temperatureGaugeCanvas;
    private GraphicsContext temperatureGc;

    private Label milliAmpValueLabel;
    private Canvas milliAmpGaugeCanvas;
    private GraphicsContext milliAmpGc;
    private double freshTemperatureValue=0;
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Car Dashboard");

        HBox topNavBar = createTopNavBar();
        HBox bottomInfoBar = createBottomInfoBar();
        VBox TemperatureGauge = createTemperatureGauge();
        VBox milliAmpGauge = createMilliAmpGauge();
        centralContent = createMediaInfo(); // Initialize with media info

        root = new BorderPane();
        root.setTop(topNavBar);
        root.setBottom(bottomInfoBar);
        root.setLeft(milliAmpGauge);
        root.setRight(TemperatureGauge);
        root.setCenter(centralContent);
        root.setStyle("-fx-background-color: #2C2F33;");

        StackPane imagePane = new StackPane();
        Scene scene = new Scene(imagePane, 760, 380);
        primaryStage.setScene(scene);

        Framework.showInitialImage(primaryStage, root);

        startClock();
        animateTempGauge("temps.txt");
        animateMilliAmpGauge("milliamp.txt");
    }

    private HBox createTopNavBar() {
        HBox topNavBar = new HBox(30);
        topNavBar.setAlignment(Pos.CENTER);
        topNavBar.setStyle("-fx-padding: 10; -fx-background-color: #23272A;");

        String[] navOptions = {"Phone", "Nav", "Media", "Settings"};
        String[] iconPaths = {"images/phone.png", "images/map.png", "images/video.png", "images/settingss.png"};

        for (int i = 0; i < navOptions.length; i++) {
            Button button = new Button();
            button.setTextFill(Color.ORANGE);
            button.setFont(new Font("Arial", 25));
            button.setStyle("-fx-background-color: #23272A; -fx-border-color: #7289DA; -fx-border-radius: 10; -fx-text-fill: #FFFFFF;");

            try {
                // Load and set the icon
                Image icon = new Image(iconPaths[i]);
                ImageView iconView = new ImageView(icon);
                iconView.setFitWidth(30);
                iconView.setFitHeight(30);

                // Add the icon to the button
                button.setGraphic(iconView);
            } catch (Exception e) {
                // Handle any exception (e.g., missing image file)
                System.err.println("Error loading icon for " + navOptions[i] + ": " + e.getMessage());
                showErrorAlert("Error", "Could not load icon for " + navOptions[i]);
            }

            final String option = navOptions[i]; // Final variable for lambda expression

            button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #7289DA; -fx-border-radius: 10; -fx-text-fill: #FFFFFF;"));
            button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #23272A; -fx-border-color: #7289DA; -fx-border-radius: 10; -fx-text-fill: #FFFFFF;"));
            button.setOnAction(e -> handleMenuSelection(option));

            topNavBar.getChildren().add(button);
        }

        return topNavBar;
    }

// Helper method to show an alert
    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private HBox createBottomInfoBar() {
        
        HBox bottomInfoBar = new HBox(30);
        bottomInfoBar.setAlignment(Pos.CENTER);
        bottomInfoBar.setStyle("-fx-padding: 10; -fx-background-color: #23272A;");

    String[] infoItems = {"5G", "33.0 °C"};

        for (String item : infoItems) {
            Label label = new Label(item);
            label.setTextFill(Color.GRAY);
            label.setFont(new Font("Arial", 16));
            bottomInfoBar.getChildren().add(label);
        }

        dateLabel = new Label();
        dateLabel.setTextFill(Color.GRAY);
        dateLabel.setFont(new Font("Arial", 14));
        bottomInfoBar.getChildren().add(dateLabel);

        timeLabel = new Label();
        timeLabel.setTextFill(Color.GRAY);
        timeLabel.setFont(new Font("Arial", 14));
        bottomInfoBar.getChildren().add(timeLabel);

        return bottomInfoBar;
    }
 
    private VBox createTemperatureGauge() {
        VBox gaugeBox = new VBox(10);
        gaugeBox.setAlignment(Pos.CENTER);

        temperatureGaugeCanvas = new Canvas(200, 200);
        temperatureGc = temperatureGaugeCanvas.getGraphicsContext2D();
        Framework.drawTemperatureGaugeArc(temperatureGc, Color.ORANGE, 0);

        temperatureValueLabel = new Label("0");
        temperatureValueLabel.setFont(new Font("Arial", 40));
        temperatureValueLabel.setTextFill(Color.ORANGE);

        Label unitLabel = new Label("°C");
        unitLabel.setFont(new Font("Arial", 25));
        unitLabel.setTextFill(Color.ORANGE);

        HBox valueUnitBox = new HBox(10);
        valueUnitBox.setAlignment(Pos.CENTER);
        valueUnitBox.getChildren().addAll(temperatureValueLabel, unitLabel);

        gaugeBox.getChildren().addAll(temperatureGaugeCanvas, valueUnitBox);
        return gaugeBox;
    }
    
    

    private void animateTempGauge(String filePath) {
    new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                Timer timer = new Timer(true);
                TimerTask task = new TimerTask() {
                    private long lastFileLength = 0;
                    private Double lastReadValue = null;

                    @Override
                    public void run() {
                        try {
                            File file = new File(filePath);
                            if (!file.exists()) {
                                throw new FileNotFoundException("Temperature file not found: " + filePath);
                            }

                            if (file.length() > lastFileLength) {
                                List<Double> temperatureValues = Framework.readTemperatureValuesFromFile(filePath);
                                if (!temperatureValues.isEmpty()) {
                                    double latestValue = temperatureValues.get(temperatureValues.size() - 1);

                                    if (lastReadValue == null || latestValue != lastReadValue) {
                                        lastReadValue = latestValue;
                                        freshTemperatureValue = lastReadValue;

                                        javafx.application.Platform.runLater(() -> {
                                            if (latestValue < 40) {
                                                Framework.drawTemperatureGaugeArc(temperatureGc, Color.ORANGE, latestValue);
                                            } else {
                                                Framework.drawTemperatureGaugeArc(temperatureGc, Color.RED, latestValue);
                                            }
                                            // Update gauge drawing
                                            temperatureValueLabel.setText(String.valueOf(latestValue));
                                        });
                                    }
                                }
                                lastFileLength = file.length();
                            }
                        } catch (FileNotFoundException e) {
                            javafx.application.Platform.runLater(() -> showErrorAlert("File Not Found", "Temperature file not found: " + filePath));
                            this.cancel();
                        } catch (IOException e) {
                            javafx.application.Platform.runLater(() -> showErrorAlert("Error", "An error occurred while reading the temperature values file."));
                        } catch (Exception e) {
                            javafx.application.Platform.runLater(() -> showErrorAlert("Unexpected Error", "An unexpected error occurred: " + e.getMessage()));
                        }
                    }
                };
                timer.scheduleAtFixedRate(task, 0, 200);
            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> showErrorAlert("Error", "An error occurred while setting up the temperature gauge animation."));
            }
        }
    }).start();
}

    
  private VBox createMilliAmpGauge() {
    VBox gaugeBox = new VBox(10);
    gaugeBox.setAlignment(Pos.CENTER);

    milliAmpGaugeCanvas = new Canvas(200, 200); // Ensure the size is appropriate
    milliAmpGc = milliAmpGaugeCanvas.getGraphicsContext2D();
    Framework.drawMilliAmpGaugeArc(milliAmpGc, Color.BLUE, 0); // Initial draw

    milliAmpValueLabel = new Label("0");
    milliAmpValueLabel.setFont(new Font("Arial", 40));
    milliAmpValueLabel.setTextFill(Color.BLUE);

    Label unitLabel = new Label("mA");
    unitLabel.setFont(new Font("Arial", 25));
    unitLabel.setTextFill(Color.BLUE);

    HBox valueUnitBox = new HBox(10);
    valueUnitBox.setAlignment(Pos.CENTER);
    valueUnitBox.getChildren().addAll(milliAmpValueLabel, unitLabel);

    gaugeBox.getChildren().addAll(milliAmpGaugeCanvas, valueUnitBox);
    return gaugeBox;
}

private void animateMilliAmpGauge(String filePath) {
    new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                Timer timer = new Timer(true);
                TimerTask task = new TimerTask() {
                    private long lastFileLength = 0; // Track the last file length
                    private Double lastReadValue = null; // Track the last read value

                    @Override
                    public void run() {
                        try {
                            File file = new File(filePath);
                            if (!file.exists()) {
                                throw new FileNotFoundException("MilliAmp file not found: " + filePath);
                            }

                            if (file.length() > lastFileLength) { // Check if new data has been added
                                List<Double> milliAmpValues = Framework.readMilliAmpValuesFromFile(filePath);
                                if (!milliAmpValues.isEmpty()) {
                                    double latestValue = milliAmpValues.get(milliAmpValues.size() - 1);

                                    // Ensure the value does not exceed 5000 mA
                                    if (latestValue > 5000) {
                                        latestValue = 5000;
                                    }

                                    // Check if the value has changed
                                    if (lastReadValue == null || latestValue != lastReadValue) {
                                        lastReadValue = latestValue;

                                        final double valueToDisplay = latestValue; // Create a final copy for the lambda

                                        javafx.application.Platform.runLater(() -> {
                                            System.out.println("Latest MilliAmp Value: " + valueToDisplay); // Log value

                                            // Determine color based on value ranges
                                            Color color;
                                            if (valueToDisplay < 1000) {
                                                color = Color.GREEN; // 0 to 1 ampere
                                            } else if (valueToDisplay < 3000) {
                                                color = Color.YELLOW; // 1 to 3 ampere
                                            } else {
                                                color = Color.RED; // 3 to 5 ampere (warning color)
                                            }

                                            // Draw the gauge based on the value
                                            Framework.drawMilliAmpGaugeArc(milliAmpGc, color, valueToDisplay);

                                            // Update the value label with mA units
                                            milliAmpValueLabel.setText(String.format("%.0f", valueToDisplay));
                                        });
                                    }
                                }
                                lastFileLength = file.length(); // Update the file length tracker
                            }
                        } catch (FileNotFoundException e) {
                            // Handle file not found error
                            javafx.application.Platform.runLater(() -> showErrorAlert("File Not Found", "MilliAmp file not found: " + filePath));
                            this.cancel(); // Stop the timer task if the file is not found
                        } catch (IOException e) {
                            // Handle IO errors
                            javafx.application.Platform.runLater(() -> showErrorAlert("Error", "An error occurred while reading the milliampere values file."));
                        } catch (Exception e) {
                            // Handle any other unexpected exceptions
                            javafx.application.Platform.runLater(() -> showErrorAlert("Unexpected Error", "An unexpected error occurred: " + e.getMessage()));
                        }
                    }
                };
                timer.scheduleAtFixedRate(task, 0, 200); // Check every 200 ms
            } catch (Exception e) {
                // Handle errors during timer/task creation or execution
                javafx.application.Platform.runLater(() -> showErrorAlert("Error", "An error occurred while setting up the milliampere gauge animation."));
            }
        }
    }).start();
}


    private VBox createMediaInfo() {
    VBox mediaBox = new VBox(20);
    mediaBox.setAlignment(Pos.CENTER);
    mediaBox.setStyle("-fx-background-color: #FFFFFF;"); // Set background color to white

    // Load the image
    Image mediaImage = new Image(getClass().getResourceAsStream("/images/iti.png")); // Update with the correct path
    ImageView mediaImageView = new ImageView(mediaImage);
    mediaImageView.setFitWidth(100);  // Set the width of the image
    mediaImageView.setFitHeight(100); // Set the height of the image
    mediaImageView.setPreserveRatio(true); // Preserve the aspect ratio

    // Add the image and label to the VBox
    mediaBox.getChildren().addAll(mediaImageView);

    return mediaBox;
}


    private void startClock() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Calendar calendar = Calendar.getInstance();
                final String time = String.format("%02d:%02d:%02d",
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        calendar.get(Calendar.SECOND));
                final String date = String.format("%02d/%02d/%04d",
                        calendar.get(Calendar.DAY_OF_MONTH),
                        calendar.get(Calendar.MONTH) + 1,
                        calendar.get(Calendar.YEAR));
                javafx.application.Platform.runLater(() -> {
                    timeLabel.setText(time);
                    dateLabel.setText(date);
                });
            }
        }, 0, 1000); // Update every second
    }

    private void handleMenuSelection(String option) {
        System.out.println("Button clicked: " + option); // Debug message

        // Ensure centralContent is initialized before using it
        if (centralContent == null) {
            System.err.println("centralContent is not initialized");
            return;
        }

        // Clear previous content
        centralContent.getChildren().clear();

        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), centralContent);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), centralContent);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        fadeOut.setOnFinished(e -> {
            try {
                switch (option) {
                    case "Phone":
                        displayPhoneOptions(); // Show Phone options
                        break;

                    case "Nav":
                        displayGoogleMaps();
                        break;

                    case "Media":
                        showMedia();
                        break;

                    case "Settings":
                        displaySettings();
                        break;

                    default:
                        System.err.println("Unhandled menu option: " + option);
                        break;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            fadeIn.play();
        });

        fadeOut.play();
    }

   private void displayPhoneOptions() {
    // Load the image
    Image backgroundImage = new Image(getClass().getResourceAsStream("images/phonewall.png"));

    // Check if the image was loaded successfully
    if (backgroundImage.isError()) {
        // Show an alert if the image could not be loaded
        showErrorAlert("Error", "Could not load background image.");
        return; // Exit the method
    }

    // Create an ImageView for the background image
    ImageView backgroundImageView = new ImageView(backgroundImage);
    backgroundImageView.setPreserveRatio(false); // Disable aspect ratio preservation
    backgroundImageView.setFitWidth(centralContent.getWidth()); // Set to match container width
    backgroundImageView.setFitHeight(centralContent.getHeight()); // Set to match container height

    // Ensure the image resizes when the window changes size
    backgroundImageView.fitWidthProperty().bind(centralContent.widthProperty());
    backgroundImageView.fitHeightProperty().bind(centralContent.heightProperty());

    // Create a VBox to hold all elements
    VBox phoneOptions = new VBox(20);
    phoneOptions.setAlignment(Pos.CENTER);

    // Create the buttons with a blue background and strong white bold text
    Button contactListButton = new Button("Contact List");
    contactListButton.setStyle(
            "-fx-background-color: rgba(64, 115, 158, 0.8); " +  // Blue background with 80% opacity
            "-fx-text-fill: #FFFFFF; " +                        // Strong white text color
            "-fx-font-size: 16px; " +                           // Font size
            "-fx-font-weight: bold; " +                         // Bold text
            "-fx-background-radius: 25; " +                     // Rounded corners
            "-fx-padding: 10 20; " +                            // Padding inside the button
            "-fx-border-width: 0;"                              // No border
    );
    contactListButton.setOnAction(e -> showContactList());

    Button insertNumberButton = new Button("Insert New Number");
    insertNumberButton.setStyle(
            "-fx-background-color: rgba(64, 115, 158, 0.8); " + // Blue background with 80% opacity
            "-fx-text-fill: #FFFFFF; " +                        // Strong white text color
            "-fx-font-size: 16px; " +                           // Font size
            "-fx-font-weight: bold; " +                         // Bold text
            "-fx-background-radius: 25; " +                     // Rounded corners
            "-fx-padding: 10 20; " +                            // Padding inside the button
            "-fx-border-width: 0;"                              // No border
    );
    insertNumberButton.setOnAction(e -> showKeypad());

    // Create a StackPane to layer the background image and content
    StackPane stackPane = new StackPane();
    stackPane.getChildren().add(backgroundImageView);
    stackPane.getChildren().add(phoneOptions);

    // Add the buttons to the VBox
    phoneOptions.getChildren().addAll(contactListButton, insertNumberButton);

    // Add the StackPane to centralContent
    centralContent.getChildren().setAll(stackPane);
}


    private VBox createIconWithLabel(String label, String iconPath) {
        StackPane stackPane = new StackPane();

        // Create a circle for the icon background
        Circle circle = new Circle(25);
        circle.setFill(Color.LIGHTGRAY);  // Default off-white color
        circle.setStroke(Color.TRANSPARENT);  // No border

        // Load the icon image
        ImageView iconView = new ImageView();
        try {
            Image icon = new Image(getClass().getResourceAsStream(iconPath));
            iconView.setImage(icon);
        } catch (Exception e) {
            System.err.println("Error loading icon: " + e.getMessage());
            iconView.setImage(new Image(getClass().getResourceAsStream("/icons/default_icon.png"))); // Default icon
        }
        iconView.setFitWidth(30);
        iconView.setFitHeight(30);

        stackPane.getChildren().addAll(circle, iconView);
        stackPane.setAlignment(Pos.CENTER);

        // Create a label below the circle
        Label labelView = new Label(label);
        labelView.setFont(javafx.scene.text.Font.font("Arial", 10));
        labelView.setTextFill(Color.BLACK);
        labelView.setAlignment(Pos.CENTER);

        VBox vbox = new VBox(5, stackPane, labelView);
        vbox.setAlignment(Pos.CENTER);

        // Boolean flag to track the circle's state
        final boolean[] isActive = {false};

        // Handle mouse events for color toggle
        vbox.setOnMouseClicked(event -> {
            if (isActive[0]) {
                circle.setFill(Color.LIGHTGRAY);  // Set to off-white if currently active
            } else {
                circle.setFill(Color.GRAY);  // Set to gray if currently inactive
            }
            isActive[0] = !isActive[0];  // Toggle the flag
        });

        return vbox;
    }

    private void showContactList() {
        // Clear existing content
        centralContent.getChildren().clear();

        // Create a VBox to hold all elements
        VBox mainVBox = new VBox(10);
        mainVBox.setAlignment(Pos.CENTER);
        mainVBox.setStyle("-fx-background-color: #F0F0F0; -fx-padding: 20;");

        // Create the "Back" button
        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #7289DA; -fx-text-fill: #FFFFFF; -fx-font-size: 16px;");
        backButton.setOnAction(e -> displayPhoneOptions());

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
                // Create a Stage to show the calling dialog
                Stage callStage = new Stage();
                callStage.initModality(Modality.APPLICATION_MODAL);
                callStage.initOwner(centralContent.getScene().getWindow()); // Set owner to block interaction with other windows
                callStage.setTitle("Calling");

                // Create a VBox for the calling dialog content
                VBox contentBox = new VBox(10);
                contentBox.setAlignment(Pos.CENTER);
                contentBox.setPadding(new Insets(10));
                contentBox.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 10; -fx-background-radius: 10;");

                // Add contact image
                ImageView contactImageView = new ImageView(selectedContact.getPhoto());
                contactImageView.setFitWidth(80);
                contactImageView.setFitHeight(80);
                contactImageView.setClip(new Circle(40, 40, 40)); // Circular clip

                // Label for the contact name
                Label contactNameLabel = new Label(selectedContact.getName());
                contactNameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

                // Label for the phone number
                Label phoneNumberLabel = new Label(selectedContact.getPhoneNumber());
                phoneNumberLabel.setFont(Font.font("Arial", 12));

                // Create the TranslateTransition for the "Calling..." text
                Label callingLabel = new Label("Calling...");
                callingLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
                TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(7), callingLabel);
                translateTransition.setFromX(-140);
                translateTransition.setToX(160);
                translateTransition.setCycleCount(TranslateTransition.INDEFINITE); // Keep looping
                translateTransition.play();

                // Create ImageView for the GIF (optional)
                ImageView gifImageView = new ImageView();
                try {
                    gifImageView.setImage(new Image(getClass().getResourceAsStream("/images/calling.gif")));
                    gifImageView.setFitWidth(40);
                    gifImageView.setFitHeight(40);
                } catch (Exception e) {
                    System.err.println("Error: Calling GIF not found.");
                }

                // Create circular icons with labels below
                VBox speakerIcon = createIconWithLabel("Speaker", "/images/speaker.png");
                VBox muteIcon = createIconWithLabel("Mute", "/images/mute.png");
                VBox keyboardIcon = createIconWithLabel("Keyboard", "/images/keyboard.png");

                // Create the "End Call" button
                Button endCallButton = new Button("End Call");
                endCallButton.setStyle(
                        "-fx-background-color: #FF3B30; "
                        + // iPhone-like red for ending a call
                        "-fx-text-fill: white; "
                        + // White text on the red button
                        "-fx-font-size: 14px; "
                        + // Smaller font size for better fit
                        "-fx-background-radius: 20;" // Rounded button
                );

                // Event handler for end call button
                endCallButton.setOnAction(e -> {
                    translateTransition.stop(); // Stop the animation
                    callStage.close(); // Close the stage
                });

                // Create an HBox for control icons
                HBox controlBox = new HBox(15);
                controlBox.setAlignment(Pos.CENTER);
                controlBox.getChildren().addAll(speakerIcon, muteIcon, keyboardIcon);

                // Add all components to the VBox
                contentBox.getChildren().addAll(contactImageView, contactNameLabel, phoneNumberLabel, gifImageView, callingLabel, controlBox, endCallButton);

                // Create and set a scene
                Scene scene = new Scene(contentBox);
                callStage.setScene(scene);

                // Set stage size and show
                callStage.setWidth(250);
                callStage.setHeight(350);
                callStage.show();
            }
        });

        // Add components to the VBox
        contactListBox.getChildren().addAll(contactListLabel, contactListView);

        // Add the Back button and contact list VBox to the main VBox
        mainVBox.getChildren().addAll(backButton, contactListBox);

        // Add the VBox to centralContent
        centralContent.getChildren().add(mainVBox);
    }

    private void showKeypad() {
        GridPane keypad = new GridPane();
        keypad.setHgap(10);
        keypad.setVgap(10);
        keypad.setAlignment(Pos.CENTER);
        keypad.setStyle("-fx-background-color: #1F1F1F;");

        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #7289DA; -fx-text-fill: #FFFFFF; -fx-font-size: 16px;");
        backButton.setOnAction(e -> displayPhoneOptions());

        // Adding number buttons to the keypad
        for (int i = 1; i <= 9; i++) {
            Button numberButton = new Button(String.valueOf(i));
            numberButton.setStyle("-fx-background-color: #7289DA; -fx-text-fill: #FFFFFF; -fx-font-size: 16px;");
            keypad.add(numberButton, (i - 1) % 3, (i - 1) / 3);
        }

        Button zeroButton = new Button("0");
        zeroButton.setStyle("-fx-background-color: #7289DA; -fx-text-fill: #FFFFFF; -fx-font-size: 16px;");
        keypad.add(zeroButton, 1, 3);

        keypad.add(backButton, 0, 4, 3, 1);

        centralContent.getChildren().setAll(keypad);
    }

public class DebugThreadFactory implements ThreadFactory {
    private final AtomicInteger threadCount = new AtomicInteger(0);
    private final AtomicInteger activeCount = new AtomicInteger(0);

    @Override
    public Thread newThread(Runnable r) {
        int id = threadCount.incrementAndGet();
        activeCount.incrementAndGet();
        System.out.println("Thread created: Thread-" + id + ", Active Threads: " + activeCount.get());

        return new Thread(() -> {
            try {
                r.run();
            } finally {
                // Decrement active count when thread finishes execution
                activeCount.decrementAndGet();
                System.out.println("Thread terminated: Thread-" + id + ", Active Threads: " + activeCount.get());
            }
        }, "Thread-" + id);
    }

    public int getActiveCount() {
        return activeCount.get();
    }
}


private boolean isGifLoading = false; // Flag to track if the GIF is currently being loaded
private final ThreadFactory threadFactory = new DebugThreadFactory(); // Custom ThreadFactory

private void showMedia() {
    if (isGifLoading) {
        System.out.println("GIF is already loading. Please wait.");
        return; // Exit if a GIF is already loading
    }

    // Set the flag to true to indicate that GIF loading has started
    isGifLoading = true;

    // Create a Task to run the GIF loading in the background
    Task<Void> loadGifTask = new Task<Void>() {
        @Override
        protected Void call() throws Exception {
            try (InputStream gifStream = getClass().getResourceAsStream("/luxoft.gif")) {
                if (gifStream == null) {
                    // Call showErrorAlert on the JavaFX Application Thread
                    Platform.runLater(() -> showErrorAlert("GIF Load Error", "luxoft.gif file not found or failed to load."));
                    return null;
                }

                // Load the GIF image
                Image gifImage = new Image(gifStream);

                // Update UI components on the JavaFX Application Thread
                Platform.runLater(() -> {
                    try {
                        // Create ImageView to display the loaded GIF
                        ImageView logoView = new ImageView(gifImage);

                        // Set the size of the ImageView to fill the parent container
                        logoView.setFitWidth(centralContent.getWidth());
                        logoView.setFitHeight(centralContent.getHeight());
                        logoView.setPreserveRatio(false); // Disable aspect ratio preservation

                        // Ensure the GIF image resizes with the centralContent
                        logoView.fitWidthProperty().bind(centralContent.widthProperty());
                        logoView.fitHeightProperty().bind(centralContent.heightProperty());

                        // Create a StackPane to layer the GIF and ensure it fills the background
                        StackPane logoPane = new StackPane(logoView);
                        logoPane.setStyle("-fx-background-color: #F5F5F5;"); // Set background color

                        // Clear existing content and add the new logoPane to centralContent
                        centralContent.getChildren().clear();
                        centralContent.getChildren().add(logoPane);

                        System.out.println("GIF displayed and running successfully.");
                    } catch (Exception e) {
                        System.err.println("Error displaying GIF: " + e.getMessage());
                        e.printStackTrace();
                        // Call showErrorAlert on the JavaFX Application Thread
                        Platform.runLater(() -> showErrorAlert("GIF Display Error", "Error displaying GIF: " + e.getMessage()));
                    } finally {
                        // Reset the flag after the GIF has been displayed or failed
                        isGifLoading = false;
                    }
                });
            } catch (IOException e) {
                System.err.println("Error loading GIF: " + e.getMessage());
                e.printStackTrace();
                // Call showErrorAlert on the JavaFX Application Thread
                Platform.runLater(() -> showErrorAlert("GIF Load Error", "Error loading GIF: " + e.getMessage()));
            }
            return null;
        }
    };

    // Create and start the background thread using the custom ThreadFactory
    ExecutorService executorService = Executors.newSingleThreadExecutor(threadFactory);
    executorService.execute(loadGifTask);

    // Shutdown the executor service once the task is done
    loadGifTask.setOnSucceeded(event -> executorService.shutdown());
    loadGifTask.setOnFailed(event -> executorService.shutdown());
}
    
    private void displayGoogleMaps() {
        // Clear existing content
        centralContent.getChildren().clear();

        // Create a WebView to display Google Maps
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();

        // Set the size of the WebView
        webView.setPrefSize(800, 600); // Adjust size as needed

        // Load Google Maps URL
        String googleMapsUrl = "https://www.google.com/maps?hl=en";
        webEngine.load(googleMapsUrl);

        // Create a container to set the background color
        StackPane container = new StackPane(webView);
        container.setStyle("-fx-background-color: #F0F0F0;"); // Off-white color

        // Add the container to centralContent
        centralContent.getChildren().add(container);
    }

private void displaySettings() {
    try {
        // Ensure centralContent is initialized
        if (centralContent == null) {
            throw new IllegalStateException("centralContent is not initialized");
        }

        // Clear previous content
        centralContent.getChildren().clear();

        // Create a VBox to hold the settings content
        VBox settingsBox = new VBox(20);
        settingsBox.setAlignment(Pos.CENTER);
        settingsBox.setStyle("-fx-background-color: #F0F0F0; -fx-padding: 20;");

        // Create and add the settings label
        Label settingsLabel = new Label("Settings Menu");
        settingsLabel.setFont(new Font("Arial", 24));
        settingsLabel.setTextFill(Color.BLACK);

        // Create an HBox to arrange buttons horizontally
        HBox buttonBox = new HBox(30);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20));

        // Create WiFi icon button
        Button wifiButton = createIconButton("/images/wifi.png", "WiFi Icon");
        wifiButton.setOnAction(event -> showWiFiSettings());

        // Create Bluetooth icon button
        Button bluetoothButton = createIconButton("/images/blutooth.png", "Bluetooth Icon");
        bluetoothButton.setOnAction(event -> showBluetoothSettings());

        // Add the WiFi and Bluetooth buttons to the HBox
        buttonBox.getChildren().addAll(wifiButton, bluetoothButton);

        // Create ColorPicker for theme color
        Label themeLabel = new Label("Theme Color:");
        ColorPicker colorPicker = new ColorPicker();
        colorPicker.setValue(Color.WHITE); // Default color
        colorPicker.setOnAction(event -> changeThemeColor(colorPicker.getValue()));

        // Add the components to the settingsBox
        settingsBox.getChildren().addAll(settingsLabel, buttonBox, themeLabel, colorPicker);

        // Add the VBox to centralContent
        centralContent.getChildren().add(settingsBox);

    } catch (IllegalStateException e) {
        // Handle case when centralContent is not initialized
        javafx.application.Platform.runLater(() -> showErrorAlert("Error", "centralContent is not initialized: " + e.getMessage()));
    } catch (Exception e) {
        // Handle any other unexpected exceptions
        javafx.application.Platform.runLater(() -> showErrorAlert("Unexpected Error", "An error occurred while displaying settings: " + e.getMessage()));
    }
}

// Helper method to create an ImageView wrapped in a Button and handle missing images
private Button createIconButton(String imagePath, String imageName) {
    try {
        Image image = new Image(imagePath);
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(50);
        imageView.setFitHeight(50);

        // Create a button and set the ImageView as its graphic
        Button button = new Button();
        button.setGraphic(imageView);

        return button;
    } catch (IllegalArgumentException e) {
        // Handle image not found and show error alert
        javafx.application.Platform.runLater(() -> showErrorAlert("Image Not Found", imageName + " not found at " + imagePath));
        return new Button(); // Return an empty button to avoid breaking the layout
    }
}

private void showWiFiSettings() {
    // Display a GIF and text when WiFi settings are clicked
    try {
        // Clear the current content
        centralContent.getChildren().clear();

        // Load the WiFi GIF
        ImageView wifiGif = new ImageView(new Image("/images/wifisearch.gif"));
        wifiGif.setFitWidth(200);
        wifiGif.setFitHeight(200);

        // Create a Label for the text
        Label wifiLabel = new Label("Searching for network...");
        wifiLabel.setFont(new Font("Arial", 18));
        wifiLabel.setTextFill(Color.BLACK);

        // Create a VBox to hold the GIF and label
        VBox wifiBox = new VBox(10);
        wifiBox.setAlignment(Pos.CENTER);
        wifiBox.setStyle("-fx-background-color: white; -fx-padding: 20;"); // Set background to white
        wifiBox.getChildren().addAll(wifiGif, wifiLabel);

        // Add the VBox to centralContent
        centralContent.getChildren().add(wifiBox);

    } catch (IllegalArgumentException e) {
        // Handle the case where the GIF is not found
        javafx.application.Platform.runLater(() -> showErrorAlert("GIF Not Found", "WiFi GIF not found at /images/wifisearch.gif"));
    }
}

private void showBluetoothSettings() {
    // Display a GIF and text when Bluetooth settings are clicked
    try {
        // Clear the current content
        centralContent.getChildren().clear();

        // Load the Bluetooth GIF
        ImageView bluetoothGif = new ImageView(new Image("/images/blutoothgif.gif"));
        bluetoothGif.setFitWidth(200);
        bluetoothGif.setFitHeight(200);

        // Create a Label for the text
        Label bluetoothLabel = new Label("Connect to Bluetooth device...");
        bluetoothLabel.setFont(new Font("Arial", 18));
        bluetoothLabel.setTextFill(Color.BLACK);

        // Create a VBox to hold the GIF and label
        VBox bluetoothBox = new VBox(10);
        bluetoothBox.setAlignment(Pos.CENTER);
        bluetoothBox.setStyle("-fx-background-color: white; -fx-padding: 20;"); // Set background to white
        bluetoothBox.getChildren().addAll(bluetoothGif, bluetoothLabel);

        // Add the VBox to centralContent
        centralContent.getChildren().add(bluetoothBox);

    } catch (IllegalArgumentException e) {
        // Handle the case where the GIF is not found
        javafx.application.Platform.runLater(() -> showErrorAlert("GIF Not Found", "Bluetooth GIF not found at /images/bluetoothgif.gif"));
    }
}


private void changeThemeColor(Color color) {
    // Change the background color of the centralContent or the main window
    centralContent.setStyle("-fx-background-color: " + toRgbaString(color) + ";");

    // Optionally, change the background of the settingsBox or other UI elements
    VBox settingsBox = (VBox) centralContent.getChildren().get(0);  // Assuming it's the first child
    settingsBox.setStyle("-fx-background-color: " + toRgbaString(color) + ";");
}

// Helper method to convert Color to a CSS-friendly RGBA string (with transparency support)
private String toRgbaString(Color color) {
    int red = (int) (color.getRed() * 255);
    int green = (int) (color.getGreen() * 255);
    int blue = (int) (color.getBlue() * 255);
    double alpha = color.getOpacity(); // Use the opacity (alpha) channel for transparency

    return "rgba(" + red + "," + green + "," + blue + "," + alpha + ")";
}


    public static void main(String[] args) {
        launch(args);
    }
}
