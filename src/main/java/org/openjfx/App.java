package org.openjfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nl.liacs.watch.protocol.server.ConnectionManager;
import nl.liacs.watch.protocol.tcpserver.Server;
import nl.liacs.watch.protocol.types.Constants;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main class for loading the main screen
 */
public class App extends Application {
    /**
     * The global connection manager for watch communication.
     */
    private static ConnectionManager connectionManager;
    /**
     * @return The global connection manager for watch communication.
     */
    public static ConnectionManager GetConnectionManager() {
        return connectionManager;
    }

    /**
     * Start function
     * @param stage Stage to load scene into
     * @throws IOException Thrown by {@link App#loadFXML(String)}
     */
    @Override
    public void start(Stage stage) throws IOException {
        Scene scene = new Scene(loadFXML("primary"));
        stage.setScene(scene);
        stage.show();
    }


    /**
     * Loads a fxml file
     * @param fxml File to load. Should not include the .fxml extension
     * @return Loaded fxml file as Parent
     * @throws IOException Thrown by {@code FXMLLoader}
     */
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    /**
     * Main function
     * @param args Unused launch arguments
     */
    public static void main(String[] args) throws IOException {
        var server = Server.createServer(Constants.TcpPort);
        Logger.getGlobal().log(Level.INFO, "running tcp server on port " + Constants.TcpPort);
        connectionManager = new ConnectionManager(server);

        launch();
    }

}