import java.io.*;

/**
 * automate de reconnaissance des fiches de livraison de vin
 * @author ?? MERCI DE PRÉCISER LE NOM DU TRINÖME
 *
 */
public class AutoVin extends Automate{

	/** table des transitions */
	private final int[][] transit =
    {   /* Etat        BJ   BG   IDENT  NBENT  ,    ;    /  AUTRES  */
	 	/* 0 */      {  0,   0,    0,     0,   0,   0,   0,    0   },
	 	/* 1 */      {  0,   0,    0,     0,   0,   0,   0,    0   },
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
		this.etatFinal = transit.length;
		this.etatErreur = transit.length - 1;
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
