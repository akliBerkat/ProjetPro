package especes;

import bdd.*;

public class Espece {
	private static int cptId = 0;//alloue des identifiants aux espéces et sert aussi de compteur d'espéces créées
	private int id;
	private String nom; //le nom de l'espéce
	private String abbr; //abbr�viation
	private boolean estFeuille = true; //si l'esp�ce est un anc�tre commun ou une feuille
	private double SecGen; //la séquence du géne 
	private String lienPhoto;
	static public final String zooPath = "src/gui/vues/ressources/zoo/";
	private boolean supprimable = false;
	
	//-----------------------------------------------------
	//pour la base de données
	public Espece() {
		// constructeur vide pour que la methode .toObject foctionne dans dataBase.java !ne pas modifier ou supprimer!	
	}
	// pour les espèces feuilles
	public Espece(String nom, double sequence) {
		this.nom = nom;
		this.abbr = nom.substring(0,1).toUpperCase();
		this.SecGen = sequence;
		cptId ++;
		this.id = cptId;
	}
	
	//ajoute image esp�ce
	public Espece(String nom, double sequence, boolean bool) {
		this(nom, sequence);
		if(bool) this.lienPhoto = zooPath + nom.toLowerCase() + ".jpg";
	}
	
	// création d'une espéce avec accés base de données
	public Espece(String nom, double sequence, DataBase dbb) {
		this.nom = nom;
		this.abbr = nom.substring(0,1).toUpperCase();
		this.SecGen = sequence;
		dbb.incEspeceId();
		this.id = getActualcptId(dbb);
	}
	
	// pour les ancètres communs
	public Espece(String nom, String abbr, double sequence) {
		this(nom, sequence);
		this.abbr = abbr;
		this.estFeuille = false;
	}

	// pour les ajouts, si ce constructeur est appelé,
	// l'espèce est forcement supprimable
	public Espece(String nom, double sequence, int estSupprimable) {
		this(nom, sequence);
		supprimable = true;
	}
	
	public String getNom() {
		return this.nom;
	}
	
	public String getAbbr() {
		return this.abbr;
	}
	
	public double getSecGen() {
		return this.SecGen;
	}
	
	public int getId() {
		return this.id;
	}
	
	public String getPath() {
		return this.lienPhoto;
	}

	public boolean getSupprimable() {
		return supprimable;
	}
	
	public void setSupprimable(boolean b) {
		this.supprimable = b;
	}

	public String displayName() {
		if(estFeuille) return nom;
		else return abbr;
	}

	public boolean estFeuille() {
		return estFeuille;
	}

	public void setEstFeuille(boolean b) {
		estFeuille = b;
	}

	public String toString() {
		return displayName();
	}
	
	public static int getActualcptId(DataBase dbb) {
		return dbb.getStaticFields().getStaticId();
	}

	// 2 espèces sont égales si elle portent le même nom
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Espece)) return false;
		Espece tmp = (Espece)o;
		if(this.nom.equals(tmp.nom)) return true;
		return false;
	}
	
	
}
