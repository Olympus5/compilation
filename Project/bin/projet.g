// Grammaire du langage PROJET
// COMP L3  
// Anne Grazon, Veronique Masson
// il convient d'y inserer les appels e {PtGen.pt(k);}
// relancer Antlr apres chaque modification et raffraichir le projet Eclipse le cas echeant

// attention l'analyse est poursuivie apres erreur si l'on supprime la clause rulecatch

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

 
// variables globales et methodes utiles e placer ici
  
}
// la directive rulecatch permet d'interrompre l'analyse e la premiere erreur de syntaxe
@rulecatch {
catch (RecognitionException e) {reportError (e) ; throw e ; }}


unite  :   unitprog  EOF
      |    unitmodule  EOF
  ;
  
unitprog
  : 'programme' ident ':'  
     declarations  
     {PtGen.pt(102);} corps { System.out.println("succes, arret de la compilation "); } {PtGen.pt(255);}
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
  
consts  : 'const' ( ident  '=' valeur {PtGen.pt(3);} ptvg  )+ 
  ;
  
vars  : 'var' ( type ident {PtGen.pt(2);} ( ','  ident {PtGen.pt(2);} )* ptvg  )+ {PtGen.pt(1);}
  ;
  
type  : 'ent'  {PtGen.pt(4);}  |    'bool'   {PtGen.pt(5);} 
  ;
  
decprocs: {PtGen.pt(90);} (decproc ptvg)+
  ;
  
decproc :  'proc' ident {PtGen.pt(91);} parfixe? parmod? {PtGen.pt(94);} consts? vars? corps {PtGen.pt(99);}
  ;
  
ptvg  : ';'
  | 
  ;
  
corps : 'debut' instructions 'fin'
  ;
  
parfixe: 'fixe' '(' pf ( ';' pf)* ')'
  ;
  
pf  : type ident {PtGen.pt(92);} ( ',' ident {PtGen.pt(92);} )*  
  ;

parmod  : 'mod' '(' pm ( ';' pm)* ')'
  ;
  
pm  : type ident {PtGen.pt(93);} ( ',' ident {PtGen.pt(93);} )*
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
  
inssi : 'si' expression {PtGen.pt(21);} 'alors' instructions ('sinon' {PtGen.pt(22);}  instructions)? {PtGen.pt(23);} 'fsi'
  ;
  
inscond : 'cond' {PtGen.pt(34);} expression ':' {PtGen.pt(35);} instructions 
          (',' {PtGen.pt(36);}  expression ':' {PtGen.pt(35);}  instructions)* 
          ('aut' {PtGen.pt(37);} instructions)? 'fcond' {PtGen.pt(38);}
  ;
  
boucle  : 'ttq' {PtGen.pt(31);} expression {PtGen.pt(32);} 'faire' instructions {PtGen.pt(33);} 'fait' 
  ;
  
lecture: 'lire' '(' ident {PtGen.pt(51);} ( ',' ident {PtGen.pt(51);} )* ')' 
  ;
  
ecriture: 'ecrire' '(' expression {PtGen.pt(52);} ( ',' expression {PtGen.pt(52);} )* ')'
   ;
  
affouappel
  : ident  (  {PtGen.pt(87);}  ':=' expression {PtGen.pt(88);}
            | {PtGen.pt(96);}   (effixes (effmods)? )? {PtGen.pt(98);}
           )
  ;
  
effixes : {PtGen.pt(100);} '(' (expression  (',' expression )*)? ')'
  ;
  
effmods : {PtGen.pt(101);} '(' (ident {PtGen.pt(86);} (',' ident {PtGen.pt(86);} )*)? ')'
  ; 
  
expression: (exp1) ('ou'  exp1 {PtGen.pt(62);} )*
  ;
  
exp1  : exp2  ('et'  exp2  {PtGen.pt(61);} )*
  ;
  
exp2  : 'non' exp2 {PtGen.pt(63);}
  | exp3  
  ;
  
exp3  : exp4 
  ( '='   exp4 {PtGen.pt(64);}{PtGen.pt(5);}
  | '<>'  exp4 {PtGen.pt(65);}{PtGen.pt(5);}
  | '>'   exp4 {PtGen.pt(66);}{PtGen.pt(5);}
  | '>='  exp4 {PtGen.pt(67);}{PtGen.pt(5);}
  | '<'   exp4 {PtGen.pt(68);}{PtGen.pt(5);}
  | '<='  exp4 {PtGen.pt(69);}{PtGen.pt(5);}
  ) ?
  ;
  
exp4  : exp5 
        ('+'  exp5 {PtGen.pt(70);}
        |'-'  exp5 {PtGen.pt(71);}
        )*
  ;
  
exp5  : primaire 
        (    '*'   primaire {PtGen.pt(72);}
          | 'div'  primaire {PtGen.pt(73);}
        )*
  ;
  
primaire: valeur  {PtGen.pt(80);}
  | ident  {PtGen.pt(86);}
  | '(' expression ')'
  ;
  
valeur  : nbentier {PtGen.pt(81);}{PtGen.pt(4);}
  | '+' nbentier {PtGen.pt(81);}{PtGen.pt(4);}
  | '-' nbentier {PtGen.pt(82);}{PtGen.pt(4);}
  | 'vrai' {PtGen.pt(83);}{PtGen.pt(5);}
  | 'faux' {PtGen.pt(84);}{PtGen.pt(5);}
  ;

// partie lexicale  : cette partie ne doit pas etre modifiee  //
// les unites lexicales de ANTLR doivent commencer par une majuscule
// attention : ANTLR n'autorise pas certains traitements sur les unites lexicales, 
// il est alors necessaire de passer par un non-terminal intermediaire 
// exemple : pour l'unite lexicale INT, le non-terminal nbentier a de etre introduit
 
      
nbentier  :   INT { UtilLex.valNb = Integer.parseInt($INT.text);}; // mise e jour de valNb

ident : ID  { UtilLex.traiterId($ID.text, $ID.line); } ; // mise e jour de numId
     // tous les identificateurs seront places dans la table des identificateurs, y compris le nom du programme ou module
     // la table des symboles n'est pas geree au niveau lexical
        
  
ID  :   ('a'..'z'|'A'..'Z')('a'..'z'|'A'..'Z'|'0'..'9'|'_')* ; 
     
// zone purement lexicale //

INT :   '0'..'9'+ ;
WS  :   (' '|'\t' | '\n' |'\r')+ {skip();} ; // definition des "espaces"


COMMENT
  :  '\{' (.)* '\}' {skip();}   // toute suite de caracteres entouree d'accolades est un commentaire
  |  '#' ~( '\r' | '\n' )* {skip();}  // tout ce qui suit un caractere diese sur une ligne est un commentaire
  ;

// commentaires sur plusieurs lignes
ML_COMMENT    :   '/*' (options {greedy=false;} : .)* '*/' {$channel=HIDDEN;}
    ;