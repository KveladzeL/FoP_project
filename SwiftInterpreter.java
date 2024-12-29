import java.util.HashMap;
import java.util.Map;

public class SwiftInterpreter {
    private final Map<String, Integer> variables = new HashMap<>(); // Variable storage

    public void eval(String code) {
        String[] lines = code.split("\n"); // Split by newline for Swift-style statements
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;

            // Handle variable assignment
            if (line.startsWith("let")) {
                handleAssignment(line);
            }
            // Handle print statements
            else if (line.startsWith("print")) {
                handlePrint(line);
            }
        }
    }

    private void handleAssignment(String line) {
        // Handle assignment
        String[] parts = line.split("=");
        String varName = parts[0].trim().substring(4).trim(); // Remove "let" keyword
        String expression = parts[1].trim();

        // Evaluate the expression
        int value = evaluateExpression(expression);
        variables.put(varName, value); // Store the evaluated value
    }

    private int evaluateExpression(String expression) {
        // Remove spaces for ease of parsing
        expression = expression.replaceAll("\\s+", "");

        // First, check for each operator in the order of precedence
        String[] operators = {"\\+", "-", "\\*", "/", "%"};
        for (String operator : operators) {
            // Split by the operator
            String[] operands = expression.split(operator);

            // If the split results in exactly two operands, evaluate the expression
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

        // If no operators were found, treat it as a direct integer
        return Integer.parseInt(expression);
    }

    private void handlePrint(String line) {
        // Extract the variable name from the print statement: print(sum)
        String varName = line.substring(line.indexOf('(') + 1, line.indexOf(')')).trim();
        Integer value = variables.get(varName);
        if (value != null) {
            System.out.println(value);
        } else {
            System.out.println("Variable not found: " + varName);
        }
    }

    public static void main(String[] args) {
        SwiftInterpreter interpreter = new SwiftInterpreter();

        // Example program:
        String program = """
            let sum = 10 + 20
            let product = 4 * 5
            let difference = 15 - 5
            let quotient = 20 / 4
            let remainder = 10 % 3
            print(sum)
            print(product)
            print(difference)
            print(quotient)
            print(remainder)
        """;

        interpreter.eval(program);
    }
}
