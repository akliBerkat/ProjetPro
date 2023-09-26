package arbre;

public class Arbre {
    private Noeud racine;
    
    public Arbre() {
    	this.racine = null;
    }

    public Arbre(Noeud n) {
        this.racine = n;
    }

    public Noeud getRacine() {
        return this.racine;
    }

    public void affiche() {
        System.out.println(racine.print(0));
    }

    public String getArbreSchema() {
        return racine.print(0);
    }

}
