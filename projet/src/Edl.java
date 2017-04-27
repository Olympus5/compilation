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

	//Déclaration de transdon, transcode
	private static int[] transDon = new int[MAXMOD+1], transCode = new int[MAXMOD+1];
	
	//Déclaration de dicoDef
	private static Descripteur.EltDef[] dicoDef = new Descripteur.EltDef[60];//J'hésite entre 60 ou 61 car soit il faut commencer à remplir à 0 ou soit à partir de 1
	
	/**
	 * Teste si une proc est présente dans dicoDef
	 * @param nomDef nom de la def à ajouter
	 * @return true si la def est présente dans dicoDef, false sinon
	 */
	private static int estDansDico(String nomDef) {
		int ret = -1;
		
		for(int i = 0; i < 60 && ret == -1; i++) {
			if(nomDef.equals(dicoDef[i].nomProc)) {
				ret = i;
			}
		}
		
		return ret;
	}
	
	//Déclaration de adFinale
	private static int[][] adFinale = new int[MAXMOD+1][10];
	
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
		
		if (!tabDesc[0].getUnite().equals("programme")) {
			erreur(FATALE, "programme attendu");
		}
		
		nomProg = s;
		
		nMod = 0;
		
		//Initialisation de transDon et transCode pour programme
		transDon[0] = 0;
		transCode[0] = 0;
		
		//Initialisation de dicoDef pour programme
		for(int i = 0; i < tabDesc[nMod].getNbDef(); i++) {
			if(estDansDico(tabDesc[nMod].getDefNomProc(i+1)) == -1) {
				dicoDef[i] = tabDesc[nMod].new EltDef(tabDesc[nMod].getDefNomProc(i+1), tabDesc[nMod].getDefAdPo(i+1) + transCode[nMod], tabDesc[nMod].getDefNbParam(i+1));
			} else {
				erreur(NONFATALE, "definition: "+tabDesc[nMod].getDefNomProc(i+1)+" en doublon");
			}
		}
		
		int nbDef = 0;
		
		while (!s.equals("") && nMod < MAXMOD) {
			
			System.out.print("nom de module " + (nMod + 1) + " (RC si termine) ");
			s = Lecture.lireString();
			
			if (!s.equals("")) {
				
				nMod = nMod + 1;
				tabDesc[nMod] = new Descripteur();
				tabDesc[nMod].lireDesc(s);
				
				//Initialisation de transDon et transCode pour les modules
				transDon[nMod] = tabDesc[nMod-1].getTailleGlobaux() + transDon[nMod-1];
				transCode[nMod] = tabDesc[nMod-1].getTailleCode() + transCode[nMod-1];
				
				for(int i = 0; i < tabDesc[nMod].getNbDef(); i++) {
					if(estDansDico(tabDesc[nMod].getDefNomProc(i+1)) == -1) {
						dicoDef[i+nbDef] = tabDesc[nMod].new EltDef(tabDesc[nMod].getDefNomProc(i+1), tabDesc[nMod].getDefAdPo(i+1), tabDesc[nMod].getDefNbParam(i+1));
						nbDef++;
					} else {
						erreur(NONFATALE, "definition: "+tabDesc[nMod].getDefNomProc(i+1)+" en doublon");
					}
				}
				
				if (!tabDesc[nMod].getUnite().equals("module")) {
					erreur(FATALE, "module attendu");
				}
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
		
		//Initialisation de adFinale
		int adresseDico;
		
		for(int i = 0; i < nMod; i++) {
			for(int j = 0; j < tabDesc[i].getNbRef(); j++) {
				adresseDico = estDansDico(tabDesc[i].getRefNomProc(j+1));
				if(adresseDico != -1) {
					adFinale[i][j] = dicoDef[adresseDico].adPo;
				} else {
					erreur(NONFATALE, "ref: "+tabDesc[i].getRefNomProc(j+1)+" non défini dans le dicoDef");
				}
			}
		}
		
		if (nbErr > 0) {
			System.out.println("programme exécutable non produit");
			System.exit(1);
		}
		
		// Phase 2 de l'�dition de liens
		// -----------------------------
		constMap();				// a completer
		System.out.println("Edition de liens terminee");
	}
}
