package adrian.snakegame.engine;

import adrian.snakegame.display.DisplayProcessor;

public class DefaultEngine implements GameEngine{

  private final DisplayProcessor processor;

  public DefaultEngine(DisplayProcessor processor){
    this.processor = processor;
  }

  @Override
  public void processCommand(Command command) {
    
  }
}
