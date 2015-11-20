package utils.file;

import java.io.File;
import java.nio.file.Path;

/**
 * The Class FileUtils.
 */
public class FileUtils {
	
	/**
	 * Delete a Directory and all its contents recursively if it exists.
	 *
	 * @param root the root directory to delete.
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
		if(root.toFile().exists())
			root.toFile().delete();
	}
}
