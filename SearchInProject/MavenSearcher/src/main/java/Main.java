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
        String pathToFile = "../Linux/linux-4.8-rc8/kernel/power";
        Searcher searcher = new Searcher();
        String result = searcher.search(funName, pathToFile);
        System.out.println(result);
    }

    public static void main(String[] args) {
        Searcher searcher = new Searcher();
        System.out.println(searcher.search(args[0], args[1]));
    }
}

