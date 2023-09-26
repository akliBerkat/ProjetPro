package gui.vues;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import especes.Espece;
import gui.Window;
import upgma.Upgma;

public class MainPanel extends JPanel {
    private Window frame;
    private Upgma modele;
    private ModifierDialog modifierDialog;
    private ArbrePanel arbrePanel;
    private MatricePanel matricePanel;
    private JLabel retour, nom, especesTitre, especesLogo;
    private JPanel top, retourP, nomP, center, centerTop, centerBottom, especes, especesTop, bottom;
    private JScrollPane scrollPane;
    private JButton modifier;

    public MainPanel(Window f, Upgma m) {
        this.frame = f;
        this.modele = m;
        this.setLayout(new BorderLayout());
        
        //******************************* TOP
        
        retour = new JLabel();
        retourP = new JPanel();
        iniRetour();
        retourP.setBorder(BorderFactory.createEmptyBorder(5,30,0,0));
        retourP.setOpaque(false);
        retourP.add(retour);
        nom = new JLabel("phyloApp");
        nom.setFont(new Font("Helvetica", 1, 45));
        nom.setForeground(new Color(11, 119, 89));
        nomP = new JPanel();
        nomP.setBorder(BorderFactory.createEmptyBorder(0,0,0,30));
        nomP.setOpaque(false);
        nomP.add(nom);
        
        top = new JPanel();
        top.setLayout(new BorderLayout());
        top.setBackground(new Color(232, 232, 232));
        top.add(retourP, BorderLayout.WEST);
        top.add(nomP, BorderLayout.EAST);
        
        //******************************* CENTER - TOP
        
        arbrePanel = new ArbrePanel(this);
        matricePanel = new MatricePanel(this, false);
        matricePanel.setPreferredSize(new Dimension(50,50));
        
        centerTop = new JPanel();
        centerTop.setLayout(new BoxLayout(centerTop, BoxLayout.X_AXIS));
        centerTop.add(arbrePanel);
        centerTop.add(matricePanel);
        
        //******************************* CENTER - BOTTOM
        
        especesLogo = new JLabel();
        iniEspecesLogo();
        especesTitre = new JLabel("ESPECES");
        especesTitre.setFont(new Font("Helvetica", 0, 35));
        especesTop = new JPanel();
        especesTop.add(especesLogo);
        especesTop.add(especesTitre);
        especes = new JPanel();
        especes.setLayout(new FlowLayout(1, 20, 10));
        especes.setPreferredSize(new Dimension(1000, 190));
        remplirEspeces();
        scrollPane = new JScrollPane(especes, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(especes.getPreferredSize());
        centerBottom = new JPanel();
        centerBottom.setLayout(new BoxLayout(centerBottom, BoxLayout.Y_AXIS));
        centerBottom.add(especesTop);
        centerBottom.add(scrollPane);

        center = new JPanel();
        center.setBorder(new EmptyBorder(30, 30, 15, 30));
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.add(centerTop);
        center.add(centerBottom);

        //******************************* BOTTOM

        modifierDialog = new ModifierDialog(this);
        modifier = new JButton("Ajouter / Supprimer des espèces");
        modifier.setPreferredSize(new Dimension(400, 40));
        modifier.addActionListener((event) -> {
            JButton bouton = (JButton)event.getSource();
            if(bouton.isEnabled()) {
                modifierDialog.setVisible(true);
            }
        });
        bottom = new JPanel();
        bottom.setBorder(BorderFactory.createEmptyBorder(0,0,20,0));
        bottom.add(modifier);
        
        this.add(top, BorderLayout.NORTH);
        this.add(center, BorderLayout.CENTER);
        this.add(bottom, BorderLayout.SOUTH);
    }

    public Window getFrame() {
        return frame;
    }

    public Upgma getModele() {
        return modele;
    }

    private void iniRetour() {
        String path = "src/gui/vues/ressources/retour.png";
        try {
            BufferedImage raw = ImageIO.read(new File(path));
            Image dimg = raw.getScaledInstance(45, 45, Image.SCALE_SMOOTH);
            retour.setIcon(new ImageIcon(dimg));
        } catch (IOException e) {
            System.out.println("Impossible de trouver l'image");
            e.printStackTrace();
        }
        retourP.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent event) {
                if(event.getButton() == MouseEvent.BUTTON1) {
                    frame.setAccueilPanel(modele.getGestionnaire());
                    frame.updateFrame();
                }
            }

            @Override
            public void mouseEntered(MouseEvent event) {
                retourP.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent arg0) {}

            @Override
            public void mousePressed(MouseEvent arg0) {}

            @Override
            public void mouseReleased(MouseEvent arg0) {}
            
        });
    }

    private void iniEspecesLogo() {
        try {
            BufferedImage raw;
            raw = ImageIO.read(new File("src/gui/vues/ressources/especes.png"));
            Image dimg = raw.getScaledInstance(35, 35, Image.SCALE_SMOOTH);
            especesLogo.setIcon(new ImageIcon(dimg));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void remplirEspeces() {
        for(Espece e : modele.getGestionnaire().getSelection()) {
            especes.add(new EspeceItem(e));
        }
        int newW = especes.getComponentCount() 
                    * (especes.getComponent(0).getPreferredSize().width + 20);
        int newH = especes.getPreferredSize().height;
        especes.setPreferredSize(new Dimension(newW, newH));
    }

    class EspeceItem extends JLayeredPane {
        private Espece espece;
        private JPanel imageP, nomP;
        private JLabel image, nom;

        public EspeceItem(Espece e) {
            this.espece = e;
            this.setLayout(null);
            this.setPreferredSize(new Dimension(150, 181));
            this.setToolTipText("séquence : " + espece.getSecGen());
            image = new JLabel();
            if(e.getPath() != null) setImage(e); 
            else setImage();
            imageP = new JPanel();
            imageP.setLayout(new FlowLayout(0, 0, 0));
            imageP.setBounds(0, 0, 150, 150);
            imageP.add(image);
            nom = new JLabel(" " + espece.getNom() + " ");
            nom.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
            nom.setOpaque(true);
            nomP = new JPanel();
            nomP.setOpaque(false);
            nomP.setBackground(new Color(255, 255, 255, 188));
            nomP.setBounds(0, 120, 150, 30);
            nomP.add(nom);
            this.add(imageP, 0);
            this.add(nomP, JLayeredPane.PALETTE_LAYER);
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
    }
}