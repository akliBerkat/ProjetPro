package bdd;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import especes.Espece;

public class DataBase {
	public DataBase() { //initialisation de la base de données et association avec le programme
		FileInputStream serviceAccount = null;
		try {
			serviceAccount = new FileInputStream("./serviceAccount.json");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		FirebaseOptions options = null;
		try {
			options = new FirebaseOptions.Builder()
					.setCredentials(GoogleCredentials.fromStream(serviceAccount))
					.build();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (options != null) {
			FirebaseApp.initializeApp(options);
		}
	}
//----------------------------------------------------------------------------------------------------------
public void initStaticFields(StaticFields stat) { //met l'objet staticfield dans la bdd
	Firestore db = FirestoreClient.getFirestore();
	ApiFuture<com.google.cloud.firestore.WriteResult> future = db.collection("DATA").document("StaticFields").set(stat);
	try {
		System.out.println("Successfully updated at: " + future.get().getUpdateTime());
	} catch (InterruptedException | ExecutionException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}


public StaticFields getStaticFields() {  //recupére l'objet static field de la bdd sinon retourne un objet null
	Firestore db = FirestoreClient.getFirestore();
	DocumentReference docRef = db.collection("DATA").document("StaticFields");
	ApiFuture<DocumentSnapshot> future = docRef.get();
	DocumentSnapshot doc = null;
	try {
		doc = future.get();
	} catch (InterruptedException | ExecutionException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	StaticFields stat = null;
	if (doc != null) {
		return doc.toObject(StaticFields.class);
	} 
	return stat;
}

public void incEspeceId()  { // incrémente staticId si l'objet existe
	StaticFields stat = null;
	stat = getStaticFields();
	if (stat != null) {
		stat.incStaticId();
		initStaticFields(stat);
	}
}

public void incNbrEspece() {
	StaticFields stat = null;
	stat = getStaticFields();
	if (stat != null) {
		stat.incNbrEspeces();
		initStaticFields(stat);
	}
}

public void decNbrEspece() {
	StaticFields stat = null;
	stat = getStaticFields();
	if (stat != null) {
		stat.decNbrEspeces();
		initStaticFields(stat);
	}
}

//---------------------------------------------------------------------------------

public void addEspece(Espece e){ //ajoute un objet de type Espece à la base de données
	Firestore db = FirestoreClient.getFirestore();
	int  Id = getStaticFields().getStaticId();
	ApiFuture<com.google.cloud.firestore.WriteResult> future =db.collection("Especes").document(Integer.toString(Id)).set(e);
	try {
		System.out.println("Successfully updated at: " + future.get().getUpdateTime());
		System.out.println("add Espece");
		incNbrEspece();
		incEspeceId();
	} catch (InterruptedException | ExecutionException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
		
	}
}

public void rmvEspece(int s){
	Firestore db = FirestoreClient.getFirestore();
	ApiFuture<com.google.cloud.firestore.WriteResult> future =db.collection("Especes").document(Integer.toString(s)).delete();
	decNbrEspece();
}

public void addListEspeces(LinkedList<Espece> e) {
	for (Espece esp :e) {
		this.addEspece(esp);
	}
}

public void rmvListEspeces() {
	int i = 0;
	while(i <= getStaticFields().getStaticId()) {
		rmvEspece(i);
		i ++;
	}
}

public Espece getEspece(String nom) {
	Firestore db = FirestoreClient.getFirestore();
	DocumentReference docRef = db.collection("Especes").document(nom);
	ApiFuture<DocumentSnapshot> future = docRef.get();
	DocumentSnapshot doc = null;
	try {
		doc = future.get();
	} catch (InterruptedException | ExecutionException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		
	 
	Espece esp = new Espece();
	if (doc != null) {
		return doc.toObject(Espece.class);
	} 
	return esp;
}

public LinkedList<Espece> getListEspeces() {
	Firestore db = FirestoreClient.getFirestore();
	int i = 0;
	LinkedList<Espece> especes = new LinkedList<Espece>();
	try {
		while(i <= getStaticFields().getStaticId()) {
			DocumentReference docRef = db.collection("Especes").document(Integer.toString(i));
			ApiFuture<DocumentSnapshot> future = docRef.get();
			DocumentSnapshot doc = future.get();
			if (doc.exists()) {
				especes.add(doc.toObject(Espece.class));
			} 
			i ++;
		}
	} catch (InterruptedException | ExecutionException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return especes;
}



}
