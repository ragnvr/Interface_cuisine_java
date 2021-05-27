package simulationCuisine;

import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import controller.ControllerLoadFxml;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import modele.Donnees;
import modele.ManagerMeuble;
import modele.MeubleModele;
import vue.Catalogue;
import vue.Cuisine;
import vue.PanneauInformation;
import vue.Panier;


public class ApplicationCuisineController extends ControllerLoadFxml {


    private Cuisine cuisine;


    @FXML
    private VBox panier;

    @FXML
    private VBox catalogue;

    @FXML
    private StackPane infoPane;

    @FXML
    private Pane baseInfoText;

    @FXML
    private AnchorPane cuisinePlan;

    @FXML
    private BorderPane container;


    @FXML
    private ImageView moveImageView;

    @FXML
    private Button suppr;

    @FXML
    private ImageView grilleImageView;
    
    @FXML
    private Canvas plan;
    
    @FXML
    private Button validationBtn;
    
    @FXML
    private AnchorPane contentContainer;
    
    @FXML
    private TextField longueur;
    
    @FXML
    private TextField largeur;


    /*-----------------Pas FXML-----------------------*/


    /**Les images du button move**/
    private Image moveImage, dontMoveImage;
    /**Les images du bouton grille**/
    private Image grilleImage, grilleCrossedImage;

    public ApplicationCuisineController(Cuisine cuisine){
        this.cuisine = cuisine;
        this.moveImage = new Image(getClass().getResourceAsStream("../Images/move.png"));
        this.dontMoveImage = new Image(getClass().getResourceAsStream("../Images/dont-move.png"));
        this.grilleImage = new Image(getClass().getResourceAsStream("../Images/hashtag.png"));
        this.grilleCrossedImage = new Image(getClass().getResourceAsStream("../Images/la-grille.png"));
    }

    @FXML
    public void initialize(){
        container.setCenter(cuisine);

        Donnees.panneaux.panier = new Panier(panier);
        Donnees.panneaux.catalogue = new Catalogue(catalogue);
        Donnees.panneaux.infoPane = new PanneauInformation(infoPane, baseInfoText);

        Donnees.properties.isMeubleMovable.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                this.moveImageView.setImage(this.moveImage);
            } else {
                this.moveImageView.setImage(this.dontMoveImage);
            }
        });

        Donnees.properties.isGrilleVisible.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                this.grilleImageView.setImage(this.grilleImage);
            } else {
                this.grilleImageView.setImage(this.grilleCrossedImage);
            }
        });
        longueur.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, 
                String newValue) {
                if (!newValue.matches("\\d*")) {
                	longueur.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        largeur.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, 
                String newValue) {
                if (!newValue.matches("\\d*")) {
                	largeur.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        BooleanBinding bb = new BooleanBinding() {
            {
                super.bind(largeur.textProperty(), longueur.textProperty());
            }

            @Override
            protected boolean computeValue() {
                return (largeur.getText().isEmpty()
                        || longueur.getText().isEmpty());
            }
        };

        validationBtn.disableProperty().bind(bb);
        longueur.setText("400");
        largeur.setText("400");

    }

    @FXML
    public void cancelSelectionHandler(){
    	ManagerMeuble.unselect();
    }

    @FXML
    public void supprHandler(){
        MeubleModele selection = ManagerMeuble.getSelection();
        if(selection != null){
            selection.isInPanier().set(false);
            selection.isInPlanProperty().set(false);
        }
    }

    @FXML
    public void moveHandler(){
    	Donnees.properties.isMeubleMovable.set(! Donnees.properties.isMeubleMovable.get());
    }

    @FXML
    public void grilleHandler(){
    	Donnees.properties.isGrilleVisible.set(! Donnees.properties.isGrilleVisible.get());
    }

	@FXML
	protected void onExit() {
	        System.out.println("Exiting program");
	        Platform.exit();
	}
	
    //Fonction de sauvegarde d'image
	@FXML
    public void on_save() {
        try {
            Image snapshot = container.snapshot(null, null);
            ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", new File("cuisine.png"));
        } catch (Exception e) {
            System.out.println("Erreur lors de la sauvegarde de l'image: " + e);
        }
    }
	@FXML
	protected void onNew() {
        System.out.println("Nouveau plan");
        ArrayList<MeubleModele> panier= ManagerMeuble.getPanier();
        System.out.println(panier.size());
        panier.clear();   
    }
    
	@FXML
	protected void on_print() {
        System.out.println("Imprimer");
        final PrinterJob printerJob = PrinterJob.createPrinterJob();
        // Affichage de la boite de dialog de configation de l'impression.    
        if (printerJob.showPrintDialog(container.getScene().getWindow())) {
          // Mise en page, si nécessaire.
          // Lancement de l'impression.
          if (printerJob.printPage(container)) {
            // Fin de l'impression.
            printerJob.endJob();
          }
        }
    }
    
	@FXML
	protected void setDimension() {
		try{
            int largeurInt = Integer.parseInt(largeur.getText());
            int longeurInt= Integer.parseInt(longueur.getText());
            Point2D point = new Point2D(largeurInt, longeurInt);
            cuisine.updateSize(point);
            // output = 25
        }
        catch (NumberFormatException ex){
            ex.printStackTrace();
        }
		
	}
    
    /**
     * Link les events globaux.
     * @param root la scene qui fire les events
     */
    public void setGlobalEventHandler(Node root) {
        root.addEventHandler(KeyEvent.KEY_PRESSED, ev -> {
            if (ev.getCode() == KeyCode.DELETE) {
                suppr.fire();
                ev.consume();
            }
        });
    }

}
