public class Printer {
    private static String log = "";

    public static void println(String string) {
        log += string + "\n";
        System.out.println(string);
    }

    public static void println(int num) {
        log += Integer.toString(num) + "\n";
        System.out.println(num);
    }

    public static void println() {
        log += "\n";
        System.out.println();
    }

    public static void print(String string) {
        log += string;
        System.out.print(string);
    }

    public static void printLog() {
        System.out.print(log);
    }
}
