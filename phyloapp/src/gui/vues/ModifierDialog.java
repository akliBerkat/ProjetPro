package gui.vues;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.swing.*;

import especes.Espece;
import gui.controleurs.ModifierControleur;
import gui.modeles.SelectionModele;

public class ModifierDialog extends JDialog {
    private MainPanel main;
    private SelectionModele modele;
    private ModifierControleur controleur;
    private JPanel top, center, centerBottom, especes, bottom, reinitialiserP, actualiserP;
    private JScrollPane scrollPane;
    private JLabel titre;
    private JButton reinitialiser, actualiser;

    public ModifierDialog(MainPanel main) {
        super(main.getFrame());
        this.main = main;
        this.modele = null;
        this.modele = new SelectionModele(main.getModele().getGestionnaire());
        this.controleur = new ModifierControleur(modele, this);
        this.setTitle("Modifier");
        this.setMinimumSize(new Dimension(450,560));
        this.setResizable(false);
        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        this.setAlwaysOnTop(true);

        //******************************* TOP
        
        titre = new JLabel("modifier les espèces");
        titre.setFont(new Font("Arial", 1, 30));
        top = new JPanel();
        top.add(titre);

        //******************************* CENTER
        
        especes = new JPanel();
        especes.setLayout(new FlowLayout(FlowLayout.CENTER, 200, 0));
        especes.setMinimumSize(new Dimension(380, 370));
        especes.setPreferredSize(new Dimension(380, 370));
        scrollPane = new JScrollPane(especes, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(especes.getPreferredSize());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        remplirEspeces();
        reinitialiser = new JButton("Réinitialiser");
        reinitialiserP = new JPanel();
        reinitialiserP.add(reinitialiser);
        centerBottom = new JPanel();
        centerBottom.setLayout(new BoxLayout(centerBottom, BoxLayout.Y_AXIS));
        centerBottom.add(reinitialiserP);
        center = new JPanel();
        center.add(scrollPane);
        center.add(centerBottom);

        //******************************* BOTTOM
        
        actualiser = new JButton("Actualiser");
        actualiserP = new JPanel();
        actualiserP.add(actualiser);
        bottom = new JPanel();
        bottom.add(actualiserP);
        iniBoutons();

        this.getContentPane().add(top, BorderLayout.NORTH);
        this.getContentPane().add(center, BorderLayout.CENTER);
        this.getContentPane().add(bottom, BorderLayout.SOUTH);
    }

    public void remplirEspeces() {
        EspecesItem tmp = null;
        LinkedList<Espece> liste = main.getModele().getGestionnaire().getAllEspeces();
        
        controleur.preRemplirSelection();

        for(Espece e : liste) {
            if(estDansLalgo(e))
                tmp = new EspecesItem(e, true);
            else
                tmp = new EspecesItem(e, false);
            especes.add(tmp);
        }
        resizeEspeces();
    }

    private void resizeEspeces() {
        int w = 380;
        int h = especes.getComponentCount() 
                * especes.getComponent(0).getPreferredSize().height + 30;
        if(h > 370) {
            especes.setPreferredSize(new Dimension(w, h));
        }
    }

    public void reinitialiserEspeces() {
        especes.removeAll();
    }

    private void iniBoutons() {
        reinitialiser.addActionListener((event) -> {
            JButton bouton = (JButton) event.getSource();
            if(bouton.isEnabled()) {
                controleur.reinitialiserPressed();
            }
        });
        actualiser.addActionListener((event) -> {
            JButton bouton = (JButton) event.getSource();
            if(bouton.isEnabled()) {
                controleur.actualiserPressed();
            }
        });
    }

    private boolean estDansLalgo(Espece e) {
        LinkedList<Espece> algo = main.getModele().getGestionnaire().getSelection();
        for(Espece e1 : algo) {
            if(e1.equals(e)) return true;
        }
        return false;
    }

    public MainPanel getMain() {
        return main;
    }

    public JButton getActualiser() {
        return actualiser;
    }

    class EspecesItem extends JPanel {
        private Espece espece;
        private JPanel imageP, nomP;
        private JLabel image, nom;
        private boolean isSelected;

        public EspecesItem(Espece e, boolean selected) {
            this.espece = e;
            this.isSelected = selected;
            this.setMinimumSize(new Dimension(380, 30));
            this.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.black));
            if(selected) setSelected(true);
            image = new JLabel();
            
            if(e.getPath() != null) setImage(e); 
            else setImage(); 
            
            imageP = new JPanel();
            imageP.setBorder(BorderFactory.createEmptyBorder(0,5,0,5));
            imageP.setOpaque(false);

            imageP.add(image);
            nom = new JLabel(espece.getNom());
            nomP = new JPanel();
            nomP.setPreferredSize(new Dimension(280,30));
            nomP.setOpaque(false);
            nomP.add(nom);

            this.addMouseListener(new MouseListener() {

                @Override
                public void mouseClicked(MouseEvent event) {
                    if(event.getButton() == MouseEvent.BUTTON1) {
                        isSelected = !isSelected;
                        controleur.itemClicked(espece, isSelected);
                        setSelected(isSelected);
                    }
                }

                @Override
                public void mouseEntered(MouseEvent event) {
                    EspecesItem.this.setCursor(new Cursor(Cursor.HAND_CURSOR));
                }

                @Override
                public void mouseExited(MouseEvent arg0) {}

                @Override
                public void mousePressed(MouseEvent arg0) {}

                @Override
                public void mouseReleased(MouseEvent arg0) {}
                
            });
            
            this.add(imageP);
            this.add(nomP);
        }

        private void setSelected(boolean selected) {
            if(selected)
                this.setBackground(new Color(200,200, 200, 175));
            else
                this.setBackground(UIManager.getColor("Panel.background"));
        }

        private void setImage() {
            String path = "src/gui/vues/ressources/default.png";
            try {
                BufferedImage raw = ImageIO.read(new File(path));
                Image dimg = raw.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
                image.setIcon(new ImageIcon(dimg));
            } catch (IOException e) {
                System.out.println("Impossible de trouver l'image");
                e.printStackTrace();
            }
        }
        
        private void setImage(Espece esp) {
        	String path = esp.getPath();
        	try {
                BufferedImage raw = ImageIO.read(new File(path));
                Image dimg = raw.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
                image.setIcon(new ImageIcon(dimg));
            } catch (IOException e) {
                System.out.println("Impossible de trouver l'image");
                e.printStackTrace();
            }
        }
    }
}
