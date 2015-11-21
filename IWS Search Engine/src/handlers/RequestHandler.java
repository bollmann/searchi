/*
 * Written by Shreejit Gangadharan
 */
package handlers;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

import responses.HttpResponse;
import threadpool.Queue;

// TODO: Auto-generated Javadoc
/**
 * The Class RequestHandler.
 */
public abstract class RequestHandler {
	
	/** The socket. */
	protected Socket socket;
	
	/**
	 * Handle request.
	 *
	 * @param fullInput the full input
	 * @param webRoot the web root
	 * @param jobQueue the job queue
	 * @return the http response
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public abstract HttpResponse handleRequest(List<String> fullInput, String webRoot,
			Queue<Socket> jobQueue) throws IOException;
	
	/**
	 * Write output.
	 *
	 * @param output the output
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public abstract void writeOutput(byte[] output) throws IOException;
}
