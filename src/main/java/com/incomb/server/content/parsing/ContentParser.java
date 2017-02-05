package com.incomb.server.content.parsing;

public interface ContentParser<T> {
	
	/**
	 * Parses the local <code>parsingElement</code> and returns true if
	 * the parsing was successful.
	 * @return <code>true</code> if the parsing was successful. 
	 * <code>false</code> if not.
	 */
	public boolean parse();
	
	/**
	 * Returns the parsed object.
	 * @return T parsed object
	 */
	public T getParsedObject();
	
}
