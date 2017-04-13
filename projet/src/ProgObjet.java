import java.io.*;

// Classe de définition du programme objet en mémoire
// et du vecteur de translation associé (pour la compilation séparée)

public class ProgObjet {

	// mémorisation du code objet produit dans un tableau po
	private static final int MAXOBJ = 1000;
	private int[] po = new int[MAXOBJ + 1]; 
	private int ipo; // indice de remplissage

	// COMPILATION SEPAREE: mémorisation des translations à effectuer 
	private int[] vTrans = new int[MAXOBJ + 1];
	
	// constructeur
	public ProgObjet(){
		ipo = 0;
		initvTrans();
	}

	// méthode permettant d'ajouter un code Mapile ou une valeur 
	// à la fin du code produit
	//
	public void produire(int codeOuArg) {
		if (ipo == MAXOBJ)
			UtilLex.messErr("débordement : programme objet trop long");
		ipo = ipo + 1;
		po[ipo] = codeOuArg;
	}

	// méthode permettant de modifier un code Mapile ou une valeur 
	// dans le code déjà produit (à l'indice i)
	//
	public void modifier(int i, int codeOuArg) {
		if (i > ipo)
			UtilLex.messErr("programme objet non defini a l'indice " + i);
		po[i] = codeOuArg;
	}
	
	// méthode d'accès à l'indice du dernier code produit
	public int getIpo() {
		return ipo;
	}

	// méthode d'accès au code produit à l'indice i
	public int getElt(int i) {
		return po[i];
	}
	

	// construction du fichier objet pour MAPILE
	// -----------------------------------------
	public void constObj() {
		OutputStream f = Ecriture.ouvrir(UtilLex.nomSource + ".obj");
		if (f == null) {
			System.out.println("impossible de créer " + UtilLex.nomSource
					+ ".obj");
			System.exit(1);
		}
		for (int i = 1; i <= ipo; i++)
			if (vTrans[i] != -1)
				Ecriture.ecrireStringln(f, i + "   " + vTrans[i]);
		for (int i = 1; i <= ipo; i++)
			Ecriture.ecrireStringln(f, "" + po[i]);
		Ecriture.fermer(f);
	}

	// construction du fichier objet sous forme mnémonique
	// ---------------------------------------------------
	public void constGen() {
		Mnemo.creerFichier(ipo, po, UtilLex.nomSource + ".gen"); 
	}

	// Gestion du vecteur de translation associé au programme objet
	// ------------------------------------------------------------
		
	// méthode d'initialisation du vecteur de translation
	public void initvTrans() {
		for (int i = 1; i <= MAXOBJ; i++)
			vTrans[i] = -1;
	}

	// ajout d'un doublet au vecteur de translation
	public void vecteurTrans(int x) { 
		vTrans[ipo] = x;
	}

}