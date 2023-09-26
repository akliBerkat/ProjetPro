package gui;

import javax.swing.*;

import bdd.DataBase;
import especes.Espece;
import especes.GestionnaireEspeces;
import gui.modeles.SelectionModele;
import gui.vues.AccueilPanel;
import gui.vues.MainPanel;
import upgma.Upgma;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedList;

public class Window extends JFrame {
    private JPanel actualPanel;
    private DataBase bdd;

    public Window(GestionnaireEspeces ge, DataBase db) {
        this.bdd = db;
        this.setTitle("phyloApp");
        this.setMinimumSize(new Dimension(1300,850));
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                LinkedList<Espece> ajouts = null;
                if(actualPanel instanceof MainPanel) ajouts = ((MainPanel) actualPanel).getModele().getGestionnaire().getAjouts();
                if(actualPanel instanceof AccueilPanel) ajouts = ((AccueilPanel) actualPanel).getModele().getGestionnaire().getAjouts();
                if(ajouts != null && ajouts.size() > 0) {
                    int result = JOptionPane.showConfirmDialog(Window.this, "Voulez vous sauvegarder les modifications ?", "", JOptionPane.YES_NO_OPTION);
                    if (result == JOptionPane.YES_OPTION) {
                        for (Espece e : ajouts) e.setSupprimable(false);
                        bdd.addListEspeces(ajouts);
                        if(actualPanel instanceof MainPanel) ajouts = ((MainPanel) actualPanel).getModele().getGestionnaire().getAjouts();
                        if(actualPanel instanceof AccueilPanel) ajouts = ((AccueilPanel) actualPanel).getModele().getGestionnaire().getAjouts();
                    }
                }
                Window.this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            }
        });
        setAccueilPanel(ge);
    }

    public DataBase getBDD() {
        return bdd;
    }

    public void setAccueilPanel(GestionnaireEspeces ge) {
        if(this.getComponentCount() != 0) this.getContentPane().removeAll();
        actualPanel = new AccueilPanel(this, new SelectionModele(ge));
        this.setContentPane(actualPanel);
    }

    public void setMainPanel(GestionnaireEspeces ge) {
        this.getContentPane().removeAll();
        Upgma modele = new Upgma(ge);
        actualPanel = new MainPanel(this, modele);
        this.setContentPane(actualPanel);
        updateFrame();        
    }

    public void updateFrame() {
        revalidate();
        repaint();
    }
}
