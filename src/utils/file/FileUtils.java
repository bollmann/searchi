/*
 * Written by Shreejit Gangadharan
 * Ishan Srivastava

 */
package utils.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

// TODO: Auto-generated Javadoc
/**
 * The Class FilePolicy.
 */
public class FileUtils {

	/**
	 * Exists.
	 *
	 * @param filePath
	 *            the file path
	 * @return true, if file exists
	 */
	public static boolean exists(Path filePath) {
		return Files.exists(filePath);
	}

	/**
	 * Checks if is readable.
	 *
	 * @param filePath
	 *            the file path
	 * @return true, if is readable
	 */
	public static boolean isReadable(Path filePath) {
		return Files.isReadable(filePath);
	}

	/**
	 * Checks if is accessible.
	 *
	 * @param filePath
	 *            the file path
	 * @param webRoot
	 *            the web root
	 * @return true, if is accessible
	 */
	public static boolean isAccessible(Path filePath, Path webRoot) {
		return filePath.toAbsolutePath().startsWith(webRoot.toAbsolutePath());
	}

	/**
	 * Checks if is directory.
	 *
	 * @param filePath
	 *            the file path
	 * @return true, if is directory
	 */
	public static boolean isDirectory(Path filePath) {
		return Files.isDirectory(filePath);
	}

	/**
	 * Probe content type.
	 *
	 * @param filePath
	 *            the file path
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
			sb.append((char) c);
		}
		fis.close();
		return sb.toString();

	}

	/**
	 * Delete a Directory and all its contents recursively if it exists.
	 *
	 * @param root
	 *            the root directory to delete.
	 */
	public static void deleteIfExists(Path root) {
		if (root.toFile().isDirectory()) {
			for (File file : root.toFile().listFiles()) {
				if (file.isFile()) {
					file.delete();
				} else if (file.isDirectory()) {
					deleteIfExists(file.toPath());
					file.delete();
				}
			}
		}
		if (root.toFile().exists())
			root.toFile().delete();
	}

	public static void deleteFileIfPresent(String refFileList,
			String newFileList, String fileDirectory) throws IOException {
		File refFile = new File(refFileList);
		File newFile = new File(newFileList);

		Set<String> seenFiles = new HashSet<String>();
		String line = null;
		BufferedReader br = new BufferedReader(new FileReader(refFile));
		while ((line = br.readLine()) != null) {
			seenFiles.add(line);
		}
		System.out.println("Seen files has " + seenFiles.size()
				+ " files in it");

		br = new BufferedReader(new FileReader(newFileList));
		while ((line = br.readLine()) != null) {
			if (seenFiles.contains(line)) {
				// delete file
				String pathString = fileDirectory + "/" + line;
				System.out.println("Will delete " + pathString);
				Path path = Paths.get(pathString);
				Files.deleteIfExists(path);
			}
		}

	}

	public static void main(String[] args) {
		if(args.length < 3) {
			System.out.println("utils.file.FileUtils referenceFileList newFileList fileDirectory");
			return;
		}
		try {
			deleteFileIfPresent(args[0], args[1], args[2]);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
	}
}
