import java.io.*;

/**
 * automate de reconnaissance des fiches de livraison de vin
 * @author Erwan IQUEL, Adrien LEBLANC, Mikael ROYET
 */
public class AutoVin extends Automate{

	/** table des transitions */
	private final int[][] transit =
    {   /* Etat        BJ   BG   IDENT  NBENT  ,    ;    /  AUTRES  */
	 	/* 0 */      {  8,   8,    1,     8,   8,   8,   9,    8   },
	 	/* 1 */      {  5,   5,    2,     4,   8,   8,   8,    8   },
	 	/* 2 */      {  8,   8,    8,     3,   8,   8,   8,    8   },
	 	/* 3 */      {  8,   8,    2,     8,   6,   7,   8,    8   },
	 	/* 4 */      {  5,   5,    2,     8,   8,   8,   8,    8   },
	 	/* 5 */      {  8,   8,    2,     8,   8,   8,   8,    8   },
	 	/* 6 */      {  5,   5,    2,     8,   8,   8,   8,    8   },
	 	/* 7 */      {  8,   8,    1,     8,   8,   8,   9,    8   },
	 	/* 8 */      {  8,   8,    8,     8,   8,   8,   9,    8   },
	 	/* 9 */      {  8,   8,    8,     8,   8,   8,   8,    8   }
	 	/* A COMPLETER */      
    } ;
	
	/** utilitaire de suivi des modifications pour affichage */
	public void newObserver(ObserverAutomate oAuto, ObserverLexique oLex ){
		this.newObserver(oAuto);
		this.lex.newObserver(oLex);
		lex.notifyObservers(((LexVin)lex).getCarlu());
	}
 
	/** constructeur classe AutoVin */
	public AutoVin(InputStream flot) {
		/** on utilise ici un analyseur lexical de type LexVin */
		lex = new LexVin(flot);
		this.etatInitial = 0;
		//Erreur car longueur tableau = 10 (nb élément du tab) mais dernier elem = 9 !!! STUPIDE
		this.etatFinal = transit.length - 1;
		this.etatErreur = transit.length - 2;
	}

	/** definition de la methode abstraite getTransition de Automate */
	int getTransition(int etat, int unite) {
		return this.transit[etat][unite];
	}

	/** ici la methode abstraite faireAction de Automate n'est pas encore definie */
	void faireAction(int etat, int unite) {};
	
	/** ici la methode abstraite actionInit de Automate n'est pas encore definie */
	void initAction() {};
	
	/** ici la methode abstraite getAction de Automate n'est pas encore definie */
	int getAction(int etat, int unite) {return 0;};
		
}/** AutoVin */
