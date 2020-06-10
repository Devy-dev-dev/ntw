import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class Main {
    private static void seekPosition(List<List<String>> posList, Index index) {
        try {
            String path = "src/data/smol.csv";
            File f = new File(path);
            RandomAccessFile raf = new RandomAccessFile(f, "r");


            for (List<String> currentKeyFromList: posList) {
                List<Long> positions = index.getBytesOffset(currentKeyFromList);
                for (Long positionsArray : positions) {
                    raf.seek(positionsArray);
                    String row = raf.readLine();
                    String[] data = row.split(",");
                    System.out.println(Arrays.toString(data));
                }
            }
            raf.close();


        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private static void workingOnFile(Index index, int[] indexesWanted) {
        String path = "src/data/smol.csv";
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

    public static void main(String[] args) {

        // INDEX 1
        Index index = new Index();

        String[] indexesNames = {"vendor_name", "Payment_Type"};
        int[] indexes = index.getLabelPos(indexesNames);

        workingOnFile(index, indexes);



        // Find all Keys :
        Set<List<String>> keysSet = index.keys.keySet();

        List<List<String>> indexWanted = new ArrayList<>();
        String[] labelWanted = {"vendor_name"};
        String[] valueWanted = {"VTS"};

        int[] indexAsked = index.getUserNamePos(labelWanted, indexesNames);
        index.retrieveNarrowerIndex(keysSet, indexWanted, indexAsked, valueWanted);

        System.out.println(indexWanted);
        List<List<Long>> indexList = index.getBytesOffsetList(indexWanted);


        for (List<Long> longs : indexList) System.out.println(longs);
//        seekPosition(indexWanted, index);







    }
}
