package gui.vues;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import gui.controleurs.AjouterControleur;
import gui.modeles.SelectionModele;

public class AjouterDialog extends JDialog{
    private AccueilPanel main;
    private SelectionModele modele;
    private AjouterControleur controleur;
    private JLabel image, nom, sequence, notif;
    private JTextField nomTF, sequenceTF; 
    private JPanel imageP, nomP, sequenceP, validerP, center, centerRight, bottom;
    private JButton valider;

    public AjouterDialog(SelectionModele m, AccueilPanel p) {
        this.main = p;
        this.modele = m;
        this.controleur = new AjouterControleur(modele, this);
        this.setTitle("Ajouter une espèce");
        this.setMinimumSize(new Dimension(400, 200));
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());
        this.setAlwaysOnTop(true);
        
        //******************************* RIGHT
        
        image = new JLabel();
        setImage();
        imageP = new JPanel();
        imageP.add(image);
        
        //*******************************  LEFT
        
        nom = new JLabel("Nom :");
        nomTF = new JTextField();
        nomTF.setPreferredSize(new Dimension(120, 22));
        nomP = new JPanel(new FlowLayout(0));
        nomP.add(nom);
        nomP.add(nomTF);
        sequence = new JLabel("Numéro de séquence :");
        sequenceTF = new JTextField();
        sequenceTF.setPreferredSize(new Dimension(30, 22));
        sequenceP = new JPanel(new FlowLayout(0));
        sequenceP.add(sequence);
        sequenceP.add(sequenceTF);
        valider = new JButton("Valider");
        valider.addActionListener((event) -> {
            JButton bouton = (JButton) event.getSource();
            if(bouton.isEnabled()) {
                if(isSaissieValide()) {
                    String nomTexte = nomTF.getText();
                    int seqTexte = Integer.parseInt(sequenceTF.getText());
                    controleur.validerPressed(nomTexte, seqTexte);
                }
            }
        });
        validerP = new JPanel(new FlowLayout(0));
        validerP.add(valider);
        centerRight = new JPanel();
        centerRight.setLayout(new BoxLayout(centerRight, BoxLayout.Y_AXIS));
        centerRight.add(nomP);
        centerRight.add(sequenceP);
        centerRight.add(validerP);
        center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.X_AXIS));
        center.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        center.add(imageP);
        center.add(centerRight);

        notif = new JLabel("Veuillez remplir les champs.");
        bottom = new JPanel();
        bottom.add(notif);
        
        this.add(center, BorderLayout.CENTER);
        this.add(bottom, BorderLayout.SOUTH);
    }

    public AccueilPanel getMain() {
        return main;
    }

    public JLabel getNotif() {
        return notif;
    }

    public JTextField getNomTF() {
        return nomTF;
    }

    public JTextField getSequenceTF() {
        return sequenceTF;
    }

    private void setImage() {
        String path = "src/gui/vues/ressources/default.png";
        try {
            BufferedImage raw = ImageIO.read(new File(path));
            Image dimg = raw.getScaledInstance(85, 85, Image.SCALE_SMOOTH);
            image.setIcon(new ImageIcon(dimg));
        } catch (IOException e) {
            System.out.println("Impossible de trouver l'image");
            e.printStackTrace();
        }
    }

    private boolean isSaissieValide() {
        String nom = nomTF.getText();
        String seq = sequenceTF.getText();
        if(nom.isBlank() || seq.isBlank()) {
            notif.setText("Veuillez remplir tous les champs.");
            notif.setForeground(Color.RED);
            return false;
        }
        if(!isValidNumber(seq)) {
            notif.setText("Veuillez remplir le champs \"séquence\" avec un entier positif.");
            notif.setForeground(Color.RED);
            return false;
        }
        return true;
    }

    private boolean isValidNumber(String s) {
        for(int i=0; i<s.length(); i++) {
            if(!Character.isDigit(s.charAt(i)))
                return false;
        }
        return true;
    }
}
