
import java.awt.TextArea;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Analyseur lexical pour TP livraison vins
 * @author Erwan IQUEL, Adrien LEBLANC, Mikael ROYET
 *
 */
public class LexVin extends Lex {

	/** codage des items lexicaux */
	protected final int
			BEAUJOLAIS = 0, BOURGOGNE = 1, IDENT = 2, NBENTIER = 3,
			VIRGULE = 4, PTVIRG = 5, BARRE = 6, AUTRES = 7;
	public static final String[] images = { "BEAUJ", "BOURG", "IDENT", "NBENT", "  ,  ",
			"  ;  ", "  /  ", "AUTRE" };

	/** nombre de mots r�serv�s dans Lex.tabId */
    private final int NBRES = 2 ;
    /** indice de remplissage de Lex.tabid */
    private int itab ;

    /** attributs lexicaux */
    private int valNb, numId ;

    /** caractere courant */
    private char carlu ;

    public char getCarlu(){
			return this.carlu;
    }

    /** constructeur classe LexVin */
    public LexVin(InputStream flot) {
	 	/** initialisation du flot par la classe abstraite */
    	super(flot);
    	/** prelecture du premier caractere de la donnee */
    	lireCarlu();
    	/** initialisation tabId par mots reserves */
    	Lex.tabId[0] = "BEAUJOLAIS" ; Lex.tabId[1] = "BOURGOGNE" ;
    	itab = 1;
    }

    /** procedure de lecture du caractere courant */
	private void lireCarlu() {
		carlu = Lecture.lireChar(flot);
		this.notifyObservers(carlu);
		if ((carlu == '\r') || (carlu == '\n') || (carlu == '\t'))
			carlu = ' ';
		if (Character.isWhitespace(carlu))
			carlu = ' ';
		else
			carlu = Character.toUpperCase(carlu);
	}

	/** determination de l'item lexical
	 * definition de la methode abstraite lireSymb de Lex
	 *
	 * @return code entier de l'item lexical reconnu
	 * */
	public int lireSymb() {

		return AUTRES; // A COMPLETER

	} /** liresymb */

	/** fonction donnant la chaine associee a un ident de tabId
	 * definition de la methode abstraite repId de Lex
	 *
	 * @param nId indice de l'ident dans tabId
	 * @return chaine associee a l'ident
	 * */
	public String repId(int nId) {

		return ""; // A COMPLETER

	}

	/** methodes d'acces aux attributs lexicaux */
	public int getValNb() {
		return this.valNb;
	}

	public int getNumId() {
		return this.numId;
	}

} /** class Lexvin */
