package gui.controleurs;

import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;

import javax.swing.JOptionPane;

import gui.vues.MatricePanel;
import upgma.Matrice;
import upgma.Upgma;

public class MatriceControleur {
    private MatricePanel vue;
    private Upgma modele;
    
    public MatriceControleur(MatricePanel v, Upgma m) {
        this.vue = v;
        this.modele = m;
    }

    public void precedentePressed() {
        int idx = modele.getEtapeActuelle() - 1;
        modele.setEtapeActuelle(idx);
        Matrice precedente = modele.getEtapes().get(idx).getMatrice();
        if(idx == 0) {
            vue.getPrecedente().setEnabled(false);
        }
        if(!vue.getReduire().isEnabled()) {
            vue.getReduire().setEnabled(true);
        }
		vue.setMatrice(precedente);
        vue.getMain().getFrame().updateFrame();
    }

    public void reduirePressed() {
        int idx = modele.getEtapeActuelle();
        Matrice suivante = modele.getEtapes().get(idx+1).getMatrice();
        if(suivante.isMinimale()) {
	        JOptionPane.showMessageDialog(vue, "Forme finale : " + suivante.get(0).getNom());
		} else {
            modele.setEtapeActuelle(idx+1);
            vue.setMatrice(suivante);
        }
        if(!vue.getPrecedente().isEnabled()) {
            vue.getPrecedente().setEnabled(true);
        }
        vue.getMain().getFrame().updateFrame();
    }

    public void copierPressed() {
        Matrice m = modele.getEtapes().getLast().getMatrice();
        String texte = m.get(0).getNom();
        StringSelection stringSelection = new StringSelection(texte);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }
}
