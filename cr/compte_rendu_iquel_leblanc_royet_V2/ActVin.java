import java.io.InputStream;

/**
 * gestion des actions associees a la reconnaissance des fiches de livraison de vin
 * @author Erwan IQUEL, Adrien LEBLANC, Mikael ROYET
 */
public class ActVin extends AutoVin {

	/** table des actions */
	private final int[][] action =
		{/* état        BJ    BG   IDENT  NBENT   ,     ;     /  AUTRES  */
			/* 0 */      { -1,   -1,   -1,    -1,   -1,   -1,   -1,   -1   },
			/* 1 */      { 1,   1,   1,    1,   -1,   -1,   -1,   -1   },
			/* 2 */      { -1,   -1,   -1,    2,   -1,   -1,   -1,   -1   },
			/* 3 */      { -1,   -1,   -1,    -1,   3,   6,   -1,   -1   },
			/* 4 */      { -1,   -1,   -1,    -1,   -1,   -1,   -1,   -1   },
			/* 5 */      { -1,   -1,   -1,    -1,   -1,   -1,   -1,   -1   },
			/* 6 */      { -1,   -1,   -1,    -1,   -1,   -1,   -1,   -1   },
			/* 7 */      { -1,   -1,   4,    -1,   -1,   -1,   5,   -1   },
			/* 8 */      { -1,   -1,   -1,    -1,   -1,   -1,   -1,   -1   },
			/* 9 */      { -1,   -1,   -1,    -1,   -1,   -1,   -1,   -1   }
			/*!!! A COMPLETER !!!*/      
		} ;	       

	/** constructeur classe ActVin */
	public ActVin(InputStream flot) {
		super(flot);
	}

	/** types d'erreurs detectees */
	private static final int FATALE = 0, NONFATALE = 1;

	/** taille d'une colonne pour affichage ecran */
	private static final int MAXLGID = 20;
	/** nombre maximum de chauffeurs */
	private static final int MAXCHAUF = 10;
	/** tableau des chauffeurs et resume des livraison de chacun */
	private Chauffeur[] tabChauf = new Chauffeur[MAXCHAUF];

	/** indice courant du nombre de chauffeurs dans le tableaau tabChauf */
	private int ichauf;

	//Attributs chauffeur
	private int typeVin = -1;
	private int numChauff = 0;
	private int capaciteCiterne = 0;
	private int quantiteBJ = 0;
	private int quantiteBG = 0;
	private int quantiteORD = 0;
	private SmallSet magasins = new SmallSet();
	private int vinLivre = 0;
	/*!!! DECLARATIONS A COMPLETER !!!*/

	/** utilitaire d'affichage � l'ecran 
	 * @param ch est une chaine de longueur quelconque
	 * @return chaine ch cadree a gauche sur MAXLGID caracteres
	 * */
	private String chaineCadrageGauche(String ch) {

		int lgch = Math.min(MAXLGID, ch.length());
		String chres = ch.substring(0, lgch);
		for (int k = lgch; k < MAXLGID; k++)
			chres = chres + " ";
		return chres;
	} 

	/** affichage de tout le tableau de chauffeurs � l'�cran */
	private void afficherchauf() {
		String idchaufcour, ch;
		Ecriture.ecrireStringln("");
		ch = "CHAUFFEUR                 BJ        BG       ORD     NBMAG\n"
				+ "---------                   --        --       ---     -----";
		Ecriture.ecrireStringln(ch);
		for (int i = 0; i <= ichauf; i++) {
			//			System.out.println(" numchauf courant "+tabChauf[i].numchauf);
			idchaufcour = ((LexVin)lex).repId(tabChauf[i].numchauf);
			Ecriture.ecrireString(chaineCadrageGauche(idchaufcour));
			Ecriture.ecrireInt(tabChauf[i].bj, 10);
			Ecriture.ecrireInt(tabChauf[i].bg, 10);
			Ecriture.ecrireInt(tabChauf[i].ordin, 10);
			Ecriture.ecrireInt(tabChauf[i].magdif.size(), 10);
			Ecriture.ecrireStringln("");
		}
	} 

	/** gestion des erreurs 
	 * @param te type de l'erreur
	 * @param messErr message associe a l'erreur
	 */
	private void erreur(int te, String messErr) {
		Lecture.attenteSurLecture(messErr);
		switch (te) {
		case FATALE:
			errFatale = true;
			break;
		case NONFATALE:
			etatCourant = etatErreur;
			break;
		default:
			Lecture.attenteSurLecture("param�tre incorrect pour erreur");
		}
	} 

