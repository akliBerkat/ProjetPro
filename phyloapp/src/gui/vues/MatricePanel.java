package gui.vues;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.*;

import gui.controleurs.MatriceControleur;
import upgma.Matrice;

public class MatricePanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final float C_MAX = 25F;
    private MainPanel main;
    private MatriceControleur controleur;
    private JPanel top, matricePanel, bottom;
    private JLabel titre, logo;
    private JButton precedente, reduire, copier;

    public MatricePanel(MainPanel m, boolean fullScreen) {
        this.main = m;
        this.controleur = new MatriceControleur(this, main.getModele());
        this.setLayout(new BorderLayout());
        
        //******************************* TOP
        
        logo = new JLabel();
        iniLogo();
        titre = new JLabel("MATRICE");
        titre.setFont(new Font("Helvetica", 0, 35));
        top = new JPanel();
		top.add(logo);
        top.add(titre);
        
        //******************************* CENTER
        
        matricePanel = new JPanel(new GridLayout());
        setMatrice(main.getModele().getEtapes().getFirst().getMatrice());
        
        //******************************* BOTTOM
        
        precedente = new JButton("Précédente");
        precedente.addActionListener((event) -> {
            JButton bouton = (JButton)event.getSource();
            if(bouton.isEnabled()) controleur.precedentePressed();
		});
        precedente.setEnabled(false);
        reduire = new JButton("Réduire");
        reduire.addActionListener((event) -> {
            JButton bouton = (JButton)event.getSource();
            if(bouton.isEnabled()) controleur.reduirePressed();
		});
        copier = new JButton("Copier la forme finale");
        copier.addActionListener((event) -> {
            JButton bouton = (JButton)event.getSource();
            if(bouton.isEnabled()) controleur.copierPressed();
        });
        bottom = new JPanel();
        bottom.add(precedente);
        bottom.add(reduire);
        bottom.add(copier);
        this.add(top, BorderLayout.NORTH);
        this.add(matricePanel, BorderLayout.CENTER);
        this.add(bottom, BorderLayout.SOUTH);
    }

    public MainPanel getMain() {
        return main;
    }

    public JPanel getMatricePanel() {
        return matricePanel;
    }
    
    public void setMatricePanel(JPanel panel) {
        matricePanel = panel;
    }

    public JButton getPrecedente() {
        return precedente;
    }

    public JButton getReduire() {
        return reduire;
    }

    private void iniLogo() {
        try {
            BufferedImage raw;
            raw = ImageIO.read(new File("src/gui/vues/ressources/matrice.png"));
            Image dimg = raw.getScaledInstance(35, 35, Image.SCALE_SMOOTH);
            logo.setIcon(new ImageIcon(dimg));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clearMatrice(int dimension) {
		matricePanel.removeAll();
		matricePanel.setLayout(new GridLayout(dimension+1, dimension+1, 10, 10));
        main.getFrame().updateFrame();
	}

    public void setMatrice(Matrice m) {
		int[] minimum = m.Minimum();
		clearMatrice(m.size());
		for(int i=0; i<m.size()+1; i++) {
			for(int j=0; j<m.size()+1; j++) {
				if(i == 0 && j == 0) matricePanel.add(new JLabel());
				else if(j == 0) {
					JLabel l = new JLabel(m.get(i-1).displayName().toUpperCase());
					l.setToolTipText(m.get(i-1).getNom());
					l.setOpaque(true);
				    l.setHorizontalAlignment(SwingConstants.CENTER);
				    l.setFont(new Font("Arial", Font.PLAIN, 12));
					matricePanel.add(l);
				}
				else if (i == 0) {
					JLabel l = new JLabel(m.get(j-1).displayName().toUpperCase());
					l.setToolTipText(m.get(j-1).getNom());
					l.setOpaque(true);
				    l.setHorizontalAlignment(SwingConstants.CENTER);
				    l.setFont(new Font("Arial", Font.PLAIN, 12));
					matricePanel.add(l);
				}
				else {
					int x = i-1;
					int y = j-1;
					JLabel l = new JLabel(Double.toString(m.get(x,y)));
					if(Arrays.equals(new int[]{x,y}, minimum) || Arrays.equals(new int[]{y,x}, minimum)){
						l.setForeground(Color.RED);
					}
					l.setOpaque(true);
					l.setBackground(getColor(m.get(x,y)));
				    l.setFont(new Font("Rockwell", Font.BOLD, 25));
				    l.setHorizontalAlignment(SwingConstants.CENTER);
					matricePanel.add(l);
				}
			}
		}
	}

    private Color getColor(double val) {
		float v = (float)val;
		if(val > C_MAX) v = C_MAX;
		v = Math.abs(C_MAX-v);
		v = v/(C_MAX*(10F/7F)); //valeur minimum: 0.3 ; maximum = 1
		v += 0.3;
		return new Color(v, v, v);
	}
}
