package gui.controleurs;

import java.util.LinkedList;

import javax.swing.JButton;

import especes.Espece;
import especes.GestionnaireEspeces;
import gui.Window;
import gui.modeles.SelectionModele;
import gui.vues.ModifierDialog;

public class ModifierControleur {
    private SelectionModele modele;
    private ModifierDialog vue;

    public ModifierControleur(SelectionModele m, ModifierDialog v) {
        this.modele = m;
        this.vue = v;
    }

    public void itemClicked(Espece selection, boolean aAjouter) {
        if(aAjouter) modele.getSelection().add(selection);
        else modele.getSelection().remove(selection);
        verifierNombreItems();
    }

    public void reinitialiserPressed() {
        preRemplirSelection();
        vue.reinitialiserEspeces();
        vue.remplirEspeces();
        vue.getMain().getFrame().updateFrame();
    }

    public void actualiserPressed() {
        GestionnaireEspeces gest = vue.getMain().getModele().getGestionnaire();
        Window frame = vue.getMain().getFrame();
        gest.getSelection().clear();
        gest.getSelection().addAll(modele.getSelection());
        frame.setMainPanel(gest);
        frame.updateFrame();
        vue.setVisible(false);
    }

    private void verifierNombreItems() {
        JButton bouton = vue.getActualiser();
        if(modele.getSelection().size() < 3) {
            if(bouton.isEnabled()) {
                bouton.setEnabled(false);
                bouton.setToolTipText("Vous n'avez pas sélectionné assez d'espèces.");
            }
        } else if(modele.getSelection().size() > 8) {
            if(bouton.isEnabled()) {
                bouton.setEnabled(false);
                bouton.setToolTipText("Vous avez sélectionné trop d'espèces.");
            }
        } else {
            if(!bouton.isEnabled()) {
                bouton.setEnabled(true);
                bouton.setToolTipText("");
            }
        }
    }
    
    public void preRemplirSelection() {
        LinkedList<Espece> selection = modele.getSelection();
        if(selection.size() != 0) selection.clear();
        for(Espece e : modele.getGestionnaire().getSelection())
            selection.add(e);
    }

}
