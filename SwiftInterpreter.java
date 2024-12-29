import java.util.HashMap;
import java.util.Map;

public class SwiftInterpreter {
    private final Map<String, Integer> variables = new HashMap<>(); // Variable storage

    public void eval(String code) {
        String[] lines = code.split(";"); // Split by statement terminator
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;

            // Handle variable assignment
            if (line.contains("=")) {
                handleAssignment(line);
            }
            // Handle print statements
            else if (line.startsWith("print")) {
                handlePrint(line);
            }
        }
    }

    private void handleAssignment(String line) {
        String[] parts = line.split("=");
        String varName = parts[0].trim();
        String expression = parts[1].trim();

        // Parse and evaluate the expression
        int value = evaluateExpression(expression);

        // Store the result in the variables map
        variables.put(varName, value);
    }

    private int evaluateExpression(String expression) {
        // Handle basic arithmetic operations (+, -, *, /, %)
        String[] operators = { "\\+", "-", "\\*", "/", "%" };

        for (String operator : operators) {
            String[] operands = expression.split(operator);
            if (operands.length == 2) {
                int left = Integer.parseInt(operands[0].trim());
                int right = Integer.parseInt(operands[1].trim());
                switch (operator) {
                    case "\\+" -> {
                        return left + right;
                    }
                    case "-" -> {
                        return left - right;
                    }
                    case "\\*" -> {
                        return left * right;
                    }
                    case "/" -> {
                        return left / right;
                    }
                    case "%" -> {
                        return left % right;
                    }
                }
            }
        }
        return Integer.parseInt(expression); // If no operator, return the number itself
    }

    private void handlePrint(String line) {
        String varName = line.substring(line.indexOf('(') + 1, line.indexOf(')')).trim();
        System.out.println(variables.get(varName));
    }

    public static void main(String[] args) {
        SwiftInterpreter interpreter = new SwiftInterpreter();

        // Example program: Calculate and print the sum of 10 and 20, product of 4 and 5
        String program = """
            sum = 10 + 20;
            product = 4 * 5;
            print(sum);
            print(product);
        """;

        interpreter.eval(program);
    }
}

