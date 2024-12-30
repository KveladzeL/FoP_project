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
            // Handle For loop
            else if (line.startsWith("for")) {
                i = handleForLoop(lines, i);
            }
        }
    }

    private void handleAssignment(String line) {
        // Handle assignment: let <var> = <expression>
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

        // Check if it's a variable reference
        if (variables.containsKey(expression)) {
            return variables.get(expression);
        }

        // Evaluate expressions with basic operators
        String[] operators = {"\\+", "-", "\\*", "/", "%"};
        for (String operator : operators) {
            String[] operands = expression.split(operator);
            if (operands.length == 2) {
                int left = evaluateExpression(operands[0].trim());
                int right = evaluateExpression(operands[1].trim());

                switch (operator) {
                    case "\\+" -> { return left + right; }
                    case "-" -> { return left - right; }
                    case "\\*" -> { return left * right; }
                    case "/" -> { return left / right; }
                    case "%" -> { return left % right; }
                }
            }
        }

        // If no operators, treat as a direct integer
        try {
            return Integer.parseInt(expression);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid expression: " + expression);
        }
    }

    private void handlePrint(String line) {
        // Extract variable from print statement: print(sum)
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

    private int handleForLoop(String[] lines, int currentIndex) {
        String line = lines[currentIndex].trim();
        String[] parts = line.substring(4).split("in");
        String loopVar = parts[0].trim();
        String rangeStr = parts[1].trim();

        // Ensure range is properly parsed
        String[] range = rangeStr.replace("{", "").trim().split("\\.\\.\\.");
        if (range.length != 2) {
            throw new RuntimeException("Invalid range format: " + rangeStr);
        }

        int start = Integer.parseInt(range[0].trim());
        int end = Integer.parseInt(range[1].trim());

        int startBlockIndex = currentIndex + 1;
        int endBlockIndex = findBlockEnd(lines, startBlockIndex);

        for (int i = start; i <= end; i++) {
            variables.put(loopVar, i);
            for (int j = startBlockIndex; j < endBlockIndex; j++) {
                eval(lines[j]);
            }
        }

        return endBlockIndex;
    }

    private int findBlockEnd(String[] lines, int startIndex) {
        int braceCount = 0;
        for (int i = startIndex; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.equals("{")) braceCount++;
            if (line.equals("}")) braceCount--;
            if (braceCount == 0) return i;
        }
        throw new RuntimeException("Missing closing brace");
    }

    public static void main(String[] args) {
        SwiftInterpreter interpreter = new SwiftInterpreter();

        // Example program with if-else
        String program = """
        let b = 10
        let a = 20
        let sum = a + b
        print(sum)
        """;

        interpreter.eval(program);
    }
}
