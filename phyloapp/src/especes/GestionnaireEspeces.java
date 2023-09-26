package especes;

import java.util.LinkedList;

import bdd.DataBase;

public class GestionnaireEspeces { // classe qui regroupe toutes les espèces
    private LinkedList<Espece> local; // ensemble des espèces pré-définies localement
    private LinkedList<Espece> bdd; // ensemble des espèces de la database
    private LinkedList<Espece> selection; // ensemble des espèces de l'algorithme
    private LinkedList<Espece> ajouts;

    public GestionnaireEspeces() {
        // creer toutes les espèces une par une
    	this.local = new LinkedList<Espece>();
    	Espece Bronteroc = new Espece("Bronteroc", 14, true);
    	this.local.add(Bronteroc);
    	Espece Totoro = new Espece("Totoro", 12, true);
    	this.local.add(Totoro);
    	Espece Marsupilami= new Espece("Marsupilami", 7, true);
    	this.local.add(Marsupilami);
    	Espece Hippogriffe = new Espece("Hyppogriffe", 15, true);
    	this.local.add(Hippogriffe);
    	Espece Chewbacca = new Espece("Chewbacca", 25, true);
    	this.local.add(Chewbacca);
        this.bdd = new LinkedList<>();
        this.ajouts = new LinkedList<>();
        this.selection = new LinkedList<>();
    }
    
    public GestionnaireEspeces(DataBase db) {
    	this(); // especes locales
        this.bdd = db.getListEspeces();
    }

    public GestionnaireEspeces(GestionnaireEspeces ge) {
        this.local = ge.getLocal();
        this.bdd = ge.getBDD();
        this.ajouts = ge.getAjouts();
    }

    public void setAjouts(LinkedList<Espece> l) {
        this.ajouts = l;
    }

    public void setSelection(LinkedList<Espece> l) {
        this.selection = l;
    }

    public LinkedList<Espece> getLocal() {
        return local;
    }

    public LinkedList<Espece> getBDD() {
        return bdd;
    }

    public LinkedList<Espece> getAjouts() {
        return ajouts;
    }

    public LinkedList<Espece> getSelection() {
        return selection;
    }

    public LinkedList<Espece> getLocalBDD() {
        LinkedList<Espece> res = new LinkedList<>();
        res.addAll(local);
        res.addAll(bdd);
        return res;
    }

    public LinkedList<Espece> getAllEspeces() {
        LinkedList<Espece> res = new LinkedList<>();
        res.addAll(local);
        res.addAll(bdd);
        res.addAll(ajouts);
        return res;
    }

    public boolean isAjouts() {
        return ajouts.size() > 0;
    }
    
}