package vue;

import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import modele.MeubleModele;

public class PanneauInformation {

    private StackPane infoPane;

    private Pane defaultPane;

    /**
     * Defini le panneau d informations sous la forme d un stackPane.
     * @param infoPane le contener du panneau
     * @param initPane le premier panneau enregistre, celui par default
     * @see StackPane
     */
    public PanneauInformation(StackPane infoPane, Pane initPane){
        this.infoPane = infoPane;
        this.defaultPane = initPane;
    }

    /**
     * Ajoute la fiche d info du meuble au panneau d infos
     * @param meubleModele le meuble a ajouter
     */
    public void initCommit(MeubleModele meubleModele){
        this.infoPane.getChildren().add(meubleModele.getBigFiche());
        unselect();
    }

    /**
     * Affiche le panneau par defaut
     */
    public void unselect(){
        this.defaultPane.toFront();
    }

    /**
     * Affiche le panneau d informations du meuble
     * @param meuble
     */
    public void select(MeubleModele meuble){
        meuble.getBigFiche().toFront();
    }

}
