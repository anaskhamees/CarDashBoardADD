import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.geometry.Pos;

public class BottomInfoBar {

    private HBox infoBar;
    private Label dateLabel;
    private Label timeLabel;

    public BottomInfoBar() {
        createInfoBar();
    }

    private void createInfoBar() {
        infoBar = new HBox(30);
        infoBar.setAlignment(Pos.CENTER);
        infoBar.setStyle("-fx-padding: 10; -fx-background-color: #23272A;");

        String[] infoItems = {"5G", "28°C"};
        for (String item : infoItems) {
            Label label = new Label(item);
            label.setTextFill(Color.GRAY);
            label.setFont(new Font("Arial", 16));
            infoBar.getChildren().add(label);
        }

        dateLabel = new Label();
        dateLabel.setTextFill(Color.GRAY);
        dateLabel.setFont(new Font("Arial", 14));
        infoBar.getChildren().add(dateLabel);

        timeLabel = new Label();
        timeLabel.setTextFill(Color.GRAY);
        timeLabel.setFont(new Font("Arial", 14));
        infoBar.getChildren().add(timeLabel);
    }

    public HBox getInfoBar() {
        return infoBar;
    }

    public void updateDate(String date) {
        dateLabel.setText(date);
    }

    public void updateTime(String time) {
        timeLabel.setText(time);
    }
}
