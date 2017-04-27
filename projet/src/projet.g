// Grammaire du langage PROJET
// COMP L3  
// Anne Grazon, V�ronique Masson
// il convient d'y ins�rer les appels � {PtGen.pt(k);}
// relancer Antlr apr�s chaque modification et raffraichir le projet Eclipse le cas �ch�ant

// attention l'analyse est poursuivie apr�s erreur si l'on supprime la clause rulecatch

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

 
// variables globales et m�thodes utiles � placer ici
  
}
// la directive rulecatch permet d'interrompre l'analyse � la premi�re erreur de syntaxe
@rulecatch {
catch (RecognitionException e) {reportError (e) ; throw e ; }}


unite  :   {PtGen.pt(91);} unitprog  EOF
      |    {PtGen.pt(92);} unitmodule  EOF
  ;
  
unitprog
  : 'programme' ident ':'
     declarations 
     corps { System.out.println("succ�s, arret de la compilation "); }
  {PtGen.pt(255);};
  
unitmodule
  : 'module' ident ':' 
     declarations
  {PtGen.pt(255);};
  
declarations
  : partiedef? partieref? consts? vars? decprocs? 
  ;
  
partiedef
  : 'def' ident {PtGen.pt(93);} (',' ident {PtGen.pt(93);} )* ptvg
  ;
  
partieref: 'ref'  specif {PtGen.pt(94);} (',' specif {PtGen.pt(94);} )* ptvg
  ;
  
specif  : ident  ( 'fixe' '(' type {PtGen.pt(95);} ( ',' type {PtGen.pt(95);} )* ')' )? 
                 ( 'mod'  '(' type {PtGen.pt(95);} ( ',' type {PtGen.pt(95);} )* ')' )? 
  ;
  
consts  : 'const' ( ident '=' valeur  ptvg {PtGen.pt(3);})+ 
  ;
  
vars  : 'var' ( type ident {PtGen.pt(1);}  ( ','  ident  {PtGen.pt(1);})* ptvg )+{PtGen.pt(2);}
  ;
  
type  : 'ent' {PtGen.pt(4);} 
  |     'bool' {PtGen.pt(5);} 
  ;
  
decprocs:  {PtGen.pt(71);} (decproc ptvg)+ {PtGen.pt(81);} 
  ;
  
decproc :  'proc' ident {PtGen.pt(72);} parfixe? parmod? {PtGen.pt(75);} consts? vars? corps {PtGen.pt(80);} 
  ;
  
ptvg  : ';'
  | 
  ;
  
corps : 'debut' instructions 'fin'
  ;
  
parfixe: 'fixe' '(' pf ( ';' pf)* ')'
  ;
  
pf  : type ident {PtGen.pt(73);} ( ',' ident {PtGen.pt(73);} )*  
  ;

parmod  : 'mod' '(' pm ( ';' pm)* ')'
  ;
  
pm  : type ident {PtGen.pt(74);} ( ',' ident {PtGen.pt(74);} )*
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
  
inssi : 'si' expression {PtGen.pt(37);PtGen.pt(41);} 'alors' instructions ( {PtGen.pt(42);} 'sinon'  instructions)? {PtGen.pt(43);} 'fsi' 
  ;
  
inscond : 'cond' {PtGen.pt(61);}  expression {PtGen.pt(37);PtGen.pt(62);} ':' instructions
          (',' {PtGen.pt(63);} expression {PtGen.pt(37);PtGen.pt(62);} ':' instructions )* 
          ('aut'  instructions {PtGen.pt(64);} )? {PtGen.pt(65);} 'fcond' 
  ;
  
boucle  : 'ttq' {PtGen.pt(51);} expression {PtGen.pt(37);PtGen.pt(52);} 'faire' instructions {PtGen.pt(53);} 'fait'
  ;
  
lecture: 'lire' '(' ident  {PtGen.pt(12);PtGen.pt(11);} ( ',' ident {PtGen.pt(12);PtGen.pt(11);} )* ')' 
  ;
  
ecriture: 'ecrire' '(' expression {PtGen.pt(13);} ( ',' expression {PtGen.pt(13);} )* ')'
   ;
  
affouappel
  : ident  ( {PtGen.pt(10);} ':=' expression {PtGen.pt(11);}
            | {PtGen.pt(76);} (effixes (effmods)?)? {PtGen.pt(77);}  
           )
  ;
  
