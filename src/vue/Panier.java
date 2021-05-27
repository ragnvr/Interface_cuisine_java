package vue;

import javafx.scene.layout.VBox;
import modele.MeubleModele;

public class Panier {


    /**
     * Le panier dans l application
     */
    private VBox container;

    public Panier(VBox container){
        this.container = container;
    }

    /**
     * Ajout initial
     * @param meubleModele le meuble a ajouter
     */
    public void initCommit(MeubleModele meubleModele){
        meubleModele.getLittleFichePanier().setVisible(false);
        container.getChildren().add(meubleModele.getLittleFichePanier());
    }

    /**
     * Fait apparaitre le meuble dans la liste du panier
     * @param meubleModele
     */
    public void add(MeubleModele meubleModele){
        meubleModele.getLittleFichePanier().setVisible(true);
    }

    /**
     * Fait disparaitre le meuble dans la liste du panier
     * @param meubleModele
     */
    public void remove(MeubleModele meubleModele){
        meubleModele.getLittleFichePanier().setVisible(false);
    }

}
