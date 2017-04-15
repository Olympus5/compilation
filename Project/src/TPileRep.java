
import java.io.*;

public class TPileRep {

    // pile pour g�rer les cha�nes de reprise et les branchements en avant
    // -------------------------------------------------------------------

    
    	private final int MAXPILEREP=50;
    	private int ip; // sommet de pile
    	private int [] T = new int [MAXPILEREP+1];
    	
    	public void empiler(int x) {
    		if (ip==MAXPILEREP) 
    			UtilLex.messErr("d�bordement de la pile de gestion des reprises");
    		ip=ip+1;T[ip]=x;
    	}
    	
		public int depiler() {
		    if (ip==0) 
			UtilLex.messErr("action depiler sur chaine de reprise vide ");
		    ip=ip-1;return T[ip+1];
		}
		// constructeur
		public TPileRep() {ip=0;}
    } // TpileRep
   

    
    
    
    
    
    
    
    
    
    
    
    
    
 