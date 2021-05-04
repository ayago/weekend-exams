package adrian.snakegame.objects;

public interface GameState {
  
  Snake snakeState();

  default boolean isGameOver() {
    return false;
  }
}
