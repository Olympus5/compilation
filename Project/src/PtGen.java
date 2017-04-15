/*********************************************************************************
 * VARIABLES ET METHODES FOURNIES PAR LA CLASSE UtilLex (cf libclass)            *
 *       complement a l'ANALYSEUR LEXICAL produit par ANTLR                      *
 *                                                                               *
 *                                                                               *
 *   nom du programme compile, sans suffixe : String UtilLex.nomSource           *
 *   ------------------------                                                    *
 *                                                                               *
 *   attributs lexicaux (selon items figurant dans la grammaire):                *
 *   ------------------                                                          *
 *     int UtilLex.valNb = valeur du dernier nombre entier lu (item nbentier)    *
 *     int UtilLex.numId = code du dernier identificateur lu (item ident)        *
 *                                                                               *
 *                                                                               *
 *   methodes utiles :                                                           *
 *   ---------------                                                             *
 *     void UtilLex.messErr(String m)  affichage de m et arret compilation       *
 *     String UtilLex.repId(int nId) delivre l'ident de codage nId               *
 *     void afftabSymb()  affiche la table des symboles                          *
 *********************************************************************************/


import java.io.*;

// classe de mise en oeuvre du compilateur
// =======================================
// (verifications semantiques + production du code objet)

public class PtGen {


	// constantes manipulees par le compilateur
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

	// categories possibles des identificateurs :
	CONSTANTE=1,VARGLOBALE=2,VARLOCALE=3,PARAMFIXE=4,PARAMMOD=5,PROC=6,
	DEF=7,REF=8,PRIVEE=9,

	//valeurs possible du vecteur de translation
	TRANSDON=1,TRANSCODE=2,REFEXT=3;

	private static int nbDeclarationsVariables;

	private static int nbDeclarationsVariablesGlob;

	private static int nbDeclarationsParamProc;

	private static int adresseIdentConnuAffectation;

	private static int adresseIdentConnuAppelProc;


	// utilitaires de controle de type
	// -------------------------------

	private static void verifEnt() {
		if (tCour != ENT)
			UtilLex.messErr("expression entiere attendue");
	}

	private static void verifBool() {
		if (tCour != BOOL)
			UtilLex.messErr("expression booleenne attendue");
	}

	// pile pour gerer les chaines de reprise et les branchements en avant
	// -------------------------------------------------------------------

	private static TPileRep pileRep; 


	// production du code objet en memoire
	// -----------------------------------

	private static ProgObjet po;


	// COMPILATION SEPAREE
	// -------------------
	//
	// modification du vecteur de translation associe au code produit
	// + incrementation attribut nbTransExt du descripteur
	// NB: effectue uniquement si c'est une reference externe ou si on compile un module
	private static void modifVecteurTrans(int valeur) {
		if (valeur == REFEXT || desc.getUnite().equals("module")) {
			po.vecteurTrans(valeur);
			desc.incrNbTansExt();
		}
	}

	// descripteur associe a un programme objet
	private static Descripteur desc;


	// autres variables fournies
	// -------------------------
	public static String trinome="IQUEL_LEBLANC_ROYET"; // MERCI de renseigner ici un nom pour le trinome, constitue de exclusivement de lettres

	private static int tCour; // type de l'expression compilee
	private static int vCour; // valeur de l'expression compilee le cas echeant
	private static int typeParamAppelProc;

	// Definition des symboles
	//
	private static EltTabSymb[] tabSymb = new EltTabSymb[MAXSYMB + 1];

	// it = indice de remplissage de tabSymb
	// bc = bloc courant (=1 si le bloc courant est le programme principal)
	private static int it, bc;

	// utilitaire de recherche de l'ident courant (ayant pour code UtilLex.numId) dans tabSymb
	// rend en resultat l'indice de cet ident dans tabSymb (O si absence)
	private static int presentIdent(int binf) {
		int i = it;
		while (i >= binf && tabSymb[i].code != UtilLex.numId)
			i--;
		if (i >= binf)
			return i;
		else
			return 0;
	}

