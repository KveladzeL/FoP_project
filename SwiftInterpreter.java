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
            else if (line.contains("=")) { // Handle reassignment (not starting with 'let')
                handleAssignment(line);
            }
        }
    }

    private void handleAssignment(String line) {
        // Handle assignment: let <var> = <expression>
        String[] parts = line.split("=");
        String varName = ""; // Declare varName outside of the if-else block
    
        if (line.contains("let")) {
            varName = parts[0].trim().substring(4).trim(); // Remove "let" keyword
        } else {
            varName = parts[0].trim();
        }
    
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
        // Extract content inside the print statement: print(1) or print(sum)
        String content = line.substring(line.indexOf('(') + 1, line.indexOf(')')).trim();
    
        // If the content is a variable, fetch it from the map
        if (variables.containsKey(content)) {
            System.out.println(variables.get(content));
        } else {
            try {
                // Try parsing the content as a number (literal value)
                int value = Integer.parseInt(content);
                System.out.println(value);
            } catch (NumberFormatException e) {
                // If it's neither a variable nor a valid number, print error
                System.out.println("Variable or value not found: " + content);
            }
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
        let number = 121
let originalNumber = number
let reversedNumber = 0

while (originalNumber != 0) {
    let digit = originalNumber % 10
    reversedNumber = reversedNumber * 10 + digit
    originalNumber = originalNumber / 10
}

if (number == reversedNumber) {
    print(1)
} 
else {
    print(0)
}
        """;

        interpreter.eval(program);
    }
}
