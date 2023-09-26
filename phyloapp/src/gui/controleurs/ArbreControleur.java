package gui.controleurs;

import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;

import upgma.Upgma;
import arbre.Arbre;

public class ArbreControleur {
    private Arbre modele;

    public ArbreControleur(Upgma m) {
        this.modele = m.getEtapes().getLast().getRacines().getLast();
    }

    public void copierPressed() {
        String texte = modele.getArbreSchema();
        StringSelection stringSelection = new StringSelection(texte);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }
}
