import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class Main {
    private static List<Object[]> generateData() {
        return Arrays.asList(new Object[][]{
                // 9 data
                {"VTS", "2009-01-04 02:52:00", "2009-01-04 03:02:00", 1, 2.6299999999999999, -73.991956999999999, 40.721567, 0, 0, -73.993803, 40.695922000000003, "CASH", 8.9000000000000004, 0.5, 0, 0, 0, 9.4000000000000004},
                {"VTS", "2009-01-04 03:31:00", "2009-01-04 03:38:00", 3, 4.5499999999999998, -73.982101999999998, 40.736289999999997, 0, 0, -73.955849999999998, 40.768030000000003, "Credit", 12.1, 0.5, 0, 2, 0, 14.6},
                {"VTS", "2009-01-03 15:43:00", "2009-01-03 15:57:00", 5, 10.35, -74.002587000000005, 40.739747999999999, 0, 0, -73.869983000000005, 40.770225000000003, "Credit", 23.699999999999999, 0, 0, 4.7400000000000002, 0, 28.440000000000001},
                {"VTS", "2009-01-03 15:43:00", "2009-01-03 15:57:00", 1, 10.35, -74.002587000000005, 40.739747999999999, 0, 0, -73.869983000000005, 40.770225000000003, "Credit", 23.699999999999999, 0, 0, 4.7400000000000002, 0, 28.440000000000001},
                {"DDS", "2009-01-01 20:52:58", "2009-01-01 21:14:00", 1, 5, -73.974266999999998, 40.790954999999997, 0, 0, -73.996557999999993, 40.731848999999997, "CREDIT", 14.9, 0.5, 0, 3.0499999999999998, 0, 18.449999999999999},
                {"DDS", "2009-01-24 16:18:23", "2009-01-24 16:24:56", 1, 0.40000000000000002, -74.001580000000004, 40.719382000000003, 0, 0, -74.008377999999993, 40.720350000000003, "CASH", 3.7000000000000002, 0, 0, 0, 0, 3.7000000000000002},
                {"DDS", "2009-01-16 22:35:59", "2009-01-16 22:43:35", 2, 1.2, -73.989806000000002, 40.735005999999998, 0, 0, -73.985021000000003, 40.724494, "CASH", 6.0999999999999996, 0.5, 0, 0, 0, 6.5999999999999996},
                {"DDS", "2009-01-21 08:55:57", "2009-01-21 09:05:42", 1, 0.40000000000000002, -73.984049999999996, 40.743544, 0, 0, -73.980260000000001, 40.748925999999997, "CREDIT", 5.7000000000000002, 0, 0, 1, 0, 6.7000000000000002},
                {"VTS", "2009-01-04 04:31:00", "2009-01-04 04:36:00", 1, 1.72, -73.992635000000007, 40.748362, 0, 0, -73.995585000000005, 40.728307000000001, "CASH", 6.0999999999999996, 0.5, 0, 0, 0, 6.5999999999999996},
        });
    }

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
        // ex bytes offset: 219, 399, 571, 1520
        String path = "src/data/smol.csv";
        int currentLine = 0;
        long currentPoint = 0;
        int row = 0;
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
                row++;

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

        String[] indexesNames = {"vendor_name", "Passenger_Count", "surcharge"};
        int[] indexes = index.getLabelPos(indexesNames);

        workingOnFile(index, indexes);


        // fetching
        List<String> valuesWanted = Arrays.asList("CMT", "4", "0");
        List<Long> bytesOffsetList = index.getBytesOffset(valuesWanted);
//        System.out.println("Number of keys: "+index.keys.size());
//        System.out.println("Number of /elements for " + valueWanted+ ": "+bytesOffsetList.size());
//        System.out.println("bytesOffsetList:                               " + bytesOffsetList);
//        System.out.println("bytesOffsetList.get(0):                        " + bytesOffsetList.get(0));
//        System.out.println("bytesOffsetList.get(bytesOffsetList.size()-1): " + bytesOffsetList.get(bytesOffsetList.size()-1));

        // Find all VTS :
        Set<List<String>> keysSet = index.keys.keySet();
//        System.out.println(keysSet);

        // find nb pasenger = 4 --> 2nd index = 4
        List<List<String>> indexWanted = new ArrayList<>();
        int indexAsked = index.getUserNamePos("Passenger_Count", indexesNames);
        String valueWanted = "4";
        index.retrieveNarrowerIndex(keysSet, indexWanted, indexAsked, valueWanted);

        System.out.println(indexWanted);
        System.out.println("=======================");
        System.out.println("RESULTS\n");
//        for (List<String> strings : indexWanted) {
////            System.out.println(index.getBytesOffset(strings));
//            List<Long> positions = index.getBytesOffset(strings);
//            for(int j = 0; j < positions.size(); j++){
//                Long positionsArray = positions.get(j);
//                testPosition(indexWanted);
//            }
//        }
        seekPosition(indexWanted, index);
        System.out.println("=======================");
//        testPosition(41083);







    }
}
