import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;  // Import the Scanner class

public class Reader {
    public static List<String>  input(){
        Scanner scanner = new Scanner(System.in);
        List<String> input1 = new ArrayList<>();
        while (true){
            String input = scanner.nextLine();
            if (input.isEmpty()){
                break;
            }
            String[] split = input.split(" ");
            Collections.addAll(input1, split);
        }
        return input1;
    }
}
