package ch.zhaw.pm2.multichat.client;

import static ch.zhaw.pm2.multichat.protocol.util.LogUtil.printError;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Main class of the client UI
 * Starting the client UI
 */
public class ClientUI extends Application {

    @Override
    public void start(Stage primaryStage) {
        chatWindow(primaryStage);
    }

    private void chatWindow(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ChatWindow.fxml"));
            Pane rootPane = loader.load();
            // fill in scene and stage setup
            Scene scene = new Scene(rootPane);
            //scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

            // configure and show stage
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(420);
            primaryStage.setMinHeight(250);
            primaryStage.setTitle("Multichat Client");
            primaryStage.show();

            ChatWindowController controller = loader.getController();
            controller.addShutdownRoutines();
        } catch (Exception e) {
            printError("Error starting up UI" + e.getMessage());
        }
    }
}
