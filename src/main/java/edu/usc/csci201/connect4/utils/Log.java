package edu.usc.csci201.connect4.utils;

public final class Log {
	
	public static void printlnServer(String s) {
		System.out.println("[Server] " + s);
	}
	
	public static void printlnClient(String s) {
		System.out.println("[Client] " + s);
	}
	
	public static void println(Object ... objs) {
		StringBuffer sb = new StringBuffer();
		for (Object obj : objs) sb.append(obj.toString() + ", ");
		System.out.println(sb.toString());
	}
	
	public static void print(Object ... objs) {
		StringBuffer sb = new StringBuffer();
		for (Object obj : objs) sb.append(obj.toString() + ", ");
		System.out.print(sb.toString());
	}
	
	public static void println(Object o) {
		System.out.println(o.toString());
	}
	
	public static void print(Object o) {
		System.out.print(o.toString());
	}
	
	public static void println(String ... strings) {
		StringBuffer sb = new StringBuffer();
		for (String s : strings) sb.append(s + ", ");
		System.out.println(sb.toString());
	}
	
	public static void print(String ... strings) {
		StringBuffer sb = new StringBuffer();
		for (String s : strings) sb.append(s + ", ");
		System.out.print(sb.toString());
	}
	
	public static void println(String s) {
		System.out.println(s);
	}
	

}
