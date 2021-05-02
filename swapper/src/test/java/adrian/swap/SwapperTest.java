package adrian.swap;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static adrian.swap.Swapper.Result;
import static adrian.swap.Swapper.swapNumbers;

public class SwapperTest {

  @ParameterizedTest
  @CsvSource({"3,2", "1,1", "-9,3", "0,-1", "1,1", "1000000, -2900000", "0,0"})
  public void swapNumbersTest(int first, int second){
    Result result = swapNumbers(first, second);
    assertEquals(first, result.second);
    assertEquals(second, result.first);
  }
}
