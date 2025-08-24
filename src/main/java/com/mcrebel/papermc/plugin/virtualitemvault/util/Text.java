package com.mcrebel.papermc.plugin.virtualitemvault.util;

import java.text.NumberFormat;
import java.util.Locale;

public class Text {
	private static final NumberFormat NF = NumberFormat.getIntegerInstance(Locale.US);

	public static String formatLong(long v) {
		return NF.format(v);
	}
}
