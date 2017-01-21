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

###Tableau des transitions de l'AFDC de l'analyse syntaxique

<table>
  <thead>
    <tr>
      <td></td>
      <th>Beaujolais</th>
      <th>Bourgogne</th>
      <th>Identifiant</th>
      <th>Nombre entier</th>
      <th>,</th>
      <th>;</th>
      <th>/</th>
      <th>Autres</th>
    </tr>
  </thead>
  
  <tbody>
    <tr>
      <th>0</th>
      <td>8</td>
      <td>8</td>
      <td>1</td>
      <td>8</td>
      <td>8</td>
      <td>8</td>
      <td>9</td>
      <td>8</td>
    </tr>
    
    <tr>
      <th>1</th>
      <td>5</td>
      <td>5</td>
      <td>2</td>
      <td>4</td>
      <td>8</td>
      <td>8</td>
      <td>8</td>
      <td>8</td>
    </tr>
    
    <tr>
      <th>2</th>
      <td>8</td>
      <td>8</td>
      <td>8</td>
      <td>3</td>
      <td>8</td>
      <td>8</td>
      <td>8</td>
      <td>8</td>
    </tr>
    
    <tr>
      <th>3</th>
      <td>8</td>
      <td>8</td>
      <td>2</td>
      <td>8</td>
      <td>6</td>
      <td>7</td>
      <td>8</td>
      <td>8</td>
    </tr>
    
    <tr>
      <th>4</th>
      <td>5</td>
      <td>5</td>
      <td>2</td>
      <td>8</td>
      <td>8</td>
      <td>8</td>
      <td>8</td>
      <td>8</td>
    </tr>
    
    <tr>
      <th>5</th>
      <td>8</td>
      <td>8</td>
      <td>2</td>
      <td>8</td>
      <td>8</td>
      <td>8</td>
      <td>8</td>
      <td>8</td>
    </tr>
    
    <tr>
      <th>6</th>
      <td>5</td>
      <td>5</td>
      <td>2</td>
      <td>8</td>
      <td>8</td>
      <td>8</td>
      <td>8</td>
      <td>8</td>
    </tr>
    
    <tr>
      <th>7</th>
      <td>8</td>
      <td>8</td>
      <td>1</td>
      <td>8</td>
      <td>8</td>
      <td>8</td>
      <td>9</td>
      <td>8</td>
    </tr>
    
    <tr>
      <th>8</th>
      <td>8</td>
      <td>8</td>
      <td>8</td>
      <td>8</td>
      <td>8</td>
      <td>8</td>
      <td>8</td>
      <td>8</td>
    </tr>
    
    <tr>
      <th>9</th>
      <td>8</td>
      <td>8</td>
      <td>8</td>
      <td>8</td>
      <td>8</td>
      <td>8</td>
      <td>8</td>
      <td>8</td>
    </tr>
  </tbody>
</table>

##Phase 2

###Tableau des actions de l'AFDC de l'analyse syntaxique

<table>
  <thead>
    <tr>
      <td></td>
      <th>Beaujolais</th>
      <th>Bourgogne</th>
      <th>Identifiant</th>
      <th>Nombre entier</th>
      <th>,</th>
      <th>;</th>
      <th>/</th>
      <th>Autres</th>
    </tr>
  </thead>
  
  <tbody>
    <tr>
      <th>0</th>
      <td>-1</td>
      <td>-1</td>
      <td>-1</td>
      <td>-1</td>
      <td>-1</td>
      <td>-1</td>
      <td>-1</td>
      <td>-1</td>
    </tr>
    
    <tr>
      <th>1</th>
      <td>1</td>
      <td>1</td>
      <td>1</td>
      <td>1</td>
      <td>-1</td>
      <td>-1</td>
      <td>-1</td>
      <td>-1</td>
    </tr>
    
    <tr>
      <th>2</th>
      <td>-1</td>
      <td>-1</td>
      <td>-1</td>
      <td>2</td>
      <td>-1</td>
      <td>-1</td>
      <td>-1</td>
      <td>-1</td>
    </tr>
    
    <tr>
      <th>3</th>
      <td>-1</td>
      <td>-1</td>
      <td>-1</td>
      <td>-1</td>
      <td>3</td>
      <td>6</td>
      <td>-1</td>
      <td>-1</td>
    </tr>
    
    <tr>
      <th>4</th>
      <td>-1</td>
      <td>-1</td>
      <td>-1</td>
      <td>-1</td>
      <td>-1</td>
      <td>-1</td>
      <td>-1</td>
      <td>-1</td>
    </tr>
    
    <tr>
      <th>5</th>
      <td>-1</td>
      <td>-1</td>
      <td>-1</td>
      <td>-1</td>
      <td>-1</td>
      <td>-1</td>
      <td>-1</td>
      <td>-1</td>
    </tr>
    
    <tr>
      <th>6</th>
      <td>-1</td>
      <td>-1</td>
      <td>-1</td>
      <td>-1</td>
      <td>-1</td>
      <td>-1</td>
      <td>-1</td>
      <td>-1</td>
    </tr>
    
    <tr>
      <th>7</th>
      <td>-1</td>
      <td>-1</td>
      <td>4</td>
      <td>-1</td>
      <td>-1</td>
      <td>-1</td>
      <td>5</td>
      <td>-1</td>
    </tr>
    
    <tr>
      <th>8</th>
      <td>-1</td>
      <td>-1</td>
      <td>-1</td>
      <td>-1</td>
      <td>-1</td>
      <td>-1</td>
      <td>-1</td>
      <td>-1</td>
    </tr>
    
    <tr>
      <th>9</th>
      <td>-1</td>
      <td>-1</td>
      <td>-1</td>
      <td>-1</td>
      <td>-1</td>
      <td>-1</td>
      <td>-1</td>
      <td>-1</td>
    </tr>
  </tbody>
</table>
