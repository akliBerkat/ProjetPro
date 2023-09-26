package upgma;

import java.util.LinkedList;

import arbre.Arbre;
import arbre.Noeud;
import especes.Espece;
import especes.GestionnaireEspeces;

public class Upgma {
    private GestionnaireEspeces gestionnaire;
    // private LinkedList<Espece> ajouts; // nouvelles especes créées
    private LinkedList<Etape> etapes;
    // index de la matrice affichée dans la gui dans la linkedlist etapes
    private int etapeActuelle; 


    public Upgma(GestionnaireEspeces ge) {
        gestionnaire = ge;
        etapes = new LinkedList<>();
        initialisation();
        execution();
        etapeActuelle = 0;
    }

    public GestionnaireEspeces getGestionnaire() {
        return gestionnaire;
    }

    public LinkedList<Etape> getEtapes() {
        return etapes;
    }

    public int getEtapeActuelle() {
        return etapeActuelle;
    }

    public void setEtapeActuelle(int e) {
        etapeActuelle = e;
    }



    public void initialisation() {
        Matrice mTmp = new Matrice(gestionnaire);
        Etape eTmp = new Etape(gestionnaire, mTmp);
        etapes.add(eTmp);
    }

    public void execution() {
        Etape etapeTmp = etapes.getLast();
        LinkedList<Espece> oldListeEspece = null, nouvListeEspece = null;
        LinkedList<Arbre> oldArbres = null, arbresTmp = null;
        Matrice matriceTmp = null;
        while(true) {
            // on copie l'anciene matrice
            matriceTmp = Matrice.copie(etapeTmp.getMatrice());
            // si fin de l'algo, on sort de la boucle
            if(matriceTmp.size() == 1) break;
            // on recupère les infos de l'étape précédente
            oldListeEspece = matriceTmp.getEspeces();
            oldArbres = etapeTmp.getRacines();
            // on calcule la nouvelle matrice
            int[] min = matriceTmp.Minimum();
            matriceTmp.actualiseTabEspeces(min);
            // on récupère l'indice de la nvelle espèce dans la
            // liste d'espèce de la nouvelle matrice
            nouvListeEspece = matriceTmp.getEspeces();
            int idxNouvEspece = getIndexNouvEspece(oldListeEspece, nouvListeEspece);
            // on crée le nouvel arbre
            arbresTmp = actualiserRacines(oldArbres, nouvListeEspece, idxNouvEspece);
            // et la nouvelle étape
            etapeTmp = new Etape(arbresTmp, matriceTmp);
            etapes.addLast(etapeTmp);
        }
    }

    // on cherche les 2 espèces qui ont été "rassemblées" par la matrice
    // pour les rassembler dans la liste d'arbres
    @SuppressWarnings ("unchecked")
    public LinkedList<Arbre> actualiserRacines(LinkedList<Arbre> oldRacines, LinkedList<Espece> newEspeces, int idxNouv) {
        // oldRacines = "arbre" asocicé à la matrice d'avant
        // newEspeces = espèces de la matrice venant d'être calculée
        // idxNouv = indice de la nouvelle "espece" créée par la matrice dans newEspeces
        LinkedList<Arbre> res = (LinkedList<Arbre>) oldRacines.clone();
        Noeud n1 = null, n2 = null;
        Noeud tmp = null;
        for(Arbre a : oldRacines) {
            tmp = a.getRacine();
            if(newEspeces.indexOf(tmp.getNoyau()) == -1) {
                if (n1 == null) n1 = tmp;
                else if (n2 == null) n2 = tmp;
                res.remove(a);
            }
        }
        Arbre a = new Arbre(new Noeud(newEspeces.get(idxNouv), n1, n2));
        res.addLast(a);
        return res;
    }

    private int getIndexNouvEspece(LinkedList<Espece> old, LinkedList<Espece> nouv) {
        for(int i=0; i<nouv.size(); i++) {
            if(old.indexOf(nouv.get(i)) == -1) return i;
        }
        return -1;
    }

    public void afficher() {
        for(Etape e : etapes) e.afficher();        
    }
}
