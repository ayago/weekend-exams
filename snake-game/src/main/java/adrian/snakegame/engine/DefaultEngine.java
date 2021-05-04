package adrian.snakegame.engine;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import adrian.snakegame.display.DisplayProcessor;
import adrian.snakegame.objects.Command;
import adrian.snakegame.objects.Coordinate;
import adrian.snakegame.objects.GameDimensions;
import adrian.snakegame.objects.GameState;
import adrian.snakegame.objects.Snake;

import static java.util.Collections.unmodifiableList;

import java.util.HashSet;

public class DefaultEngine implements GameEngine{

  private static final int INITIAL_SNAKE_LENGTH = 8;
  private static final GameDimensions GAME_DIMENSIONS = new DefaultGameDimensions(15, 15);
  private final ExecutorService stateProcessService = Executors.newSingleThreadExecutor();

  private final DisplayProcessor processor;
  private final AtomicReference<Command> commandRef = new AtomicReference<>();

  private DefaultGameState currentState;

  public DefaultEngine(Function<GameDimensions, DisplayProcessor> processorFactory){
    this.processor = processorFactory.apply(GAME_DIMENSIONS);
    currentState = createInitialState();
  }

  @Override
  public void processCommand(Command command) {
    commandRef.set(command);
  }

  @Override
  public void start() {

    processor.start();

    stateProcessService.execute(() -> {
      do {
        Command command = commandRef.get();
        if(Command.STOP.equals(command)){
          DefaultGameState newState = endGame();
          processor.displayFrame(newState);
          currentState = newState;
        } else if(!currentState.isGameOver() && null != command) {
          DefaultGameState newState = applyCommand(currentState, command);
          processor.displayFrame(newState);
          currentState = newState;
        } else if(!currentState.isGameOver()) {
          DefaultGameState newState = continueMovement(currentState);
          processor.displayFrame(newState);
          currentState = newState;
        }
        commandRef.set(null);
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      } while(!currentState.isGameOver());
  
    });
  }

  private static DefaultGameState continueMovement(DefaultGameState currentState){
    DefaultSnake defaultSnake = currentState.snake;
    DefaultSnake movedSnake = defaultSnake.continueMovement();

    return new DefaultGameState(movedSnake);
  }

  private static DefaultGameState applyCommand(DefaultGameState currentState, Command command){
    DefaultSnake defaultSnake = currentState.snake;
    DefaultSnake movedSnake = defaultSnake.processCommand(command);

    return new DefaultGameState(movedSnake);
  }

  private static DefaultGameState endGame(){
    return new DefaultGameState(DefaultSnake.DEAD_SNAKE);
  }

  @Override
  public void end() {
    processor.end();
    stateProcessService.shutdown();
  }

  private static DefaultGameState createInitialState(){
    DefaultSnake initialSnake = DefaultSnake.newSnake();
    return new DefaultGameState(initialSnake); 
  }

  private static class SnakeNode implements Coordinate {
    private final int x;
    private final int y;
    private final Command vector;

    private SnakeNode(int x, int y, Command vector){
      this.x = x;
      this.y = y;
      this.vector = vector;
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
    public int hashCode() {
      return Objects.hash(x, y);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj){
        return true;
      }
        
      if (obj == null) {
        return false;
      }
          
      if (!(obj instanceof SnakeNode)) {
        return false;
      }
          
      SnakeNode another = (SnakeNode) obj;
      return this.getX() == another.getX() && 
        this.getY() == another.getY();
    }
  }

  private static class DefaultSnake implements Snake {

    private final LinkedList<SnakeNode> nodes;
    private final boolean isDead;

    private static DefaultSnake newSnake(){
      int maxIndex = INITIAL_SNAKE_LENGTH - 1;
      LinkedList<SnakeNode> initial = IntStream.range(0, INITIAL_SNAKE_LENGTH)
        .mapToObj(i -> new SnakeNode(0, maxIndex - i, Command.RIGHT))
        .collect(Collectors.toCollection(LinkedList::new));

      return new DefaultSnake(initial);
    }

    private static DefaultSnake DEAD_SNAKE = new DefaultSnake();

    private DefaultSnake(LinkedList<SnakeNode> initial){
      this.nodes = initial;
      isDead = false;
    }

    private DefaultSnake(){
      this.nodes = null;
      isDead = true;
    }

    @Override
    public List<Coordinate> getPlacement() {
      return unmodifiableList(nodes);
    }

    DefaultSnake processCommand(Command command){
      if(Command.STOP.equals(command)) {
        return DEAD_SNAKE;
      } 
      
      Command effectiveCommand = commandIsReverseOfHead(command) ? this.nodes.getFirst().vector : command;
      if(diesOnCommand(effectiveCommand)){
        return DEAD_SNAKE;
      }

      return executeCommand(effectiveCommand);
    }

