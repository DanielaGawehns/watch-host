package org.openjfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nl.liacs.watch.protocol.server.ConnectionManager;
import nl.liacs.watch.protocol.tcpserver.Server;
import nl.liacs.watch.protocol.types.Constants;
import org.openjfx.controllers.PrimaryController;

import java.io.IOException;


/**
 * Main class for starting the application
 */
public class App extends Application {

    /**
     * Start function
     * @param stage Stage to load scene into
     * @throws IOException Thrown by {@link FXMLLoader}
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/primary.fxml"));
        Parent root = fxmlLoader.load();
        PrimaryController controller = fxmlLoader.getController();
        controller.setHostServices(getHostServices());
        stage.setScene(new Scene(root));
        stage.setTitle("Watchboard");
        stage.show();
    }

    /**
     * Main function
     * @param args Unused arguments
     * @throws IOException Thrown by {@link Server#createServer(int)}
     */
    public static void main(String[] args) throws IOException {
        launch();
    }
}
