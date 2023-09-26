package gui.vues;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.*;

import arbre.Arbre;
import arbre.Noeud;
import gui.controleurs.ArbreControleur;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ArbrePanel extends JPanel {
    private MainPanel main;
    private ArbreControleur controleur;
    private JTree tree;
    private JScrollPane scrollPane;
    private JPanel top, bottom;
    private JLabel logo, titre;
    private JButton copier;

    public ArbrePanel(MainPanel m) {
        main = m;
        controleur = new ArbreControleur(main.getModele());
        this.setLayout(new BorderLayout());
        this.setMaximumSize(new Dimension(350, 500));
        this.setBorder(BorderFactory.createEtchedBorder());
        
        //******************************* TOP
        
        logo = new JLabel();
        iniLogo();
        titre = new JLabel("ARBRE");
        titre.setFont(new Font("Helvetica", 0, 35));
        top = new JPanel();
        top.add(logo);
        top.add(titre);

        //******************************* CENTER
        
        tree = new JTree(creerTree());
        tree.setOpaque(false);
        tree.setFont(new Font("Arial", 0, 20));
        tree.setPreferredSize(new Dimension(300, 75));
        tree.addTreeExpansionListener(new TreeExpansionListener() {

            @Override
            public void treeExpanded(TreeExpansionEvent event) {
                resizeTree(true);
            }

            @Override
            public void treeCollapsed(TreeExpansionEvent event) {
                resizeTree(false);                
            }

        });
        scrollPane = new JScrollPane(tree, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(300, 10));
        scrollPane.setBorder(null);

        //******************************* BOTTOM
        
        copier = new JButton("Copier le schéma de l'arbre final");
        copier.addActionListener((event) -> {
            JButton bouton = (JButton)event.getSource();
            if(bouton.isEnabled()) {
                controleur.copierPressed();
            }
        });
        bottom = new JPanel();
        bottom.add(copier);

        this.add(top, BorderLayout.NORTH);
        this.add(scrollPane, BorderLayout.CENTER);
        this.add(bottom, BorderLayout.SOUTH);
    }

    private void iniLogo() {
        try {
            BufferedImage raw;
            raw = ImageIO.read(new File("src/gui/vues/ressources/arbre.png"));
            Image dimg = raw.getScaledInstance(35, 35, Image.SCALE_SMOOTH);
            logo.setIcon(new ImageIcon(dimg));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private DefaultMutableTreeNode creerTree() {
        Arbre a = main.getModele().getEtapes().getLast().getRacines().getFirst();
        DefaultMutableTreeNode racine = new DefaultMutableTreeNode(a.getRacine());
        creerTreeRec(racine);
        return racine;
    }

    private void creerTreeRec(DefaultMutableTreeNode parent) {
        Noeud element = (Noeud)parent.getUserObject();
        DefaultMutableTreeNode tmp = null;
        if(element.getFilsG() != null) {
            tmp = new DefaultMutableTreeNode(element.getFilsG());
            parent.add(tmp);
            creerTreeRec(tmp);
        }
        if(element.getFilsD() != null) {
            tmp = new DefaultMutableTreeNode(element.getFilsD());
            parent.add(tmp);
            creerTreeRec(tmp);
        }
    }

    private void resizeTree(boolean expansion) {
        int height = tree.getPreferredSize().height;
        int width = tree.getPreferredSize().width;
        if(expansion) height += 50;
        else height -= 50;
        tree.setPreferredSize(new Dimension(width, height));
    }
}
