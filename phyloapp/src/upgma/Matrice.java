package upgma;

import java.util.LinkedList;

import arbre.Noeud;
import especes.Espece;
import especes.GestionnaireEspeces;

public class Matrice {
    double[][] matrice;
    private LinkedList<Noeud> noeuds;

	/* public Matrice(GestionnaireEspeces gest) {
		this.matrice = new double[gest.getEspeces().size()][gest.getEspeces().size()];
		this.noeuds = setNoeuds(gest.getEspeces());
		remplir();
	}//constructeur() */

	public Matrice(GestionnaireEspeces gest) {
		this.matrice = new double[gest.getSelection().size()][gest.getSelection().size()];
		this.noeuds = setNoeuds(gest.getSelection());
		remplir();
	}//constructeur()

	public Matrice(double[][] d, LinkedList<Espece> e) {
		this.matrice = d;
		this.noeuds = setNoeuds(e);
	}

	public LinkedList<Espece> getEspeces() {
		LinkedList<Espece> especes = new LinkedList<>();
		for(Noeud n : noeuds) especes.add(n.getNoyau());
		return especes;
	}
	
	public boolean isMinimale() {
		if(this.size() <= 1) return true;
		else return false;
	}
	
	public int size() {
		return noeuds.size();
	}
	
	public Espece get(int i) { //get esp�ce
		return noeuds.get(i).getNoyau();
	}
	
	public Noeud getN(int i) { //get noeud
		return noeuds.get(i);
	}
	
	public double get(int x, int y) {
		return matrice[x][y];
	}
	
	public Noeud top() {
		return noeuds.get(0);
	}
	
	public LinkedList<Noeud> setNoeuds(LinkedList<Espece> especes){
		LinkedList<Noeud> noeuds = new LinkedList<>();
		for(int i=0; i<especes.size(); i++) {
			noeuds.add(new Noeud(especes.get(i)));
		}
		return noeuds;
	}
	
	public Noeud getArbre() {
		Matrice m = copie(this);
		while(!m.isMinimale()) m.reduction();
		return m.top();
	}
	
	public void reduction() {
		if(this.isMinimale()) return;
		int[] min = Minimum();
		actualiseTabEspeces(min);
	}
	
	public void actualiseTabEspeces(int[] min) {
		String newNom = "(" + get(min[0]).getNom() + " x " + get(min[1]).getNom() + ")";
		String newAbbr = "(" + get(min[0]).getAbbr() + "-" + get(min[1]).getAbbr() + ")";
		double newSecGen = (get(min[0]).getSecGen() + get(min[1]).getSecGen())/2;
		
		Espece e = new Espece(newNom, newAbbr, newSecGen);

		noeuds.add(new Noeud(e, noeuds.get(min[0]), noeuds.get(min[1])));
		noeuds.remove(min[0]);
		noeuds.remove(min[1]-1);
		
		double[][] newMat = new double[this.matrice.length -1][this.matrice.length - 1];
		
		this.matrice = newMat;
		remplir();
	}
	
	public double calculeDistance(int i, int j) {
		return Math.sqrt((this.get(i).getSecGen()- this.get(j).getSecGen())*(this.get(i).getSecGen()- this.get(j).getSecGen()));
	}
	
	public void remplir() {
		for(int i=0; i < matrice.length ; i++) {
			for(int j=0; j < matrice[i].length ; j++) {
				this.matrice[i][j] = calculeDistance(i, j);
			}//boucle j
		}//boucle i
	}//remplir()
	
	
	public void afficher() { 
		for(int i=0; i < matrice.length ; i++) {
			for(int j=0; j < matrice[i].length ; j++) {
				System.out.print(matrice[i][j]+" ");
			}//boucle j
			System.out.println();
		}//boucle i
	}//afficher()
	
	public int[] Minimum(){
		if(this.isMinimale()) return new int[] {0,0};
		double min = matrice[0][1]; //la distance entre la premiere espéce et la deuxieme
		int[] casemin = {0,1};
		for(int i=0; i < matrice.length ; i++) {
			for(int j=0; j < matrice[i].length ; j++) {
				if(i != j & matrice[i][j] < min) {
					min = matrice[i][j];
					casemin[0] = i;
					casemin[1] = j;
				}//retoune l'indice i et j de la case la plus petite en exluant les éléments de la diagonale
			}//boucle j
		}//boucle i
		return casemin;
	}//Minimum()

	public static Matrice copie(Matrice old) {
		double[][] donnees = old.matrice.clone();
		for (int i = 0; i < donnees.length; i++) {
		    donnees[i] = donnees[i].clone();
		}
		
		LinkedList<Espece> esps = new LinkedList<>();
		for(Noeud n : old.noeuds) esps.add(n.getNoyau());
		return new Matrice(donnees, esps);
	}
}
