package gui;

import javax.swing.SwingUtilities;

import bdd.DataBase;
import especes.GestionnaireEspeces;

public class Lanceur {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			DataBase bdd = new DataBase();
			GestionnaireEspeces ge = new GestionnaireEspeces(bdd);
			Window acc = new Window(ge, bdd);	
			acc.setVisible(true);
			acc.pack();
		});
	}
}
