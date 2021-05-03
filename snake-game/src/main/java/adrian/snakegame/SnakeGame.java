package adrian.snakegame;

import adrian.snakegame.console.CLIConsole;
import adrian.snakegame.console.Console;
import adrian.snakegame.display.CLIDisplayProcessor;
import adrian.snakegame.display.DisplayProcessor;
import adrian.snakegame.engine.DefaultEngine;
import adrian.snakegame.engine.GameEngine;

public class SnakeGame {
  public static void main(String[] args) {
    DisplayProcessor processor = new CLIDisplayProcessor();
    GameEngine engine = new DefaultEngine(processor);
    Console gameConsole = new CLIConsole(engine);

    gameConsole.start();
  }
}
