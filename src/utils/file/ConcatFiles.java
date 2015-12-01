package utils.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Scanner;

public class ConcatFiles {
	
	private String inpDir;
	private String outFilePrefix;
	private int numFilesToCombine;

	public ConcatFiles(String inpDir, String outFilePrefix, int numFiles) {
		this.inpDir = inpDir;
		this.outFilePrefix = outFilePrefix;
		this.numFilesToCombine = numFiles;
	}
	
	public void concat() throws ArrayIndexOutOfBoundsException, IOException {
		File inputDir = new File(this.inpDir);

		File[] files = inputDir.listFiles();
		int prefixCount = 1;
		for (int i = 0; i < files.length; i += this.numFilesToCombine) {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(String.format("%s-%d", outFilePrefix,
					prefixCount)), Charset.forName("UTF-8")));

			for (int j = 0; j < this.numFilesToCombine && i + j < files.length; ++j) {
				writer.write(new Scanner(files[i + j], "UTF-8").useDelimiter(
					"\\Z").next() + "\n");
			}
				
			writer.close();
			++prefixCount;
		}
	}
	
	public static void main(String[] args) throws IOException {
		try {
			
			ConcatFiles concatUtil = new ConcatFiles(args[0], args[1],
				Integer.parseInt(args[2]));
			concatUtil.concat();
			
		} catch (ArrayIndexOutOfBoundsException | FileNotFoundException e) {
			usage();
			System.exit(1);
		}
	}

	private static void usage() {
		System.out.println("ConcatFiles <inputdir> <outfile> <num>");
		System.out.println("  concatenates all files in <inputdir> into files <outfile-i>");
		System.out.println("  where each <outfile-i> is made out of <num> original input files.");
	}
}
