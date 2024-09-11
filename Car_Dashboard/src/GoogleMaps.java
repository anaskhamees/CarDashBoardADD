import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class GoogleMaps {

    public GoogleMaps(StackPane centerPane) {
        // Create a WebView to display Google Maps
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();

        // Set the size of the WebView
        webView.setPrefSize(800, 600); // Adjust size as needed

        // Load Google Maps URL
        String googleMapsUrl = "https://www.google.com/maps";
        webEngine.load(googleMapsUrl);

        // Create a container to set the background color
        StackPane container = new StackPane(webView);
        container.setStyle("-fx-background-color: #F0F0F0;"); // Off-white color

        // Add the container to centerPane
        centerPane.getChildren().setAll(container);
    }
}
