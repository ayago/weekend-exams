package adrian.snakegame.console;

import java.util.Scanner;

import adrian.snakegame.engine.GameEngine;
import adrian.snakegame.objects.Command;
public class CLIConsole implements Console{

  private final GameEngine gameEngine;

  public CLIConsole(final GameEngine gameEngine) {
    this.gameEngine = gameEngine;
  }

  @Override
  public void start() {
    this.gameEngine.start();
    char move;
    System.out.println("Welcome to Console Snake, Enter one of the following: w=UP, a=LEFT, s=DOWN, d=RIGHT, q=QUIT and hit enter");
    try(Scanner in = new Scanner(System.in)){
      while ((move = in.nextLine().charAt(0)) != 'q') {
        Command command = parseCommand(move);
        if(command != null){
          this.gameEngine.processCommand(command);
        }
      }
      this.gameEngine.processCommand(Command.STOP);
      System.out.println("Player quitted. Thanks for trying. Terminate now :)");
      this.end();
    } catch(Exception e){
      e.printStackTrace();
    }
  }

  private static Command parseCommand(final char move){
    switch(move){
      case 'w': return Command.UP;
      case 'a': return Command.LEFT;
      case 's': return Command.DOWN;
      case 'd': return Command.RIGHT;
      case 'q': return Command.STOP;
      default: return null;
    }
  }

  private void end(){
    this.gameEngine.end();
  }
  
}
