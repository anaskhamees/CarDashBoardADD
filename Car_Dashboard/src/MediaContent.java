import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class MediaContent {

    private boolean isGifLoading = false; // Flag to track if the GIF is currently being loaded
    private Task<Void> loadGifTask; // Task for loading GIF
    private final StackPane centerPane;
    private final ExecutorService executorService;

    public MediaContent(StackPane centerPane) {
        this.centerPane = centerPane;
        this.executorService = Executors.newSingleThreadExecutor(new DebugThreadFactory()); // Use custom ThreadFactory
        showMedia();
    }

    private void showMedia() {
        if (isGifLoading) {
            System.out.println("GIF is already loading. Please wait.");
            return; // Exit if a GIF is already loading
        }

        // Set the flag to true to indicate that GIF loading has started
        isGifLoading = true;

        // Clear previous content
        centerPane.getChildren().clear();

        // Create a Task to run the GIF loading in the background
        loadGifTask = new Task<Void>() {
            @Override
            protected Void call() {
                try (InputStream gifStream = getClass().getResourceAsStream("/luxoft.gif")) {
                    if (gifStream == null) {
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
                            logoView.setFitWidth(centerPane.getWidth());
                            logoView.setFitHeight(centerPane.getHeight());
                            logoView.setPreserveRatio(false); // Disable aspect ratio preservation

                            // Ensure the GIF image resizes with the centerPane
                            logoView.fitWidthProperty().bind(centerPane.widthProperty());
                            logoView.fitHeightProperty().bind(centerPane.heightProperty());

                            // Create a StackPane to layer the GIF and ensure it fills the background
                            StackPane logoPane = new StackPane(logoView);
                            logoPane.setStyle("-fx-background-color: #F5F5F5;"); // Set background color

                            // Add the new logoPane to centerPane
                            centerPane.getChildren().add(logoPane);

                            System.out.println("GIF displayed and running successfully.");
                        } catch (Exception e) {
                            System.err.println("Error displaying GIF: " + e.getMessage());
                            e.printStackTrace();
                            Platform.runLater(() -> showErrorAlert("GIF Display Error", "Error displaying GIF: " + e.getMessage()));
                        } finally {
                            // Reset the flag after the GIF has been displayed or failed
                            isGifLoading = false;
                        }
                    });
                } catch (IOException e) {
                    System.err.println("Error loading GIF: " + e.getMessage());
                    e.printStackTrace();
                    Platform.runLater(() -> showErrorAlert("GIF Load Error", "Error loading GIF: " + e.getMessage()));
                }
                return null;
            }
        };

        // Start the background thread using the custom ThreadFactory
        executorService.execute(loadGifTask);

        // Shutdown the executor service once the task is done
        loadGifTask.setOnSucceeded(event -> executorService.shutdown());
        loadGifTask.setOnFailed(event -> executorService.shutdown());
    }

    // Stop the GIF loading task if necessary
    public void stopLoading() {
        if (loadGifTask != null && loadGifTask.isRunning()) {
            loadGifTask.cancel();
            executorService.shutdownNow();
            System.out.println("GIF loading task has been cancelled.");
        }
    }

    // Display an error alert
    private void showErrorAlert(String title, String message) {
        // Implement your preferred method for displaying error alerts
        System.err.println(title + ": " + message);
    }

    // Define the DebugThreadFactory class
    private static class DebugThreadFactory implements ThreadFactory {
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
}
