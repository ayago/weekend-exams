package adrian.snakegame.console;

import adrian.snakegame.engine.GameEngine;

public class CLIConsole implements Console{

  private final GameEngine gameEngine;

  public CLIConsole(final GameEngine gameEngine) {
    this.gameEngine = gameEngine;
  }

  @Override
  public void start() {
    
  }
  
}
