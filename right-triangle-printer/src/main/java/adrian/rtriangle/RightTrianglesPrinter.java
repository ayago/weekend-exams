
package adrian.rtriangle;

import java.lang.StringBuilder;
import java.util.Scanner;

public class RightTrianglesPrinter {

  private static final String CHARACTER_TRIANGLE_ELEMENT = "*";
  private static final String CHARACTER_WHITESPACE = " ";
  private static final String CHARACTER_NEWLINE = "\n";

  public static void main(String[] args) {
    try(Scanner in = new Scanner(System.in)){
      System.out.println("Input length of triangles : ");
      int length = in.nextInt();  
      Triangles triangles = constructTriangles(length);
      System.out.print(triangles.defaultTriangle);
      System.out.print("\n\n");
      System.out.print(triangles.upsideDown);
      System.out.print("\n\n");
      System.out.print(triangles.mirrorDefault);
      System.out.print("\n\n");
      System.out.print(triangles.mirrorUpsideDown);
      System.out.print("\n\n");
    } catch (Exception e){
      e.printStackTrace();
    }
  }

  static Triangles constructTriangles(final int length){

    if(length <= 1 || length > 99){
      throw new RuntimeException(
        String.format("Invalid length %s. Should be from 2 to 99 only", length));
    }
    int builderInitialCapacity = resolveInitialBuilderCapacity(length);
    StringBuilder defaultTriangleBuilder = new StringBuilder(builderInitialCapacity);
    StringBuilder upsideDownBuilder = new StringBuilder(builderInitialCapacity);
    StringBuilder mirrorDefaultBuilder = new StringBuilder(builderInitialCapacity);
    StringBuilder mirrorUpsideDownBuilder = new StringBuilder(builderInitialCapacity);

    for(int row = 0; row < length; row++){
      boolean defaultRowFinished = false;
      boolean upsideDownRowFinished = false;
      for(int column = 0; column < length; column++){
        if(!defaultRowFinished){
          defaultRowFinished = column == row;
          defaultTriangleBuilder.append(CHARACTER_TRIANGLE_ELEMENT);
        }

        if(!upsideDownRowFinished){
          upsideDownRowFinished = column == (length - 1) - row;
          upsideDownBuilder.append(CHARACTER_TRIANGLE_ELEMENT);
        }

        boolean mirrorDefaultStart = column >= (length - 1) - row;
        mirrorDefaultBuilder.append(mirrorDefaultStart ? CHARACTER_TRIANGLE_ELEMENT: CHARACTER_WHITESPACE);

        boolean mirrorUpsideDownStart = column >= row;
        mirrorUpsideDownBuilder.append(mirrorUpsideDownStart ? CHARACTER_TRIANGLE_ELEMENT: CHARACTER_WHITESPACE);
      }

      if(row < (length - 1)){
        defaultTriangleBuilder.append(CHARACTER_NEWLINE);
        upsideDownBuilder.append(CHARACTER_NEWLINE);
        mirrorDefaultBuilder.append(CHARACTER_NEWLINE);
        mirrorUpsideDownBuilder.append(CHARACTER_NEWLINE);
      }
    }

    return new Triangles(
      defaultTriangleBuilder.toString(), 
      upsideDownBuilder.toString(), 
      mirrorDefaultBuilder.toString(), 
      mirrorUpsideDownBuilder.toString()
    );
  }

  private static int resolveInitialBuilderCapacity(int length){
    return length * length;
  }

  static class Triangles {
    final String defaultTriangle;
    final String upsideDown;
    final String mirrorDefault;
    final String mirrorUpsideDown;

    private Triangles(
      final String defaultTriangle,
      final String upsideDown,
      final String mirrorDefault,
      final String mirrorUpsideDown
    ){
      this.defaultTriangle = defaultTriangle;
      this.upsideDown = upsideDown;
      this.mirrorDefault = mirrorDefault;
      this.mirrorUpsideDown = mirrorUpsideDown;
    }
  }
}