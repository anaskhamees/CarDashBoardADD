// Framework.java

import javafx.animation.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.util.Duration;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.*;
import java.util.*;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Framework {

    private static long lastFilePointer = 0;

public static void showInitialImage(Stage primaryStage, BorderPane root) {
    Image logoImage = new Image(Framework.class.getResourceAsStream("/IVI.gif"));
    if (logoImage.isError()) {
        System.out.println("Error loading the image.");
        return;
    }

    ImageView logoView = new ImageView(logoImage);
    logoView.setOpacity(0.0);

    StackPane imagePane = new StackPane(logoView);
    imagePane.setStyle("-fx-background-color: #0D0D1A;"); // Set background color to a dark blue shade

    imagePane.setPrefSize(760, 380);

    primaryStage.getScene().setRoot(imagePane);
    primaryStage.show();

    FadeTransition fadeIn = new FadeTransition(Duration.millis(500), logoView);
    fadeIn.setFromValue(0.0);
    fadeIn.setToValue(1.0);

    PauseTransition pause = new PauseTransition(Duration.seconds(12));

    FadeTransition fadeOut = new FadeTransition(Duration.millis(500), logoView);
    fadeOut.setFromValue(1.0);
    fadeOut.setToValue(0.0);

    fadeOut.setOnFinished(event -> {
        primaryStage.getScene().setRoot(root);
    });

    SequentialTransition sequence = new SequentialTransition(fadeIn, pause, fadeOut);
    sequence.play();
}

    public static List<Double> readTemperatureValuesFromFile(String filePath) throws IOException {
        List<Double> temperatures = new ArrayList<>();
        try (RandomAccessFile file = new RandomAccessFile(filePath, "r")) {
            file.seek(lastFilePointer); // Start reading from the last position
            String line;
            while ((line = file.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    System.err.println("Skipping empty line.");
                    continue;
                }
                try {
                    temperatures.add(Double.parseDouble(line));
                } catch (NumberFormatException e) {
                    System.err.println("Invalid number format in file: \"" + line + "\"");
                }
            }
            lastFilePointer = file.getFilePointer(); // Update the last position
        }
        return temperatures;
    }

    public static List<Double> readMilliAmpValuesFromFile(String filePath) throws IOException {
    List<Double> milliAmpValues = new ArrayList<>();
    try (RandomAccessFile file = new RandomAccessFile(filePath, "r")) {
        String line;
        while ((line = file.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) {
                System.err.println("Skipping empty line.");
                continue;
            }
            try {
                milliAmpValues.add(Double.parseDouble(line));
            } catch (NumberFormatException e) {
                System.err.println("Invalid number format in file: \"" + line + "\"");
            }
        }
    }
    return milliAmpValues;
}


    public static void drawTemperatureGaugeArc(GraphicsContext gc, Color color, double value) {
        gc.clearRect(0, 0, 200, 200); // Clear previous drawing
        gc.setFill(Color.TRANSPARENT);
        gc.setStroke(color);
        gc.setLineWidth(15);

        double startAngle = 225;
        double extent = (value / 100.0) * -270; // Assuming the maximum value is 100, adjust as necessary
        gc.strokeArc(30, 30, 140, 140, startAngle, extent, ArcType.OPEN);
    }

    
   public static void drawMilliAmpGaugeArc(GraphicsContext gc, Color color, double value) {
    gc.clearRect(0, 0, 200, 200); // Clear previous drawing
    gc.setFill(Color.TRANSPARENT);
    gc.setStroke(color);
    gc.setLineWidth(15);

    double startAngle = 225;
    double maxMilliAmp = 5000.0; // Maximum milliampere value
    double extent = (value / maxMilliAmp) * -270; // Adjust the extent based on the milliampere value

    // Draw the arc
    gc.strokeArc(30, 30, 140, 140, startAngle, extent, ArcType.OPEN);
}




    
    public static void scrollText(Label label, BorderPane root) {
        final Timeline timeline = new Timeline(new KeyFrame(Duration.millis(20), e -> {
            if (label.getLayoutX() < -label.getWidth()) {
                label.setLayoutX(root.getWidth());
            }
            label.setLayoutX(label.getLayoutX() - 1);
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
}