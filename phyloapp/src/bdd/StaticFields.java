package bdd;

public class StaticFields {
	private int staticId;
	private int nbrEspeces;
	
	public StaticFields() {
		// à laisser vide pour l'instanciation
		this.staticId = 0;
		this.nbrEspeces = 0;
	}
	
	public StaticFields(int id) {
		this.staticId = id;
	}
	
	public int getStaticId() {
		return this.staticId;
	}
	
	public int getNbrEspeces() {
		return nbrEspeces;
	}
	
	public void incStaticId() {
		this.staticId ++;
		System.out.println("up");
		}
	
	public void decNbrEspeces() {
		if (this.nbrEspeces > 0) {
		this.nbrEspeces --;}
	}
	
	public void incNbrEspeces() {
		this.nbrEspeces ++;
	}
}
