import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;

public class TopNavBar {

    private HBox navBar;
    private Dashboard dashboard;
    private MediaContent mediaContent; // Reference to MediaContent

    public TopNavBar(Dashboard dashboard) {
        this.dashboard = dashboard;
        createNavBar();
    }

    private void createNavBar() {
        navBar = new HBox(30);
        navBar.setAlignment(Pos.CENTER);
        navBar.setStyle("-fx-background-color: #333; -fx-padding: 10;");

        Button phoneButton = createNavButton("Phone", "images/phone.png");
        Button navButton = createNavButton("Nav", "images/map.png");
        Button mediaButton = createNavButton("Media", "images/video.png");
        Button settingsButton = createNavButton("Settings", "images/details.png");

        phoneButton.setOnAction(e -> handleNavigation("Phone"));
        navButton.setOnAction(e -> handleNavigation("Nav"));
        mediaButton.setOnAction(e -> handleNavigation("Media"));
        settingsButton.setOnAction(e -> handleNavigation("Settings"));

        navBar.getChildren().addAll(phoneButton, navButton, mediaButton, settingsButton);
    }

    private Button createNavButton(String text, String iconPath) {
        Button button = new Button(text);
        ImageView icon = new ImageView(new Image(getClass().getResourceAsStream(iconPath)));
        icon.setFitWidth(30);
        icon.setPreserveRatio(true);
        button.setGraphic(icon);
        button.setStyle(
            "-fx-background-color: #23272A; " +
            "-fx-border-color: #7289DA; " +
            "-fx-border-radius: 10; " +
            "-fx-text-fill: #FFFFFF; " +
            "-fx-font-size: 14px;" // Added font size for consistency
        );
        return button;
    }

    private void handleNavigation(String section) {
        // Stop the existing media content if it's running
        if (mediaContent != null) {
            mediaContent.stopLoading();
        }

        dashboard.getCenterPane().getChildren().clear();
        switch (section) {
            case "Phone":
                new PhoneMenu(dashboard.getCenterPane());
                break;
            case "Nav":
                new GoogleMaps(dashboard.getCenterPane()); // Ensure GoogleMaps class is properly implemented
                break;
            case "Media":
                mediaContent = new MediaContent(dashboard.getCenterPane()); // Store the MediaContent instance
                break;
            case "Settings":
                new Settings(dashboard.getCenterPane());
                break;
        }
    }

    public HBox getNavBar() {
        return navBar;
    }
}
