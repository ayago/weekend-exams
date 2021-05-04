package adrian.snakegame.factory;

import adrian.snakegame.console.CLIConsole;
import adrian.snakegame.console.Console;
import adrian.snakegame.display.CLIDisplayProcessor;
import adrian.snakegame.engine.DefaultEngine;
import adrian.snakegame.engine.GameEngine;

public final class GameFactory {
  private GameFactory(){}

  public static Console createCLISnake(){
    GameEngine engine = new DefaultEngine(CLIDisplayProcessor::new);
    Console gameConsole = new CLIConsole(engine);

    return gameConsole;
  }
}
