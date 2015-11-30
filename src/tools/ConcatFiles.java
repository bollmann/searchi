package tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Scanner;

public class ConcatFiles {
	public static void main(String[] args) throws IOException {
		try {
			File inputDir = new File(args[0]);
			String outFile = args[1];
			int pushToFileCount = Integer.parseInt(args[2]);
			File[] files = inputDir.listFiles();
			int prefixCount = 1;
			for (int i = 0; i < files.length; i += pushToFileCount) {
				BufferedWriter writer = new BufferedWriter(
						new OutputStreamWriter(new FileOutputStream(
								String.format("%s-%d", outFile, prefixCount)),
								Charset.forName("UTF-8")));

				for (int j = 0; j < pushToFileCount && i+j < files.length; ++j)
					writer.write(new Scanner(files[i+j],
							"UTF-8").useDelimiter("\\Z").next() + "\n");

				writer.close();
				++prefixCount;
			}
		} catch (ArrayIndexOutOfBoundsException | FileNotFoundException e) {
			usage();
			System.exit(1);
		}
	}

	public static void usage() {
		System.out.println("ConcatFiles <inputdir> <outfile> <num>");
		System.out.println("  concatenates all files in <inputdir> into files <outfile-i>");
		System.out.println("  where each <outfile-i> is made out of <num> original input files.");
	}
}
