package gui.modeles;

import java.util.LinkedList;

import especes.Espece;
import especes.GestionnaireEspeces;

public class SelectionModele {
    protected GestionnaireEspeces gestionnaire;
    protected final LinkedList<Espece> selection; 

    public SelectionModele() {
        gestionnaire = new GestionnaireEspeces();
        selection = new LinkedList<>();
    } 

    public SelectionModele(GestionnaireEspeces ge) {
        gestionnaire = ge;
        selection = new LinkedList<>();
    }

    public GestionnaireEspeces getGestionnaire() {
        return gestionnaire;
    }

    public LinkedList<Espece> getSelection() {
        return selection;
    }
}
