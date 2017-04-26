/*********************************************************************************
 * VARIABLES ET METHODES FOURNIES PAR LA CLASSE UtilLex (cf libclass)            *
 *       compl�ment � l'ANALYSEUR LEXICAL produit par ANTLR                      *
 *                                                                               *
 *                                                                               *
 *   nom du programme compil�, sans suffixe : String UtilLex.nomSource           *
 *   ------------------------                                                    *
 *                                                                               *
 *   attributs lexicaux (selon items figurant dans la grammaire):                *
 *   ------------------                                                          *
 *     int UtilLex.valNb = valeur du dernier nombre entier lu (item nbentier)    *
 *     int UtilLex.numId = code du dernier identificateur lu (item ident)        *
 *                                                                               *
 *                                                                               *
 *   m�thodes utiles :                                                           *
 *   ---------------                                                             *
 *     void UtilLex.messErr(String m)  affichage de m et arr�t compilation       *
 *     String UtilLex.repId(int nId) d�livre l'ident de codage nId               *
 *     void afftabSymb()  affiche la table des symboles                          *
 *********************************************************************************/


import java.io.*;

// classe de mise en oeuvre du compilateur
// =======================================
// (v�rifications s�mantiques + production du code objet)

public class PtGen {
    

    // constantes manipul�es par le compilateur
    // ----------------------------------------

	private static final int 
	
	// taille max de la table des symboles
	MAXSYMB=300,

	// codes MAPILE :
	RESERVER=1,EMPILER=2,CONTENUG=3,AFFECTERG=4,OU=5,ET=6,NON=7,INF=8,
	INFEG=9,SUP=10,SUPEG=11,EG=12,DIFF=13,ADD=14,SOUS=15,MUL=16,DIV=17,
	BSIFAUX=18,BINCOND=19,LIRENT=20,LIREBOOL=21,ECRENT=22,ECRBOOL=23,
	ARRET=24,EMPILERADG=25,EMPILERADL=26,CONTENUL=27,AFFECTERL=28,
	APPEL=29,RETOUR=30,

	// codes des valeurs vrai/faux
	VRAI=1, FAUX=0,

    // types permis :
	ENT=1,BOOL=2,NEUTRE=3,

	// cat�gories possibles des identificateurs :
	CONSTANTE=1,VARGLOBALE=2,VARLOCALE=3,PARAMFIXE=4,PARAMMOD=5,PROC=6,
	DEF=7,REF=8,PRIVEE=9,

    //valeurs possible du vecteur de translation 
    TRANSDON=1,TRANSCODE=2,REFEXT=3;


    // utilitaires de contr�le de type
    // -------------------------------
    
	private static void verifEnt() {
		if (tCour != ENT)
			UtilLex.messErr("expression enti�re attendue");
	}

	private static void verifBool() {
		if (tCour != BOOL)
			UtilLex.messErr("expression bool�enne attendue");
	}

    // pile pour g�rer les cha�nes de reprise et les branchements en avant
    // -------------------------------------------------------------------

    private static TPileRep pileRep;  


    // production du code objet en m�moire
    // -----------------------------------

    private static ProgObjet po;
    
    
    // COMPILATION SEPAREE 
    // -------------------
    //
    // modification du vecteur de translation associ� au code produit 
    // + incr�mentation attribut nbTransExt du descripteur
    // NB: effectu� uniquement si c'est une r�f�rence externe ou si on compile un module
    private static void modifVecteurTrans(int valeur) {
		if (valeur == REFEXT || desc.getUnite().equals("module")) {
			po.vecteurTrans(valeur);
			desc.incrNbTansExt();
		}
	}
    
    // descripteur associ� � un programme objet
    private static Descripteur desc;

     
    // autres variables fournies
    // -------------------------
    public static String trinome="Erwan IQUEL"; // MERCI de renseigner ici un nom pour le trinome, constitu� de exclusivement de lettres
    
    private static int tCour; // type de l'expression compil�e
    private static int vCour; // valeur de l'expression compil�e le cas echeant
  
   
    // D�finition de la table des symboles
    //
    private static EltTabSymb[] tabSymb = new EltTabSymb[MAXSYMB + 1];
    
    // it = indice de remplissage de tabSymb
    // bc = bloc courant (=1 si le bloc courant est le programme principal)
	private static int it, bc;
	
	//Variable complémentaire
	private static int nombreDeVariableGlobale;
	private static int adresseIdentAffectation;
	
	// utilitaire de recherche de l'ident courant (ayant pour code UtilLex.numId) dans tabSymb
	// rend en r�sultat l'indice de cet ident dans tabSymb (O si absence)
	private static int presentIdent(int binf) {
		int i = it;
		while (i >= binf && tabSymb[i].code != UtilLex.numId)
			i--;
		if (i >= binf)
			return i;
		else
			return 0;
	}