	/**
	 * initialisations a effectuer avant les actions
	 */
	private void initialisations() {
		ichauf = 0;
		/*!!! A COMPLETER SI BESOIN !!!*/
	} 

	/**
	 * acces a un attribut lexical 
	 * cast pour preciser que lex est de type LexVin
	 * @return valNb associe a l'unite lexicale nbentier
	 */
	private int valNb() {
		return ((LexVin)lex).getValNb();
	}
	/**
	 * acces a un attribut lexical 
	 * cast pour preciser que lex est de type LexVin
	 * @return numId associe a l'unite lexicale ident
	 */
	private int numId() {
		return ((LexVin)lex).getNumId();
	}

	/**
	 * execution d'une action
	 * @param numact numero de l'action a executer
	 */
	public void executer(int numact) {

		switch (numact) {
		case -1:	// action vide
			break;

		case 0:
			this.initAction();
			break;

		case 1:
			if(this.capaciteCiterne < 100 || this.capaciteCiterne > 200) {
				this.capaciteCiterne = 100;
			}
			
			break;

		case 2:
			if(this.vinLivre <= 0) {
				erreur(ActVin.NONFATALE, "La quantité de vin livré doit etre supérieur à 0L");
			}
			
			break;

		case 3:
			if(this.vinLivre > this.capaciteCiterne) {
				erreur(ActVin.NONFATALE, "On peut pas livré plus de vin qu'il y en a dans la citerne");
			}
			
			break;

		case 4:
			if(this.ichauf >= 10) {
				this.etatInitial = this.etatFinal;
				erreur(ActVin.FATALE, "Il y a déjà 10 chauffeurs en action");
			} else {
				ichauf++;
			}
			
			break;

		case 5:
			int numChauffeur = 0;

			for(int i = 1; i < this.ichauf;i++) {
				if(this.tabChauf[i].magdif.size() > this.tabChauf[numChauffeur].magdif.size()) {
					numChauffeur = i;
				}
			}

			System.out.println("Le chauffeur "+lex.repId(tabChauf[numChauffeur].numchauf)+" est celui qui a livré le plus.");
			
			break;

		case 6:
			this.tabChauf[ichauf] = new Chauffeur(this.numChauff, this.quantiteBJ, this.quantiteBG, this.quantiteORD, this.magasins);
			System.out.println("\nFiche n°"+(this.ichauf+1)+":");
			this.afficherchauf();
			
			break;
			/*!!! A COMPLETER !!!*/
		default:
			Lecture.attenteSurLecture("action " + numact + " non prevue");
		}
	}

	/**
	 * definition methode abstraite faireAction de Automate
	 */
	public void faireAction(int etat, int unite) {
		LexVin lex = (LexVin) this.lex;
		
		switch(unite) {
			case 0:
				this.typeVin = 0;
			break;

			case 1:
				this.typeVin = 1;
			break;
			
			case 2:
				
				if(etat == 0 || etat == 7) {
					this.magasins = new SmallSet();
					this.capaciteCiterne = 0;
					this.quantiteBG = 0;
					this.quantiteBJ = 0;
					this.quantiteORD = 0;
					this.vinLivre = 0;
					this.typeVin = -1;
					this.numChauff = 0;
					
					this.numChauff = numId();
				} else {
					this.magasins.add(numId());
				}
			break;
			
			case 3:
				if(etat == 1) {
					this.capaciteCiterne = this.valNb();
				} else {
					if(typeVin == 0) this.quantiteBJ = this.valNb();
					else if(typeVin == 1) this.quantiteBG = this.valNb();
					else if(typeVin == -1) this.quantiteORD = this.valNb();
					
					this.vinLivre += this.valNb();
				}
			break;
			
			case 4: //On fait rien car virgule
				this.vinLivre = 0;
				this.typeVin = -1;
			break;
			
			case 5://On fait rien car fin d'une fiche
			break;
			
			case 6://On fait rien car fin de l'analyse
			break;
			
			default:
		}
		
		executer(action[etat][unite]);
	};

	/**
	 * definition methode abstraite initAction de Automate
	 */
	public void initAction() {
		// action 0 � effectuer a l'init
		initialisations();
	};

	/**
	 * definition methode abstraite getAction de Automate
	 */
	public int getAction(int etat, int unite) {
		//return -1;
		return action[etat][unite];
	};

} // class Actvin
