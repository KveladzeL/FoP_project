# Syntax Document for Simple Interpreter (Swift Subset)

## 1. **Introduction**
This document describes the syntax rules for the minimal subset of Swift supported by the interpreter. The interpreter supports basic constructs like variable assignment, arithmetic operations, conditionals, and loops. Advanced Swift features like functions, classes, and collections (arrays, dictionaries, etc.) are not supported in this subset.

## 2. **Variable Assignment**
Variables are declared and assigned using the `var` keyword, followed by the variable name, an assignment operator (`=`), and the value. The value can be an integer.

### Syntax:var <variable_name> = <value>;

### Example:var x = 10; var y = 5;

## 3. **Arithmetic Operations**
The language supports basic arithmetic operations: addition (`+`), subtraction (`-`), multiplication (`*`), division (`/`), and modulus (`%`).

### Syntax:<expression> = <operand1> <operator> <operand2>;

### Operators:
- `+` for addition
- `-` for subtraction
- `*` for multiplication
- `/` for division
- `%` for modulus

### Example:var result = x + y; // Adds x and y var result = x - y; // Subtracts y from x var result = x * y; // Multiplies x and y var result = x / y; // Divides x by y var result = x % y; // Modulus of x by y

## 4. **Conditional Statements (If-Else)**
The language supports basic `if-else` conditionals to control the flow of the program based on conditions.

### Syntax:if <condition> { <statements>; } else { <statements>; }

### Example:if x > y { print("x is greater than y"); } else { print("x is less than or equal to y"); }

## 5. **While Loops**
The language supports `while` loops to iterate through statements as long as a condition is true.

### Syntax:while <condition> { <statements>; }

### Example:var i = 1; while i <= 10 { print(i); i = i + 1; }

## 6. **Print Statements**
The interpreter supports printing output using the `print` function. This can be used to display values or the result of an expression.

### Syntax:print(<expression>);

### Example:print(x); // Outputs the value of x print(x + y); // Outputs the sum of x and y

## 7. **Program Example**
Here is an example program written in the supported syntax of the interpreter:var x = 10; var y = 5; var sum = 0; var i = 1;

while i <= x { sum = sum + i; i = i + 1; }

if sum == 55 { print("The sum of the first (x) numbers is (sum)"); } else { print("There was an error."); }

### Expected Output:The sum of the first 10 numbers is 55

## 8. **Error Handling**
The interpreter will produce an error message in the following cases:
- Invalid variable names (e.g., using reserved keywords).
- Division by zero.
- Syntax errors, such as missing semicolons or mismatched parentheses.

## 9. **Supported Data Types**
This language subset only supports the **integer** data type for variables and expressions.

### Example:var number = 42;

## 10. **Limitations**
- **No functions or recursion**: Functions, closures, and recursion are not supported in this subset.
- **No advanced data structures**: Collections like arrays, dictionaries, and sets are not supported.
- **No type inference**: You must explicitly declare integer types for variables, even though Swift typically allows type inference.

## 11. **Supported Keywords**
- `var` for variable declaration and assignment.
- `if`, `else` for conditional statements.
- `while` for loops.
- `print` for output.

## 12. **Comments (Optional for Documentation)**
Although comments are not part of the syntax for the interpreter, you can add comments within the code for documentation purposes (if your interpreter supports them).















