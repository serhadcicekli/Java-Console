# Java-Console
An easy-to-integrate shell system for Java
# Example
### DemoProgram.java
```java
import com.serhadcicekli.console.*;
public class DemoProgram extends ConsoleProgram {

    @Override
    public void start(DataProvider dataProvider) {
        print("Welcome to Demo Program! Write exit to exit from the program.");
    }

    @Override
    public void input(DataProvider dataProvider) {
        if(dataProvider.str("arg0").equals("exit")){
            print("Goodbye!");
            end();
            return;
        }
        print("You wrote " + dataProvider.str("input"));
        print("Argument count: " + dataProvider.itg("argc"));
        print("Arguments:");
        print("----------");
        int argumentCount = dataProvider.itg("argc"); //Shortened name of InTeGer
        for(int i = 0; i < argumentCount; i++){
            print("Argument " + i + ": " + dataProvider.str("arg" + i));
        }
    }
}
```

### Main.java
```java
import com.serhadcicekli.console.*;
import java.util.Scanner;
public class Main {
    public static void main(String args[]){
        ConsoleCore core = new ConsoleCore();
        core.init(dataProvider -> {
            String method = dataProvider.str("method");
            if(method.equals("out")){
                if(dataProvider.str("stream").equals("console")){
                    System.out.println(dataProvider.str("content"));
                }
            }
            if(method.equals("shutdown")){
                System.err.println("Goodbye!");
                System.exit(0);
            }
        }, 0, 100, 50);
        core.addExecutor(ConsoleProgram.firstArgumentEquals("demo", DemoProgram.class));
        Scanner scanner = new Scanner(System.in);
        while(true){
            System.out.println("");
            System.out.print("$ ");
            String line = scanner.nextLine();
            core.input(line);
        }
    }
}
```
