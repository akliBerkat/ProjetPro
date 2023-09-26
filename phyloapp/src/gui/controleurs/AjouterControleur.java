package gui.controleurs;

import java.awt.Color;

import especes.Espece;
import gui.modeles.SelectionModele;
import gui.vues.AjouterDialog;

public class AjouterControleur {
    private SelectionModele modele;
    private AjouterDialog vue;

    public AjouterControleur(SelectionModele m, AjouterDialog v) {
        this.modele = m;
        this.vue = v;
    }

    public void validerPressed(String nom, int sequence) {
        Espece tmp = new Espece(nom, sequence, 1);
        modele.getGestionnaire().getAjouts().add(tmp);
        vue.getNomTF().setText("");
        vue.getSequenceTF().setText("");
        vue.getNotif().setText("\"" + nom + "\" a bien été ajouté !");
        vue.getNotif().setForeground(Color.GREEN);
        vue.getMain().remplirSelecteur();
        vue.getMain().getFrame().updateFrame();
    }
}
