import java.util.HashMap;
import java.util.Map;

public class MultiplicationTable {

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
        // Handle assignment or compound assignment
        String[] compoundOperators = {"+=", "-=", "*=", "/=", "%="};
        
        // Check for compound operators first
        for (String operator : compoundOperators) {
            if (line.contains(operator)) {
                String[] parts = line.split("\\" + operator);
                if (parts.length == 2) {
                    String varName = parts[0].trim();
                    String rightExpr = parts[1].trim();
    
                    if (!variables.containsKey(varName)) {
                        throw new RuntimeException("Variable not defined: " + varName);
                    }
    
                    // Evaluate the right-hand side expression
                    int rightValue = evaluateExpression(rightExpr);
                    int currentValue = variables.get(varName);
    
                    // Perform the operation and update the variable
                    int newValue = switch (operator) {
                        case "+=" -> currentValue + rightValue;
                        case "-=" -> currentValue - rightValue;
                        case "*=" -> currentValue * rightValue;
                        case "/=" -> currentValue / rightValue;
                        case "%=" -> currentValue % rightValue;
                        default -> throw new RuntimeException("Unsupported operator: " + operator);
                    };
    
                    variables.put(varName, newValue);
                    return; // Exit after handling the compound assignment
                }
            }
        }
    
        // If not a compound operator, handle standard assignment
        String[] parts = line.split("=");
        String varName;
        
        if (line.contains("let")) {
            varName = parts[0].trim().substring(4).trim(); // Remove "let" keyword
        } else {
            varName = parts[0].trim();
        }
    
        String expression = parts[1].trim();
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
    
        if (conditionResult) {
            // Execute the block of code under the 'if' part
            for (int i = startBlockIndex; i < endBlockIndex; i++) {
                eval(lines[i]);
            }
    
            // Skip over the 'else' block, if it exists
            if (endBlockIndex + 1 < lines.length && lines[endBlockIndex + 1].trim().startsWith("else")) {
                int elseStartBlockIndex = endBlockIndex + 2;
                int elseEndBlockIndex = findBlockEnd(lines, elseStartBlockIndex);
                return elseEndBlockIndex; // Skip the else block
            }
        } else {
            // Find the 'else' statement, if it exists
            int elseStartLine = endBlockIndex + 1;
        
            // Skip blank lines or comments to find the actual 'else' keyword
            while (elseStartLine < lines.length && lines[elseStartLine].trim().isEmpty()) {
                elseStartLine++;
            }
        
            // Check if the next non-empty line starts with 'else'
            if (elseStartLine < lines.length && lines[elseStartLine].trim().startsWith("else")) {
                // Check if 'else' is followed by a block or a single statement
                int elseBlockStart = elseStartLine + 1; // Next line after 'else'
                if (lines[elseStartLine].trim().endsWith("{")) {
                    // Block starts on the same line as 'else'
                    elseBlockStart = elseStartLine;
                }
        
                // Find the end of the 'else' block
                int elseEndBlockIndex = findBlockEnd(lines, elseBlockStart);
        
                // Execute the 'else' block
                for (int i = elseBlockStart; i < elseEndBlockIndex; i++) {
                    eval(lines[i]);
                }
        
                return elseEndBlockIndex; // Return the index after the 'else' block
            }
        }
        
        return endBlockIndex; // Return the index after the closing brace of the if block
    }
    
    private int handleForLoop(String[] lines, int currentIndex) {
        String line = lines[currentIndex].trim();
        String[] parts = line.substring(4).split("in"); // Split 'for <var> in <range>'
        
        if (parts.length != 2) {
            throw new RuntimeException("Invalid for loop format: " + line);
        }
    
        String loopVar = parts[0].trim(); // Extract loop variable
        String rangeStr = parts[1].trim(); // Extract range
    
        // Clean up and validate range string
        rangeStr = rangeStr.replace("{", "").replace("}", "").trim();
        String[] range = rangeStr.split("\\.\\.\\.");
        
        if (range.length != 2) {
            throw new RuntimeException("Invalid range format: " + rangeStr);
        }
    
        // Evaluate the range values dynamically (supports variables or literals)
        int start = evaluateExpression(range[0].trim());
        int end = evaluateExpression(range[1].trim());
    
        // Locate the start and end of the loop block
        int startBlockIndex = currentIndex + 1; // Start after the 'for' line
        int endBlockIndex = findBlockEnd(lines, startBlockIndex); // Locate closing brace '}'
    
        // Execute the loop
        for (int i = start; i <= end; i++) { // Use a standard for loop
            variables.put(loopVar, i); // Update the loop variable in the map
            // Iterate over the loop block
            for (int j = startBlockIndex; j < endBlockIndex + 1; j++) {
                eval(lines[j]); // Evaluate each line in the loop block
            }
        }
    
        return endBlockIndex; // Return the index after the loop block
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
        MultiplicationTable interpreter = new MultiplicationTable();

        String program = """
            let a = 0
            let b = 1
            let i = 0
            while i <= 5 {
                let temp = a
                a = b
                b = temp + b
                i = i + 1
            }
            print(a)
        """;

        interpreter.eval(program);
    }
}

