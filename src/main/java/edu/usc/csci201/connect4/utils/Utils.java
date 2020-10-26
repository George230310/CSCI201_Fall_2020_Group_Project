/**
* Utils.java is a static class used for general random methods
* that can be used throughout the program but don't pertain exactly
* to a class and is more relevant to be a general tool
* 
* @author      Mario Figueroa
* @version     %I%, %G%
* @since       1.0
*/

package edu.usc.csci201.connect4.utils;

import java.util.Base64;

public class Utils {
	
	
	public static String encrypt(String plain) {
		   String b64encoded = Base64.getEncoder().encodeToString(plain.getBytes());

		   // Reverse the string
		   String reverse = new StringBuffer(b64encoded).reverse().toString();

		   StringBuilder tmp = new StringBuilder();
		   final int OFFSET = 4;
		   for (int i = 0; i < reverse.length(); i++) {
		      tmp.append((char)(reverse.charAt(i) + OFFSET));
		   }
		   return tmp.toString();
		}
		
	public static String decrypt(String secret) {
	   StringBuilder tmp = new StringBuilder();
	   final int OFFSET = 4;
	   for (int i = 0; i < secret.length(); i++) {
	      tmp.append((char)(secret.charAt(i) - OFFSET));
	   }

	   String reversed = new StringBuffer(tmp.toString()).reverse().toString();
	   return new String(Base64.getDecoder().decode(reversed));
	}
		
}
