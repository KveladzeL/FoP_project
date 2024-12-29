import java.util.HashMap;
import java.util.Map;

public class SwiftInterpreter {
    private final Map<String, Value> variables = new HashMap<>(); // Variable storage

    public void eval(String code) {
        String[] lines = code.split(";"); // Split by statement terminator
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty()) continue;

            // Handle if-else block
            if(line.startsWith("if")) {
                i = handleIfElse(lines, i);
            }
            // Handle variable assignment
            else if (line.contains("=")) {
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
            variables.put(varName, value); // Store the evaluated value
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
                        return new IntValue(left + right); 
                    }
                    case "-" -> {
                        return new IntValue(left - right); 
                    }
                    case "\\*" -> {
                        return new IntValue(left * right); 
                    }
                    case "/" -> {
                        return new IntValue(left / right); 
                    }
                    case "%" -> {
                        return new IntValue(left % right); 
                    }
                }
            }
        }
        
        // If no operator, assume it's a direct integer value
        return new IntValue(Integer.parseInt(expression)); // Return an IntValue object
    }
    
    private void handlePrint(String line) {
        String varName = line.substring(line.indexOf('(') + 1, line.indexOf(')')).trim();
        Value value = variables.get(varName);  // Fetch updated value of the variable
        if (value instanceof IntValue) {
            System.out.println(((IntValue) value).getValue());
        } else if (value instanceof StringValue) {
            System.out.println(((StringValue) value).getValue());  // Print updated string value
        }
    }
    
    
    private int handleIfElse(String[] lines, int currentIndex) {
        String conditionLine = lines[currentIndex];
        String condition = conditionLine.substring(conditionLine.indexOf('(') + 1, conditionLine.indexOf(')')).trim();
    
        boolean conditionResult = evaluateCondition(condition);
    
        // Extract if block and optional else block, including possible nested blocks
        String ifBlock = lines[currentIndex + 1].trim();
        String elseBlock = (currentIndex + 2 < lines.length) ? lines[currentIndex + 2].trim() : "";
    
        if (conditionResult) {
            executeBlock(ifBlock); // Execute if block
            return elseBlock.startsWith("else") ? currentIndex + 2 : currentIndex + 1; // Skip to next block or statement
        } else if (elseBlock.startsWith("else")) {
            executeBlock(elseBlock); // Execute else block
            return currentIndex + 2;
        }
    
        return currentIndex + 1; // Continue with the next line
    }
    
    private void executeBlock(String block) {
        // Extract and execute each statement in the block
        String[] statements = block.substring(1, block.length() - 1).split(";");
        for (String statement : statements) {
            statement = statement.trim();
            if (!statement.isEmpty()) {
                eval(statement); // Reuse eval to handle each statement
            }
        }
    }    
    
    private boolean evaluateCondition(String condition) {
        String[] operators = { "==", "!=", ">", "<", ">=", "<=" };
    
        for (String operator : operators) {
            String[] operands = condition.split(operator);
            if (operands.length == 2) {
                String left = operands[0].trim();
                String right = operands[1].trim();
    
                // Check if left is a variable or a literal value (integer or string)
                Value leftValueObj = variables.get(left);
                int leftIntValue = 0;
                String leftStringValue = null;
    
                if (leftValueObj instanceof IntValue) {
                    leftIntValue = ((IntValue) leftValueObj).getValue();  // Get integer value
                } else if (leftValueObj instanceof StringValue) {
                    leftStringValue = ((StringValue) leftValueObj).getValue();  // Get string value
                } else {
                    // Parse literal value for left
                    try {
                        leftIntValue = Integer.parseInt(left);
                    } catch (NumberFormatException e) {
                        leftStringValue = left; // Assume it's a string if not an integer
                    }
                }
    
                // Check if right is a variable or a literal value (integer or string)
                int rightIntValue = 0;
                String rightStringValue = null;
    
                if (variables.containsKey(right)) {
                    Value rightValueObj = variables.get(right);
                    if (rightValueObj instanceof IntValue) {
                        rightIntValue = ((IntValue) rightValueObj).getValue();  // Get integer value
                    } else if (rightValueObj instanceof StringValue) {
                        rightStringValue = ((StringValue) rightValueObj).getValue();  // Get string value
                    }
                } else {
                    // Parse literal value for right
                    try {
                        rightIntValue = Integer.parseInt(right);
                    } catch (NumberFormatException e) {
                        rightStringValue = right; // Assume it's a string if not an integer
                    }
                }
    
                // Perform the comparison based on the operator
                switch (operator) {
                    case "==":
                        if (leftStringValue != null && rightStringValue != null) {
                            return leftStringValue.equals(rightStringValue);  // Compare strings
                        } else {
                            return leftIntValue == rightIntValue;  // Compare integers
                        }
                    case "!=":
                        if (leftStringValue != null && rightStringValue != null) {
                            return !leftStringValue.equals(rightStringValue);
                        } else {
                            return leftIntValue != rightIntValue;
                        }
                    case ">":
                        return leftIntValue > rightIntValue;
                    case "<":
                        return leftIntValue < rightIntValue;
                    case ">=":
                        return leftIntValue >= rightIntValue;
                    case "<=":
                        return leftIntValue <= rightIntValue;
                    default:
                        return false;
                }
            }
        }
    
        return false;
    }

    public static void main(String[] args) {
        SwiftInterpreter interpreter = new SwiftInterpreter();

        // Example program:
        String program = """
            sum = 10 + 20;
            product = 4 * 5;
            message = "Hello World";
            print(sum);
            print(product);
            print(message);

            x = 15;
            if (x > 10) {
                message = "x is greater than 10";
                print(message);
            } else {
                message = "x is 10 or less";
                print(message);
            }
        """; 

        interpreter.eval(program);
    }
}

