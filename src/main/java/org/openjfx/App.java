package org.openjfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main class for loading the main screen
 */
public class App extends Application {


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
    public static void main(String[] args) {
        launch();
    }

}