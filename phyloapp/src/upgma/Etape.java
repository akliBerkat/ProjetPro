package upgma;

import java.util.LinkedList;

import arbre.Arbre;
import arbre.Noeud;
import especes.Espece;
import especes.GestionnaireEspeces;

public class Etape {
    // on compte à partir de 1
	private static int etapesCount = 1;
	private int etapeNum;
    private LinkedList<Arbre> racines;
    private Matrice matrice;
    
    public Etape(LinkedList<Arbre> r, Matrice m) {
        racines = r;
        matrice = m;
        etapeNum = etapesCount;
        etapesCount++;
    }

    public Etape(GestionnaireEspeces e, Matrice m) {
        //racines = creerListeArbres(e.getEspeces());
        racines = creerListeArbres(e.getSelection());
        matrice = m;
    }

    private LinkedList<Arbre> creerListeArbres(LinkedList<Espece> liste) {
        LinkedList<Arbre> res = new LinkedList<>();
        for(Espece e : liste) {
            res.add(new Arbre(new Noeud(e)));
        }
        return res;
    }

    public LinkedList<Arbre> getRacines() {
        return racines;
    }

    public Matrice getMatrice() {
        return matrice;
    }

    public int getEtapeNum() {
        return etapeNum;
    }

    public void afficher() {
        System.out.println("- Matrice -\n");
        matrice.afficher();
        System.out.println();
        System.out.println("- Arbre -\n");
        for(Arbre a : racines) a.affiche();
    }
}
