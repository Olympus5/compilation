import java.io.*;
import java.util.HashMap;


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

	//Declaration d'un tableau contenant le nom des unités compilées
	private static String[] nomUnites = new String[MAXMOD+1];
	
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
		
		for(int i = 0; i < 60 && ret == -1 && dicoDef[i] != null; i++) {
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
		
		nomUnites[nMod] = new String(s);
		
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
				
				nomUnites[nMod] = new String(s);
				
				//Initialisation de transDon et transCode pour les modules
				transDon[nMod] = tabDesc[nMod-1].getTailleGlobaux() + transDon[nMod-1];
				transCode[nMod] = tabDesc[nMod-1].getTailleCode() + transCode[nMod-1];
				for(int i = nbDef, j = 0, c = tabDesc[nMod].getNbDef() + nbDef; i < c; i++, j++) {
					if(estDansDico(tabDesc[nMod].getDefNomProc(j+1)) == -1) {
						System.out.println(nbDef);
						dicoDef[i] = tabDesc[nMod].new EltDef(tabDesc[nMod].getDefNomProc(j+1), tabDesc[nMod].getDefAdPo(j+1), tabDesc[nMod].getDefNbParam(j+1));
						nbDef++;
					} else {
						erreur(NONFATALE, "definition: "+tabDesc[nMod].getDefNomProc(j+1)+" en doublon");
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
		
		if (f2 == null) {
			erreur(FATALE, "cr�ation du fichier " + nomProg + ".map impossible");
		}
		
		// pour construire le code concat�n� de toutes les unit�s
		int[] po = new int[(nMod + 1) * MAXOBJ + 1];
// 
// ... A COMPLETER ...
//
		
		for (int i = 0; i <= nMod; i++) {

			// Ouvre le fichier
			InputStream uniteCourant = Lecture.ouvrir(nomUnites[i] + ".obj");

			// Si probleme lors de l'ouverture du fichier
			if (uniteCourant == null) erreur(FATALE, "Fichier " + nomUnites[i] + ".obj illisible");


			// ######################### Recuperation des vecteurs de translation #########################
			// Creer un nouveau tableau associatif de transitions
			HashMap<Integer, Integer> tabTrans = new HashMap<Integer, Integer>();

			// Variables pour la lecture des transitions
			int adresse;
			int type_trans;

			// Recupere toutes les transitions
			for (int t = 0; t < tabDesc[i].getNbTransExt(); t++) {

				// Recupere chaque couples
				adresse = Lecture.lireInt(uniteCourant) + transCode[i];
				type_trans = Lecture.lireIntln(uniteCourant);

				// Ajoute le couple
				tabTrans.put(adresse, type_trans);
			}


			// ######################### Recuperation du code mapile brut #########################
			// Valeurs temporaire
			int ref_ext = 1, derniere_instruction = tabDesc[i].getTailleCode();
			Integer res_get;

			// Ne prends pas la toute derniere instruction (vide)
			if (i == nMod) derniere_instruction = tabDesc[i].getTailleCode()-1;

			// Recupere et met dans po tout le code brut
			for (int p = 1; p <= derniere_instruction; p++) {

				// Recupere le code
				po[ipo] = Lecture.lireIntln(uniteCourant);

				// Si contenu dans le vecteur de translation
				res_get = tabTrans.get(ipo);
				if (res_get != null) {

					// En fonction du type de transition
					switch (res_get.intValue()) {

						case TRANSDON:
							po[ipo] += transDon[i];
							break;

						case TRANSCODE:
							po[ipo] += transCode[i];
							break;

						case REFEXT:
							po[ipo] = adFinale[i][ref_ext];
							ref_ext++;
							break;
					}
				}

				// Incremente ipo
				ipo++;
			}  // For de la recuperation des transExt

			// Ferme le fichier
			Lecture.fermer(uniteCourant);

		}  // For du parcours des unites

		// Met a jour le nombre de variables globales a reserver
		po[2] = transDon[nMod] + tabDesc[nMod].getTailleGlobaux();

		// Ecrit po dans le fichier au nom du programme
		for (int i = 1; i <= ipo; i++) Ecriture.ecrireStringln(f2, "" + po[i]);
		
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
		
		System.out.println(dicoDef[2]);
		for(int i = 0; i < dicoDef.length && dicoDef[i] != null; i++) {
			System.out.println("nom: "+dicoDef[i].nomProc+" adPo: "+dicoDef[i].adPo+" nbParam: "+dicoDef[i].nbParam);
		}
		System.out.println();
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
