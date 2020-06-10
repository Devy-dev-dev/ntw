import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class Main {
    private static void seekPosition(String path, List<Long> posList) {
        try {
            File f = new File(path);
            RandomAccessFile raf = new RandomAccessFile(f, "r");

            for (Long positionsArray : posList) {
                raf.seek(positionsArray);
                String row = raf.readLine();
                String[] data = row.split(",");
                System.out.println(Arrays.toString(data));
            }
            raf.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }


    private static void indexCreation(String path, Index index, String[] indexesNames){
        int[] indexes = index.getLabelPos(indexesNames);
        workingOnFile(path, index, indexes);
    }


    private static void workingOnFile(String path, Index index, int[] indexesWanted) {
        int currentLine = 0;
        long currentPoint = 0;
        try (FileInputStream inputStream = new FileInputStream(path); Scanner sc = new Scanner(inputStream, StandardCharsets.UTF_8)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] currentRow = line.split(",");
                if (currentLine > 2) {
                    String[] currentKeyName = index.keyNameFromArray(currentRow, indexesWanted);
                    index.addKeyValue(currentKeyName, currentPoint);
                }
                byte[] utf8Bytes = line.getBytes(StandardCharsets.UTF_8);
                if (line.length() > 0) {
                    if (currentLine == 0)
                        currentPoint = currentPoint + utf8Bytes.length + 3;
                    else
                        currentPoint = currentPoint + utf8Bytes.length + 2;
                }
                currentLine++;
            }

            // note that Scanner suppresses exceptions
            if (sc.ioException() != null) {
                throw sc.ioException();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static List<Long> fetchValidElementsOffset(Index index, String[] indexesNames, String[] labelWanted, String[] valueWanted) {
        Set<List<String>> keysSet = index.keys.keySet();
        List<List<String>> indexWanted = new ArrayList<>();
        int[] indexAsked = index.getUserNamePos(labelWanted, indexesNames);
        index.retrieveNarrowerIndex(keysSet, indexWanted, indexAsked, valueWanted);
        List<List<Long>> indexList = index.getBytesOffsetList(indexWanted);
        return index.concatenateElements(indexList);
    }


    public static void main(String[] args) {
        String filePath = "src/data/smol.csv";
        intersectionExample(filePath);

    }

    private static void intersectionExample(String filePath) {
        // ================================================= 1ST INDEX =================================================
        // Create index
        Index index = new Index();
        String[] indexesNames = {"vendor_name", "Payment_Type"}; // indexes
        String[] labelWanted = {"vendor_name"};                  // label wanted
        String[] valueWanted = {"VTS"};                          // name for this wanted label

        indexCreation(filePath, index, indexesNames);
        // Find all Keys
        List<Long> positionList = fetchValidElementsOffset(index, indexesNames, labelWanted, valueWanted);


        // ================================================= 2ND INDEX =================================================
        // Create index
        Index index2 = new Index();
        String[] indexesNames2 = {"Passenger_Count", "Total_Amt"}; // indexes
        String[] labelWanted2 = {"Passenger_Count"};                  // label wanted
        String[] valueWanted2 = {"2"};                          // name for this wanted label

        indexCreation(filePath, index2, indexesNames2);
        // Find all Keys
        List<Long> positionList2 = fetchValidElementsOffset(index2, indexesNames2, labelWanted2, valueWanted2);

        var intersection = new ArrayList<>(positionList);
        intersection.retainAll(positionList2);
//        System.out.println("resultat intersection : " +intersection);


        // ================================================= AFFICHAGE =================================================
        seekPosition(filePath, intersection);
    }

}
