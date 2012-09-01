/*
 * RequestCallback.java
 * Interface for AsyncRequest's callback
 * Copyright (C) Shawn Busolits, 2012 All Rights Reserved
 */

package busoLibs.asyncRequest;

/**
 * Interface for AsyncRequest's callback
 * @author Shawn Busolits
 * @version 1.0
 */
public interface RequestCallback {
	
	/**
	 * Implement this method to run code after AsycRequest's request resolves
	 * @param o Return object from AsyncRequest
	 */
	public void doOnResult(Object o);

}
