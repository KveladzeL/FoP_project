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
            // Handle If-Else Statements
            else if (line.startsWith("if")) {
                i = handleIfElse(lines, i);
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

    private int handleIfElse(String[] lines, int currentIndex) {
        String conditionLine = lines[currentIndex].trim();
        String condition = conditionLine.substring(conditionLine.indexOf('(') + 1, conditionLine.indexOf(')')).trim();

        boolean conditionResult = evaluateCondition(condition);

        // Find the block of code for the if-else statement
        int startBlockIndex = currentIndex + 1;
        int endBlockIndex = startBlockIndex;

        // Find the start and end of the if block (if enclosed by braces)
        while (endBlockIndex < lines.length && !lines[endBlockIndex].trim().equals("}")) {
            endBlockIndex++;
        }

        // Check if the block is valid
        if (endBlockIndex >= lines.length) {
            throw new RuntimeException("Missing closing brace for if block");
        }

        // Execute the block based on the condition
        if (conditionResult) {
            // Execute the block of code under the 'if' part
            for (int i = startBlockIndex; i < endBlockIndex; i++) {
                eval(lines[i]);
            }
        } else {
            // Check for 'else' part after the 'if' block
            int elseBlockStart = endBlockIndex + 1;
            if (elseBlockStart < lines.length && lines[elseBlockStart].trim().startsWith("else")) {
                int elseStartBlockIndex = elseBlockStart + 1;
                int elseEndBlockIndex = elseStartBlockIndex;

                // Find the end of the else block
                while (elseEndBlockIndex < lines.length && !lines[elseEndBlockIndex].trim().equals("}")) {
                    elseEndBlockIndex++;
                }

                // Execute the block of code under the 'else' part
                for (int i = elseStartBlockIndex; i < elseEndBlockIndex; i++) {
                    eval(lines[i]);
                }

                return elseEndBlockIndex; // Return the index after the else block
            }
        }

        return endBlockIndex; // Return the index after the closing brace of the if block
    }

    public static void main(String[] args) {
        SwiftInterpreter interpreter = new SwiftInterpreter();

        // Example program with if-else
        String program = """
           let number = 10
           if (number > 0) {
               print(number)
           }
           else {
               print(number)
           }
        """;

        interpreter.eval(program);
    }
}
