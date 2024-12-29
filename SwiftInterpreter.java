import java.util.HashMap;
import java.util.Map;

public class SwiftInterpreter {
    private final Map<String, Integer> variables = new HashMap<>(); // Variable storage

    public void eval(String code) {
        String[] lines = code.split("\n"); // Split by newline for Swift-style statements
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty()) continue;

            // Handle variable assignment
            if (line.startsWith("let")) {
                handleAssignment(line);
            }
            // Handle print statements
            else if (line.startsWith("print")) {
                handlePrint(line);
            }
            // Handle while loops
            else if (line.startsWith("while")) {
                i = handleWhile(lines, i);
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
    
        // First, check if the expression is a variable
        if (variables.containsKey(expression)) {
            return variables.get(expression); // Return the value of the variable
        }
    
        // Check for each operator in the order of precedence
        String[] operators = {"\\+", "-", "\\*", "/", "%"};
        for (String operator : operators) {
            // Split by the operator
            String[] operands = expression.split(operator);
    
            // If the split results in exactly two operands, evaluate the expression
            if (operands.length == 2) {
                int left = evaluateExpression(operands[0].trim()); // Recursively evaluate left operand
                int right = evaluateExpression(operands[1].trim()); // Recursively evaluate right operand
    
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
        try {
            return Integer.parseInt(expression); // If it's an integer, parse it
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid expression: " + expression);
        }
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

    private int handleWhile(String[] lines, int currentIndex) {
        String conditionLine = lines[currentIndex].trim();
        String condition = conditionLine.substring(conditionLine.indexOf('(') + 1, conditionLine.indexOf(')')).trim();

        // Extract the block of the while loop
        int startBlockIndex = currentIndex + 1;
        int endBlockIndex = startBlockIndex;

        // Find the block enclosed by braces
        while (endBlockIndex < lines.length && !lines[endBlockIndex].trim().equals("}")) {
            endBlockIndex++;
        }

        // Check if block is valid
        if (endBlockIndex >= lines.length) {
            throw new RuntimeException("Missing closing brace for while loop");
        }

        // Execute the loop
        while (evaluateCondition(condition)) {
            for (int i = startBlockIndex; i < endBlockIndex; i++) {
                eval(lines[i]);
            }
        }

        return endBlockIndex; // Return the index after the closing brace
    }

    private boolean evaluateCondition(String condition) {
        String[] operators = {"<=", ">=", "==", "!=", "<", ">"};
        for (String operator : operators) {
            if (condition.contains(operator)) {
                String[] operands = condition.split(operator);
                int left = evaluateExpression(operands[0]);
                int right = evaluateExpression(operands[1]);

                return switch (operator) {
                    case "<=" -> left <= right;
                    case ">=" -> left >= right;
                    case "==" -> left == right;
                    case "!=" -> left != right;
                    case "<" -> left < right;
                    case ">" -> left > right;
                    default -> throw new RuntimeException("Invalid operator in condition: " + operator);
                };
            }
        }
        throw new RuntimeException("Invalid condition: " + condition);
    }

    public static void main(String[] args) {
        SwiftInterpreter interpreter = new SwiftInterpreter();
        
        // Example program with while loop
        String program = """
            let sum = 1
            let i = 6
            while (i > 0) {
                let sum = sum * i
                let i = i - 1
            }
            print(sum)
        """;

        interpreter.eval(program);
    }
}
