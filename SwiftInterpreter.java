import java.util.HashMap;
import java.util.Map;

public class SwiftInterpreter {
    private final Map<String, Value> variables = new HashMap<>(); // Variable storage

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

        if (expression.startsWith("\"") && expression.endsWith("\"")) {
            // It's a String
            String stringValue = expression.substring(1, expression.length() - 1); // Strip quotes
            variables.put(varName, new StringValue(stringValue));
        } else {
            // If it's a number or expression, evaluate it
        Value value = evaluateExpression(expression); 
        variables.put(varName, value); // Store the evaluated value;
        }

    }

    private Value evaluateExpression(String expression) {
        // Remove spaces
        expression = expression.replaceAll("\\s+", "");
    
        // Handle basic arithmetic operations (+, -, *, /, %)
        String[] operators = { "\\+", "-", "\\*", "/", "%" };
    
        for (String operator : operators) {
            // Split by operator
            String[] operands = expression.split(operator);
            // Check if valid split into two operands
            if (operands.length == 2) {
                int left = Integer.parseInt(operands[0].trim());
                int right = Integer.parseInt(operands[1].trim());
                switch (operator) {
                    case "\\+" -> {
                        return new IntValue(left + right); // Return an IntValue object
                    }
                    case "-" -> {
                        return new IntValue(left - right); // Return an IntValue object
                    }
                    case "\\*" -> {
                        return new IntValue(left * right); // Return an IntValue object
                    }
                    case "/" -> {
                        return new IntValue(left / right); // Return an IntValue object
                    }
                    case "%" -> {
                        return new IntValue(left % right); // Return an IntValue object
                    }
                }
            }
        }
    
        // If no operator, assume it's a direct integer value
        return new IntValue(Integer.parseInt(expression)); // Return an IntValue object
    }
    
    private void handlePrint(String line) {
        String varName = line.substring(line.indexOf('(') + 1, line.indexOf(')')).trim();
        Value value = variables.get(varName);
        if (value instanceof IntValue) {
            System.out.println(((IntValue) value).getValue());
        } else if (value instanceof StringValue) {
            System.out.println(((StringValue) value).getValue());
        }
    }

    public static void main(String[] args) {
        SwiftInterpreter interpreter = new SwiftInterpreter();

        // Example program: Calculate and print the sum of 10 and 20, product of 4 and 5
        String program = """
            sum = 10 + 20;
            product = 4 * 5;
            message = "Hello World";
            print(sum);
            print(product);
            print(message);
        """;

        interpreter.eval(program);
    }
}

