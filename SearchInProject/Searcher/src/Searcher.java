import java.io.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Станислав on 28.09.2016.
 */
public class Searcher {

    public String search(String functionName, String pathToFile) {
        String result = "";
        try {
            BufferedReader in = new BufferedReader(new FileReader(pathToFile));
            String str = "";
            final String newLine = "\n";

            final int lengthOfBuffer = 2;
            int numberOfExample = 1;
            List<String> buffer = new LinkedList<>();

            while ((str = in.readLine()) != null) {
                if (str.contains(functionName)) {
                    result += "Example " + numberOfExample + ":" + newLine + newLine;

                    for(String s : buffer) {
                        result += s + newLine;
                    }

                    buffer.clear();
                    result += str + newLine;

                    for (int i = 0; i < lengthOfBuffer; i++) {
                        if (((str = in.readLine()) != null)) {
                            result += str + newLine;
                        }
                    }

                    result += newLine;
                    numberOfExample++;
                }
                else {
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
        return result;
    }
}
