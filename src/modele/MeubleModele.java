package modele;

import controller.MouvementsController;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class MeubleModele {

/*-------------ID--------------*/
    /**Le nombre total de meubles crees**/
    private static int nbMeubles = 0;
    /**L id du meuble. Est unique**/
    protected int id;

    /**Est ce que le meuble est la selection active**/
    private BooleanProperty selected = new SimpleBooleanProperty(false);

    /**Est ce que le meuble se trouve dans le panier
     * <br/>Le gestionnaire de meuble y attache un change listener qui l ajoute ou l enleve du panier automatiquement**/
    private BooleanProperty inPanier = new SimpleBooleanProperty(false);

    private BooleanProperty inPlan = new SimpleBooleanProperty(false);

    /**Les dimentions du meuble**/
    protected double LARGEUR, HAUTEUR;

    private Image img = null;

    private String nom;
    private String constructeur;
    private double prix;
    private String description;

/*-----------Forme---------------*/

    /**La forme du meuble dans la cuisine**/
    private Shape forme;
    /**The fill property of the forme while in normal state**/
    private Paint paintUsual = Color.GRAY;
    /**The fill property of the forme while in selected state**/
    private Paint paintSelected = Color.BLUE;

    private MouvementsController dragController;


    /*-----------------Fiche-------------------*/
    /**La petite fiche utilisee dans le catalogue**/
    private FicheProduitPanier ficheCatalogue;
    /**La petite fiche utilisee dans le panier**/
    private FicheProduitPanier fichePanier;
    /**La fiche ou est inscrite toutes les infos**/
    private FicheProduit infoFiche;



    /*----------------------Mouvement par fiche-----------------*/

    /**La sauvegarde de la property a laquelle est liee le dragController**/
    private BooleanProperty savedDragBind;

    /**Defini si le mouvement de la forme se fait a partir de la fiche / avec un clic et pas un drag
     * <br/> -> Est ce que le meuble est deplace sans press&drag&drop (depuis catalogue ou panier)
     * <li>
     *     <ul>Si set a true, declanche automatiquement le debut du mouvement</ul>
     *     <ul>Si set a false, arrete le mouvement en cours</ul>
     * </li>
     */
    private BooleanProperty isClickedMove = new SimpleBooleanProperty(false);

    private EventHandler<MouseEvent> dragByClic;
    private EventHandler<MouseEvent> releaseByClic;

    /**
     * Cree un meuble possedant une forme et des fiches. Possede un controlleur integre
     * <br/>Il est possible d ajouter une image a la description du meuble.
     * <br/>Ne pas utiliser le meuble tant qu il n a pas ete ajoute au catalogue du gestionnaire de meubles
     * @param nom Le nom du meuble
     * @param constructeur Le constructeur du meuble
     * @param prix le prix du meuble en euros
     * @param largeur la largeur du meuble en cm
     * @param hauteur la hauteur du meuble en cm
     * @param description la description du meuble
     * @see GestionaireMeubles#addCatalogue(MeubleModele)
     */
    public MeubleModele(String nom, String constructeur, double prix, double largeur, double hauteur, String description){
        identify();

        this.LARGEUR = largeur;
        this.HAUTEUR = hauteur;
        this.nom = nom;
        this.constructeur = constructeur;
        this.prix = prix;
        this.description = description;
        contructFiches();
        buildForme();
        this.dragController = new MouvementsController(this.forme);

        setHandlers();
    }

    /**
     * Cree un meuble possedant une forme et des fiches. Possede un controlleur integre.
     * <br/>Il est possible d ajouter une image a la description du meuble.
     * <br/>Ne pas utiliser le meuble tant qu il n a pas ete ajoute au catalogue du gestionnaire de meubles
     * @param nom Le nom du meuble
     * @param constructeur Le constructeur du meuble
     * @param prix le prix du meuble en euros
     * @param largeur la largeur du meuble en cm
     * @param hauteur la hauteur du meuble en cm
     * @see GestionaireMeubles#addCatalogue(MeubleModele)
     */
    public MeubleModele(String nom, String constructeur, double prix, double largeur, double hauteur){
        this(nom, constructeur, prix,largeur,hauteur, "Ce meuble n a pas de description.");
    }


    /*------------------------------------------------------------------------*/

    /**Set the identity of the meuble. Unique for each meuble
     * @see MeubleModele#nbMeubles
     * @see MeubleModele#id**/
    private void identify(){
        this.id = nbMeubles++;
    }

    /**Renvoie le nombre total de meubles crees**/
    public static int getNbMeubles() {
        return nbMeubles;
    }

    /**
     * Renvoie l id du meuble
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     * Defini une image pour le meuble
     * @param img
     */
    public void setImg(Image img){
        this.img = img;
    }

    /**
     * Renvoie l image du meuble. Peu etre null
     * @return l image du meuble
     */
    public Image getImg(){return this.img;}

    /**
     * Deplace le point vers celui donne
     * @param p
     */
    public void move(Point2D p){
        //TODO move ? Check its use
    }

    /**Renvoie la position du meuble
     * @see DragController#getCurrentPos(Node) **/
    public Point2D getPos(){
        return getDragController().getCurrentPos(getForme());
    }

    /**Renvoie les dimensions du meuble**/
    public Point2D getSize(){return new Point2D(this.LARGEUR, this.HAUTEUR);}

    /**
     * Renvoie la boolean property qui verifie si le meuble est dans le panier
     * <br/>Modifier sa valeur enleve ou ajoute le meuble au panier :
     * <br/>Le gestionnaire de meuble y a attache un change listener qui l ajoute ou l enleve du panier automatiquement
     * @return le boolean property
     */
    public BooleanProperty isInPanier(){
        return inPanier;
    }

    /**
     * Verifie si un meuble est egal a un autre par leur id
     * @param m le meuble a comparer
     * @return le resultat de la comparaison
     */
    public boolean equals(MeubleModele m){
        return this.id == m.id;
     }


    /*-------------------Selection----------------------*/

    /**
     * Selectionne le meuble
     */
    public void select(){
        this.selected.set(true);
    }

    /**
     * Deselectionne le meuble
     */
    public void unselect(){
        this.getDragController().resetDrag();
        this.isClickedMove.set(false);
        this.selected.set(false);
        if(! isInPanier().get()){
            isInPlanProperty().set(false);
        }
    }

    /**
     * Verifie si le meuble est selectionne
     * @return
     */
    public boolean isSelected(){
        return this.selected.get();
    }

    /**
     * Renvoie la propriete verifiant si le meuble est selectionne
     * @return
     */
    public BooleanProperty isSelectedProperty(){
        return this.selected;
    }

    /**
     * Reset la position initiale sur le point a gauche-milieu du parent et retire du plan en le rendant invisible
     */
    public void reset(){
        getForme().setTranslateX(0);
        getForme().setTranslateY(0);
        getForme().relocate(0, getForme().getParent().getBoundsInLocal().getHeight()/2);
        this.inPlan.set(false);
    }

    /**
     * Verifie que le meuble est visible dans le plan
     * @return
     */
    public BooleanProperty isInPlanProperty(){
        return this.inPlan;
    }




    /*---------------------------Selection Handlers----------------*/
    private void setHandlers(){
        setEventHandlersDragAndReleaseByClic();
        MeubleModele self = this;
        getForme().setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
            	ManagerMeuble.select(self);
            }
        });

        selected.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                getForme().setFill(paintSelected);
            } else {
                getForme().setFill(paintUsual);
            }
        });

        // Cas clic to move

        isClickedMove.addListener((observable, oldValue, newValue) -> {
            if (newValue && ! oldValue) {
                getDragController().getDraggableProperty().unbind();
                getDragController().getDraggableProperty().set(false);
                getForme().setTranslateX(0); //Reset au cas ou on clique plusieurs fois dessus
                getForme().setTranslateY(0);
                grabFormeByFiche();
                getForme().getParent().addEventFilter(MouseEvent.MOUSE_MOVED, dragByClic);
                getForme().getParent().addEventFilter(MouseEvent.MOUSE_PRESSED, releaseByClic);
            } else if(! newValue && oldValue) {
                getForme().getParent().removeEventFilter(MouseEvent.MOUSE_MOVED, dragByClic);
                getForme().getParent().removeEventFilter(MouseEvent.MOUSE_PRESSED, releaseByClic);
                getForme().setTranslateX(0); //Reset au cas ou annule
                getForme().setTranslateY(0);
                getDragController().getDraggableProperty().bind(savedDragBind);
            }
        });

        inPlan.addListener((observable, oldValue, newValue) -> {
            Platform.runLater(()->{
                getForme().getParent().requestFocus();
                getForme().requestFocus();
            });
        });

    }

    /**
     * Defini les handlers pour le drag by clic
     */
    private void setEventHandlersDragAndReleaseByClic(){
        this.dragByClic = event -> {
            getForme().requestFocus();
            if(getDragController().getDragChecker() != null){
                if(getDragController().getDragChecker().check()){
                    Platform.runLater(new Runnable() {
                        @Override public void run() {
                            getDragController().dragHandler(event);
                        }
                    });

                }
            } else {
                Platform.runLater(new Runnable() {
                    @Override public void run() {
                        getDragController().dragHandler(event);
                    }
                });
                //this.dragController.dragHandler(event);
            }
        };

        this.releaseByClic = event -> {
            getForme().requestFocus();
            if(getDragController().getReleaseChecker() != null){
                if(getDragController().getReleaseChecker().check()){
                    Platform.runLater(new Runnable() {
                        @Override public void run() {
                            if(getDragController().releaseHandler(event)){
                                isClickedMove.set(false);
                                isInPanier().set(true);
                            } else {
                                getForme().setTranslateX(0);
                                getForme().setTranslateY(0);
                                grabFormeByFiche();
                            }
                        }
                    });

                } else {
                    Platform.runLater(new Runnable() {
                        @Override public void run() {
                            getForme().setTranslateX(0);
                            getForme().setTranslateY(0);
                            grabFormeByFiche();
                        }
                    });

                }
            } else {
                Platform.runLater(new Runnable() {
                    @Override public void run() {
                        if(getDragController().releaseHandler(event)){
                            isClickedMove.set(false);
                        }
                    }
                });

            }
        };

    }

    /**
     * Bind dragged property to the one given and save it for move by clic
     * @param propertyBindedTo
     */
    public void bindIsDraggedPropertyTo(BooleanProperty propertyBindedTo){
        getDragController().getDraggableProperty().bind(propertyBindedTo);
        this.savedDragBind = propertyBindedTo;
    }

    /*----------------------------Deplacement--------------------------*/

    /**
     * Renvoie le dragController du meuble
     * @return
     */
    public MouvementsController getDragController() {
        return dragController;
    }

    /**
     * Defini l action de saisir la forme de la fiche lorsque le mouvement par clic est active.
     * <br/>Defini les anchor et offset dans le dragController
     * @see MeubleModele#isClickedMove
     */
    private void grabFormeByFiche(){
        Point2D offset = new Point2D(this.LARGEUR/2, this.HAUTEUR/2);
        getDragController().setMouseOffsetFromNode(offset);

        Point2D posInParent = getDragController().getCurrentPos(getForme());
        double anchorInParentX = posInParent.getX() + offset.getX();
        double anchorInParentY = posInParent.getY() + offset.getY();
        Parent parent = getForme().getParent();
        Point2D anchorInScene = parent.localToScene(anchorInParentX, anchorInParentY);
        getDragController().setAnchors(anchorInScene);
    }

    /**
     * Renvoie le boolean qui defini si un move by clic va commencer.
     * <li>
     *     <ul>Si set a true, declanche automatiquement le debut du mouvement</ul>
     *     <ul>Si set a false, arrete le mouvement en cours</ul>
     * </li>
     * @return le boolean property
     * @see MeubleModele#isClickedMove
     */
    public BooleanProperty getIsClickedMoveProperty(){
        return isClickedMove;
    }

    /*--------------------------Forme-----------------------------*/

    /**
     * Cree la forme de la cuisine en fonction de ses dimensions
     */
    private void buildForme(){
        this.forme = new Rectangle(this.LARGEUR, this.HAUTEUR);
        this.forme.setFill(paintUsual);
        this.forme.setStroke(Color.BLACK);
        this.forme.setStrokeWidth(2);
        this.forme.visibleProperty().bindBidirectional(this.inPlan);
    }


    /**
     * Renvoie la forme avec laquelle le meuble apparait dans le plan
     * @return la forme du meuble
     */
    public Shape getForme() {
        return forme;
    }

    /**
     * Deplace la forme du meuble en utilisant la methode relocate de Node
     * @param x la position x du meuble sur le plan
     * @param y la position y du meuble sur le plan
     * @see javafx.scene.Node#relocate(double, double)
     */
    public void relocate(double x, double y){
        getForme().relocate(x,y);
    }

    /**
     * Verifie si la position donnee est dans le meuble
     * @param pos
     * @return
     */
    public boolean isInside(Point2D pos){
        //TODO isInside meuble
        return false;
    }

    /**
     * Verifie si les 2 meubles se superposent
     * @param m le meuble a comparer
     * @return le resultat de la comparaison
     */
    public boolean intersect(MeubleModele m){
        return getForme().intersects(getForme().sceneToLocal(m.getForme().localToScene(
                m.getForme().getBoundsInLocal())));
    }

    /**
     * Defini la forme du meuble comme enfant du parent donne
     * @param parent le parent du meuble
     */
    public void setParent(Pane parent){
        parent.getChildren().add(getForme());
    }

    /*--------------------------Fiche----------------------------------*/
    /**
     * Construit les 2 fiches du meuble
     */
    public void contructFiches(){
        this.fichePanier = new FicheProduitPanier(this);
        this.ficheCatalogue = new FicheProduitPanier(this);
        //Si il est visible = false, le parent va se reorganiser
        this.fichePanier.managedProperty().bind(this.fichePanier.visibleProperty());
        this.ficheCatalogue.managedProperty().bind(this.ficheCatalogue.visibleProperty());

        //Big fiche/info fiche
        this.infoFiche = new FicheProduit(this);
    }

    /**
     * Renvoie la fiche du meuble pour le panier
     */
    public FicheProduitPanier getLittleFichePanier(){
        return this.fichePanier;
    }

    /**
     * Renvoie la fiche du meuble pour le catalogue
     */
    public FicheProduitPanier getLittleFicheCatalogue(){
        return this.ficheCatalogue;
    }

    /**
     * Renvoie la grande fiche du meuble avec sa description
     */
    public  FicheProduit getBigFiche(){
        return this.infoFiche;
    }


    /*-----------------------Informations meuble-------------------*/

    /**
     * Renvoie le nom du meuble
     * @return
     */
    public String getNom() {
        return nom;
    }

    /**
     * Renvoie le prix du meuble en euros
     * @return information en tant que String
     */
    public String getPrix() {
        return Double.toString(prix) + "€";
    }

    /**
     * Renvoie les dimensions du meuble
     * @return information en tant que String sous forme 0 x 0
     */
    public String getDimensions(){
        return this.LARGEUR + " x " + this.HAUTEUR;
    }

    /**
     * Renvoie le constructeur du meuble
     * @return information en tant que String
     */
    public String getConstructeur() {
        return constructeur;
    }

    /**
     * Defini la description du meuble
     * @param description la description du meuble. Sans retour a la ligne
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Renvoie la description du meuble
     * @return la description du meuble en tant que String
     */
    public String getDescription() {
        return description;
    }

    /**
     * La classe qu un meuble represente dans le plan
     */
    public class Forme extends Rectangle{

        public Forme(MeubleModele meuble){

        }

    }
}
