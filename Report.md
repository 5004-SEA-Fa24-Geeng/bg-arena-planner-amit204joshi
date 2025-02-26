# Report

Submitted report to be manually graded. We encourage you to review the report as you read through the provided
code as it is meant to help you understand some of the concepts. 

## Technical Questions

1. What is the difference between == and .equals in java? Provide a code example of each, where they would return different results for an object. Include the code snippet using the hash marks (```) to create a code block.
   ```java
   // your code here
   BoardGame game1 = new BoardGame("17 days", 6, 1, 8, 70, 70, 9.0, 600, 9.0, 2005);
   BoardGame game2 = new BoardGame("17 days", 6, 1, 8, 70, 70, 9.0, 600, 9.0, 2005);
   System.out.print(game1 == game2); //False
   System.out.print(game1.equals(game2)); //True
   ```


2. Logical sorting can be difficult when talking about case. For example, should "apple" come before "Banana" or after? How would you sort a list of strings in a case-insensitive manner? 


To sort a list of strings in a case-insensitive manner, we can use the compareToIgnoreCase method in Java or convert all strings to lowercase (or uppercase) before sorting.

```java
   List<String> fruits = Arrays.asList("apple", "Banana", "cherry", "Apricot", "banana");
   fruits.sort(String::compareToIgnoreCase);
```


3. In our version of the solution, we had the following code (snippet)
    ```java
    public static Operations getOperatorFromStr(String str) {
        if (str.contains(">=")) {
            return Operations.GREATER_THAN_EQUALS;
        } else if (str.contains("<=")) {
            return Operations.LESS_THAN_EQUALS;
        } else if (str.contains(">")) {
            return Operations.GREATER_THAN;
        } else if (str.contains("<")) {
            return Operations.LESS_THAN;
        } else if (str.contains("=="))...
    ```
    Why would the order in which we checked matter (if it does matter)? Provide examples either way proving your point. 
   Because ">=" contains ">", if we check whether the str(maxPlayers>=10) contains ">" first, the result would show that it is ">" operator, not ">=".
   So we should check ">=" first to ensure it is correctly detected, then to check for ">".


4. What is the difference between a List and a Set in Java? When would you use one over the other? 

List would maintain the order of elements added in it while Set would not. And List allows duplicates in it while Set would not. Also, we can use index to get element in List while Set would not allow this.
Use List when you need an ordered, indexed collection with duplicates. Use Set when you need a collection of unique elements with fast lookups.


5. In [GamesLoader.java](src/main/java/student/GamesLoader.java), we use a Map to help figure out the columns. What is a map? Why would we use a Map here? 

A Map in Java is a data structure that stores key-value pairs, where each key is unique and maps to a specific value.

In GamesLoader.java, the method processHeader is responsible for mapping CSV column names to their respective index positions in the file, as CSV headers may not always be in the same order.

So using a Map allows us to Make column processing order-independent. The Map will correctly associate each GameData field with the correct index.


6. [GameData.java](src/main/java/student/GameData.java) is actually an `enum` with special properties we added to help with column name mappings. What is an `enum` in Java? Why would we use it for this application?

An enum  is a special type that represents a fixed set of named constants. Enums are useful when a variable should have a restricted set of possible values, improving code clarity and reducing errors.

In this application, GameData is an enum that represents different columns in the CSV file. Using an enum can ensure type safety, with predefined and fixed column names. Also, instead of "minplayers" appearing as a string in multiple places, we can refer to GameData.MIN_PLAYERS, making the code self-explanatory.



7. Rewrite the following as an if else statement inside the empty code block.
    ```java
    switch (ct) {
                case CMD_QUESTION: // same as help
                case CMD_HELP:
                    processHelp();
                    break;
                case INVALID:
                default:
                    CONSOLE.printf("%s%n", ConsoleText.INVALID);
            }
    ``` 

    ```java
    // your code here, don't forget the class name that is dropped in the switch block..
    if(ct == CMD_QUESTION || CT == CMD_HELP){
        processHelp();
   }else if(ct == INVALID){
        CONSOLE.printf("%s%n", ConsoleText.INVALID);
   }else{
        CONSOLE.printf("%s%n", ConsoleText.INVALID);
   }
    ```

## Deeper Thinking

ConsoleApp.java uses a .properties file that contains all the strings
that are displayed to the client. This is a common pattern in software development
as it can help localize the application for different languages. You can see this
talked about here on [Java Localization – Formatting Messages](https://www.baeldung.com/java-localization-messages-formatting).

Take time to look through the console.properties file, and change some of the messages to
another language (probably the welcome message is easier). It could even be a made up language and for this - and only this - alright to use a translator. See how the main program changes, but there are still limitations in 
the current layout. 

Post a copy of the run with the updated languages below this. Use three back ticks (```) to create a code block. 

```text
// your consoles output here
> Task :student.BGArenaPlanner.main()

*******Bienvenue sur le plateau de jeu Arena Planner.*******

A tool to help people plan which games they 
want to play on Board Game Arena. 

To get started, enter your first command below, or type ? or help for command options.
> 
```

Now, thinking about localization - we have the question of why does it matter? The obvious
one is more about market share, but there may be other reasons.  I encourage
you to take time researching localization and the importance of having programs
flexible enough to be localized to different languages and cultures. Maybe pull up data on the
various spoken languages around the world? What about areas with internet access - do they match? Just some ideas to get you started. Another question you are welcome to talk about - what are the dangers of trying to localize your program and doing it wrong? Can you find any examples of that? Business marketing classes love to point out an example of a car name in Mexico that meant something very different in Spanish than it did in English - however [Snopes has shown that is a false tale](https://www.snopes.com/fact-check/chevrolet-nova-name-spanish/).  As a developer, what are some things you can do to reduce 'hick ups' when expanding your program to other languages?


As a reminder, deeper thinking questions are meant to require some research and to be answered in a paragraph for with references. The goal is to open up some of the discussion topics in CS, so you are better informed going into industry.

This is important because developers can update UI text without modifying and recompiling source code. Also, Non-programmers like translators can edit the .properties file without touching the code. Also, it is great to Keeps UI text separate from business logic, making the code cleaner and more readable. [^1]

This could be dangerous because of  Cultural Insensitivity. In the game Wolfenstein 2: The New Colossus, the German version had to remove Nazi symbols and even altered Hitler's appearance to comply with local laws. Failure to make such adjustments could have led to legal issues and public backlash.[^2]

To avoid this situation, we need to collaborate with native speakers and individuals familiar with the target culture to ensure translations are accurate and culturally appropriate.

## References

[^1]: Java Properties file (.properties) https://localizely.com/java-properties-file/. Accessed: 2025-02-25.

[^2]: Who shaved Hitler’s mustache? The localizer https://www.polygon.com/gaming/506037/video-game-localization-explained. Accessed: 2025-02-25.