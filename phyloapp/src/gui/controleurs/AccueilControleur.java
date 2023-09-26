package gui.controleurs;

import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import bdd.StaticFields;
import especes.Espece;
import especes.GestionnaireEspeces;
import gui.Window;
import gui.modeles.SelectionModele;
import gui.vues.AccueilPanel;

public class AccueilControleur {
    private SelectionModele modele;
    private AccueilPanel vue;

    public AccueilControleur(SelectionModele m, AccueilPanel v) {
        this.modele = m;
        this.vue = v;
        if(modele.getGestionnaire().getSelection().size() > 0) {
            modele.getSelection().addAll(m.getGestionnaire().getSelection());
            modele.getGestionnaire().getSelection().clear();
        }
    }

    public void itemClicked(Espece selection, boolean aAjouter) {
        if(aAjouter) modele.getSelection().add(selection);
        else modele.getSelection().remove(selection);
        vue.getEspeces().setText(getSelectionText());
        verifierNombreItems();
    }

    public void commencerPressed() {
        if(vue.getAjouterDialog().isVisible())
            vue.getAjouterDialog().setVisible(false);
        GestionnaireEspeces ge = modele.getGestionnaire();
        ge.setSelection(modele.getSelection());
        vue.getFrame().setMainPanel(ge);
    }

    public void supprimerPressed(Espece e) {
        modele.getGestionnaire().getAjouts().remove(e);
        modele.getSelection().remove(e); // enlève si sélectionné
        vue.remplirSelecteur();
        vue.getEspeces().setText(getSelectionText());
        vue.getFrame().updateFrame();
    }

    public void reinitialiserPressed() {
        Window frame = vue.getFrame();
        int result = JOptionPane.showConfirmDialog(frame, "Cette action va supprimer TOUTES les espèces crées dans la base de donnée. Continuer ?", "", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            frame.getBDD().rmvListEspeces();
            frame.getBDD().initStaticFields(new StaticFields());
            modele.getGestionnaire().getBDD().clear();
            cleanSelection();
            vue.remplirSelecteur();
            vue.getEspeces().setText(getSelectionText());
            frame.updateFrame();
        }
    }

    public String getSelectionText() {
        String res = "";
        if(modele.getSelection().isEmpty()) return "- vide -";
        for(Espece e : modele.getSelection()) {
            res += e.getNom() + ", ";
        }
        res = res.substring(0, res.length()-2);
        return res;
    }

    public void verifierNombreItems() {
        JButton bouton = vue.getCommencer();
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

    // après avoir réinitialisé la db
    private void cleanSelection() { 
        LinkedList<Espece> old = modele.getSelection();
        for(Espece e : old) {
            if(!modele.getGestionnaire().getAllEspeces().contains(e))
                old.remove(e);
        }
    }
}