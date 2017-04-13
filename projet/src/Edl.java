import java.io.*;


public class Edl {
	
	// nombre max de modules, taille max d'un code objet d'une unit�
	static final int MAXMOD = 5, MAXOBJ = 1000;
	// nombres max de r�f�rences externes (REF) et de points d'entr�e (DEF)
	// pour une unit�
	private static final int MAXREF = 10, MAXDEF = 10;
	
	// typologie des erreurs
	private static final int FATALE = 0, NONFATALE = 1;
	
	// valeurs possibles du vecteur de translation
	private static final int TRANSDON=1,TRANSCODE=2,REFEXT=3;
	
	// table de tous les descripteurs concernes par l'edl
	static Descripteur[] tabDesc = new Descripteur[MAXMOD + 1];
	
	// declarations de variables A COMPLETER SI BESOIN
	static int ipo, nMod, nbErr;
	static String nomProg;

	// utilitaire de traitement des erreurs
	// ------------------------------------
	static void erreur(int te, String m) {
		System.out.println(m);
		if (te == FATALE) {
			System.out.println("ABANDON DE L'EDITION DE LIENS");
			System.exit(1);
		}
		nbErr = nbErr + 1;
	}

	// utilitaire de remplissage de la table des descripteurs tabDesc
	// --------------------------------------------------------------
	static void lireDescripteurs() {
		String s;
		System.out.println("les noms doivent etre fournis sans suffixe");
		System.out.print("nom du programme : ");
		s = Lecture.lireString();
		tabDesc[0] = new Descripteur();
		tabDesc[0].lireDesc(s);
		if (!tabDesc[0].getUnite().equals("programme"))
			erreur(FATALE, "programme attendu");
		nomProg = s;
		
		nMod = 0;
		while (!s.equals("") && nMod < MAXMOD) {
			System.out.print("nom de module " + (nMod + 1)
					+ " (RC si termine) ");
			s = Lecture.lireString();
			if (!s.equals("")) {
				nMod = nMod + 1;
				tabDesc[nMod] = new Descripteur();
				tabDesc[nMod].lireDesc(s);
				if (!tabDesc[nMod].getUnite().equals("module"))
					erreur(FATALE, "module attendu");
			}
		}
	}

	
	static void constMap() {
		// f2 = fichier ex�cutable .map construit
		OutputStream f2 = Ecriture.ouvrir(nomProg + ".map");
		if (f2 == null)
			erreur(FATALE, "cr�ation du fichier " + nomProg
					+ ".map impossible");
		// pour construire le code concat�n� de toutes les unit�s
		int[] po = new int[(nMod + 1) * MAXOBJ + 1];
// 
// ... A COMPLETER ...
//
		Ecriture.fermer(f2);
		// cr�ation du fichier en mn�monique correspondant
		Mnemo.creerFichier(ipo, po, nomProg + ".ima");
	}

	public static void main(String argv[]) {
		System.out.println("EDITEUR DE LIENS / PROJET LICENCE");
		System.out.println("---------------------------------");
		System.out.println("");
		nbErr = 0;
		
		// Phase 1 de l'�dition de liens
		// -----------------------------
		lireDescripteurs();		// lecture des descripteurs a completer si besoin
// 
// ... A COMPLETER ...
//
		if (nbErr > 0) {
			System.out.println("programme ex�cutable non produit");
			System.exit(1);
		}
		
		// Phase 2 de l'�dition de liens
		// -----------------------------
		constMap();				// a completer
		System.out.println("Edition de liens terminee");
	}
}