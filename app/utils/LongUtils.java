/**
 * 
 */
package utils;

/**
 * @author azrail
 *
 */
public class LongUtils {
	/**
	 * Überprüft ob ein Long null ist oder = 0 ist
	 * 
	 * @param l Der zu überprüfende Long
	 * @return true wenn der Long nichts enthält oder false wenn er etwas enthält
	 */
	public static boolean nullCheck(Long l) {
		if (l == null || l == 0L) {
			return true;
		} else {
			return false;
		}
	}
}
