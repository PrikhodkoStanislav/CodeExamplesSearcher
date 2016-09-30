import java.util.Scanner;

/**
 * Created by Станислав on 28.09.2016.
 */
public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.println("Input function name: ");
        String funName;// = in.nextLine();
        System.out.println("Input path to the file: ");
        String pathToFile;// = in.nextLine();

        funName = "strcpy";
        String pathToFile1 = "../Linux/linux-4.8-rc8/arch/alpha/boot/bootp.c";
        pathToFile = "../Linux/linux-4.8-rc8/arch/alpha/boot/";
        Searcher searcher = new Searcher();
        String result = searcher.search(funName, pathToFile);
        System.out.println(result);
    }
}
