package modelTerminal;
import java.util.Scanner;

public class ConsoleUtils {
    public static final Scanner scanner = new Scanner(System.in);

    public static void pause() {
        System.out.println("\nPress ENTER to continue...");
        scanner.nextLine();
    }

    public static void clearLine() {
        System.out.print("\r");
    }
}
