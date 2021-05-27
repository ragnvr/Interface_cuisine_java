package modele;

import controller.ControllerManager;
import controller.MouvementsController;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import vue.Cuisine;

import java.util.ArrayList;

import javax.print.DocFlavor.URL;

public class ManagerMeuble {

    /**La cuisine sur laquelle sont affiches tous les meubles**/
    private Cuisine cuisine;


    private static MeubleModele selection = null;

    public BooleanProperty isMovable = new SimpleBooleanProperty();

    /**Le catalogue des meubles**/
    private ArrayList<MeubleModele> catalogue = new ArrayList<>();

    /**Les meubles dans le panier**/
    private static ArrayList<MeubleModele> panier = new ArrayList<>();

    /**
     * Le gestionnaire de tous les meubles.
     * <br/>Pour une bonne initialisation des boundaries limitant les meubles, initialiser apres scene.show
     */
    public ManagerMeuble(Cuisine cuisine){
        this.cuisine = cuisine;
        initCatalogue();
        this.isMovable.bind(Donnees.properties.isMeubleMovable);
    }


    /**
     * Renvoie le meuble actuellement selectionne
     * @return le meuble selectionne
     * @see GestionaireMeubles#select(MeubleModele)
     * @see GestionaireMeubles#unselect()
     */
    public static MeubleModele getSelection() {
        return selection;
    }
    
    


    /**
     * Enleve tout meuble de la selection
     * @see GestionaireMeubles#getSelection()
     * @see GestionaireMeubles#select(MeubleModele)
     */
    public static void unselect(){
        if(selection != null){
            selection.unselect();
            selection = null;
        }
    }


    /**
     * Selectionne le meuble
     * @param meubleModele le meuble a selectionner
     * @see GestionaireMeubles#getSelection()
     * @see GestionaireMeubles#unselect()
     */
    public static void select(MeubleModele meubleModele){
        if(meubleModele != null){
            if(selection != null){
                if(! selection.equals(meubleModele)){
                    unselect();
                    selection = meubleModele;
                    meubleModele.select();
                    meubleModele.getForme().toFront();
                }
            } else {
                unselect();
                selection = meubleModele;
                meubleModele.select();
                meubleModele.getForme().toFront();
            }
        } else {
            throw new IllegalStateException("MeubleModel selected must not be null");
        }
    }

    /**
     * Renvoie le meuble correspondant à la sélection
     * @param posMeuble
     * @return
     */
    public MeubleModele getMeuble(Point2D posMeuble){
        return null;
    }


    /*-------------Panier---------------*/


    /**
     * Ajoute un meuble au panier.
     * <br/>Tout ajout dans le panier doit se faire a partir du meuble
     * @param meubleModele meuble a placer
     * @see MeubleModele#isInPanier()
     */
    private void addToPanier(MeubleModele meubleModele){
        this.panier.add(meubleModele);
        meubleModele.isInPlanProperty().set(true);
        Donnees.panneaux.panier.add(meubleModele);
    }

    /**
     * Verifie si un meuble est dans le panier
     * @param meubleModele verifie si le meuble est dans le panier
     * @return
     */
    public boolean isInPanier(MeubleModele meubleModele){
        return this.panier.contains(meubleModele);
    }

    /**
     * Enleve un meuble du panier
     * <br/>Tout retrait du le panier doit se faire a partir du meuble
     * @param meubleModele meuble a enlever
     * @see MeubleModele#isInPanier()
     */
    private void removeFromPanier(MeubleModele meubleModele){
        this.panier.remove(meubleModele);
        meubleModele.isInPlanProperty().set(false);
        meubleModele.reset();
        Donnees.panneaux.panier.remove(meubleModele);
    }




    /**
     * Set les checkers sur le meuble
     * @param meubleModele le meuble a initialiser
     */
    private void setChecker(MeubleModele meubleModele){
    	MouvementsController mCtrl = meubleModele.getDragController();
    	MouvementsController.SimpleChecker release = new MouvementsController.SimpleChecker() {
            @Override
            public boolean check() {
                for(MeubleModele m : panier){
                    if(! meubleModele.equals(m)){
                        if(meubleModele.intersect(m)){
                            return false;
                        }
                    }
                }
                return true;
            }
        };
        mCtrl.setReleaseChecker(release);
    }
    
    public static ArrayList<MeubleModele> getPanier(){
    	return panier;
    }

    /*--------------Catalogue----------------*/

    /**
     * Ajoute un meuble au catalogue et <b>l'initialise</b>
     * @param meubleModele le meuble a ajouter
     */
    public void addCatalogue(MeubleModele meubleModele){
        this.catalogue.add(meubleModele);
        this.cuisine.add(meubleModele);
        meubleModele.getDragController().setBoundaries(this.cuisine.getBoundsInLocal(), ControllerManager.cuisineController.getPlanBoundsInCuisine());
        meubleModele.bindIsDraggedPropertyTo(this.isMovable);
        meubleModele.isInPanier().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                addToPanier(meubleModele);
            } else {
                removeFromPanier(meubleModele);
            }
        });
        meubleModele.getForme().setVisible(false);
        setChecker(meubleModele);
        Donnees.panneaux.initCommit(meubleModele);
        meubleModele.reset();
    }


    /**
     * Initialise le catalogue, cree tous les meubles
     */
    private void initCatalogue(){
        //TODO init les meubles et les met dans la vue
        MeubleModele m1 = new MeubleModele("Refrigirateur", "MaCuisine.com", 180,100,60);
        final java.net.URL imageURL = getClass().getResource("../Images/four.jpg");  
        final Image img = new Image(imageURL.toExternalForm());
        m1.setImg(img);
        addCatalogue(m1);
        MeubleModele m2 = new MeubleModele("Four", "MaCuisine.com", 100,60,60);
        addCatalogue(m2);
        MeubleModele m3 = new MeubleModele("Lave vaisselles", "MaCuisine.com", 100,60,60);
        addCatalogue(m3);
        MeubleModele m4 = new MeubleModele("Lave Linge", "MaCuisine.com", 100,60,50);
        addCatalogue(m4);
        MeubleModele m5 = new MeubleModele("Evier", "MaCuisine.com", 100,120,60);
        addCatalogue(m5);
    }

    /** ----------------------------------------------------A supprimer --------------
     * Ajoute les nb premiers elements du catalogue au panier.
     * <br/>A utiliser uniquement pour les tests
     * @param nb le nombre d elements a prendre dans le catalogue
     */
    public void initPanierTest(int nb){
        int itMax = nb -1;
        if(nb > this.catalogue.size()){
            itMax = this.catalogue.size()-1;
        }
        for(int i = 0; i<= itMax; i++){
            this.catalogue.get(i).isInPanier().set(true);
        }
    }

}
