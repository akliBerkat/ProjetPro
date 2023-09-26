package gui.vues;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import especes.Espece;
import gui.Window;
import gui.controleurs.AccueilControleur;
import gui.modeles.SelectionModele;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class AccueilPanel extends JPanel {
    private Window frame;
    private AccueilControleur controleur;
    private SelectionModele modele;
    private JPanel titreP, texte1P, texte2P, selecteurP, ajouterP, reinitialiserP, boutons, texte3P, especesP, commencerP;
    private JLabel titre, texte1, texte2, texte3, especes;
    private JButton ajouter, commencer, reinitialiser;  
    private JScrollPane scrollPane;
    private AjouterDialog ajouterDialog;

    public AccueilPanel(Window f, SelectionModele m) {
        this.frame = f;
        this.modele = m;
        this.controleur = new AccueilControleur(modele, this);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBorder(new EmptyBorder(30, 30, 30, 30));
        ajouterDialog = new AjouterDialog(modele, this);
        
        //******************************* TOP
        
        titre = new JLabel("phyloApp");
        titre.setFont(new Font("Arial", Font.BOLD, 50));
        titre.setForeground(new Color(11, 119, 89));
        titreP = new JPanel();
        titreP.add(titre);
        
        //******************************* CENTER
        
        texte1 = new JLabel("Sélectionnez les espèces à inclure :");
        texte1.setFont(new Font("Arial", 1, 30));
        texte1P = new JPanel();
        texte1P.setPreferredSize(new Dimension(texte1.getWidth(), texte1.getHeight()));
        texte1P.add(texte1);
        texte2 = new JLabel("min : 3, max : 8");
        texte2.setFont(new Font("Arial", 2, 24));
        texte2P = new JPanel();
        texte2P.setPreferredSize(new Dimension(texte2.getWidth(), texte2.getHeight()));
        texte2P.add(texte2);
        selecteurP = new JPanel();
        selecteurP.setLayout(new FlowLayout(1, 20, 10));
        selecteurP.setPreferredSize(new Dimension(700, 175));
        scrollPane = new JScrollPane(selecteurP, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(selecteurP.getPreferredSize());
        ajouter = new JButton("Ajouter une nouvelle espèce");
        ajouter.setPreferredSize(new Dimension(260, 40));
        ajouter.addActionListener((event) -> {
            JButton bouton = (JButton) event.getSource();
            if(bouton.isEnabled()) ajouterDialog.setVisible(true);
        });
        ajouterP = new JPanel();
        ajouterP.add(ajouter);
        reinitialiser = new JButton("Réinitialiser la base de donnée");
        reinitialiser.addActionListener((event) -> {
            JButton bouton = (JButton)event.getSource();
            if(bouton.isEnabled()) controleur.reinitialiserPressed();
        });
        reinitialiserP = new JPanel();
        reinitialiserP.add(reinitialiser);
        JPanel vide = new JPanel();
        vide.setPreferredSize(new Dimension(reinitialiserP.getPreferredSize().width,10));
        boutons = new JPanel(new BorderLayout());
        boutons.setMaximumSize(new Dimension(2000, 200));
        boutons.setBorder(new EmptyBorder(20, 0, 20, 0));
        boutons.add(vide, BorderLayout.WEST);
        boutons.add(ajouterP, BorderLayout.CENTER);
        boutons.add(reinitialiserP, BorderLayout.EAST);
        texte3 = new JLabel("Vous avez sélectionné :");
        texte3.setFont(new Font("Arial", 1, 30));
        texte3P = new JPanel();
        texte3P.setPreferredSize(new Dimension(texte3.getWidth(), texte3.getHeight()+20));
        texte3P.add(texte3);
        especes = new JLabel(controleur.getSelectionText());
        especes.setFont(new Font("Arial", 2, 20));
        especesP = new JPanel();
        especesP.setPreferredSize(new Dimension(especes.getWidth(), especes.getHeight()));
        remplirSelecteur();
        especesP.add(especes);
        
        //******************************* BOTTOM
        
        commencer = new JButton("Commencer");
        commencer.setPreferredSize(new Dimension(300, 50));
        commencer.setFont(new Font(Font.DIALOG, Font.BOLD, 25));
        commencer.addActionListener((event) -> {
            JButton bouton = (JButton)event.getSource();
            if(bouton.isEnabled())  {
                controleur.commencerPressed();
            }
        });
        controleur.verifierNombreItems(); // pour initialiser le bouton
        commencerP = new JPanel();
        commencerP.add(commencer);
        

        this.add(titreP);
        this.add(texte1P);
        this.add(texte2P);
        this.add(scrollPane);
        this.add(boutons);
        this.add(texte3P);
        this.add(especesP);
        this.add(commencerP);
    }

    public JLabel getEspeces() {
        return especes;
    }

    public JButton getCommencer() {
        return commencer;
    }

    public SelectionModele getModele() {
        return modele;
    }

    public Window getFrame() {
        return frame;
    }

    public AjouterDialog getAjouterDialog() {
        return ajouterDialog;
    }

    public void remplirSelecteur() {
        if(selecteurP.getComponentCount() != 0) selecteurP.removeAll();
        EspeceItem tmp = null;
        for(Espece e : modele.getGestionnaire().getAllEspeces()) {
            if(modele.getSelection().contains(e)) {
                tmp = new EspeceItem(e, true);
                tmp.setSelected(true);
            } else 
                tmp = new EspeceItem(e);
            selecteurP.add(tmp);
        }
        int newW = selecteurP.getComponentCount() 
                    * (selecteurP.getComponent(0).getPreferredSize().width + 20);
        int newH = selecteurP.getPreferredSize().height;
        selecteurP.setPreferredSize(new Dimension(newW, newH));
    }

    class EspeceItem extends JPanel {
        private boolean isSelected;
        private Espece espece;
        private JPanel imageP, nomP, supprimerP, bottom;
        private JLabel image, nom, supprimer;

        public EspeceItem(Espece e) {
            this.isSelected = false;
            this.espece = e;
            this.setLayout(new BorderLayout());
            this.setPreferredSize(new Dimension(150, 181));
            this.setToolTipText("séquence : " + espece.getSecGen());
            
            image = new JLabel();
            
            if(e.getPath() != null) setImage(e); 
            else setImage(); 
            
            imageP = new JPanel();
            imageP.setLayout(new FlowLayout(0, 0, 0));
            imageP.add(image);
            
            nom = new JLabel(espece.getNom());
            nomP = new JPanel();
            nomP.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 3));
            nomP.add(nom);
            setBottom(espece.getSupprimable());
            
            this.add(imageP, BorderLayout.CENTER);
            this.add(bottom, BorderLayout.SOUTH);
            this.addMouseListener(new MouseListener() {

                @Override
                public void mouseClicked(MouseEvent event) {
                    if(event.getButton() == MouseEvent.BUTTON1) {
                        isSelected = !isSelected;
                        controleur.itemClicked(espece, isSelected);
                        setSelected(isSelected);
                        frame.updateFrame();
                        if(isSelected)
                            System.out.println(espece.getNom() + " sélectionné.");
                        else
                            System.out.println(espece.getNom() + " désélectionné.");
                    }
                }

                @Override
                public void mouseEntered(MouseEvent event) {
                    EspeceItem.this.setCursor(new Cursor(Cursor.HAND_CURSOR));
                }

                @Override
                public void mouseExited(MouseEvent arg0) {}

                @Override
                public void mousePressed(MouseEvent arg0) {}

                @Override
                public void mouseReleased(MouseEvent arg0) {}
                
            });
        }

        public EspeceItem(Espece e, boolean selected) {
            this(e);
            isSelected = true;
        }

        private void setBottom(boolean supprimable) {
            if(supprimable) {
                supprimer = new JLabel("x");
                supprimerP = new JPanel();
                supprimerP.setBackground(Color.red);

                supprimerP.addMouseListener(new MouseListener() {

                    @Override
                    public void mouseClicked(MouseEvent event) {
                        if(event.getButton() == MouseEvent.BUTTON1) {
                            controleur.supprimerPressed(espece);
                        }
                    }
        
                    @Override
                    public void mouseEntered(MouseEvent event) {
                        EspeceItem.this.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    }
        
                    @Override
                    public void mouseExited(MouseEvent arg0) {}
        
                    @Override
                    public void mousePressed(MouseEvent arg0) {}
        
                    @Override
                    public void mouseReleased(MouseEvent arg0) {}
                    
                });

                supprimerP.add(supprimer);
                bottom = new JPanel();
                bottom.setLayout(new BorderLayout());
                bottom.add(nomP, BorderLayout.CENTER);
                bottom.add(supprimerP, BorderLayout.EAST);
            } else {
                bottom = new JPanel();
                bottom.setLayout(new BorderLayout());
                bottom.add(nomP, BorderLayout.CENTER);
            }
        }

        private void setImage() {
            String path = "src/gui/vues/ressources/default.png";
            try {
                BufferedImage raw = ImageIO.read(new File(path));
                image.setIcon(new ImageIcon(raw));
            } catch (IOException e) {
                System.out.println("Impossible de trouver l'image");
                e.printStackTrace();
            }
        }
        
        private void setImage(Espece esp) {
        	String path = esp.getPath();
        	try {
                BufferedImage raw = ImageIO.read(new File(path));
                image.setIcon(new ImageIcon(raw));
            } catch (IOException e) {
                System.out.println("Impossible de trouver l'image");
                e.printStackTrace();
            }
        }

        public void setSelected(boolean selected) {
            if(selected) {
                this.setBorder(BorderFactory.createDashedBorder(Color.DARK_GRAY, 1, 5, 5, false));
            } else {
                this.setBorder(null);
            }
        }
    }
}