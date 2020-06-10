import java.util.*;

public class Index {
    // L'index a creer
    public final String[] indexLabels = {"vendor_name","Trip_Pickup_DateTime","Trip_Dropoff_DateTime","Passenger_Count",
            "Trip_Distance","Start_Lon","Start_Lat","Rate_Code","store_and_forward","End_Lon","End_Lat","Payment_Type",
            "Fare_Amt","surcharge","mta_tax","Tip_Amt","Tolls_Amt","Total_Amt"};

    public Map<List<String>, List<Long>> keys = new HashMap<>();  // l'index


    // Ajoute la clef unique (ou multiple) pour creer le dict
    // Ex: {"vendor_name"}, ou bien {"vendor_name", "Passenger_Count", "surcharge"}
    public void addKeyValue(String[] keyName, long rowFilePos){
        List<Long> listItemsInKey = keys.get(Arrays.asList(keyName));
        if (listItemsInKey == null){
            listItemsInKey = new ArrayList<>();
            listItemsInKey.add(rowFilePos);
            keys.put(Arrays.asList(keyName), listItemsInKey);
        }else
            listItemsInKey.add(rowFilePos);
    }

    // Recupere la/les clef(s) appropriees pour ensuite utiliser addKeyValue
    public String[] keyNameFromArray(String[] readData, int[] indexWanted){
        String[] keyName = new String[indexWanted.length];
        for (int i = 0; i < indexWanted.length; i++){
            keyName[i] = readData[indexWanted[i]];
        }
        return keyName;
    }


    // Recupere l'offset en octet pour aller le lire sur le fichier directement
    // Il faut passer en parametres la clef requise (cf. addKeyValue)
    public List<Long> getBytesOffset(List<String> key){
        return this.keys.get(key);
    }

    // La meme chose mais avec plusieurs clefs presentes. Appelle la fonction ci-dessus
    public List<List<Long>> getBytesOffsetList(List<List<String>> keys){
        List<List<Long>> keysList = new ArrayList<>();
        for (List<String> key : keys) {
            List<Long> currentList = getBytesOffset(key);
            keysList.add(currentList);
        }
        return keysList;
    }



    // Recupere la position a partir du nom de l'index.
    // Ex : Trip_Pickup_DateTime  --> retourne 1
    public int getLabelPos(String indexName){
        for (int i = 0; i < indexLabels.length; i++){
            if (indexName.equals(indexLabels[i]))
                    return i;
        }
        return -1;
    }

    // la meme chose mais pour plusieurs noms d'indexes
    public int[] getLabelPos(String[] indexNames){
        int[] labelPositions = new int[indexNames.length];
        for(int i = 0; i < indexNames.length; i++)
            labelPositions[i] = getLabelPos(indexNames[i]);
        return labelPositions;
    }


    // if a single label is asked (ex: "vendor_name") --> return its position
    public int getUserNamePos(String name, String[] userIndexNames){
        for (int i = 0; i < userIndexNames.length; i++){
            if (userIndexNames[i].equals(name))
                return i;
        }
        return -1;
    }

    // if multiple label are asked, ex: {"vendor_name", "Payment_Type"} --> return their position
    // Ex: if "Passenger_Count", "vendor_name", "surcharge" --> 1, 2, 3
    // Dons l'utilisateur peut specifier des index dans le desordre
    public int[] getUserNamePos(String[] names, String[] userIndexNames){
        int[] namePositionInArray = new int[names.length];
        for(int i = 0; i < names.length; i++)
            namePositionInArray[i] = getUserNamePos(names[i], userIndexNames);
        return namePositionInArray;
    }

    // if a single index is asked (ex: "vendor_name")
    public void retrieveNarrowerIndex(Set<List<String>> keysSet, List<List<String>> indexWanted, int indexAsked, String valueWanted) {
        for (List<String> currentSet : keysSet) {
            if (currentSet.get(indexAsked).equals(valueWanted))
                indexWanted.add(currentSet);
        }
    }

    // if multiple index are asked (ex: {"vendor_name", "Passenger_Count"})
    public void retrieveNarrowerIndex(Set<List<String>> keysSet, List<List<String>> indexWanted, int[] indexAsked, String[] valueWanted) {
        for (List<String> currentSet : keysSet) {
            boolean isFound = true;

            for(int i = 0; i < indexAsked.length; i++){
                // O(n^2) bof bof
                if (!currentSet.get(indexAsked[i]).equals(valueWanted[i])) {
                    isFound = false;
                    break;
                }
            }
            if (isFound)
                indexWanted.add(currentSet);
        }
    }

    public List<Long> concatenateElements(List<List<Long>> list2D){
        List<Long> concatenatedList = new ArrayList<>(list2D.get(0));
        for(int i = 1; i < list2D.size(); i++)
            concatenatedList.addAll(list2D.get(i));
        return concatenatedList;
    }



}
