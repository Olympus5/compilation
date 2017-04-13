// Grammaire du langage PROJET
// COMP L3  
// Anne Grazon, Véronique Masson
// il convient d'y insérer les appels à {PtGen.pt(k);}
// relancer Antlr après chaque modification et raffraichir le projet Eclipse le cas échéant

// attention l'analyse est poursuivie après erreur si l'on supprime la clause rulecatch

grammar projet;

options {
  language=Java; k=1;
 }

@header {           
import java.io.IOException;
import java.io.DataInputStream;
import java.io.FileInputStream;
} 


// partie syntaxique :  description de la grammaire //
// les non-terminaux doivent commencer par une minuscule


@members {

 
// variables globales et méthodes utiles à placer ici
  
}
// la directive rulecatch permet d'interrompre l'analyse à la première erreur de syntaxe
@rulecatch {
catch (RecognitionException e) {reportError (e) ; throw e ; }}


unite  :   unitprog  EOF
      |    unitmodule  EOF
  ;
  
unitprog
  : 'programme' ident ':'  
     declarations  
     corps { System.out.println("succès, arret de la compilation "); }
  ;
  
unitmodule
  : 'module' ident ':' 
     declarations   
  ;
  
declarations
  : partiedef? partieref? consts? vars? decprocs? 
  ;
  
partiedef
  : 'def' ident  (',' ident )* ptvg
  ;
  
partieref: 'ref'  specif (',' specif)* ptvg
  ;
  
specif  : ident  ( 'fixe' '(' type  ( ',' type  )* ')' )? 
                 ( 'mod'  '(' type  ( ',' type  )* ')' )? 
  ;
  
consts  : 'const' ( ident  '=' valeur  ptvg  )+ 
  ;
  
vars  : 'var' ( type ident  ( ','  ident  )* ptvg  )+
  ;
  
type  : 'ent'  
  |     'bool' 
  ;
  
decprocs: (decproc ptvg)+
  ;
  
decproc :  'proc'  ident  parfixe? parmod? consts? vars? corps 
  ;
  
ptvg  : ';'
  | 
  ;
  
corps : 'debut' instructions 'fin'
  ;
  
parfixe: 'fixe' '(' pf ( ';' pf)* ')'
  ;
  
pf  : type ident  ( ',' ident  )*  
  ;

parmod  : 'mod' '(' pm ( ';' pm)* ')'
  ;
  
pm  : type ident  ( ',' ident  )*
  ;
  
instructions
  : instruction ( ';' instruction)*
  ;
  
instruction
  : inssi
  | inscond
  | boucle
  | lecture
  | ecriture
  | affouappel
  |
  ;
  
inssi : 'si' expression 'alors' instructions ('sinon'  instructions)? 'fsi' 
  ;
  
inscond : 'cond'  expression  ':' instructions 
          (','  expression  ':' instructions )* 
          ('aut'  instructions)? 'fcond' 
  ;
  
boucle  : 'ttq'  expression 'faire' instructions 'fait' 
  ;
  
lecture: 'lire' '(' ident  ( ',' ident  )* ')' 
  ;
  
ecriture: 'ecrire' '(' expression  ( ',' expression  )* ')'
   ;
  
affouappel
  : ident  (    ':=' expression 
            |   (effixes (effmods)?)?  
           )
  ;
  
effixes : '(' (expression  (',' expression  )*)? ')'
  ;
  
effmods :'(' (ident  (',' ident  )*)? ')'
  ; 
  
expression: (exp1) ('ou'  exp1  )*
  ;
  
exp1  : exp2 ('et'  exp2  )*
  ;
  
exp2  : 'non' exp2 
  | exp3  
  ;
  
exp3  : exp4 
  ( '='   exp4 
  | '<>'  exp4 
  | '>'   exp4 
  | '>='  exp4 
  | '<'   exp4 
  | '<='  exp4  
  ) ?
  ;
  
exp4  : exp5 
        ('+'  exp5 
        |'-'  exp5 
        )*
  ;
  
exp5  : primaire 
        (    '*'   primaire 
          | 'div'  primaire 
        )*
  ;
  
primaire: valeur 
  | ident  
  | '(' expression ')'
  ;
  
valeur  : nbentier 
  | '+' nbentier 
  | '-' nbentier 
  | 'vrai' 
  | 'faux' 
  ;

// partie lexicale  : cette partie ne doit pas être modifiée  //
// les unités lexicales de ANTLR doivent commencer par une majuscule
// attention : ANTLR n'autorise pas certains traitements sur les unités lexicales, 
// il est alors nécessaire de passer par un non-terminal intermédiaire 
// exemple : pour l'unité lexicale INT, le non-terminal nbentier a dû être introduit
 
      
nbentier  :   INT { UtilLex.valNb = Integer.parseInt($INT.text);}; // mise à jour de valNb

ident : ID  { UtilLex.traiterId($ID.text, $ID.line); } ; // mise à jour de numId
     // tous les identificateurs seront placés dans la table des identificateurs, y compris le nom du programme ou module
     // la table des symboles n'est pas gérée au niveau lexical
        
  
ID  :   ('a'..'z'|'A'..'Z')('a'..'z'|'A'..'Z'|'0'..'9'|'_')* ; 
     
// zone purement lexicale //

INT :   '0'..'9'+ ;
WS  :   (' '|'\t' | '\n' |'\r')+ {skip();} ; // définition des "espaces"


COMMENT
  :  '\{' (.)* '\}' {skip();}   // toute suite de caractères entourée d'accolades est un commentaire
  |  '#' ~( '\r' | '\n' )* {skip();}  // tout ce qui suit un caractère dièse sur une ligne est un commentaire
  ;

// commentaires sur plusieurs lignes
ML_COMMENT    :   '/*' (options {greedy=false;} : .)* '*/' {$channel=HIDDEN;}
    ;	   



	   