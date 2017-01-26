import java.io.InputStream;

/**
* gestion des actions associees a la reconnaissance des fiches de livraison de vin
* @author Erwan IQUEL, Adrien LEBLANC, Mikael ROYET
*/
public class ActVin extends AutoVin {

    /** table des actions */
    private final int[][] action =
    {/* �tat        BJ    BG   IDENT  NBENT   ,     ;     /  AUTRES  */
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
	private int ichauf ;
	/*!!! DELARATIONS A COMPLETER !!!*/

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
		ch = "CHAUFFEUR                   BJ        BG       ORD     NBMAG\n"
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
		ichauf = -1;
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
				System.out.println("Forcer à 100 la cap");
			break;
			
			case 2:
				System.out.println("Contrôler le volume livré");
				//Erreur non fatal
			break;
			
			case 3:
				System.out.println("Vérifier que le volume livré ne dépasse pas la capacité de la citerne");
				//Erreur non fatal
			break;
			
			case 4:
				System.out.println("Abandon de l'analyse si il y a plus de 10 chauffeur (redirection vers l'état final");
				this.actionQuatre();
				//Erreur fatal
			break;
			
			case 5:
				System.out.println("Déterminer le chauffeur ayant livré le plus de magasins");
				this.actionCinq();
			break;
			
			case 6:
				System.out.println("A chaque fin de fiche afficher les info présent sur le poly de TP (action 6)");
				this.actionSix();
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

     /**
      * Test la capacité de la citerne d'un camion:
      * 100, si capacité > 200 ou non indiqué
      */
     private void actionUn() {
 
     }
     
     private void actionDeux() {
    	 
     }
     
     /**
      * Verifie que le volume livrée à un magasin n'est pas supérieur au volume total
      * de la citerne du camion
      */
     private void actionTrois() {
    	 
     }
     
     /**
      * Regarde le nombre de chauffeur existant, si == 10 alors fin de l'analyse et redirection
      * vers l'état final
      */
     private void actionQuatre() {
    	 if(this.tabChauf.length > 10) {
    		 this.etatInitial = this.etatFinal;
    		 erreur(ActVin.FATALE, "Il y a déjà 10 chauffeurs en action");
    	 }
     }
     
     /**
      * Indique le chauffeur ayant livrée le plus de magasins
      */
     private void actionCinq() {
    	 int numChauffeur = 0;
    	 
    	 for(int i = 1, c = this.tabChauf.length ; i < c;i++) {
    		if(this.tabChauf[i].magdif.size() > this.tabChauf[i].magdif.size()) {
    			numChauffeur = i;
    		}
    	 }
    	 
    	 System.out.println("Le chauffeur n°"+numChauffeur+" est celui qui a livré le plus.");
     }
     
     private void actionSix() {
    	 
     }
} // class Actvin
