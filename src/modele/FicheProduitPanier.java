package modele;

import controller.FicheProduitPanierController;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class FicheProduitPanier extends Pane {

    private MeubleModele meubleModele;

    private FicheProduitPanierController controller;

    Parent content;
    String originalContentStyle;

    public FicheProduitPanier(MeubleModele meubleModele) {
        this.meubleModele = meubleModele;
        this.controller = new FicheProduitPanierController(this);
        //TODO load le fxml et add to children
        Parent ficheContent = null;
        try{
            //ficheContent = this.controller.loadFXMLWithController("PetiteFicheMeuble.fxml");//Changer nom
            ficheContent = this.controller.loadFXMLWithController(getClass().getResource("../vue/FicheProduitPanier.fxml"));

        } catch (IOException e){
            e.printStackTrace();
        }
        if(ficheContent != null){
            this.getChildren().add(ficheContent);
        }
        //this.setStyle("-fx-border-color: black ; -fx-border-width: 2px ;");
        this.content = ficheContent;
        originalContentStyle = content.getStyle();
        content.setStyle("-fx-border-color: black ; -fx-border-width: 2px ;"+originalContentStyle);
        this.setPrefHeight(USE_COMPUTED_SIZE);
        this.setPrefWidth(USE_COMPUTED_SIZE);
    }


    /**
     * Change le style de la fiche tout en gardant son style original
     * @param style
     */
    public void setContentStyle(String style){
        content.setStyle(style + originalContentStyle);
    }

    /**
     * Renvoie le meuble lie a la fiche
     * @return
     */
    public MeubleModele getMeubleModele() {
        return meubleModele;
    }

}
