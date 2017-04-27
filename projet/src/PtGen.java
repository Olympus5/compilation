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
	private static int nombreDeParametre;
	private static int nombreDeVariableLocale;
	private static int adresseIdentAppel;
	private static int adresseDef;
	
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
		nombreDeParametre = 0;
		nombreDeVariableLocale = 0;
		adresseIdentAppel = 0;
		adresseDef = 0;
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
			 * Procédure -> 71-90
			 * Compilation séparée -> 91-110
			 */
			
			
			
			/*
			 * Declarations des variables, constantes, ... / Affectations de valeur / Lecture et Ecriture
			 */
			case 1://Compteurs de variables et ajout de variables dans la table des symboles
				int adresseVariable;
				
				if(bc <= 1) {//Déclaration de variables globales
					adresseVariable = presentIdent(1);
					
					if(adresseVariable == 0) {
						placeIdent(UtilLex.numId, VARGLOBALE, tCour, nombreDeVariableGlobale);
						nombreDeVariableGlobale++;
					} else {
						UtilLex.messErr("Variable globale déjà déclarée (1)");
					}
				} else {//Déclaration de variables locales
					adresseVariable = presentIdent(bc);
					
					if(adresseVariable == 0) {
						placeIdent(UtilLex.numId, VARLOCALE, tCour, nombreDeParametre+nombreDeVariableLocale+2);//+1 et non + 2 car les adresses commence à zéro
						nombreDeVariableLocale++;
					} else {
						UtilLex.messErr("Variable locale déjà déclarée (1)");
					}
				}
			break;
			
			case 2://Declaration de l'instructon pour réserver de la place en pile pour les vars
				if(nombreDeVariableGlobale > 0) {
					if(bc <= 1 && desc.getUnite() != "module") {
						po.produire(RESERVER);
						po.produire(nombreDeVariableGlobale);
						desc.setTailleGlobaux(nombreDeVariableGlobale);
					}
					else if(bc > 1){
						po.produire(RESERVER);
						po.produire(nombreDeVariableLocale);
					} else {
						desc.setTailleGlobaux(nombreDeVariableGlobale);
					}
				}
			break;
			
			case 3://Ajout des constantes dans la table des symboles
				int adresseConstanteGlobale = presentIdent(1);
				int adresseConstanteLocale = presentIdent(bc);
				
				if(adresseConstanteGlobale == 0 || adresseConstanteLocale == 0) {
					placeIdent(UtilLex.numId, CONSTANTE, tCour, vCour);
				} else {
					UtilLex.messErr("Constante déjà déclarée (3)");
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
					if(tabSymb[adresseIdentAffectation].categorie == VARGLOBALE
					|| tabSymb[adresseIdentAffectation].categorie == VARLOCALE
					|| tabSymb[adresseIdentAffectation].categorie == PARAMMOD) {
						tCour = tabSymb[adresseIdentAffectation].type;
					} else {
						UtilLex.messErr("Catégorie non autorisé pour l'affectation (10)");
					}
				} else {
					UtilLex.messErr("Affectation impossible car identifiant inexistant (10)");
				}
			break;
			
			case 11://Affectation de valeur
				if(tabSymb[adresseIdentAffectation].categorie == VARGLOBALE) {
					if(tabSymb[adresseIdentAffectation].type == tCour) {
						po.produire(AFFECTERG);
						po.produire(tabSymb[adresseIdentAffectation].info);
						
						if(desc.getUnite() == "module") {
							modifVecteurTrans(TRANSDON);
						}
					} else {
						UtilLex.messErr("Type incompatible pour l'affectation de variable globale (11)");
					}
				} else if(tabSymb[adresseIdentAffectation].categorie == VARLOCALE) {
					if(tabSymb[adresseIdentAffectation].type == tCour) {
						po.produire(AFFECTERL);
						po.produire(tabSymb[adresseIdentAffectation].info);
						po.produire(0);
					} else {
						UtilLex.messErr("Type incompatible pour l'affectation de variable local (11)");
					}
				} else if(tabSymb[adresseIdentAffectation].categorie == PARAMMOD) {
					if(tabSymb[adresseIdentAffectation].type == tCour) {
						po.produire(AFFECTERL);
						po.produire(tabSymb[adresseIdentAffectation].info);
						po.produire(0);
					} else {
						UtilLex.messErr("Type incompatible pour l'affectation de paramètre mod (11)");
					}
				} else {
					UtilLex.messErr("Catégorie non autorisé pour l'affectation (11)");
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
						UtilLex.messErr("Type incompatible pour une lecture (12)");
					}
				} else {
					UtilLex.messErr("Variable non présente dans la table des symboles(12)");
				}
			break;
			
			case 13:
				if(tCour == ENT) {
					po.produire(ECRENT);
				} else if(tCour == BOOL) {
					po.produire(ECRBOOL);
				} else {
					UtilLex.messErr("Type incompatible pour une écriture (13)");
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
						po.produire(tabSymb[adresseIdentAffectation].info);
						
						if(desc.getUnite() == "module") {
							modifVecteurTrans(TRANSDON);
						}
					} else if(tabSymb[adresseIdentATraiter].categorie == VARLOCALE
							||tabSymb[adresseIdentATraiter].categorie == PARAMFIXE ){
						tCour = tabSymb[adresseIdentATraiter].type;
						vCour = tabSymb[adresseIdentATraiter].info;
						
						po.produire(CONTENUL);
						po.produire(tabSymb[adresseIdentATraiter].info);
						po.produire(0);
					} else if(tabSymb[adresseIdentATraiter].categorie == PARAMMOD){
						tCour = tabSymb[adresseIdentATraiter].type;
						vCour = tabSymb[adresseIdentATraiter].info;
						
						po.produire(CONTENUL);
						po.produire(tabSymb[adresseIdentATraiter].info);
						po.produire(1);
					} else {
						UtilLex.messErr("Categorie de symbole impossible à affecter (35)");
					}
				} else {
					UtilLex.messErr("Opérattion impossible: variable ou constante inexistante (35)");
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
				
				if(desc.getUnite() == "module") {
					modifVecteurTrans(TRANSCODE);
				}
			break;
			
			case 42://Branchement avec un sinon + résolution du premier bsifaux
				po.produire(BINCOND);
				po.produire(0);
				po.modifier(pileRep.depiler(), po.getIpo()+1);
				pileRep.empiler(po.getIpo());
				
				if(desc.getUnite() == "module") {
					modifVecteurTrans(TRANSCODE);
				}
			break;
			
			case 43://Résolution du branchement restant restant
				po.modifier(pileRep.depiler(), po.getIpo()+1);
			break;
			
			/*
			 * Boucle while... do... done
			 */
			case 51://Enregistrement de l'indice de pile pour pouvoir retourner en début de boucle
				pileRep.empiler(po.getIpo());
			break;
			
			case 52://Production de la condition de boucle
				po.produire(BSIFAUX);
				po.produire(0);
				pileRep.empiler(po.getIpo());
				
				if(desc.getUnite() == "module") {
					modifVecteurTrans(TRANSCODE);
				}
			break;
			
			case 53://Retour en début de boucle et correction des branchements
				po.produire(BINCOND);
				//Positionne le point de branchement du "tant que" quand il est mis à faux
				po.modifier(pileRep.depiler(), po.getIpo()+2);
				po.produire(pileRep.depiler()+1);
				
				if(desc.getUnite() == "module") {
					modifVecteurTrans(TRANSCODE);
				}
			break;
			
			/*
			 * Conditionnel de type switch
			 */
			case 61:
				pileRep.empiler(0);
			break;
			
			case 62://Production BSIFAUX
				po.produire(BSIFAUX);
				po.produire(0);
				pileRep.empiler(po.getIpo());
				
				if(desc.getUnite() == "module") {
					modifVecteurTrans(TRANSCODE);
				}
			break;
			
			case 63://Production BINCOND et résolution BSIFAUX
				po.produire(BINCOND);
				po.modifier(pileRep.depiler(), po.getIpo()+2);
				po.produire(pileRep.depiler());
				pileRep.empiler(po.getIpo());
				
				if(desc.getUnite() == "module") {
					modifVecteurTrans(TRANSCODE);
				}
			break;
			
			case 64://Si une clause AUT
				po.produire(BINCOND);
				po.produire(0);
				po.modifier(pileRep.depiler(), po.getIpo()+1);
				pileRep.empiler(po.getIpo());
				
				if(desc.getUnite() == "module") {
					modifVecteurTrans(TRANSCODE);
				}
			break;
			
			case 65://Résolution des BINCOND et du dernier BSIFAUX
				po.modifier(pileRep.depiler(), po.getIpo()+1);
				int tmp = pileRep.depiler();
				int valeurBINCOND;
				
				while(tmp > 0) {
					valeurBINCOND = po.getElt(tmp);
					po.modifier(tmp, po.getIpo()+1);
					tmp = valeurBINCOND;
				}
				
				po.modifier(tmp, po.getIpo()+1);
			break;
			
			/*
			 * Gestion des procédures (declaration, appel, etc.) 
			 */
			case 71://Pour pouvoir sauter les procédure lors de l'execution du programme
				System.out.println("Test");
				po.produire(BINCOND);
				po.produire(0);
				pileRep.empiler(po.getIpo());
			break;
			
			case 72://Déclaration d'une procédure
				int adresseProcedure = presentIdent(1);
				
				if(adresseProcedure == 0) {
					placeIdent(UtilLex.numId, PROC, NEUTRE, po.getIpo()+1);
		
					adresseDef = desc.presentDef(UtilLex.repId(UtilLex.numId));
					
					if(adresseDef != 0) {
						desc.modifDefAdPo(adresseDef, po.getIpo()+1);
						placeIdent(-1, DEF, NEUTRE, 0);
					} else {
						placeIdent(-1, PRIVEE, NEUTRE, 0);
					}
					
					bc = it + 1;
					nombreDeParametre = 0;
					nombreDeVariableLocale = 0;
				} else {
					UtilLex.messErr("Procédure déjà déclaré (73)");
				}
			break;
			
			case 73://Déclaration des params fixes
				int adresseParamFixe = presentIdent(bc);
				
				if(adresseParamFixe == 0) {
					placeIdent(UtilLex.numId, PARAMFIXE, tCour, nombreDeParametre);
					nombreDeParametre++;
				} else {
					UtilLex.messErr("Paramètre fixe déjà déclaré (74)");
				}
			break;
			
			case 74://Déclarations des params mods
				int adresseParamMod = presentIdent(bc);
				
				if(adresseParamMod == 0) {
					placeIdent(UtilLex.numId, PARAMMOD, tCour, nombreDeParametre);
					nombreDeParametre++;
				} else {
					UtilLex.messErr("Paramètre mod déjà déclaré (75)");
				}
			break;
			
			case 75://Affecte le nombre de paramètres
				tabSymb[bc-1].info = nombreDeParametre;
				
				if(adresseDef != 0) {
					desc.modifDefNbParam(adresseDef, nombreDeParametre);
				}
			break;
			
			case 76://Prépare un appel de proc
				adresseIdentAppel = presentIdent(1);
				
				if(adresseIdentAppel != 0) {
					if(tabSymb[adresseIdentAppel].categorie == PROC || tabSymb[adresseIdentAppel].categorie == REF) {
						tCour = tabSymb[adresseIdentAppel].type;
					} else {
						UtilLex.messErr("Catégorie non autorisé pour un appel de procédure (76)");
					}
				} else {
					UtilLex.messErr("Appel de procédure impossible car identifiant inexistant (76)");
				}
			break;
			
			case 77://Produit un appel de proc
				if(tabSymb[adresseIdentAppel].categorie == PROC || tabSymb[adresseIdentAppel].categorie == REF) {
					po.produire(APPEL);
					po.produire(tabSymb[adresseIdentAppel].info);
					
					if(desc.getUnite() == "module") {
						modifVecteurTrans(TRANSCODE);
					}
					
					adresseDef = desc.presentDef(UtilLex.repId(tabSymb[adresseIdentAppel].code));
					
					if(adresseDef != 0) {
						modifVecteurTrans(REFEXT);
					}
					
					po.produire(tabSymb[adresseIdentAppel+1].info);
				} else {
					UtilLex.messErr("Catégorie non autorisé pour un appel de procédure (77)");
				}
			break;
			
			case 78:
				int adresseParamModATraiter = presentIdent(1);
				
				if(adresseParamModATraiter != 0) {
					if(tabSymb[adresseParamModATraiter].categorie == VARGLOBALE) {
						po.produire(EMPILERADG);
						po.produire(tabSymb[adresseIdentAffectation].info);
						
						if(desc.getUnite() == "module") {
							modifVecteurTrans(TRANSDON);
						}
					} else if(tabSymb[adresseParamModATraiter].categorie == VARLOCALE) {
						po.produire(EMPILERADL);
						po.produire(tabSymb[adresseParamModATraiter].info);
						po.produire(0);
					} else if(tabSymb[adresseParamModATraiter].categorie == PARAMMOD) {
						po.produire(EMPILERADL);
						po.produire(tabSymb[adresseParamModATraiter].info);
						po.produire(1);
					} else {
						UtilLex.messErr("Catégorie non autorisé pour un passage de paramètre mod (78)");
					}
				} else {
					UtilLex.messErr("Identifient inexistant dans la table des symboles (78)");
				}
			break;
			
			case 80:
				po.produire(RETOUR);
				po.produire(tabSymb[bc-1].info);
				
				it = bc + tabSymb[bc-1].info - 1;
				
				for (int i = 0 ; i < tabSymb[bc-1].info ; i++) {
					tabSymb[bc+i].code = -1;
				}
				
				bc = 1;
			break;
			
			case 81://Résolution du saut des procédures
				po.modifier(pileRep.depiler(), po.getIpo()+1);
			break;
			
			/*
			 * Compilation séparée 
			 */
			case 91://Dans un programme
				desc = new Descripteur();
				nombreDeParametre = 0;
				
				desc.setUnite("programme");
			break;
			
			case 92://Dans un module
				desc = new Descripteur();
				nombreDeParametre = 0;
				
				desc.setUnite("module");
			break;
			
			case 93:
				desc.ajoutDef(UtilLex.repId(UtilLex.numId));
			break;
			
			case 94:
				int adresseReference = presentIdent(1);
				
				if(adresseReference == 0) {
					desc.ajoutRef(UtilLex.repId(UtilLex.numId));
					desc.modifRefNbParam(desc.getNbRef(), nombreDeParametre);
					
					placeIdent(UtilLex.numId, REF, NEUTRE, desc.getNbRef());
					placeIdent(-1, PRIVEE, NEUTRE, nombreDeParametre);
					
					nombreDeParametre = 0;
				} else {
					UtilLex.messErr("Procédure déjà présente dans la table des symboles (94)");
				}
			break;
			
			case 95:
				nombreDeParametre++;
			break;
			
			case 255:
				if(desc.getUnite() != "module") po.produire(ARRET);
				desc.setTailleCode(po.getIpo());
				desc.ecrireDesc(UtilLex.nomSource);
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