	// utilitaire de placement des caracteristiques d'un nouvel ident dans tabSymb
	//
	private static void placeIdent(int c, int cat, int t, int v) {
		if (it == MAXSYMB)
			UtilLex.messErr("debordement de la table des symboles");
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
				System.out.println("reference NULL");
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
		// programme objet = code Mapile de l'unite en cours de compilation
		po = new ProgObjet();
		// COMPILATION SEPAREE: desripteur de l'unite en cours de compilation
		desc = new Descripteur();

		// initialisation necessaire aux attributs lexicaux (quand enchainement de compilations)
		//UtilLex.initialisation();

		// initialisation du type de l'expression courante
		tCour = NEUTRE;

		// initialisation de la valeur de l'expression courante
		vCour = 0;

		nbDeclarationsVariables = 0;

		nbDeclarationsVariablesGlob = 0;

		nbDeclarationsParamProc = 0;

		adresseIdentConnuAffectation = 0;

		adresseIdentConnuAppelProc = 0;

	} // initialisations

	// code des points de generation A COMPLETER
	// -----------------------------------------
	public static void pt(int numGen) {
		switch (numGen) {
		// 01 - 20  -> declarations de tout
		// 21 - 30  -> si
		// 31 - 40  -> ttq
		// 41 - 50  -> cond
		// 51 - 60  -> ecriture et lecture
		// 61 - 80  -> operation
		// 81 - 100 -> affectations etc
		// 100- 110 -> procedures
		// 110- ... -> compilation separee
		case 0:
			initialisations();
			break;
		case 1:
			// Reservations variables
			//bc =< 1 || nbDeclarationsVariables != 0
			if (!(bc > 1 && nbDeclarationsVariables == 0)) {
				po.produire(RESERVER);
				if (bc > 1) po.produire(nbDeclarationsVariables - nbDeclarationsVariablesGlob);
				else po.produire(nbDeclarationsVariables);
			}
			break;
		case 2:
			// Declaration variable
			int adresseIdentDeclarVarGlob = presentIdent(bc);
			if (adresseIdentDeclarVarGlob == 0) {
				if (bc == 1) {
					// VARGLOBALE
					placeIdent(UtilLex.numId, VARGLOBALE, tCour, po.getIpo()+nbDeclarationsVariables);
					/* numId = ADRESSE du dernier ident lu
					 * VARGLOBALE car c'est le cas etudie dans ce point de generation
					 * tCour = sa CATEGORIE (ENT, BOOL, NEUTRE)
					 * la valeur de l'ident dans le champ INFO
					 */
					nbDeclarationsVariables++;
					nbDeclarationsVariablesGlob++;
				} else {
					// VARLOCALE
					placeIdent(UtilLex.numId, VARLOCALE, tCour, nbDeclarationsVariables+2);
					/* numId = ADRESSE du dernier ident lu
					 * VARGLOBALE car c'est le cas etudie dans ce point de generation
					 * tCour = sa CATEGORIE (ENT, BOOL, NEUTRE)
					 * la valeur de l'ident dans le champ INFO
					 */
					nbDeclarationsVariables++;
				}
			} else {
				UtilLex.messErr("Ident deja declare");
			}
			break;
		case 3:
			// Declaration CONSTANTE
			int adresseIdentDeclarConst = presentIdent(bc);
			if (adresseIdentDeclarConst == 0) {
				placeIdent(UtilLex.numId, CONSTANTE, tCour, vCour);
				/* numId = ADRESSE du dernier ident lu
				 * CONSTANTE car c'est le cas etudie dans ce point de generation
				 * tCour = sa CATEGORIE (ENT, BOOL, NEUTRE)
				 * la valeur de l'ident dans le champ INFO
				 */
			} else {
				UtilLex.messErr("Ident deja declare");
			}
			break;
		case 4:
			tCour = ENT;
			break;
		case 5:
			tCour = BOOL;
			break;
		case 21:
			// Gestion du SI
			// avec empilement de l'adresse de po
			po.produire(BSIFAUX);
			po.produire(0);
			pileRep.empiler(po.getIpo());
			break;
		case 22:
			// Gestion du SINON
			// resolution du BSIFAUX
			// un BINCOND genere avec 0
			po.produire(BINCOND);
			po.produire(0);
			po.modifier(pileRep.depiler(), po.getIpo()+1);
			pileRep.empiler(po.getIpo());
			break;
		case 23:
			// Gestion du FSI
			// Resolution du BSIFAUX ou du BINCOND
			po.modifier(pileRep.depiler(), po.getIpo()+1);
			break;
		case 31:
			// Gestion du TTQ part.1
			pileRep.empiler(po.getIpo());
			break;
		case 32:
			// Gestion du TTQ part.2
			// avec empilement de l'adresse de po
			po.produire(BSIFAUX);
			po.produire(0);
			pileRep.empiler(po.getIpo());
			break;
		case 33:
			// Gestion du TTQ part.3
			// resolution du BSIFAUX
			// un BINCOND relie a l'expression du TQQ
			po.produire(BINCOND);
			po.modifier(pileRep.depiler(), po.getIpo()+2);
			po.produire(pileRep.depiler()+1);
			break;
		case 34:
			// Gestion du COND part.1
			// chaine de reprise
			pileRep.empiler(0);
			break;
		case 35:
			// Gestion du COND part.2
			// Double points
			po.produire(BSIFAUX);
			po.produire(0);
			pileRep.empiler(po.getIpo());
			break;
		case 36:
			// Gestion du COND part.3
			// Virgule
			// resolution du dernier BSIFAUX rencontre
			// BINCOND vers la fin
			po.produire(BINCOND);
			po.modifier(pileRep.depiler(), po.getIpo()+2);
			po.produire(pileRep.depiler());
			pileRep.empiler(po.getIpo());
			break;
		case 37:
			// Gestion du AUT part.4
			// BINCOND vers la fin
			po.produire(BINCOND);
			po.produire(0);
			po.modifier(pileRep.depiler(), po.getIpo()+1);
			pileRep.empiler(po.getIpo());
			break;
		case 38:
			// Gestion du FCOND part.5
			po.modifier(pileRep.depiler(), po.getIpo()+1);		
			int valeurBINCOND, tmp = pileRep.depiler();
			while (tmp > 0) {
				// Resolution a l'adresse tmp
				valeurBINCOND = po.getElt(tmp);
				po.modifier(tmp, po.getIpo()+1);
				tmp = valeurBINCOND;
			}
			// Derniere resolution
			po.modifier(tmp, po.getIpo()+1);
			break;
		case 51:
			// Lecture
			int adresseIdentConnuPourLecture = presentIdent(1);
			int operationLecture = 0;
			if (adresseIdentConnuPourLecture > 0) {
				// Selon le type de l'ident, on doit generer LIRENT ou LIREBOOL
				switch (tCour) {
				case ENT:
					operationLecture = LIRENT;
					break;
				case BOOL:
					operationLecture = LIREBOOL;
					break;
				default:
					UtilLex.messErr("Type non reconnu pour lecture");
					break;
				}
				// Selon la categorie de l'ident qui doit recevoir la nouvelle valeur
				// tapee au clavier, on doit faire differents codes MAPILE
				switch (tabSymb[adresseIdentConnuPourLecture].categorie) {
				case VARGLOBALE:
					po.produire(operationLecture);
					po.produire(AFFECTERG);
					po.produire(tabSymb[adresseIdentConnuPourLecture].info);
					break;
				case VARLOCALE:
					po.produire(operationLecture);
					po.produire(AFFECTERL);
					po.produire(tabSymb[adresseIdentConnuPourLecture].info);
					po.produire(0);
					break;
				case PARAMMOD:
					po.produire(operationLecture);
					po.produire(AFFECTERL);
					po.produire(tabSymb[adresseIdentConnuPourLecture].info);
					po.produire(1);
					break;
				default:
					UtilLex.messErr("Ident de categorie non autorisee");
				}
			} else {
				UtilLex.messErr("Ident non declare");
			}
			break;
		case 52:
			// Ecriture
			switch (tCour) {
			case ENT:
				po.produire(ECRENT);
				break;
			case BOOL:
				po.produire(ECRBOOL);
				break;
			default:
				UtilLex.messErr("Type non reconnu pour ecriture");
				break;
			}
			break;
		case 61:
			po.produire(ET);
			break;
		case 62:
			po.produire(OU);
			break;
		case 63:
			po.produire(NON);
			break;
		case 64:
			po.produire(EG);
			break;
		case 65:
			po.produire(DIFF);
			break;
		case 66:
			po.produire(SUP);
			break;
		case 67:
			po.produire(SUPEG);
			break;
		case 68:
			po.produire(INF);
			break;
		case 69:
			po.produire(INFEG);
			break;
		case 70:
			po.produire(ADD);
			break;
		case 71:
			po.produire(SOUS);
			break;
		case 72:
			po.produire(MUL);
			break;
		case 73:
			po.produire(DIV);
			break;
		case 80:
			// Rencontre d'une valeur
			po.produire(EMPILER);
			po.produire(vCour);
			break;
		case 81:
			vCour = UtilLex.valNb;
			break;
		case 82:
			// Cas de valeur negative (ajout du signe moins)
			vCour = -1 * UtilLex.valNb;
			break;
		case 83:
			vCour = VRAI;
			break;
		case 84:
			vCour = FAUX;
			break;
		case 86:
			int adresseIdentConnu;
			// Ici c'est la ou on va recuperer la valeur d'un ident qu'on rencontre dans le code
			// On verifie si on est dans une procedure ou pas
			if (bc > 1) {
				// Si oui, on cherche localement l'ident dans la table des symboles
				adresseIdentConnu = presentIdent(bc);
				if (adresseIdentConnu > 0) {
					// S'il est trouve, et que par hasard il est un argument d'un appel de procedure
					// et qu'il est appele en tant que type PARAMFIXE
					if (typeParamAppelProc == PARAMFIXE) {
						// Ben on produit un code MAPILE selon sa categorie
						switch(tabSymb[adresseIdentConnu].categorie) {
						case VARLOCALE:
							po.produire(CONTENUL);
							po.produire(tabSymb[adresseIdentConnu].info);
							po.produire(0);
							tCour = tabSymb[adresseIdentConnu].type;
							vCour = tabSymb[adresseIdentConnu].info;
							break;
						case CONSTANTE:
							po.produire(EMPILER);
							tCour = tabSymb[adresseIdentConnu].type;
							vCour = tabSymb[adresseIdentConnu].info;
							break;
						case PARAMFIXE:
							po.produire(CONTENUL);
							po.produire(tabSymb[adresseIdentConnu].info);
							po.produire(0);
							tCour = tabSymb[adresseIdentConnu].type;
							vCour = tabSymb[adresseIdentConnu].info;
							break;
						case PARAMMOD:
							po.produire(CONTENUL);
							po.produire(tabSymb[adresseIdentConnu].info);
							po.produire(1);
							tCour = tabSymb[adresseIdentConnu].type;
							vCour = tabSymb[adresseIdentConnu].info;
							break;
						default:
							UtilLex.messErr("L'ident " + UtilLex.repId(UtilLex.numId) + " ne peut pas etre passe en parametre");
							break;
						}
						typeParamAppelProc = 0;
					}
					// S'il est trouve, et que par hasard il est un argument d'un appel de procedure
					// et qu'il est appele en tant que type PARAMMOD
					else if (typeParamAppelProc == PARAMMOD) {
						// Ben on produit un code MAPILE selon sa categorie
						switch(tabSymb[adresseIdentConnu].categorie) {
						case VARLOCALE:
							po.produire(EMPILERADL);
							po.produire(tabSymb[adresseIdentConnu].info);
							po.produire(0);
							tCour = tabSymb[adresseIdentConnu].type;
							vCour = tabSymb[adresseIdentConnu].info;
							break;
						case PARAMMOD:
							po.produire(EMPILERADL);
							po.produire(tabSymb[adresseIdentConnu].info);
							po.produire(1);
							tCour = tabSymb[adresseIdentConnu].type;
							vCour = tabSymb[adresseIdentConnu].info;
							break;
						default:
							UtilLex.messErr("L'ident " + UtilLex.repId(UtilLex.numId) + " ne peut pas etre passe en parametre");
							break;
						}
						typeParamAppelProc = 0;
					}
					// S'il est trouve en corps de procedure,
					// il peut tout et n'importe quoi sauf VARGLOBALE
					else {
						switch(tabSymb[adresseIdentConnu].categorie) {
						case VARLOCALE:
							po.produire(CONTENUL);
							po.produire(tabSymb[adresseIdentConnu].info);
							po.produire(0);
							tCour = tabSymb[adresseIdentConnu].type;
							vCour = tabSymb[adresseIdentConnu].info;
							break;
						case CONSTANTE:
							po.produire(EMPILER);
							tCour = tabSymb[adresseIdentConnu].type;
							vCour = tabSymb[adresseIdentConnu].info;
							break;
						case PARAMFIXE:
							po.produire(CONTENUL);
							po.produire(tabSymb[adresseIdentConnu].info);
							po.produire(0);
							tCour = tabSymb[adresseIdentConnu].type;
							vCour = tabSymb[adresseIdentConnu].info;
							break;
						case PARAMMOD:
							po.produire(CONTENUL);
							po.produire(tabSymb[adresseIdentConnu].info);
							po.produire(1);
							tCour = tabSymb[adresseIdentConnu].type;
							vCour = tabSymb[adresseIdentConnu].info;
							break;
						default:
							UtilLex.messErr("L'ident " + UtilLex.repId(UtilLex.numId) + " est d'une categorie non reconnue");
							break;
						}
					}
				}
				// Si l'ident n'est pas trouve localement, il le sera peut etre globalement
				else {
					adresseIdentConnu = presentIdent(1);
					// S'il est trouve globalement
					if (adresseIdentConnu > 0) {
						// Euh la je ne comprends plus mon code.. help!
						if (typeParamAppelProc == PARAMMOD) {
							if (tabSymb[adresseIdentConnu].categorie == VARGLOBALE) {
								po.produire(EMPILERADG);
								po.produire(tabSymb[adresseIdentConnu].info);
								tCour = tabSymb[adresseIdentConnu].type;
								vCour = tabSymb[adresseIdentConnu].info;
							} else {
								UtilLex.messErr("Erreur de type pour un parametre");
							}
							typeParamAppelProc = 0;
						}
						// Bref sinon l'ident recherche est forcement VARGLOBALE ou CONSTANTE
						else {
							switch (tabSymb[adresseIdentConnu].categorie) {
							case VARGLOBALE:
								po.produire(CONTENUG);
								po.produire(tabSymb[adresseIdentConnu].info);
								tCour = tabSymb[adresseIdentConnu].type;
								vCour = tabSymb[adresseIdentConnu].info;
								break;
							case CONSTANTE:
								po.produire(EMPILER);
								po.produire(tabSymb[adresseIdentConnu].info);
								tCour = tabSymb[adresseIdentConnu].type;
								vCour = tabSymb[adresseIdentConnu].info;
								break;
							default:
								UtilLex.messErr("L'ident " + UtilLex.repId(UtilLex.numId) + " est d'une categorie non reconnue");
								break;
							}
						}
					} else {
						UtilLex.messErr("Ident non declare");
					}
				}
			} else {
				// On cherche l'ident globalement dans la table des symboles
				adresseIdentConnu = presentIdent(1);
				if (adresseIdentConnu > 0) {
					// Si l'ident recherche est trouve, il est forcement VARGLOBALE ou CONSTANTE
					switch (tabSymb[adresseIdentConnu].categorie) {
					case VARGLOBALE:
						po.produire(CONTENUG);
						po.produire(tabSymb[adresseIdentConnu].info);
						tCour = tabSymb[adresseIdentConnu].type;
						vCour = tabSymb[adresseIdentConnu].info;
						break;
					case CONSTANTE:
						po.produire(EMPILER);
						po.produire(tabSymb[adresseIdentConnu].info);
						tCour = tabSymb[adresseIdentConnu].type;
						vCour = tabSymb[adresseIdentConnu].info;
						break;
					default:
						UtilLex.messErr("Categorie d'ident non autorisee");
						break;
					}
				} else {
					UtilLex.messErr("Ident non declare");
				}
			}
			break;
		case 87:
			// Affectation part.1
			if (bc > 1) {
				// Si on est dans une procedure, l'ident est soit
				// VARGLOBALE, VARLOCALE, PARAMMOD
				adresseIdentConnuAffectation = presentIdent(bc);
				if (adresseIdentConnuAffectation <= 0) {
					// S'il est VARGLOBALE ou CONSTANTE
					// car pas trouve localement
					// on redefinit la variable adresseIdentConnuAffectation
					adresseIdentConnuAffectation = presentIdent(1);
					if (adresseIdentConnuAffectation <= 0) {
						UtilLex.messErr("Ident non declare");
					}
				}
			} else {
				// Sinon c'est le corps principal du programme
				// c'est forcement Ident de categorie VARGLOBALE
				adresseIdentConnuAffectation = presentIdent(1);
				if (adresseIdentConnuAffectation <= 0) {
					UtilLex.messErr("Ident non declare");
				}
			}
			break;
		case 88:
			// Affectation part.2
			switch (tabSymb[adresseIdentConnuAffectation].categorie) {
			// Selon la categorie de l'ident qui doit recevoir une nouvelle valeur
			// on doit faire differents codes MAPILE
			case VARGLOBALE:
				po.produire(AFFECTERG);
				po.produire(tabSymb[adresseIdentConnuAffectation].info);
				break;
			case VARLOCALE:
				po.produire(AFFECTERL);
				po.produire(tabSymb[adresseIdentConnuAffectation].info);
				po.produire(0);
				break;
			case PARAMMOD:
				po.produire(AFFECTERL);
				po.produire(tabSymb[adresseIdentConnuAffectation].info);
				po.produire(1);
				break;
			default:
				UtilLex.messErr("Ident de categorie non autorisee pour affectation");
			}
			adresseIdentConnuAffectation = 0;
			break;
		case 90:
			// BINCOND au tout debut du code pour sauter les procedures
			po.produire(BINCOND);
			po.produire(0);
			pileRep.empiler(po.getIpo());
			break;
		case 91:
			// Declaration d'une procedure
			int declarProc = presentIdent(1);
			// Si elle n'est pas deja presente dans la table des symboles
			if (declarProc == 0) {
				placeIdent(UtilLex.numId, PROC, NEUTRE, po.getIpo()+1);
				/* numId = ADRESSE du dernier ident lu
				 * PROC car c'est une procedure
				 * tCour = NEUTRE
				 * ligne MAPILE ou debute la procedure + 1
				 */
				placeIdent(-1, PRIVEE, NEUTRE, 0);
				/* champ INFO = 0 (nbparam pas encore connu)*/
				bc = it + 1;
			} else {
				UtilLex.messErr("Procedure deja declaree");
			}
			break;
		case 92:
			// Declaration d'un parametre de type PARAMFIXE d'une procedure
			int declarParamFixeProc = presentIdent(bc);
			// S'il n'est pas deja present dans la table des symboles
			if (declarParamFixeProc == 0) {
				placeIdent(UtilLex.numId, PARAMFIXE, tCour, nbDeclarationsParamProc);
				/* numId = ADRESSE du dernier ident lu
				 * PARAMFIXE
				 * tCour = ENT ou BOOL selon le contexte
				 * numero du parametre
				 */
				nbDeclarationsParamProc++;
			} else {
				UtilLex.messErr("Ident deja declare");
			}
			break;
		case 93:
			// Declaration d'un parametre de type PARAMMOD d'une procedure
			int declarParamModProc = presentIdent(bc);
			// S'il n'est pas deja present dans la table des symboles
			if (declarParamModProc == 0) {
				placeIdent(UtilLex.numId, PARAMMOD, tCour, nbDeclarationsParamProc);
				/* numId = ADRESSE du dernier ident lu
				 * PARAMMOD
				 * tCour = ENT ou BOOL selon le contexte
				 * numero du parametre
				 */
				nbDeclarationsParamProc++;
			} else {
				UtilLex.messErr("Ident deja declare");
			}
			break;
		case 94:
			// Remise a zero de nbDeclarationsParamProc une fois que
			// tous les parametres d'une procedure ont ete declares
			tabSymb[bc-1].info = nbDeclarationsParamProc;
			nbDeclarationsParamProc = 0;
			break;
		case 95:
			break;
		case 96:
			// Appel de procedure
			int identProcedure = presentIdent(1);
			if (identProcedure > 0) {
				// Il faut sauvegarder l'ident de la procedure, on en a besoin au case d'en dessous
				adresseIdentConnuAppelProc = identProcedure;
				tCour = tabSymb[adresseIdentConnuAppelProc].type;
				vCour = tabSymb[adresseIdentConnuAppelProc].info;
			} else {
				UtilLex.messErr("Appel de procedure non declaree");
			}
			break;
		case 98:
			// Maintenant que le code MAPILE des parametres a ete genere, on peut faire appel
			// grace a l'ident qu'on vient de sauvegarder au dessus
			po.produire(APPEL);
			po.produire(tabSymb[adresseIdentConnuAppelProc].info);
			po.produire(tabSymb[adresseIdentConnuAppelProc+1].info);
			adresseIdentConnuAppelProc = 0;
			break;
		case 99:
			// Retour de procedure
			po.produire(RETOUR);
			int nbParametres = tabSymb[bc-1].info;
			po.produire(nbParametres);
			// On met tous les parametres a -1
			for (int i = 0 ; i < nbParametres ; i++) {
				tabSymb[bc+i].code = -1;
			}
			break;
		case 100:
			// On est dans un appel de procedure et on veut passer des parametres de type PARAMFIXE
			typeParamAppelProc = PARAMFIXE;
			break;
		case 101:
			// On est dans un appel de procedure et on veut passer des parametres de type PARAMMOD
			typeParamAppelProc = PARAMMOD;
			break;
		case 102:
			// Resolution du bincond qui a saute toutes les procedures
			po.modifier(pileRep.depiler(), po.getIpo()+1);
			break;
		case 255:
			// ARRET
			po.produire(ARRET);
			afftabSymb();
			po.constObj();
			po.constGen();
			// Vidage integral de tabSymb
			int i = 0;
			while (i <= MAXSYMB) {
				tabSymb[i] = null;
				i++;
			}
			break;
		default:
			System.out.println("Point de generation non prevu dans votre liste (numGen = " + numGen + " )");
			break;
		}
	}
}