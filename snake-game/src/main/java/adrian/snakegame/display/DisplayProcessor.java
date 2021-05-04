package adrian.snakegame.display;

import adrian.snakegame.objects.GameState;

public interface DisplayProcessor {
  
  void displayFrame(GameState state);

  void start();

  void end();
}
