import java.io.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Станислав on 28.09.2016.
 */
public class Searcher {

    public String search(String functionName, String pathToFile) {
        StringBuilder sb = new StringBuilder();
        final String newLine = "\n";

        File file = new File(pathToFile);
        if (!file.exists()) {
            System.out.println("Wrong path to the file!");
            return "";
        }
        if (file.isDirectory()) {
            File[] filesInDirectory = file.listFiles();
            for (File f : filesInDirectory) {
                String res = search(functionName, f.getPath());
                if ((res.length() > 0) && f.isFile()) {
                    sb.append(f.getPath() + newLine);
                    sb.append(search(functionName, f.getPath()));
                }
            }
            return sb.toString();
        }

        try {
            FileReader fileReader = new FileReader(pathToFile);
            BufferedReader in = new BufferedReader(fileReader);
            String str = "";
            final int lengthOfBuffer = 2;
            int numberOfExample = 0;
            int strNumber = 0;
            List<String> buffer = new LinkedList<>();

            while ((str = in.readLine()) != null) {
                strNumber++;
                if (str.contains(functionName)) {
                    numberOfExample++;
                    sb.append("Example " + numberOfExample + " :" + " str " + strNumber + " :" + newLine);
                    sb.append("----------" + newLine);

                    for(String s : buffer) {
                        sb.append(s + newLine);
                    }

                    buffer.clear();
                    sb.append(str + newLine);

                    for (int i = 0; i < lengthOfBuffer; i++) {
                        if (((str = in.readLine()) != null)) {
                            sb.append(str + newLine);
                        }
                    }
                    sb.append("----------" + newLine);
                    sb.append(newLine);
                } else {
                    buffer.add(str);
                    if (buffer.size() > lengthOfBuffer) {
                        buffer.remove(0);
                    }
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
