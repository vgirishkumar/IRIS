/*
 * Copyright (c) 2002-2010 FE-Mobile Ltd, Inc. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of FE-Mobile
 *  Ltd. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with FE-Mobile Ltd.
 *
 * FE-Mobile Ltd MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. FE-Mobile Ltd SHALL NOT
 * BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */

package com.temenos.ebank.common.wicket.validators;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * Lists the worlds dial codes and country mappings, also provides utility methods for cleaning up
 * phone numbers.
 * 
 * @author slimb
 * 
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class DialCode {
	/** Mapping of country codes to international dial codes, for some countries (now all countries). */
	private static Hashtable dialCodes;

	static {
		dialCodes = new Hashtable();
		dialCodes.put("af", "93");
		dialCodes.put("ax", "35818");
		dialCodes.put("al", "355");
		dialCodes.put("dz", "213");
		dialCodes.put("as", "1684");
		dialCodes.put("ad", "376");
		dialCodes.put("ao", "244");
		dialCodes.put("ai", "1264");
		dialCodes.put("aq", "672");
		dialCodes.put("ag", "1268");
		dialCodes.put("ar", "54");
		dialCodes.put("am", "374");
		dialCodes.put("aw", "297");
		dialCodes.put("au", "61");
		dialCodes.put("at", "43");
		dialCodes.put("az", "994");
		dialCodes.put("bs", "1242");
		dialCodes.put("bh", "973");
		dialCodes.put("bd", "880");
		dialCodes.put("bb", "1246");
		dialCodes.put("by", "375");
		dialCodes.put("be", "32");
		dialCodes.put("bz", "501");
		dialCodes.put("bj", "229");
		dialCodes.put("bm", "1441");
		dialCodes.put("bt", "975");
		dialCodes.put("bo", "591");
		dialCodes.put("ba", "387");
		dialCodes.put("bw", "267");
		dialCodes.put("br", "55");
		dialCodes.put("io", "246");
		dialCodes.put("bn", "673");
		dialCodes.put("bg", "359");
		dialCodes.put("bf", "226");
		dialCodes.put("bi", "257");
		dialCodes.put("kh", "855");
		dialCodes.put("cm", "237");
		dialCodes.put("cv", "238");
		dialCodes.put("ky", "1345");
		dialCodes.put("cf", "236");
		dialCodes.put("td", "235");
		dialCodes.put("cl", "56");
		dialCodes.put("cn", "86");
		dialCodes.put("cx", "618");
		dialCodes.put("co", "57");
		dialCodes.put("km", "269");
		dialCodes.put("cg", "242");
		dialCodes.put("cd", "243");
		dialCodes.put("ck", "682");
		dialCodes.put("cr", "506");
		dialCodes.put("ci", "225");
		dialCodes.put("hr", "385");
		dialCodes.put("cu", "53");
		dialCodes.put("cy", "357");
		dialCodes.put("cz", "420");
		dialCodes.put("dk", "45");
		dialCodes.put("dj", "253");
		dialCodes.put("dm", "1767");
		dialCodes.put("do", "1809");
		dialCodes.put("ec", "593");
		dialCodes.put("eg", "20");
		dialCodes.put("sv", "503");
		dialCodes.put("gq", "240");
		dialCodes.put("er", "291");
		dialCodes.put("ee", "372");
		dialCodes.put("et", "251");
		dialCodes.put("fk", "500");
		dialCodes.put("fo", "298");
		dialCodes.put("fj", "679");
		dialCodes.put("fi", "358");
		dialCodes.put("fr", "33");
		dialCodes.put("gf", "594");
		dialCodes.put("pf", "689");
		dialCodes.put("ga", "241");
		dialCodes.put("gm", "220");
		dialCodes.put("ge", "995");
		dialCodes.put("de", "49");
		dialCodes.put("gh", "233");
		dialCodes.put("gi", "350");
		dialCodes.put("gr", "30");
		dialCodes.put("gl", "299");
		dialCodes.put("gd", "1473");
		dialCodes.put("gp", "590");
		dialCodes.put("gu", "1671");
		dialCodes.put("gt", "502");
		dialCodes.put("gn", "224");
		dialCodes.put("gw", "245");
		dialCodes.put("gy", "592");
		dialCodes.put("ht", "509");
		dialCodes.put("hn", "504");
		dialCodes.put("hk", "852");
		dialCodes.put("hu", "36");
		dialCodes.put("is", "354");
		dialCodes.put("in", "91");
		dialCodes.put("id", "62");
		dialCodes.put("ir", "98");
		dialCodes.put("iq", "964");
		dialCodes.put("ie", "353");
		dialCodes.put("il", "972");
		dialCodes.put("it", "39");
		dialCodes.put("jm", "1876");
		dialCodes.put("jp", "81");
		dialCodes.put("jo", "962");
		dialCodes.put("ke", "254");
		dialCodes.put("ki", "686");
		dialCodes.put("kp", "850");
		dialCodes.put("kr", "82");
		dialCodes.put("kw", "965");
		dialCodes.put("kg", "996");
		dialCodes.put("la", "856");
		dialCodes.put("lv", "371");
		dialCodes.put("lb", "961");
		dialCodes.put("ls", "266");
		dialCodes.put("lr", "231");
		dialCodes.put("ly", "218");
		dialCodes.put("li", "423");
		dialCodes.put("lt", "370");
		dialCodes.put("lu", "352");
		dialCodes.put("mo", "853");
		dialCodes.put("mk", "389");
		dialCodes.put("mg", "261");
		dialCodes.put("mw", "265");
		dialCodes.put("my", "60");
		dialCodes.put("mv", "960");
		dialCodes.put("ml", "223");
		dialCodes.put("mt", "356");
		dialCodes.put("mh", "692");
		dialCodes.put("mq", "596");
		dialCodes.put("mr", "222");
		dialCodes.put("mu", "230");
		dialCodes.put("mx", "52");
		dialCodes.put("fm", "691");
		dialCodes.put("md", "373");
		dialCodes.put("mc", "377");
		dialCodes.put("mn", "976");
		dialCodes.put("me", "382");
		dialCodes.put("ms", "663");
		dialCodes.put("ma", "212");
		dialCodes.put("mz", "258");
		dialCodes.put("mm", "95");
		dialCodes.put("na", "264");
		dialCodes.put("nr", "674");
		dialCodes.put("np", "977");
		dialCodes.put("nl", "31");
		dialCodes.put("an", "599");
		dialCodes.put("nc", "687");
		dialCodes.put("nz", "64");
		dialCodes.put("ni", "505");
		dialCodes.put("ne", "227");
		dialCodes.put("ng", "234");
		dialCodes.put("nu", "683");
		dialCodes.put("mp", "1670");
		dialCodes.put("no", "47");
		dialCodes.put("om", "968");
		dialCodes.put("pk", "92");
		dialCodes.put("pw", "680");
		dialCodes.put("ps", "970");
		dialCodes.put("pa", "507");
		dialCodes.put("pg", "675");
		dialCodes.put("py", "595");
		dialCodes.put("pe", "51");
		dialCodes.put("ph", "63");
		dialCodes.put("pl", "48");
		dialCodes.put("pt", "351");
		dialCodes.put("pr", "786");
		dialCodes.put("qa", "974");
		dialCodes.put("re", "262");
		dialCodes.put("ro", "40");
		dialCodes.put("ru", "7");
		dialCodes.put("rw", "250");
		dialCodes.put("sh", "290");
		dialCodes.put("lc", "1757");
		dialCodes.put("pm", "508");
		dialCodes.put("vc", "1784");
		dialCodes.put("ws", "685");
		dialCodes.put("sm", "378");
		dialCodes.put("st", "239");
		dialCodes.put("sa", "966");
		dialCodes.put("sn", "221");
		dialCodes.put("rs", "381");
		dialCodes.put("sc", "248");
		dialCodes.put("sl", "232");
		dialCodes.put("sg", "65");
		dialCodes.put("sk", "421");
		dialCodes.put("si", "386");
		dialCodes.put("sb", "677");
		dialCodes.put("so", "252");
		dialCodes.put("za", "27");
		dialCodes.put("es", "34");
		dialCodes.put("lk", "94");
		dialCodes.put("sd", "249");
		dialCodes.put("sr", "597");
		dialCodes.put("sz", "268");
		dialCodes.put("se", "46");
		dialCodes.put("ch", "41");
		dialCodes.put("sy", "963");
		dialCodes.put("tw", "886");
		dialCodes.put("tj", "992");
		dialCodes.put("tz", "255");
		dialCodes.put("th", "66");
		dialCodes.put("tl", "670");
		dialCodes.put("tg", "228");
		dialCodes.put("tk", "690");
		dialCodes.put("to", "676");
		dialCodes.put("tt", "1868");
		dialCodes.put("tn", "216");
		dialCodes.put("tr", "90");
		dialCodes.put("tm", "993");
		dialCodes.put("tc", "1649");
		dialCodes.put("tv", "688");
		dialCodes.put("ug", "256");
		dialCodes.put("ua", "380");
		dialCodes.put("ae", "971");
		dialCodes.put("gb", "44");
		dialCodes.put("us", "1");
		dialCodes.put("uy", "598");
		dialCodes.put("uz", "998");
		dialCodes.put("vu", "678");
		dialCodes.put("ve", "58");
		dialCodes.put("vn", "84");
		dialCodes.put("vg", "1284");
		dialCodes.put("vi", "1340");
		dialCodes.put("wf", "681");
		dialCodes.put("ye", "967");
		dialCodes.put("zm", "260");
		dialCodes.put("zw", "263");
	}

	/**
	 * Provides the country code for the phone number passed in.
	 * Attempts to find the longest dial code that matches the start of the msisdn.
	 * 
	 * @param msisdn
	 *            The phone number to get the country code for
	 * @return the country code for the phone number.
	 */
	public static String getCountryCode(String msisdn) {
		String formulated = formulateDestinationMsisdn(msisdn);
		Enumeration e = dialCodes.keys();
		String k = null;
		String val = null;
		String rtn = null;
		String longestMatch = null;
		int longestLength = 0;
		while (e.hasMoreElements()) {
			k = (String) e.nextElement();
			val = (String) dialCodes.get(k);
			if (formulated.startsWith(val) && val.length() > longestLength) {
				// now just because the dial code matches the start of the msisdn
				// does not mean this this is the right code for example us = 1
				// but also tt = 1868, so if the msisdn started with 1868 then its tt and not us
				// so we need to keep track of the longest length
				longestMatch = val;
				rtn = k;
				longestLength = longestMatch.length();
			}
		}
		/*
		 * if(rtn == null)
		 * System.out.println("Warning msisdn [" + msisdn + "] unable to find country code") ;
		 */
		return rtn;
	}

	/**
	 * Returns a list of zero or more dial codes that start with the value passed in.
	 * 
	 * @param startsWith
	 *            Used to match the start of the dial code.
	 * @param max
	 *            the max number to return
	 * @return A list of zero or more dial codes that start with the value passed in.
	 */
	public static List<String> getStartsWithDialCodes(String startsWith, int max) {
		if (startsWith.startsWith("+")) {
			startsWith = startsWith.substring(1, startsWith.length());
		}
		Vector<String> rtn = new Vector<String>();
		Iterator iter = dialCodes.values().iterator();
		String val = null;
		while (iter.hasNext()) {
			val = (String) iter.next();
			if (val.startsWith(startsWith)) {
				rtn.addElement(val);
			}
			if (rtn.size() >= max) {
				break;
			}
		}
		return rtn;
	}

	/**
	 * Returns the dial code for the country or null if not found.
	 * 
	 * @param countryCode
	 *            the country code to get the dial code for
	 * @return The found dial code or null if not found.
	 */
	public static String getDialCode(String countryCode) {
		// If we have no country code assume UK
		if (countryCode != null)
			return (String) dialCodes.get(countryCode.toLowerCase());
		else
			return "44";
	}

	/**
	 * Returns the country id of the dial code passed in.
	 * IE put 44 in and get gb out.
	 * 
	 * @param dialCode
	 *            The dial code to get the country code for.
	 * @return The found country code or null if not found.
	 */
	public static String getCountryId(String dialCode) {
		Enumeration e = dialCodes.keys();
		String k = null;
		String val = null;
		while (e.hasMoreElements()) {
			k = (String) e.nextElement();
			val = (String) dialCodes.get(k);
			if (val.equals(dialCode)) {
				return k;
			}
		}
		return null;
	}

	/**
	 * Returns the country dialing code for the country id passed in.
	 * IE put gb in and get 44 out.
	 */
	public static String getInternationalDialingCode(String countryCode) throws Exception {
		String prefix = getDialCode(countryCode);
		if (prefix == null) {
			throw new Exception("Country [" + countryCode + "] not supported");
		}
		return prefix;
	}

	/**
	 * Makes a full international dialing number including the +,
	 * In general we always store phone numbers with the + to show
	 * they are international. Many systems require the + to be dropped
	 * IE SMS systems, but that dropping of the + is done just prior to sending.
	 * 
	 * @param phoneNo
	 *            the phone number in +447976... or 00447976, or 07976... and dialcode, or 447976...
	 * @param dialCode
	 *            the code ie 44 or 01, etc.
	 * @return a full phone number like +447976266283
	 * @throws Exception
	 *             if the phone number cannot be made.
	 */
	public static String makeInternationalNumberWithCode(String phoneNo, String dialCode) throws Exception {
		phoneNo = deMangle(phoneNo);

		if (phoneNo.startsWith("+")) {
			return phoneNo;
		}
		// Or if international with 00 at front remove 00
		if (phoneNo.startsWith("00")) {
			int index = phoneNo.indexOf("00");
			return "+" + phoneNo.substring(index + 2, phoneNo.length());
		}
		// If none country based remove 0 and add dial prefix
		if (phoneNo.startsWith("0")) {
			int index = phoneNo.indexOf("0");
			return "+" + dialCode + phoneNo.substring(index + 1, phoneNo.length());
		}
		// maybe they forgot the + or the 00 but did include their dial code
		if (phoneNo.startsWith(dialCode)) {
			return "+" + phoneNo;
		}
		// I have no idea what it is
		throw new Exception("Invalid Phone Number [" + phoneNo + "]");
	}

	/**
	 * Converts an msisdn from some sort of free format entry into a standard form for storage and searching.
	 * Just wraps makeInternationalNumber but returns null rather than exception.
	 * 
	 * @param phoneNo
	 *            The phone number to convert
	 * @return The converted value or null if unable to convert.
	 */
	public static String makeStandardNumberFormat(String phoneNo) {
		try {
			return makeInternationalNumber(phoneNo);
		} catch (Exception ex) {
			return null;
		}
	}

	public static String makeInternationalNumber(String phoneNo) throws Exception {
		String countryCode = DialCode.getCountryCode(phoneNo);
		if (countryCode == null) {
			countryCode = "gb"; // assume gb
		}
		String dialCode = getInternationalDialingCode(countryCode);
		return DialCode.makeInternationalNumberWithCode(phoneNo, dialCode);
	}

	/**
	 * In case the user does not enter their full mobile number including country code
	 * we need to make it for them and store it in full international form. If the
	 * user has entered the country code with + or 00 we do NOT check it.
	 */
	public static String makeInternationalNumber(String phoneNo, String countryCode) throws Exception {
		String dialCode = getInternationalDialingCode(countryCode);
		return DialCode.makeInternationalNumberWithCode(phoneNo, dialCode);
	}

	/**
	 * Ensures that destination is in the correct form for this SMS provider.
	 * By default this method strips any +, 00 to hold just the international
	 * phone number. In addition if it starts with just a single 0 it is stripped and UK
	 * code of 44 is prefixed.
	 */
	// public static String formulateDestinationMsisdn(Destination destination)
	// {
	// return DialCode.formulateDestinationMsisdn(destination.getMsisdn()) ;
	// }

	/**
	 * This methods takes a phone number and removes the +, 00, 0 (adds 44) to make
	 * the phone number suitable for transmission to SMS and other systems.
	 * Internally all phone numbers are stored with +countryCodePhoneNo so that
	 * we are really sure that the country code is included.
	 * 
	 * @param msisdn
	 *            The number to convert.
	 * @return The converted phone number.
	 */
	public static String formulateDestinationMsisdn(String msisdn) {
		// see if international with + at front, if so remove +
		if (msisdn.startsWith("+")) {
			int index = msisdn.indexOf("+");
			return msisdn.substring(index + 1, msisdn.length());
		}
		// Or if international with 00 at front remove 00
		if (msisdn.startsWith("00")) {
			int index = msisdn.indexOf("00");
			return msisdn.substring(index + 2, msisdn.length());
		}
		// If none country based remove 0 and add 44, but issue warning
		if (msisdn.startsWith("0")) {
			int index = msisdn.indexOf("0");
			return "44" + msisdn.substring(index + 1, msisdn.length());
		}
		// it's ok to go as is
		return msisdn;
	}

	/**
	 * Despite asking people not to include spaces and () people still do.
	 * So this bit of code demangles phone numbers.
	 * Like +44 7976 266 283<br/>
	 * And +44 (0)7976 266 283<br/>
	 * 
	 * @param msisdn
	 * @return A demangles msisdn with no spaces and no braces
	 */
	private static String deMangle(String msisdn) {
		String noSpaces = deSpace(msisdn);
		String rtn = deBrace(noSpaces);

		return rtn;
	}

	private static String deBrace(String msisdn) {
		// see if there are some braces
		if (msisdn.indexOf('(') == -1) {
			return msisdn;
		}

		StringBuffer buffer = new StringBuffer();
		char[] chars = msisdn.toCharArray();
		boolean inside = false;
		for (int i = 0; i < chars.length; i++) {
			if (chars[i] == '(') {
				inside = true;
			}

			if (!inside) {
				buffer.append(chars[i]);
			}

			if (chars[i] == ')') {
				inside = false;
			}
		}
		return buffer.toString();

	}

	private static String deSpace(String msisdn) {
		// check if there are no spaces
		if (msisdn.indexOf(' ') == -1) {
			return msisdn;
		}

		StringBuffer buffer = new StringBuffer();
		char[] chars = msisdn.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			if (chars[i] != ' ') {
				buffer.append(chars[i]);
			}
		}
		return buffer.toString();
	}
}
