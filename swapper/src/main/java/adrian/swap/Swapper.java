package adrian.swap;

import java.util.Scanner;

public class Swapper {

  private Swapper(){}

  public static void main(String[] args) {
    try(Scanner in = new Scanner(System.in)){
      System.out.println("Input the first number : ");
      int first = in.nextInt();  
      System.out.println("Input the second number: ");
      int second = in.nextInt();
      Result result = swapNumbers(first, second);
      System.out.println(
        String.format("Swap successful - first: %s, second: %s", result.first, result.second));
    } catch (Exception e){
      e.printStackTrace();
    }
  }

  static Result swapNumbers(final int first, final int second){
    int firstValue = first; 
    int secondValue = second; 
    firstValue = firstValue + secondValue; 
    secondValue = firstValue - secondValue;
    firstValue = firstValue - secondValue;

    return new Result(firstValue, secondValue);
  }

  static class Result {
    final int first;
    final int second;

    private Result(int first, int second){
      this.first = first;
      this.second = second;
    }
  }
}