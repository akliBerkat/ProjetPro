package arbre;

import especes.Espece;

public class Noeud {
    private Espece noyau;
    private String etiquette;
    private Noeud filsG, filsD;

    public Noeud(Espece e, Noeud g, Noeud d) {
        filsG = g;
        filsD = d;
        noyau = e;
        etiquette = noyau.getAbbr();
    }

    public Noeud(Espece e) {
        this(e, null, null);
    }

    public Espece getNoyau() {
        return noyau;
    }

    public Noeud getFilsG() {
        return filsG;
    }

    public Noeud getFilsD() {
        return filsD;
    }

    public String getEtiquette() {
        return etiquette;
    }

    public String print(int niv) {
        String res = " ".repeat(niv*3);
        if(niv != 0) res += "|__";
        res += this.toString() + "\n";
        if(filsG != null) res += this.filsG.print(niv+1);
        if(filsD != null) res += this.filsD.print(niv+1);
        return res;
    }

    public String toString() {
        return noyau.displayName();
    }
}
