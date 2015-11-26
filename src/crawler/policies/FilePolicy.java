/*
 * Written by Shreejit Gangadharan
 */
package crawler.policies;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

// TODO: Auto-generated Javadoc
/**
 * The Class FilePolicy.
 */
public class FilePolicy {
	
	/**
	 * Exists.
	 *
	 * @param filePath the file path
	 * @return true, if file exists
	 */
	public static boolean exists(Path filePath) {
		return Files.exists(filePath);
	}
	
	/**
	 * Checks if is readable.
	 *
	 * @param filePath the file path
	 * @return true, if is readable
	 */
	public static boolean isReadable(Path filePath) {
		return Files.isReadable(filePath);
	}
	
	/**
	 * Checks if is accessible.
	 *
	 * @param filePath the file path
	 * @param webRoot the web root
	 * @return true, if is accessible
	 */
	public static boolean isAccessible(Path filePath, Path webRoot) {
		return filePath.toAbsolutePath().startsWith(webRoot.toAbsolutePath());
	}
	
	/**
	 * Checks if is directory.
	 *
	 * @param filePath the file path
	 * @return true, if is directory
	 */
	public static boolean isDirectory(Path filePath) {
		return Files.isDirectory(filePath);
	}
	
	/**
	 * Probe content type.
	 *
	 * @param filePath the file path
	 * @return the MIME type of the file
	 */
	public static String probeContentType(Path filePath) {
		try {
			return Files.probeContentType(filePath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}
	
	public static String readFile(String filePath) throws IOException {
		File file = new File(filePath);
		int c;
		FileInputStream fis = new FileInputStream(file);
		StringBuffer sb = new StringBuffer();
		while ((c = fis.read()) != -1) {
			sb.append((char)c);
		}
		fis.close();
		return sb.toString();
		
	}
}
