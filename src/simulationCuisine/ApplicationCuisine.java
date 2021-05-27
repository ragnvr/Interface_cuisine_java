package simulationCuisine;

import controller.ControllerLoadFxml;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import modele.ManagerMeuble;
import vue.Cuisine;

import java.awt.*;

public class ApplicationCuisine extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception{

        primaryStage.setTitle("Designer votre cuisine");

        Cuisine cuisine = new Cuisine(400,400);

        ApplicationCuisineController globalController = new ApplicationCuisineController(cuisine);
        BorderPane root = (BorderPane) globalController.loadFXMLWithController(getClass().getResource("../vue/ApplicationCuisine.fxml"));

        Rectangle dimEcran = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();

        double width = dimEcran.width-100;//800;
        double height = dimEcran.height-15;//600;
        root.setMinSize(width,height);
        root.setMaxSize(width,height);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
        globalController.setGlobalEventHandler(root);
        ManagerMeuble meubles = new ManagerMeuble(cuisine);
        //meubles.initPanierTest(4);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
