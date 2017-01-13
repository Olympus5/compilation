#Phase 1
##Grammaire de départ


1. \<suite_fiches\> → (\<fiche\>;)*/
2. \<fiche\> → \<chauffeur\> (\<volume_citerne\>)? <livraisons>
3. \<chauffeur\> → ident
4. \<volume_citerne\> → nbentier
5. \<livraisons\> → \<livraison\> (, \<livraison\>)*
6. \<livraison\> → \<qualité\> (\<livraison_magasin\>)*
7. \<qualité\> → (BEAUJOLAIS | BOURGOGNE)?
8. \<livraison_magasin\> → \<magasin\> \<quantité\>
9. \<magasin\> → ident
10. \<quantité\> → nbentier

##Grammaire algébrique linéaire droite

1. \<suite_fiches\> → \<fiche\>
2. \<fiche\> → \<chauffeur\>
3. \<chauffeur\> → ident \<livraison\> | ident \<volume_citerne\>
4. \<volume_citerne\> → nbentier \<livraison\>
5. \<livraison\> → \<qualité\>
6. \<qualité\> → BEAUJEAULAIS \<livraison_magasin\> | BOURGOGNE \<livraison_magasin\> | \<livraison_magasins\>
7. \<livraison_magasin\> → \<magasin\>
8. \<magasin\> → ident \<quantité\>
9. \<quantité\> → nbentier \<livraison_magasin\> | nbentier, \<livraison\> | nbentier;\<suite_fiches\> | nbentier;

##Expression rationnelle

(ident (nbentier)? (BEAUJOLAIS|BOURGOGNE)? (ident nbentier)+ (,(BEAUJOLAIS|BOURGOGNE)? (ident nbentier)+)*;)*/

##Automate d'analyse syntaxique

![Automate d'analyse syntaxique](http://image.noelshack.com/fichiers/2017/02/1484328032-afd-analyse-syntaxique.jpeg)

##Automate d'analyse lexicale
