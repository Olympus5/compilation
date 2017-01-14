#TP1 Compilation

**Author:**
* *Erwan IQUEL*
* *Adrien LEBLANC*
* *Mikaël ROYET*

##Phase 1

###Grammaire de départ


1. \<suite_fiches\> → (\<fiche\>;)*/
2. \<fiche\> → \<chauffeur\> (\<volume_citerne\>)? \<livraisons\>
3. \<chauffeur\> → ident
4. \<volume_citerne\> → nbentier
5. \<livraisons\> → \<livraison\> (, \<livraison\>)*
6. \<livraison\> → \<qualité\> (\<livraison_magasin\>)*
7. \<qualité\> → (BEAUJOLAIS | BOURGOGNE)?
8. \<livraison_magasin\> → \<magasin\> \<quantité\>
9. \<magasin\> → ident
10. \<quantité\> → nbentier

###Expression rationnelle

(ident (nbentier)? (BEAUJOLAIS|BOURGOGNE)? (ident nbentier)+ (,(BEAUJOLAIS|BOURGOGNE)? (ident nbentier)+)*;)*/

###Automate d'analyse syntaxique

![Automate d'analyse syntaxique](http://image.noelshack.com/fichiers/2017/02/1484328032-afd-analyse-syntaxique.jpeg)
