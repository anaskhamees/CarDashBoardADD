
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class Dashboard extends Application {

    private BorderPane rootPane;
    private Canvas temperatureGaugeCanvas;
    private GraphicsContext temperatureGc;
    private Label temperatureValueLabel;
    private Label milliAmpValueLabel;
    private Canvas milliAmpGaugeCanvas;
    private GraphicsContext milliAmpGc;
    private Label dateLabel;
    private Label timeLabel;
    private StackPane centerPane; // Center pane for navigation content

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Car Dashboard");
        rootPane = new BorderPane();

        VBox temperatureGauge = createTemperatureGauge();
        HBox bottomInfoBar = createBottomInfoBar();
//        VBox rpmGauge = Framework.createGauge("2.8", "mA", Color.CYAN);
        centerPane = createMediaInfo(); // Initialize centerPane
        VBox milliAmpGauge = createMilliAmpGauge();

        rootPane.setLeft(milliAmpGauge);
        rootPane.setRight(temperatureGauge);
        rootPane.setBottom(bottomInfoBar);
        rootPane.setCenter(centerPane); // Set the center pane

        // Create and add TopNavBar
        TopNavBar topNavBar = new TopNavBar(this);
        rootPane.setTop(topNavBar.getNavBar());

        Scene scene = new Scene(rootPane, 760, 380);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm()); // Apply CSS

        primaryStage.setScene(scene);
        primaryStage.show();
        Framework.showInitialImage(primaryStage, rootPane);

        startClock();
        ///home/root/workspace/final_work/pot.txt
        animateTempGauge("pot.txt");
        animateMilliAmpGauge("cur.txt");
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

    private HBox createBottomInfoBar() {
        HBox bottomInfoBar = new HBox(30);
        bottomInfoBar.setAlignment(Pos.CENTER);
        bottomInfoBar.setStyle("-fx-padding: 10; -fx-background-color: #23272A;");

        String[] infoItems = {"5G", "28°C"};
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
                Platform.runLater(() -> {
                    timeLabel.setText(time);
                    dateLabel.setText(date);
                });
            }
        }, 0, 1000); // Update every second
    }

    private void animateTempGauge(String filePath) {
        new Thread(() -> {
            try {
                Timer timer = new Timer(true);
                TimerTask task = new TimerTask() {
                    private long lastFileLength = 0;
                    private Integer lastReadValue = null;

                    @Override
                    public void run() {
                        try {
                            File file = new File(filePath);
                            if (!file.exists()) {
                                throw new FileNotFoundException("Temperature file not found: " + filePath);
                            }

                            if (file.length() > lastFileLength) {
                                List<Integer> temperatureValues = Framework.readTemperatureValuesFromFile(filePath);
                                if (!temperatureValues.isEmpty()) {
                                    int latestValue = temperatureValues.get(temperatureValues.size() - 1);

                                    if (lastReadValue == null || latestValue != lastReadValue) {
                                        lastReadValue = latestValue;

                                        Platform.runLater(() -> {
                                            if (latestValue < 40) {
                                                Framework.drawTemperatureGaugeArc(temperatureGc, Color.ORANGE, latestValue);
                                            } else {
                                                Framework.drawTemperatureGaugeArc(temperatureGc, Color.RED, latestValue);

                                                // Show alert dialog if temperature exceeds 40
                                                showTemperatureAlert(latestValue);
                                            }
                                            // Update gauge drawing
                                            temperatureValueLabel.setText(String.valueOf(latestValue));
                                        });
                                    }
                                }
                                lastFileLength = file.length();
                            }
                        } catch (FileNotFoundException e) {
                            Platform.runLater(() -> showErrorAlert("File Not Found", "Temperature file not found: " + filePath));
                            this.cancel();
                        } catch (IOException e) {
                            Platform.runLater(() -> showErrorAlert("Error", "An error occurred while reading the temperature values file."));
                        } catch (Exception e) {
                            Platform.runLater(() -> showErrorAlert("Unexpected Error", "An unexpected error occurred: " + e.getMessage()));
                        }
                    }
                };
                timer.scheduleAtFixedRate(task, 0, 200);
            } catch (Exception e) {
                Platform.runLater(() -> showErrorAlert("Error", "An error occurred while setting up the temperature gauge animation."));
            }
        }).start();
    }

    private void showTemperatureAlert(int temperature) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("High Temperature Warning");

        // Create a Text node for the header and set its color to red
        Text headerText = new Text("Temperature Alert");
        headerText.setFill(Color.RED);
        headerText.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        // Create a Label for the content with black text
        Label contentLabel = new Label("The temperature has exceeded the safe limit! Current temperature: " + temperature + "°C");

        // Use a VBox to combine the header and content
        VBox vBox = new VBox(headerText, contentLabel);
        vBox.setSpacing(10);

        // Set the VBox as the content of the dialog
        alert.getDialogPane().setContent(vBox);

        alert.showAndWait();
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

    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private StackPane createMediaInfo() {
        StackPane mediaPane = new StackPane();
        mediaPane.setStyle("-fx-background-color: #FFFFFF;"); // Set background color to white
        mediaPane.setAlignment(Pos.CENTER);

        // Load the image
        Image mediaImage = new Image(getClass().getResourceAsStream("images/iti.png")); // Update with the correct path
        ImageView mediaImageView = new ImageView(mediaImage);
        mediaImageView.setFitWidth(100);  // Set the width of the image
        mediaImageView.setFitHeight(100); // Set the height of the image
        mediaImageView.setPreserveRatio(true); // Preserve the aspect ratio

        // Add the image to the StackPane
        mediaPane.getChildren().add(mediaImageView);

        return mediaPane;
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

    // Method to get the root pane
    public BorderPane getRootPane() {
        return rootPane;
    }

    // Method to get the center pane
    public StackPane getCenterPane() {
        return centerPane;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
