package com.mcrebel.papermc.plugin.virtualitemvault.util;

public class Color {
	
	public static String color(String s) {
		return s == null ? "" : s.replace("&", "§");
	}

}
