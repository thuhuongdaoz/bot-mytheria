package tvlib.util;

import java.util.Random;
import java.util.Vector;

import bem.net.Constants;

public class Util {
	private static Random random = new Random();

	public static int genRandomNumber(int min, int max) {
		int myNum;
		myNum = min + Util.nextInt(random, max - min + 1);
		return myNum;
	}

	public static int nextInt(Random r, int n) {

		if ((n & -n) == n) // i.e., n is a power of 2
			return (int) ((n * (long) r.nextInt()) >> 31);

		int bits, val;
		do {
			bits = r.nextInt();
			val = bits % n;
		} while (bits - val + (n - 1) < 0);

		return val;
	}

	public static boolean isTienLen(String gameCode) {
//		boolean ret = gameCode.equals();
		return false;
	}

	
	public static String getGameTitle(String gameCode) {
		return gameCode;
		
	}

	public static int encrypt(String password, int seedCode) {
		char[] chars = password.toCharArray();
		int total = 0;
		for (int i = 0; i < chars.length; i++) {
			total += chars[i];
		}
		return (total * seedCode);
	}

	public static String encryptNew(String password, int seedCode) {
		// trans number
		String x = seedCode + "";
		int trans = Integer.parseInt(x.charAt(0) + "");
		//
		String tmp = password;
		tmp = transBase64String(tmp, trans);
		//
		String ePwd = Base64.encode(tmp);
		ePwd = transBase64String(ePwd, trans);
		return ePwd;
	}

	public static String decryptNew(String base64, int seedCode) {
		// trans number
		String x = seedCode + "";
		int trans = Integer.parseInt(x.charAt(0) + "");
		//
		String deTrans = base64;
		deTrans = transBase64String(base64, -trans);
		//
		String pwd = "";
		try {
			pwd = new String(Base64.decode(deTrans));
			pwd = transBase64String(pwd, -trans);
			return pwd;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return pwd;
	}

	public static String transBase64String(String input, int trans) {
		String ret = input;
		if (input.length() != 4) {
			int length = input.length() - 2;
			char[] charTrans = new char[length];
			//
			for (int i = 0; i < length; i++) {
				char ch = ret.charAt(i);
				int newPos = (i + trans) % length;
				if (newPos < 0)
					newPos = newPos + length;
				charTrans[newPos] = ch;
			}
			//
			ret = new String(charTrans) + input.substring(length);
		}
		//
		return ret;
	}

	public static String formatCurrency(int value, boolean appendCurrency) {
		return formatCurrency(String.valueOf(value), appendCurrency);
	}

	public static String formatCurrency(int value) {
		return formatCurrency(String.valueOf(value), false);
	}

	public static String formatCurrency(String value, boolean appendCurrency) {
		String result = "";
		int count = 0;
		for (int i = (value.length() - 1); i >= 0; i--) {
			count++;
			if (count == 3 || i == 0) {
				result = value.substring(i, i + count) + (result.equals("") ? "" : ".") + result;
				// System.out.println("-------- sub:"+value.substring(i,i+count));
				count = 0;
			}
		}
		if (appendCurrency)
			result = result + " Koin";
		//
		return result;
	}

	public final static String replaceString(String text, String searchString, String replacementString) {
		StringBuffer sBuffer = new StringBuffer();
		int pos = 0;

		while ((pos = text.indexOf(searchString)) != -1) {
			sBuffer.append(text.substring(0, pos) + replacementString);
			text = text.substring(pos + searchString.length());
		}

		sBuffer.append(text);
		return sBuffer.toString();
	}

	public static String[] splitString(String original, String separator) {
		Vector nodes = new Vector();
		// Parse nodes into vector
		int index = original.indexOf(separator);
		while (index >= 0) {
			nodes.addElement(original.substring(0, index));
			original = original.substring(index + separator.length());
			index = original.indexOf(separator);
		}
		// Get the last node
		nodes.addElement(original);

		// Create splitted string array
		String[] result = new String[nodes.size()];
		if (nodes.size() > 0) {
			for (int loop = 0; loop < nodes.size(); loop++)
				result[loop] = (String) nodes.elementAt(loop);
		}
		return result;
	}

	/**
	 * DO NOT USE IN RENDER/UPDATE !!!
	 * 
	 * @param text
	 * @return
	 */
	
	public static String getImageName(String url) {

		int index = url.lastIndexOf('/');

		if (index > 0) {
			return url.substring(index + 1, url.length());
		}

		return url;

	}


}
