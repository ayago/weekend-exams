package adrian.snakegame.display;

import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import adrian.snakegame.objects.Coordinate;
import adrian.snakegame.objects.GameDimensions;
import adrian.snakegame.objects.GameState;

public class CLIDisplayProcessor implements DisplayProcessor {

  private static final String DISPLAY_ELEMENT_SPACE = "-";
  private static final String DISPLAY_ELEMENT_HEAD = "X";
  private static final String DISPLAY_ELEMENT_BODY = "0";

  private final Queue<GameState> displayQueue = new ConcurrentLinkedQueue<>();
  private final ExecutorService displayService = Executors.newSingleThreadExecutor();
  private final GameDimensions dimensions;

  public CLIDisplayProcessor(GameDimensions dimensions) {
    this.dimensions = dimensions;
  }

  @Override
  public void displayFrame(GameState state) {
    if (state.isGameOver()) {
      System.out.println("Splat! Game Over.");
      displayService.shutdown();
    } else {
      this.displayQueue.add(state);
    }

  }

  @Override
  public void start() {
    displayService.execute(() -> {
      do {
        if (!displayQueue.isEmpty()) {
          GameState toDisplay = displayQueue.remove();
          executeDisplay(toDisplay);
        }
      } while (true);
    });

  }

  private void executeDisplay(GameState gameState) {
    System.out.println("\n\n");
    Set<DisplayNode> displayNodesSet = resolveSnakeDisplay(gameState);
    for (int row = 0; row < dimensions.getHeight(); row++) {
      for (int column = 0; column < dimensions.getWidth(); column++) {
        if (displayNodesSet.contains(new HeadNode(row, column))) {
          System.out.print(DISPLAY_ELEMENT_HEAD);
        } else if (displayNodesSet.contains(new BodyNode(row, column))) {
          System.out.print(DISPLAY_ELEMENT_BODY);
        } else {
          System.out.print(DISPLAY_ELEMENT_SPACE);
        }
      }
      System.out.println();
    }

  }

  private static Set<DisplayNode> resolveSnakeDisplay(GameState gameState) {
    List<Coordinate> placements = gameState.snakeState().getPlacement();
    return IntStream.range(0, placements.size()).mapToObj(i -> {
      Coordinate coordinate = placements.get(i);
      return i == 0 ? new HeadNode(coordinate) : new BodyNode(coordinate);
    }).collect(Collectors.toSet());
  }

  @Override
  public void end() {
    displayService.shutdown();
  }

  private static abstract class DisplayNode implements Coordinate {
    boolean isHead() {
      return false;
    }

    @Override
    public int hashCode() {
      return Objects.hash(getX(), getY(), isHead());
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }

      if (obj == null) {
        return false;
      }

      if (!(obj instanceof DisplayNode)) {
        return false;
      }

      DisplayNode displayNode = (DisplayNode) obj;
      return this.getX() == displayNode.getX() && this.getY() == displayNode.getY()
          && this.isHead() == displayNode.isHead();
    }
  }

  private static class HeadNode extends DisplayNode {
    private final int x;
    private final int y;

    private HeadNode(Coordinate coordinate) {
      x = coordinate.getX();
      y = coordinate.getY();
    }

    private HeadNode(int row, int column) {
      x = row;
      y = column;
    }

    @Override
    public int getX() {
      return x;
    }

    @Override
    public int getY() {
      return y;
    }

    @Override
    boolean isHead() {
      return true;
    }
  }

  private static class BodyNode extends DisplayNode {
    private final int x;
    private final int y;

    private BodyNode(Coordinate coordinate) {
      x = coordinate.getX();
      y = coordinate.getY();
    }

    private BodyNode(int row, int column) {
      x = row;
      y = column;
    }

    @Override
    public int getX() {
      return x;
    }

    @Override
    public int getY() {
      return y;
    }
  }

}
