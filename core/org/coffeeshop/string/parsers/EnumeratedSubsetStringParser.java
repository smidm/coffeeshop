/* THIS FILE IS A MEMBER OF THE COFFEESHOP LIBRARY
 * 
 * License:
 * 
 * Coffeeshop is a conglomerate of handy general purpose Java classes.  
 * 
 * Copyright (C) 2006-2008 Luka Cehovin
 * This library is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as 
 *  published by the Free Software Foundation; either version 2.1 of 
 *  the License, or (at your option) any later version.
 *  
 *  This library is distributed in the hope that it will be useful, 
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of 
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
 *  GNU Lesser General Public License for more details. 
 *  
 *  http://www.opensource.org/licenses/lgpl-license.php
 *  
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the 
 *  Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 *  Boston, MA 02111-1307 USA 
 * 
 * 
 * This code is based on JSAP project from Martian Software, Inc.
 * http://www.martiansoftware.com/
 */

package org.coffeeshop.string.parsers;

import java.util.HashSet;
import java.util.StringTokenizer;


public class EnumeratedSubsetStringParser extends EnumeratedStringParser implements StringParser {

	private String separator = ",";
	
	/**
	 * Constructs a new instance of EnumeratedSubsetStringParser.
	 * 
	 * @param validOptionValues a string that contains valid values for an option 
	 *        in the format "value_1;value_2;..;value_n"; spaces between values are allowed 
	 *        to make things more readable, e.g., "value_1; value_2";
	 *        option values have to be constructed using Java identifier characters
	 *        if the checkOptionChars parameter tells the parser to do this.
	 * @param caseSensitive tells the parser whether the option value is case sensitive
	 * @throws IllegalArgumentException if the option value string has wrong format
	 *         or is empty
	 */
	public EnumeratedSubsetStringParser(String validOptionValues, boolean caseSensitive, String separator) throws IllegalArgumentException {
		super(validOptionValues, caseSensitive, true);
		this.separator = separator != null ? separator : ",";
	}

	/**
	 * Constructs a new instance of EnumeratedSubsetStringParser.
	 * 
	 */
	public EnumeratedSubsetStringParser(Class<?> validOptionValues, boolean caseSensitive, String separator) throws IllegalArgumentException {
		super(validOptionValues, caseSensitive);
		this.separator = separator != null ? separator : ",";
	}

	/**
	 * Constructs a new instance of EnumeratedSubsetStringParser.
	 * 
	 */
	public EnumeratedSubsetStringParser(Iterable<?> values, boolean caseSensitive, String separator) throws IllegalArgumentException {
		super(values, caseSensitive);
		this.separator = separator != null ? separator : ",";
	}
	
	public EnumeratedSubsetStringParser(String validOptionValues, boolean caseSensitive) throws IllegalArgumentException {
		this(validOptionValues, caseSensitive, null);
	}

	/**
	 * Constructs a new instance of EnumeratedSubsetStringParser.
	 * 
	 */
	public EnumeratedSubsetStringParser(Class<?> validOptionValues, boolean caseSensitive) throws IllegalArgumentException {
		this(validOptionValues, caseSensitive, null);
	}

	/**
	 * Constructs a new instance of EnumeratedSubsetStringParser.
	 * 
	 */
	public EnumeratedSubsetStringParser(Iterable<?> values, boolean caseSensitive) throws IllegalArgumentException {
		this(values, caseSensitive, null);
	}
	
	/**
	 * Parses the specified argument, making sure it matches one of the valid
	 * options supplied to its constructor.  
	 * If the specified argument is not a valid option, 
	 * a ParseException is thrown.
	 * 
	 * @param arg the argument to parse
	 * @return the String resulting from the parsed argument.
	 * @throws ParseException if the specified argument cannot be parsed.
	 */
	public Object parse(String arg) throws ParseException {
		if (arg == null) {
			return null;
		}
		
		StringTokenizer tokens = new StringTokenizer(arg, separator);
		
		HashSet<String> subset = new HashSet<String>();
		
		while (tokens.hasMoreTokens()) {
			
			String token = tokens.nextToken().trim();
			
			subset.add((String)super.parse(token));
			
		}
		
		return subset;
	}

	public String getSeparator() {
		
		return separator;
		
	}
	
}
