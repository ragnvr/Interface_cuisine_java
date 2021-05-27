package vue;

import controller.ControllerManager;
import controller.CuisineController;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import modele.Donnees;
import modele.ManagerMeuble;
import modele.MeubleModele;

import java.io.IOException;

public class Cuisine extends ScrollPane {

    /**Les dimensions du pane**/
    public Point2D size;

    public CuisineController controller;

    public Paint background;

    public Pane content;

    /*------Grille---------------*/
    private double grilleInsets = 10;


    /**
     * Cree une cuisine, pour laquelle le plan est au milieu
     * @param LARGEUR la largeur du plan
     * @param HAUTEUR la heuteur du plan
     */
    public Cuisine(double LARGEUR, double HAUTEUR){
        this.size = new Point2D(LARGEUR, HAUTEUR);
        this.background = Color.BEIGE;


        this.controller = new CuisineController(this);
        ControllerManager.cuisineController = this.controller;
        Parent contentContainer = null;
        try {
            contentContainer = this.controller.loadFXMLWithController(getClass().getResource("CuisinePlan.fxml"));
        } catch (IOException e){
            e.printStackTrace();
        }
        //this.getChildren().add(contentContainer);
        StackPane stkpane = new StackPane();
        stkpane.minWidthProperty().bind(this.widthProperty());
        stkpane.minHeightProperty().bind(this.heightProperty());
        stkpane.getChildren().add(contentContainer);
        //stkpane.setStyle("-fx-background-color: HoneyDew ;");
        //this.setStyle("-fx-background-color: yellow");
        this.setContent(stkpane);
        this.content = (Pane) contentContainer;
        this.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
        this.setMinSize(USE_COMPUTED_SIZE,USE_COMPUTED_SIZE);
        Donnees.panneaux.cuisine = this;

        this.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            this.content.requestFocus();
        });
        this.addEventFilter(MouseEvent.MOUSE_ENTERED, event -> {
            MeubleModele selection = ManagerMeuble.getSelection();
            if(selection != null){
                if(selection.getIsClickedMoveProperty().get()){
                    selection.isInPlanProperty().set(true);
                    this.requestFocus();
                    content.requestFocus();
                    selection.getForme().requestFocus();
                }
            }
        });
        this.addEventFilter(MouseEvent.MOUSE_EXITED, event -> {
            MeubleModele selection = ManagerMeuble.getSelection();
            if(selection != null){
                if(selection.getIsClickedMoveProperty().get()){
                    selection.isInPlanProperty().set(false);
                }
            }
        });
    }
    
    public void updateSize(Point2D size) {
    	this.size =size;
    	this.controller.initialize();
    }

    /**
     * Ajoute un meuble dans la cuisine
     * @param meubleModele
     */
    public void add(MeubleModele meubleModele){
        //this.getChildren().add(meuble.getForme());
        this.content.getChildren().add(meubleModele.getForme());
    }

    /**
     * Dessine tous les meubles du panier
     */
    public void draw(GraphicsContext gc){
        clean(gc);
        gc.setStroke(Color.BLACK);
        gc.strokeRect(0,0,this.size.getX(),this.size.getY());
    }

    /**
     * Clean le canvas
     */
    public void clean(GraphicsContext gc){
        gc.setFill(this.background);
        gc.fillRect(0,0,this.size.getX(),this.size.getY());
        this.drawGrille(gc);

    }


    /**
     * Dessine la grille sur le canvas si isGrille est active
     * @param gc le contexte graphique du plan
     */
    public void drawGrille(GraphicsContext gc){
        if(Donnees.properties.isGrilleVisible.get()){
            //TODO grilles with grilleInsets
            gc.setLineWidth(1);
            gc.setStroke(Color.BURLYWOOD);
            double currentPosX = this.grilleInsets;
            while(currentPosX <= this.size.getX()){ //Lignes verticales
                gc.strokeLine(currentPosX, 0, currentPosX, this.size.getY());
                currentPosX += this.grilleInsets;
            }
            double currentPosY = this.grilleInsets;
            while(currentPosY <= this.size.getY()){ //Lignes horizontales
                gc.strokeLine(0, currentPosY, this.size.getX(), currentPosY);
                currentPosY += this.grilleInsets;
            }
            gc.setLineWidth(2);

            //gc.setFill(Color.GREEN);
            //gc.fillOval(50,50,50,50);
        }
    }

}