import java.util.HashMap;
import java.util.Map;

public class MinimalInterpreter {
    private final Map<String, Integer> variables = new HashMap<>();

    public void eval(String code) {
        String[] lines = code.split(";");
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;

            if (line.matches("^[a-zA-Z_][a-zA-Z0-9_]*\\s*=\\s*.*")) {
                handleAssignment(line);
            }
            else if (line.startsWith("PRINT")) {
                handlePrint(line);
            }
            else if (line.startsWith("IF")) {
                handleIfElse(line);
            } else {
                System.out.println("Error: Unrecognized statement -> " + line);
            }
        }
    }

    private void handleAssignment(String line) {
        String[] parts = line.split("=");

        if (parts.length != 2) {
            System.out.println("Error: Invalid assignment statement -> " + line);
            return;
        }

        String varName = parts[0].trim();
        String expression = parts[1].trim();

        try {
            if (!expression.contains("+")) {
                int value = Integer.parseInt(expression);
                variables.put(varName, value);
            } else {
                String[] numbers = expression.split("\\+");
                if (numbers.length != 2) {
                    System.out.println("Error: Invalid arithmetic expression -> " + expression);
                    return;
                }

                int value = Integer.parseInt(numbers[0].trim()) + Integer.parseInt(numbers[1].trim());
                variables.put(varName, value);
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid number format in expression -> " + expression);
        }
    }

    private void handlePrint(String line) {
        String varName = line.substring(line.indexOf('(') + 1, line.indexOf(')')).trim();
        if (variables.containsKey(varName)) {
            System.out.println(variables.get(varName));
        } else {
            System.out.println("Error: Undefined variable -> " + varName);
        }
    }

    private void handleIfElse(String line) {
        String condition = line.substring(line.indexOf('(') + 1, line.indexOf(')')).trim();
        boolean conditionResult = evaluateCondition(condition);

        if (conditionResult) {
            String ifStatement = line.substring(line.indexOf("PRINT") + 5).trim();
            handlePrint(ifStatement);
        } else if (line.contains("ELSE")) {
            String elseStatement = line.substring(line.indexOf("ELSE") + 4).trim();
            handlePrint(elseStatement);
        }
    }

    private boolean evaluateCondition(String condition) {
        String[] parts = condition.split("==");

        if (parts.length != 2) {
            System.out.println("Error: Invalid condition -> " + condition);
            return false;
        }

        String leftPart = parts[0].trim();
        String rightPart = parts[1].trim();

        int leftValue;
        int rightValue;

        try {
            if (variables.containsKey(leftPart)) {
                leftValue = variables.get(leftPart);
            } else {
                leftValue = Integer.parseInt(leftPart);
            }

            if (variables.containsKey(rightPart)) {
                rightValue = variables.get(rightPart);
            } else {
                rightValue = Integer.parseInt(rightPart);
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid number or variable in condition -> " + condition);
            return false;
        }

        return leftValue == rightValue;
    }

    public static void main(String[] args) {
        MinimalInterpreter interpreter = new MinimalInterpreter();
        String program = """
                N = 10 + 20;
                M = 15;
                IF (N == 30) PRINT("N is 30");
                ELSE PRINT("N is not 30");
            """;
        interpreter.eval(program);
    }
}
