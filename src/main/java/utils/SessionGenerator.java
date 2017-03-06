package utils;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.Calendar;
import java.math.BigInteger;

public class SessionGenerator {
	public static String nextSessionId() {
		return new BigInteger(130, new SecureRandom()).toString(32);
	}
	
	/**
	 * Ottiene timestamp del futuro per il numero di mesi specificati
	 * @param monthsDelay Numero di mesi nel futuro di cui prendere il timestamp
	 * @return Timestamp del futuro di monthsDelay mesi
	 */
	public static Timestamp getFutureTimestamp(int monthsDelay) {
		Calendar cal = Calendar.getInstance(); 
		cal.add(Calendar.MONTH, 1);
		return new Timestamp(cal.getTimeInMillis());
	}
}
