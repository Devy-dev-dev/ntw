import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Table {
    // La table a retourner

    /* Il faut pourvoir :
        - afficher les index
        - construire la table (ajouter index, ajouter posistion du fichier)
        - sélectionner le bon index pour afficher les résultats
            --> identifier l'index demandé
            --> regarder si il est présent dans l'ojet Index
            --> trouver le meilleur index
                . check si ce qui est demandé colle parfaitement à un index
                . sinon, faire intersection entre les deux index. Ex :
                    Trouver vendor + nb passager (VTS 2) :
                        Index vendor : {"VTS": [0, 27, 53], "CMT": [10, 47, 89], etc}
                        Table nb passager + coût : {1p14€: [0, 45, 79], etc}

                        intersection des deux tableaux:
                            - tableau où on a VTS
                            - tableau où on a 2 la liste des deux personnes

     */


    public String name;  // table name
    public List<Integer> indexes = new ArrayList<>();
    public Map<String, List<Long>> keys = new HashMap<>();

    public Table(String name){
        this.name = name;
    }

    public void addIndex(int indexPos){  //ex: O -> VTS, 3 -> 5 (passengers)
        indexes.add(indexPos);
    }

    public void addIndex(int[] indexesPos){
        for (int indexPos : indexesPos)
            addIndex(indexPos);
    }




}
