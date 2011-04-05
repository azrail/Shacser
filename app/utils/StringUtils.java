package utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author azrail
 * 
 */
public class StringUtils {
	
	public static String md5Hash(String text) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			md.reset();
			md.update(text.getBytes());
			byte[] digest = md.digest();
			BigInteger bigInt = new BigInteger(1, digest);
			String hashtext = bigInt.toString(16);
			while (hashtext.length() < 32) {
				hashtext = "0" + hashtext;
			}
			return hashtext;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Replaces all text Smilies with the Smilie Images
	 * 
	 * @param text
	 *            The Text to replace the Smilies with img
	 * @param smiliePath
	 *            path to the Smilie pngs, with ending /
	 * @param cssClass
	 *            css Class for the img tag
	 * @param imgOptions
	 *            aditional img options
	 * @return
	 */
	public static String replaceSmilies(String text, String smiliePath, String cssClass, String imgOptions) {
		
		String imgStart = "<img src=\"";
		String imgEnd = "\" class=\"" + cssClass + "\" " + imgOptions + " />";
		
		Map<String, String> bbMap = new LinkedHashMap<String, String>();
		Map<String, String> bbMapalt = new LinkedHashMap<String, String>();
		
		// Heart <3
		bbMap.put("&lt;3", smiliePath + "black_heart.png");
		bbMap.put(":heart:", smiliePath + "black_heart.png");
		bbMap.put("*heart*", smiliePath + "black_heart.png");
		
		// Grumpy >:-( >:(
		bbMap.put("&gt;:-(", smiliePath + "scorn.png");
		bbMap.put("&gt;:(", smiliePath + "scorn.png");
		bbMap.put(":grumpy:", smiliePath + "scorn.png");
		bbMap.put("*grumpy*", smiliePath + "scorn.png");
		
		// Smile/Happy :-) :) :] =)
		bbMap.put(":-)", smiliePath + "big_smile.png");
		bbMap.put(":)", smiliePath + "big_smile.png");
		bbMap.put(":]", smiliePath + "big_smile.png");
		bbMap.put("=)", smiliePath + "big_smile.png");
		bbMap.put(":smile:", smiliePath + "big_smile.png");
		bbMap.put("*smile*", smiliePath + "big_smile.png");
		bbMap.put(":happy:", smiliePath + "big_smile.png");
		bbMap.put("*happy*", smiliePath + "big_smile.png");
		
		// Frown/Sad :-( :( :[ =(
		bbMap.put(":-(", smiliePath + "unhappy.png");
		bbMap.put(":(", smiliePath + "unhappy.png");
		bbMap.put(":[", smiliePath + "unhappy.png");
		bbMap.put("=(", smiliePath + "unhappy.png");
		bbMap.put(":sad:", smiliePath + "unhappy.png");
		bbMap.put("*sad*", smiliePath + "unhappy.png");
		bbMap.put(":frown:", smiliePath + "unhappy.png");
		bbMap.put("*frown*", smiliePath + "unhappy.png");
		
		// Tongue :-P :P :-p :p =P
		bbMap.put(":-P", smiliePath + "grimace.png");
		bbMap.put(":P", smiliePath + "grimace.png");
		bbMap.put(":-p", smiliePath + "grimace.png");
		bbMap.put(":p", smiliePath + "grimace.png");
		bbMap.put("=P", smiliePath + "grimace.png");
		bbMap.put(":tongue:", smiliePath + "grimace.png");
		bbMap.put("*tongue*", smiliePath + "grimace.png");
		
		// Grin :-D :D =D
		bbMap.put(":-D", smiliePath + "exciting.png");
		bbMap.put(":D", smiliePath + "exciting.png");
		bbMap.put("=D", smiliePath + "exciting.png");
		bbMap.put(":grin:", smiliePath + "exciting.png");
		bbMap.put("*grin*", smiliePath + "exciting.png");
		
		// Gasp :-O :O :-o :o
		bbMap.put(":-O", smiliePath + "shocked.png");
		bbMap.put(":O", smiliePath + "shocked.png");
		bbMap.put(":-o", smiliePath + "shocked.png");
		bbMap.put(":o", smiliePath + "shocked.png");
		bbMap.put(":gasp:", smiliePath + "shocked.png");
		bbMap.put("*gasp*", smiliePath + "shocked.png");
		
		// Wink ;-) ;)
		bbMap.put(";-)", smiliePath + "secret_smile.png");
		bbMap.put(";)", smiliePath + "secret_smile.png");
		bbMap.put(":wink:", smiliePath + "secret_smile.png");
		bbMap.put("*wink*", smiliePath + "secret_smile.png");
		
		// Unsure :-/ :/ :-\ :\
		bbMap.put(":-/", smiliePath + "what.png");
		bbMap.put(":/", smiliePath + "what.png");
		bbMap.put(":-\\", smiliePath + "what.png");
		bbMap.put(":\\", smiliePath + "what.png");
		bbMap.put(":unsure:", smiliePath + "what.png");
		bbMap.put("*unsure*", smiliePath + "what.png");
		
		// Kiss :-* :*
		// TODO add kisses
		
		// Confuse o.O O.o
		bbMap.put("o.O", smiliePath + "shocked.png");
		bbMap.put("O.o", smiliePath + "shocked.png");
		bbMap.put(":confuse:", smiliePath + "shocked.png");
		bbMap.put("*confuse*", smiliePath + "shocked.png");
		
		// Glasses 8-) 8) B-) B)
		bbMap.put("8-)", smiliePath + "secret_smile.png");
		bbMap.put("8)", smiliePath + "secret_smile.png");
		bbMap.put("B-)", smiliePath + "secret_smile.png");
		bbMap.put("B)", smiliePath + "secret_smile.png");
		bbMap.put(":glasses:", smiliePath + "secret_smile.png");
		bbMap.put("*glasses*", smiliePath + "secret_smile.png");
		
		// Heart <3
		bbMapalt.put("&lt;3", "Heart");
		bbMapalt.put(":heart:", "Heart");
		bbMapalt.put("*heart*", "Heart");
		
		// Grumpy >:-( >:(
		bbMapalt.put("&lt;:-(", "Grumpy");
		bbMapalt.put("&lt;:(", "Grumpy");
		bbMapalt.put(":grumpy:", "Grumpy");
		bbMapalt.put("*grumpy*", "Grumpy");
		
		// Smile/Happy :-) :) :] =)
		bbMapalt.put(":-)", "Smile/Happy");
		bbMapalt.put(":)", "Smile/Happy");
		bbMapalt.put(":]", "Smile/Happy");
		bbMapalt.put("=)", "Smile/Happy");
		bbMapalt.put(":smile:", "Smile/Happy");
		bbMapalt.put("*smile*", "Smile/Happy");
		bbMapalt.put(":happy:", "Smile/Happy");
		bbMapalt.put("*happy*", "Smile/Happy");
		
		// Frown/Sad :-( :( :[ =(
		bbMapalt.put(":-(", "Frown/Sad");
		bbMapalt.put(":(", "Frown/Sad");
		bbMapalt.put(":[", "Frown/Sad");
		bbMapalt.put("=(", "Frown/Sad");
		bbMapalt.put(":sad:", "Frown/Sad");
		bbMapalt.put("*sad*", "Frown/Sad");
		bbMapalt.put(":frown:", "Frown/Sad");
		bbMapalt.put("*frown*", "Frown/Sad");
		
		// Tongue :-P :P :-p :p =P
		bbMapalt.put(":-P", "Tongue");
		bbMapalt.put(":P", "Tongue");
		bbMapalt.put(":-p", "Tongue");
		bbMapalt.put(":p", "Tongue");
		bbMapalt.put("=P", "Tongue");
		bbMapalt.put(":tongue:", "Tongue");
		bbMapalt.put("*tongue*", "Tongue");
		
		// Grin :-D :D =D
		bbMapalt.put(":-D", "Grin");
		bbMapalt.put(":D", "Grin");
		bbMapalt.put("=D", "Grin");
		bbMapalt.put(":grin:", "Grin");
		bbMapalt.put("*grin*", "Grin");
		
		// Gasp :-O :O :-o :o
		bbMapalt.put(":-O", "Gasp");
		bbMapalt.put(":O", "Gasp");
		bbMapalt.put(":-o", "Gasp");
		bbMapalt.put(":o", "Gasp");
		bbMapalt.put(":gasp:", "Gasp");
		bbMapalt.put("*gasp*", "Gasp");
		
		// Wink ;-) ;)
		bbMapalt.put(";-)", "Wink");
		bbMapalt.put(";)", "Wink");
		bbMapalt.put(":wink:", "Wink");
		bbMapalt.put("*wink*", "Wink");
		
		// Unsure :-/ :/ :-\ :\
		bbMapalt.put(":-/", "Unsure");
		bbMapalt.put(":/", "Unsure");
		bbMapalt.put(":-\\", "Unsure");
		bbMapalt.put(":\\", "Unsure");
		bbMapalt.put(":grumpy:", "Unsure");
		bbMapalt.put("*grumpy*", "Unsure");
		
		// Kiss :-* :*
		// TODO add kisses
		
		// Confuse o.O O.o
		bbMapalt.put("o.O", "Confuse");
		bbMapalt.put("O.o", "Confuse");
		bbMapalt.put(":confuse:", "Confuse");
		bbMapalt.put("*confuse*", "Confuse");
		
		// Glasses 8-) 8) B-) B)
		bbMapalt.put("8-)", "Glasses");
		bbMapalt.put("8)", "Glasses");
		bbMapalt.put("B-)", "Glasses");
		bbMapalt.put("B)", "Glasses");
		bbMapalt.put(":glasses:", "Glasses");
		bbMapalt.put("*glasses*", "Glasses");
		
		for (Map.Entry entry : bbMap.entrySet()) {
			String key = entry.getKey().toString();
			text = text.replace(entry.getKey().toString(), imgStart + entry.getValue().toString() + "\" alt=\"" + bbMapalt.get(key) + "\" title=\"" + bbMapalt.get(key) + imgEnd);
		}
		return text;
	}
	
}
