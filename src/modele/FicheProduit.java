package modele;

import controller.FicheProduitController;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class FicheProduit extends Pane {

    private MeubleModele meubleModele;

    private FicheProduitController controller;

    public FicheProduit(MeubleModele meubleModele){
        this.meubleModele = meubleModele;
        this.controller = new FicheProduitController(this);
        Parent ficheContent = null;
        try{
            ficheContent = this.controller.loadFXMLWithController(getClass().getResource("../vue/FicheProduit.fxml"));

        } catch (IOException e){
            e.printStackTrace();
        }
        if(ficheContent != null){
            this.getChildren().add(ficheContent);
        }
    }


    /**
     * Renvoie le meuble associé à la fiche
     * @return
     */
    public MeubleModele getMeubleModele() {
        return meubleModele;
    }


}