effixes : '(' (expression  (',' expression  )*)? ')'
  ;
  
effmods :'(' (ident {PtGen.pt(78);} (',' ident {PtGen.pt(78);} )*)? ')'
  ; 
  
expression: (exp1) ( {PtGen.pt(37);} 'ou'  exp1  {PtGen.pt(37);}{PtGen.pt(21);})*
  ;
  
exp1  : exp2 ( {PtGen.pt(37);} 'et'  exp2  {PtGen.pt(37);}{PtGen.pt(22);})*
  ;
  
exp2  : 'non' exp2 {PtGen.pt(37);}{PtGen.pt(23);}  
  | exp3  
  ;
  
exp3  : exp4 
  ( {PtGen.pt(36);} '='   exp4 {PtGen.pt(36);}{PtGen.pt(24);}
  | {PtGen.pt(36);} '<>'  exp4 {PtGen.pt(36);}{PtGen.pt(25);}
  | {PtGen.pt(36);} '>'   exp4 {PtGen.pt(36);}{PtGen.pt(26);}
  | {PtGen.pt(36);} '>='  exp4 {PtGen.pt(36);}{PtGen.pt(27);}
  | {PtGen.pt(36);} '<'   exp4 {PtGen.pt(36);}{PtGen.pt(28);}
  | {PtGen.pt(36);} '<='  exp4 {PtGen.pt(36);}{PtGen.pt(29);}
  ) ?
  ;
  
exp4  : exp5 
        ( {PtGen.pt(36);} '+'  exp5 {PtGen.pt(36);}{PtGen.pt(30);}
        | {PtGen.pt(36);} '-'  exp5 {PtGen.pt(36);}{PtGen.pt(31);}
        )*
  ;
  
exp5  : primaire 
        ( {PtGen.pt(36);} '*'   primaire {PtGen.pt(36);}{PtGen.pt(32);}
          | {PtGen.pt(36);} 'div'  primaire {PtGen.pt(36);}{PtGen.pt(33);}
        )*
  ;
  
primaire: valeur {PtGen.pt(34);}
  | ident {PtGen.pt(35);}
  | '(' expression ')'
  ;
  
valeur  : nbentier {PtGen.pt(4);PtGen.pt(6);}
  | '+' nbentier {PtGen.pt(4);PtGen.pt(6);}
  | '-' nbentier {PtGen.pt(4);PtGen.pt(7);}
  | 'vrai' {PtGen.pt(5);PtGen.pt(8);}
  | 'faux' {PtGen.pt(5);PtGen.pt(9);}
  ;

// partie lexicale  : cette partie ne doit pas �tre modifi�e  //
// les unit�s lexicales de ANTLR doivent commencer par une majuscule
// attention : ANTLR n'autorise pas certains traitements sur les unit�s lexicales, 
// il est alors n�cessaire de passer par un non-terminal interm�diaire 
// exemple : pour l'unit� lexicale INT, le non-terminal nbentier a d� �tre introduit
 
      
nbentier  :   INT { UtilLex.valNb = Integer.parseInt($INT.text);}; // mise � jour de valNb

ident : ID  { UtilLex.traiterId($ID.text, $ID.line); } ; // mise � jour de numId
     // tous les identificateurs seront plac�s dans la table des identificateurs, y compris le nom du programme ou module
     // la table des symboles n'est pas g�r�e au niveau lexical
        
  
ID  :   ('a'..'z'|'A'..'Z')('a'..'z'|'A'..'Z'|'0'..'9'|'_')* ; 
     
// zone purement lexicale //

INT :   '0'..'9'+ ;
WS  :   (' '|'\t' | '\n' |'\r')+ {skip();} ; // d�finition des "espaces"


COMMENT
  :  '\{' (.)* '\}' {skip();}   // toute suite de caract�res entour�e d'accolades est un commentaire
  |  '#' ~( '\r' | '\n' )* {skip();}  // tout ce qui suit un caract�re di�se sur une ligne est un commentaire
  ;

// commentaires sur plusieurs lignes
ML_COMMENT    :   '/*' (options {greedy=false;} : .)* '*/' {$channel=HIDDEN;}
    ;	   



	   