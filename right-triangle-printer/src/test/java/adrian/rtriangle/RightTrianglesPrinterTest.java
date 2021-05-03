package adrian.rtriangle;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import adrian.rtriangle.RightTrianglesPrinter.Triangles;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RightTrianglesPrinterTest {

  @ParameterizedTest
  @MethodSource("constructTrianglesParams")
  public void constructTriangles(
    int inputLength, 
    String expectedDefault, 
    String expectedUpsideDown, 
    String expectedMirrorDefault, 
    String expectedMirrorUpsideDown
  ){
    Triangles result = RightTrianglesPrinter.constructTriangles(inputLength);
    assertNotNull(result);
    assertAll(
      () -> assertEquals(expectedDefault, result.defaultTriangle),
      () -> assertEquals(expectedUpsideDown, result.upsideDown),
      () -> assertEquals(expectedMirrorDefault, result.mirrorDefault),
      () -> assertEquals(expectedMirrorUpsideDown, result.mirrorUpsideDown)
    );
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 0, -3, 100, Integer.MAX_VALUE})
  public void invalidLengths(int inputLength){
    RuntimeException thrownException = 
      assertThrows(RuntimeException.class, () -> RightTrianglesPrinter.constructTriangles(inputLength));

    assertEquals("Invalid length "+inputLength+". Should be from 2 to 99 only", thrownException.getMessage());
  }

  private static Stream<Arguments> constructTrianglesParams(){
    return Stream.of(
      Arguments.of(
        4,
        "*\n"+"**\n"+"***\n"+"****",
        "****\n"+"***\n"+"**\n"+"*",
        "   *\n"+"  **\n"+" ***\n"+"****",
        "****\n"+" ***\n"+"  **\n"+"   *"
      ),
      Arguments.of(
        2,
        "*\n"+"**",
        "**\n"+"*",
        " *\n"+"**",
        "**\n"+" *"
      )
    );
  }
}
