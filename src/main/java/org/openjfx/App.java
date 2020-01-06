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
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Main class for starting the application
 */
public class App extends Application {
    /**
     * The global connection manager for watch communication.
     */
    private static ConnectionManager connectionManager;
    /**
     * @return The global connection manager for watch communication.
     */
    public static ConnectionManager getConnectionManager() {
        return connectionManager;
    }

    /**
     * Global manager for managing the Database connection {@link DBManager}
     */
    private static DBManager dbManager;
    /**
     * @return The global database manager.
     */
    public static DBManager getDbManager() {
        return dbManager;
    }

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
        controller.loadOverviewFXML();
        controller.setViewListener();
    }

    /**
     * Main function
     * @param args Unused arguments
     * @throws IOException Thrown by {@link Server#createServer(int)}
     */
    public static void main(String[] args) throws IOException, SQLException {
        var server = Server.createServer(Constants.TcpPort);
        Logger.getGlobal().log(Level.INFO, "running tcp server on port " + Constants.TcpPort);
        App.connectionManager = new ConnectionManager(server);

        App.dbManager = new DBManager();

        launch();

        App.connectionManager.close();
        PrimaryController.shutdown();
    }
}
