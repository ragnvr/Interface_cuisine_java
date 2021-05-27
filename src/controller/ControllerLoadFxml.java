package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.net.URL;

/**
 * Abstract class containing methods for FXML controllers
 */
public abstract class ControllerLoadFxml {

    /**
     * The FXMLLoader used if Controller linked to fxml file with <i>loadFXMLWithController</i>
     * @see Controller#loadFXMLWithController(URL)
     */
    protected FXMLLoader fxmlLoader;

    /**
     * Load the fxml with this controller instanciated.
     * <br/>Save the FXMLLoader
     * @param fxmlPath path to fxml file
     * @return the fxml file content as Parent
     * @throws IOException
     * @see Controller#loadFXMLWithController(URL, Controller)
     * @see Controller#fxmlLoader
     */
    public Parent loadFXMLWithController(URL fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(fxmlPath);
        System.out.println(fxmlPath);
        loader.setController(this);
        Parent fxmlContent = null;
        fxmlContent = loader.load();
        this.fxmlLoader = fxmlLoader;
        return fxmlContent;
    }

    /**
     * Load the fxml with an instanciated Controller
     * @param fxmlPath path to fxml file
     * @param controller the instanciated Controller
     * @return the fxml file content as Parent
     * @throws IOException
     * @see Controller#loadFXMLWithController(URL)
     */
    public static Parent loadFXMLWithController(URL fxmlPath, ControllerLoadFxml controller) throws IOException {
        FXMLLoader loader = new FXMLLoader(fxmlPath);
        loader.setController(controller);
        Parent fxmlContent = null;
        fxmlContent = loader.load();
        return fxmlContent;
    }
}
