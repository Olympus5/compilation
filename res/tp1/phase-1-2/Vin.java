import java.awt.* ;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.swing.JFrame;

public class Vin {

	public static InputStream debutAnalyse() {
		String nomfich;
		nomfich = Lecture.lireString("nom du fichier d'entrée : ");
		InputStream f = Lecture.ouvrir(nomfich);
		if (f == null) {
			System.exit(0);
		}
		return f;
	} // debutAnalyse

	public static void finAnalyse(InputStream f) {
		Lecture.fermer(f);
		/** fermeture de la fenetre de trace d'execution */
		char tempo ;
		System.out.println("");
		System.out.println(" pour fermer la fenêtre de trace d'exécution, tapez entrée ") ;
		tempo = Lecture.lireChar() ;
		tempo = Lecture.lireChar() ;
		System.exit(0);
	} // finAnalyse
   
	public static void main(String[] args) {

		FenAffichage fenetre = new FenAffichage();

		ActVin analyseur;
		InputStream flot = debutAnalyse();
		
		analyseur = new ActVin(flot);
		analyseur.newObserver(fenetre, fenetre);
		analyseur.interpreteur();
		finAnalyse(flot);
	}
} // class Vin