	// utilitaire de placement des caract�ristiques d'un nouvel ident dans tabSymb
	//
	private static void placeIdent(int c, int cat, int t, int v) {
		if (it == MAXSYMB)
			UtilLex.messErr("d�bordement de la table des symboles");
		it = it + 1;
		tabSymb[it] = new EltTabSymb(c, cat, t, v);
	}

	// utilitaire d'affichage de la table des symboles
	//
	private static void afftabSymb() { 
		System.out.println("       code           categorie      type    info");
		System.out.println("      |--------------|--------------|-------|----");
		for (int i = 1; i <= it; i++) {
			if (i == bc) {
				System.out.print("bc=");
				Ecriture.ecrireInt(i, 3);
			} else if (i == it) {
				System.out.print("it=");
				Ecriture.ecrireInt(i, 3);
			} else
				Ecriture.ecrireInt(i, 6);
			if (tabSymb[i] == null)
				System.out.println(" r�f�rence NULL");
			else
				System.out.println(" " + tabSymb[i]);
		}
		System.out.println();
	}
    

	// initialisations A COMPLETER SI BESOIN
	// -------------------------------------

	public static void initialisations() {
	
		// indices de gestion de la table des symboles
		it = 0;
		bc = 1;
		
		// pile des reprises pour compilation des branchements en avant
		pileRep = new TPileRep(); 
		// programme objet = code Mapile de l'unit� en cours de compilation
		po = new ProgObjet();
		// COMPILATION SEPAREE: desripteur de l'unit� en cours de compilation
		desc = new Descripteur();
		
		// initialisation n�cessaire aux attributs lexicaux (quand enchainement de compilations)
		//UtilLex.initialisation();
	
		// initialisation du type de l'expression courante
		tCour = NEUTRE;
		
		//Initialisation des variable personnels
		nombreDeVariableGlobale = 0;
		adresseIdentAffectation = 0;
	} // initialisations

