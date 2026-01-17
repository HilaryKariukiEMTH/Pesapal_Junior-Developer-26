import java.util.Scanner;

public class REPL {
    private final SQLParser parser;

    public REPL(Database database) {
        this.parser = new SQLParser(database);
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Simple RDBMS - Interactive Mode");
        System.out.println("Type 'exit' to quit\n");

        while (true) {
            System.out.print("sql> ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Goodbye!");
                break;
            }

            if (input.isEmpty()) {
                continue;
            }

            String result = parser.execute(input);
            System.out.println(result);
            System.out.println();
        }

        scanner.close();
    }
}
