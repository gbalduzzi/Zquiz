package hello;
import java.security.SecureRandom;
import java.math.BigInteger;

public class SessionGenerator {
		  public static String nextSessionId() {
		    return new BigInteger(130, new SecureRandom()).toString(32);
		  }
}
