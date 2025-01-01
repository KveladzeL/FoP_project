import java.util.*;
import java.util.regex.*;

public class NumberIsPrimeCheck {

    private final Map<String, Integer> variables = new HashMap<>();

    public void eval(String code) {
        String[] lines = code.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty()) continue;

            if (line.startsWith("let") || line.startsWith("var")) {
                handleAssignment(line);
            } else if (line.startsWith("print")) {
                handlePrint(line);
            } else if (line.startsWith("while")) {
                i = handleWhile(lines, i);
            } else if (line.startsWith("if")) {
                i = handleIfElse(lines, i);
            } else if (line.startsWith("for")) {
                i = handleForLoop(lines, i);
            } else if (line.contains("=")) {
                handleAssignment(line);
            }
        }
    }

    private void handleAssignment(String line) {
        String[] parts = line.contains("let") || line.contains("var") 
            ? line.substring(4).split("=") 
            : line.split("=");

        String varName = parts[0].trim();
        String expression = parts[1].trim();
        variables.put(varName, evaluateExpression(expression));
    }

    private int evaluateExpression(String expression) {
        expression = expression.replaceAll("\\s+", "");
    
        if (variables.containsKey(expression)) {
            return variables.get(expression);
        }
    
        if (expression.matches("\\d+")) {
            return Integer.parseInt(expression);
        }
    
        // Handle sqrt() function
        if (expression.startsWith("sqrt(") && expression.endsWith(")")) {
            String inner = expression.substring(5, expression.length() - 1);
            return (int) Math.sqrt(evaluateExpression(inner));
        }
    
        // Handle Double() function (just parse it as an integer for simplicity)
        if (expression.startsWith("Double(") && expression.endsWith(")")) {
            String inner = expression.substring(7, expression.length() - 1);
            return evaluateExpression(inner);
        }
    
        // Handle Int() function
        if (expression.startsWith("Int(") && expression.endsWith(")")) {
            String inner = expression.substring(4, expression.length() - 1);
            return evaluateExpression(inner);
        }
    
        String[] operators = {"\\+", "-", "\\*", "/", "%"};
        for (String operator : operators) {
            String[] operands = expression.split(operator);
            if (operands.length == 2) {
                int left = evaluateExpression(operands[0]);
                int right = evaluateExpression(operands[1]);
                return switch (operator) {
                    case "\\+" -> left + right;
                    case "-" -> left - right;
                    case "\\*" -> left * right;
                    case "/" -> left / right;
                    case "%" -> left % right;
                    default -> throw new RuntimeException("Unknown operator: " + operator);
                };
            }
        }
    
        throw new RuntimeException("Invalid expression: " + expression);
    }
    

    private void handlePrint(String line) {
        String content = line.substring(line.indexOf('(') + 1, line.indexOf(')')).trim();
        if (variables.containsKey(content)) {
            System.out.println(variables.get(content));
        } else {
            System.out.println(content);
        }
    }

    private int handleWhile(String[] lines, int currentIndex) {
        String conditionLine = lines[currentIndex].trim();
        String condition = conditionLine.substring(conditionLine.indexOf('(') + 1, conditionLine.indexOf(')')).trim();

        int startBlockIndex = currentIndex + 1;
        int endBlockIndex = findBlockEnd(lines, startBlockIndex);

        while (evaluateCondition(condition)) {
            for (int i = startBlockIndex; i < endBlockIndex; i++) {
                eval(lines[i]);
            }
        }

        return endBlockIndex;
    }

    private boolean evaluateCondition(String condition) {
        String[] operators = {"<=", ">=", "==", "!=", "<", ">"};
        for (String operator : operators) {
            if (condition.contains(operator)) {
                String[] operands = condition.split(Pattern.quote(operator));
                int left = evaluateExpression(operands[0].trim());
                int right = evaluateExpression(operands[1].trim());
                return switch (operator) {
                    case "<=" -> left <= right;
                    case ">=" -> left >= right;
                    case "==" -> left == right;
                    case "!=" -> left != right;
                    case "<" -> left < right;
                    case ">" -> left > right;
                    default -> false;
                };
            }
        }
        throw new RuntimeException("Invalid condition: " + condition);
    }

    private int handleIfElse(String[] lines, int currentIndex) {
        String conditionLine = lines[currentIndex].trim();
        String condition = conditionLine.substring(conditionLine.indexOf('(') + 1, conditionLine.indexOf(')')).trim();

        int startBlockIndex = currentIndex + 1;
        int endBlockIndex = findBlockEnd(lines, startBlockIndex);

        if (evaluateCondition(condition)) {
            for (int i = startBlockIndex; i < endBlockIndex; i++) {
                eval(lines[i]);
            }
            return endBlockIndex;
        }

        int elseIndex = endBlockIndex + 1;
        if (elseIndex < lines.length && lines[elseIndex].trim().startsWith("else")) {
            int elseBlockIndex = findBlockEnd(lines, elseIndex + 1);
            for (int i = elseIndex + 1; i < elseBlockIndex; i++) {
                eval(lines[i]);
            }
            return elseBlockIndex;
        }

        return endBlockIndex;
    }

    private int handleForLoop(String[] lines, int currentIndex) {
        String line = lines[currentIndex].trim();
        String[] parts = line.substring(4).split("in");
        String loopVar = parts[0].trim();
        String[] range = parts[1].replace("{", "").replace("}", "").trim().split("\\.\\.\\.");
        if (range.length != 2) {
            throw new RuntimeException("Invalid range: " + parts[1]);
        }
        int start = evaluateExpression(range[0].trim());
        int end = evaluateExpression(range[1].trim());


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
        NumberIsPrimeCheck interpreter = new NumberIsPrimeCheck();

        String program = """
        let number = 14
        let isPrime = 1

        if (number <= 1) {
            isPrime = 0
        } else {
            for i in 2...Int(sqrt(Double(number))) {
                if (number % i == 0) {
                    isPrime = 0
                    break
                }

            }
        }
        print(isPrime) 
        
        """;

        interpreter.eval(program);
    }
}

