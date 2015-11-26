/*
 * Written by Shreejit Gangadharan
 */
package crawler.responses;

// TODO: Auto-generated Javadoc
/**
 * The Class Response.
 */
public class Response {
	
	/** The response code. */
	private Integer responseCode;
	
	/** The response string. */
	private String responseString;
	
	/**
	 * Gets the response code.
	 *
	 * @return the response code
	 */
	public Integer getResponseCode() {
		return responseCode;
	}
	
	/**
	 * Sets the response code.
	 *
	 * @param responseCode the new response code
	 */
	public void setResponseCode(Integer responseCode) {
		this.responseCode = responseCode;
	}
	
	/**
	 * Gets the response string.
	 *
	 * @return the response string
	 */
	public String getResponseString() {
		return responseString;
	}
	
	/**
	 * Sets the response string.
	 *
	 * @param responseString the new response string
	 */
	public void setResponseString(String responseString) {
		this.responseString = responseString;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return responseCode + " " + responseString;
	}
}