    DefaultSnake continueMovement(){
      SnakeNode head = this.nodes.getFirst();
      Command command = head.vector;

      if(diesOnCommand(command)) {
        return DEAD_SNAKE;
      } 

      return executeCommand(head.vector);
    }

    private boolean commandIsReverseOfHead(Command command){
      SnakeNode head = this.nodes.getFirst();
      return head.vector.equals(Command.RIGHT) && Command.LEFT.equals(command) ||  
        head.vector.equals(Command.LEFT) && Command.RIGHT.equals(command) || 
        head.vector.equals(Command.UP) && Command.DOWN.equals(command) ||
        head.vector.equals(Command.DOWN) && Command.UP.equals(command);
    }

    private DefaultSnake executeCommand(Command command){
      SnakeNode newHead = deriveNewHead(command);

      LinkedList<SnakeNode> shiftedNodes = new LinkedList<>();
      shiftedNodes.add(newHead);

      for(int index = 1; index < this.nodes.size(); index ++){
        SnakeNode nextNode = this.nodes.get(index);
        SnakeNode followedNode = this.nodes.get(index - 1);

        if(Command.DOWN.equals(followedNode.vector)) {
          shiftedNodes.add(new SnakeNode(nextNode.x + 1, nextNode.y, Command.DOWN));
        } else if(Command.UP.equals(followedNode.vector)) {
          shiftedNodes.add(new SnakeNode(nextNode.x - 1, nextNode.y, Command.UP));
        } else if(Command.LEFT.equals(followedNode.vector)) {
          shiftedNodes.add(new SnakeNode(nextNode.x, nextNode.y - 1, Command.LEFT));
        } else if(Command.RIGHT.equals(followedNode.vector)) {
          shiftedNodes.add(new SnakeNode(nextNode.x, nextNode.y + 1, Command.RIGHT));
        }
      }

      return new DefaultSnake(shiftedNodes);

    }

    private SnakeNode deriveNewHead(Command command){
      SnakeNode head = this.nodes.getFirst();
      if(Command.RIGHT.equals(command)){
        return new SnakeNode(head.x, head.y  + 1, Command.RIGHT);
      } else if(Command.UP.equals(command)){
        return new SnakeNode(head.x  - 1, head.y, Command.UP);
      } else if(Command.DOWN.equals(command)){
        return new SnakeNode(head.x + 1, head.y, Command.DOWN);
      } else if(Command.LEFT.equals(command)){
        return new SnakeNode(head.x, head.y - 1, Command.LEFT);
      }

      throw new RuntimeException("Invalid state. Cannot derive head. Is the command supported?");
    }

    private boolean diesOnCommand(Command command){
      SnakeNode head = this.nodes.getFirst();
      SnakeNode simulatedHead = simulateHead(head, command);
      return Command.UP.equals(command) && simulatedHead.x == -1 ||
        Command.DOWN.equals(command) && simulatedHead.x == GAME_DIMENSIONS.getHeight() || 
        Command.RIGHT.equals(command) && simulatedHead.y == GAME_DIMENSIONS.getWidth() || 
        Command.LEFT.equals(command) && simulatedHead.y == -1 || 
        willBiteItself(simulatedHead);
    }

    private boolean willBiteItself(SnakeNode simulatedHead){
      Set<SnakeNode> currentSet = new HashSet<>(this.nodes);
      return currentSet.contains(simulatedHead);
    }

    private static SnakeNode simulateHead(SnakeNode head, Command command){
      if(Command.UP.equals(command)){
        return new SnakeNode(head.x - 1, head.y, Command.UP);
      } else if(Command.DOWN.equals(command)){
        return new SnakeNode(head.x + 1, head.y, Command.DOWN);
      } else if(Command.RIGHT.equals(command)){
        return new SnakeNode(head.x, head.y + 1, Command.RIGHT);
      } else if(Command.LEFT.equals(command)){
        return new SnakeNode(head.x, head.y - 1, Command.LEFT);
      }

      //weird
      throw new RuntimeException("Invalid state. Cannot simulate head. Is the command supported?");
    }
  }

  private static class DefaultGameState implements GameState{

    private final DefaultSnake snake;
  
    private DefaultGameState(DefaultSnake snake) {
      this.snake = snake;
    }

    @Override
    public DefaultSnake snakeState() {
      return snake;
    }

    @Override
    public boolean isGameOver() {
      return snake.isDead;
    }
    
  }
  private static class DefaultGameDimensions implements GameDimensions{

    private final int width;
    private final int height;
  
    DefaultGameDimensions(int width, int height) {
      this.width = width;
      this.height = height;
    }
  
    @Override
    public int getWidth() {
      return width;
    }
  
    @Override
    public int getHeight() {
      return height;
    }
    
  }
}
