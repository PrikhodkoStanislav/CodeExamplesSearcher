import java.util.Scanner;

/**
 * Created by Станислав on 28.09.2016.
 */
public class Main {
    public static void test1() {
        String funName = "strcpy";
        String pathToFile = "../Linux/linux-4.8-rc8/arch/alpha/boot/bootp.c";
        Searcher searcher = new Searcher();
        String result = searcher.search(funName, pathToFile);
        System.out.println(result);
    }

    public static void test2() {
        String funName = "strcpy";
        String pathToFile = "../Linux/linux-4.8-rc8/arch/alpha/boot/";
        Searcher searcher = new Searcher();
        String result = searcher.search(funName, pathToFile);
        System.out.println(result);
    }

    public static void test3() {
        String funName = "printk";
        String pathToFile = "../Linux/linux-4.8-rc8/";
        Searcher searcher = new Searcher();
        String result = searcher.search(funName, pathToFile);
        System.out.println(result);
    }

    public static void main(String[] args) {
//        Scanner in = new Scanner(System.in);
//        System.out.println("Input function name: ");
//        String funName = in.nextLine();
//        System.out.println("Input path to the file: ");
//        String pathToFile = in.nextLine();

//        test1();
//        test2();
        test3();


    }
}
