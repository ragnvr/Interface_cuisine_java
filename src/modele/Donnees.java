package modele;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import vue.Catalogue;
import vue.Cuisine;
import vue.PanneauInformation;
import vue.Panier;

/**
 * Reuni la liste des booleans representant les options que l utilisateur peut afficher
 */
public class Donnees {


    /**La liste des properties, notament celles utilisees par la tools Bar**/
    public static Properties properties = new Properties();

    /**
     * La liste de tous les panes de l appli utilisables
     */
    public static Panneaux panneaux = new Panneaux();


    public static class Properties {

        public Properties(){

        }

        /**
         * Defini si la grille est visible.
         * <br/>Initialise par le CuisineController
         */
        public BooleanProperty isGrilleVisible = new SimpleBooleanProperty(false);

        /**
         * Defini si il est possible de deplacer les meubles dans le plan.
         * <br/>Initialise par le CuisineController
         */
        public BooleanProperty isMeubleMovable = new SimpleBooleanProperty(true);


    }

    public static class Panneaux {

        public Panneaux(){

        }

        /**
         * Commit le meuble a tous les panneaux
         * @param meuble le meuble a commit
         * @see Panier#initCommit(MeubleModele)
         * @see Catalogue#initCommit(MeubleModele)
         * @see InfoPane#initCommit(MeubleModele)
         */
        public void initCommit(MeubleModele meuble){
            this.panier.initCommit(meuble);
            this.catalogue.initCommit(meuble);
            this.infoPane.initCommit(meuble);
        }
        
        /**La gestion de la vue du panier
         * <br/>Initialise par le controlleur de l appli**/
        public Panier panier;

        /**La gestion de la vue du catalogue
         * <br/>Initialise par le controlleur de l appli**/
        public Catalogue catalogue;

        /**La gestion de la vue du panneau information
         * <br/>Initialise par le controlleur de l appli**/
        public PanneauInformation infoPane;

        /**
         * La cuisine au milieu de l appli
         */
        public Cuisine cuisine;

    }

}