	// code des points de g�n�ration A COMPLETER
	// -----------------------------------------
	public static void pt(int numGen) {
	
		switch (numGen) {
			case 0:
				initialisations();
			break;
	
			// A COMPLETER
			
			
			/*
			 * Declaration, Affectation, Lecture et Ecriture -> 1-20
			 * Opérations -> 21-40
			 * Conditionnel if..then..else.. -> 41-50
			 * Boucle while..do..done -> 51-60
			 * Conditionnel switch -> 61-70
			 */
			
			
			
			/*
			 * Declarations des variables, constantes, ... / Affectations de valeur / Lecture et Ecriture
			 */
			case 1://Compteurs de variables et ajout de variables dans la table des symboles
				int adresseVariableGlobale = presentIdent(1);
				
				if(adresseVariableGlobale == 0) {
					placeIdent(UtilLex.numId, VARGLOBALE, tCour, nombreDeVariableGlobale);
					nombreDeVariableGlobale++;
				} else {
					UtilLex.messErr("Variable déjà déclarée");
				}
			break;
			
			case 2://Declaration de l'instructon pour réserver de la place en pile pour les vars
				if(nombreDeVariableGlobale > 0) {
					po.produire(RESERVER);
					po.produire(nombreDeVariableGlobale);
				}
			break;
			
			case 3://Ajout des constantes dans la table des symboles
				int adresseConstante = presentIdent(1);
				
				if(adresseConstante == 0) {
					placeIdent(UtilLex.numId, CONSTANTE, tCour, vCour);
				} else {
					UtilLex.messErr("Constante déjà déclarée");
				}
			break;
			
			case 4://Déclaration du type entier
				tCour = ENT;
			break;
			
			case 5://Déclaration du type booleen
				tCour = BOOL;
			break;
			
			case 6://Initialisation de la valeur pour un entier positif
				vCour = UtilLex.valNb;
			break;
			
			case 7://Initialisation de la valeur pour un Entier négatif
				vCour = -UtilLex.valNb;
			break;
			
			case 8://Initialisation de la valeur à vrai
				vCour = VRAI;
			break;
			
			case 9://Initialisation de la valeur à faux
				vCour = FAUX;
			break;
			
			case 10://Initialisation des variables pour gérer l'affection de valeur
				adresseIdentAffectation = presentIdent(1);
				
				if(adresseIdentAffectation != 0) {
					if(tabSymb[adresseIdentAffectation].categorie == VARGLOBALE) {
						tCour = tabSymb[adresseIdentAffectation].type;
					} else {
						UtilLex.messErr("Catégorie non autorisé pour l'affectation");
					}
				} else {
					UtilLex.messErr("Affectation impossible car identifiant inexistant");
				}
			break;
			
			case 11://Affectation de valeur
				if(tabSymb[adresseIdentAffectation].categorie == VARGLOBALE) {
					if(tabSymb[adresseIdentAffectation].type == tCour) {
						po.produire(AFFECTERG);
						po.produire(tabSymb[adresseIdentAffectation].info);
					} else {
						UtilLex.messErr("Type incompatible pour l'affectation");
					}
				} else {
					UtilLex.messErr("Catégorie non autorisé pour l'affectation");
				}
			break;
			
			case 12:
				adresseIdentAffectation = presentIdent(1);
				
				if(adresseIdentAffectation > 0) {
					if(tabSymb[adresseIdentAffectation].type == ENT) {
						tCour = ENT;
						po.produire(LIRENT);
					} else if(tabSymb[adresseIdentAffectation].type == BOOL) {
						tCour = BOOL;
						po.produire(LIREBOOL);
					} else {
						UtilLex.messErr("Type incompatible pour une lecture");
					}
				} else {
					UtilLex.messErr("Variable non présente dans la table des symboles");
				}
			break;
			
			case 13:
				if(tCour == ENT) {
					po.produire(ECRENT);
				} else if(tCour == BOOL) {
					po.produire(ECRBOOL);
				} else {
					UtilLex.messErr("Type incompatible pour une écriture");
				}
			break;
			
			/*
			 * Opération entière et booléenne
			 */
			//Production des opérateurs
			case 21:
				po.produire(OU);
			break;
			
			case 22:
				po.produire(ET);
			break;
			
			case 23:
				po.produire(NON);
			break;
				
			case 24:
				tCour = BOOL;
				po.produire(EG);
			break;
			
			case 25:
				tCour = BOOL;
				po.produire(DIFF);
			break;
			
			case 26:
				tCour = BOOL;
				po.produire(SUP);
			break;
			
			case 27:
				tCour = BOOL;
				po.produire(SUPEG);
			break;
			
			case 28:
				tCour = BOOL;
				po.produire(INF);
			break;
			
			case 29:
				tCour = BOOL;
				po.produire(INFEG);
			break;
			
			case 30:
				po.produire(ADD);
			break;
			
			case 31:
				po.produire(SOUS);
			break;
			
			case 32:
				po.produire(MUL);
			break;
			
			case 33:
				po.produire(DIV);
			break;
			
			case 34://Gestion des valeurs lors d'opérations
				po.produire(EMPILER);
				po.produire(vCour);
			break;
			
			case 35://Gestion des constantes et des variables lors d'opérations
				int adresseIdentATraiter = presentIdent(1);
				
				if(adresseIdentATraiter != 0) {
					if(tabSymb[adresseIdentATraiter].categorie == CONSTANTE) {
						tCour = tabSymb[adresseIdentATraiter].type;
						vCour = tabSymb[adresseIdentATraiter].info;
						
						po.produire(EMPILER);
						po.produire(vCour);
					} else if(tabSymb[adresseIdentATraiter].categorie == VARGLOBALE) {
						tCour = tabSymb[adresseIdentATraiter].type;
						vCour = tabSymb[adresseIdentATraiter].info;
						
						po.produire(CONTENUG);
						po.produire(tabSymb[adresseIdentATraiter].info);
					} else {
						UtilLex.messErr("Categorie de symbole impossible à affecter");
					}
				} else {
					UtilLex.messErr("Opérattion impossible: variable ou constante inexistante");
				}
			break;
			
			case 36://Verification type entier
				verifEnt();
			break;
			
			case 37://Verification type booleen
				verifBool();
			break;
			
			/*
			 * Condition de type if... then... [else...] endif
			 */
			case 41://Branchement si la condition est fausse
				po.produire(BSIFAUX);
				po.produire(0);
				pileRep.empiler(po.getIpo());
			break;
			
			case 42://Branchement avec un sinon + résolution du premier bsifaux
				po.produire(BINCOND);
				po.produire(0);
				po.modifier(pileRep.depiler(), po.getIpo()+1);
				pileRep.empiler(po.getIpo());
			break;
			
			case 43://Résolution du branchement restant restant
				po.modifier(pileRep.depiler(), po.getIpo()+1);
			break;
			
			case 51:
				
			break;
			
			case 255:
				po.produire(ARRET);
				po.constObj();
				po.constGen();
				afftabSymb();
			break;
			
			default:
				System.out.println("Point de génération non prévu dans votre liste");
			break;

		}
	}
}