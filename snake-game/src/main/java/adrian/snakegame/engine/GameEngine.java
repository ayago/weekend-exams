package adrian.snakegame.engine;

import adrian.snakegame.objects.Command;

public interface GameEngine {
  
  void processCommand(Command command);

  void start();

  void end();
}
