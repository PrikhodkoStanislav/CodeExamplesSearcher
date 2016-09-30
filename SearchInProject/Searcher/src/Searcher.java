import java.io.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Станислав on 28.09.2016.
 */
public class Searcher {

    public void search(String functionName, String pathToFile) {
        try {
            BufferedReader in = new BufferedReader(new FileReader(pathToFile));
            String str;

            final int lengthOfBuffer = 2;
            int numberOfExample = 1;
            List<String> buffer = new LinkedList<>();

            while ((str = in.readLine()) != null) {
                if (str.contains(functionName)) {
                    System.out.println("Example " + numberOfExample + ":");
                    System.out.println();

                    for(String s : buffer) {
                        System.out.println(s);
                    }

                    buffer.clear();
                    System.out.println(str);

                    for (int i = 0; i < lengthOfBuffer; i++) {
                        if (((str = in.readLine()) != null)) {
                            System.out.println(str);
                        }
                    }

                    System.out.println();
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
    }
}
