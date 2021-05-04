package adrian.snakegame;

import adrian.snakegame.console.Console;
import adrian.snakegame.factory.GameFactory;

public class SnakeGame {
  public static void main(String[] args) {
    Console gameConsole = GameFactory.createCLISnake();
    gameConsole.start();
  }
}
