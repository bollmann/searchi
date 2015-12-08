package utils.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import utils.nlp.LanguageDetector;
import utils.nlp.PornDetector;

import com.google.gson.Gson;

import crawler.dao.URLContent;

public class ConcatFiles {

	private String inDir;
	private String outFilePrefix;
	private int numFilesToCombine;

	public ConcatFiles(String inpDir, String outFilePrefix, int numFiles) {
		this.inDir = inpDir;
		this.outFilePrefix = outFilePrefix;
		this.numFilesToCombine = numFiles;
	}

	public void concat() throws ArrayIndexOutOfBoundsException, IOException {
		File inputDir = new File(this.inDir);

		File[] files = inputDir.listFiles();
		int lineNr = 1;
		int prefixCount = 1;
		for (int i = 0; i < files.length; i += this.numFilesToCombine) {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(String.format("%s-%d", outFilePrefix,
							prefixCount)), Charset.forName("UTF-8")));

			for (int j = 0; j < this.numFilesToCombine && i + j < files.length; ++j) {
				BufferedReader br = new BufferedReader(new InputStreamReader(
						new FileInputStream(files[i + j])));
				String fileContent = br.readLine();
				URLContent content = new Gson().fromJson(fileContent,
						URLContent.class);
				if (LanguageDetector.isEnglish(content.getContent())
						&& !PornDetector.isPorn(content.getContent())) {
					writer.write(Integer.toString(lineNr) + "\t" + fileContent
							+ "\n");
					lineNr++;
				} else {
					files[i + j].delete();
				}

				br.close();
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

		} catch (ArrayIndexOutOfBoundsException | FileNotFoundException
				| NumberFormatException e) {
			usage();
			System.exit(1);
		}
	}

	private static void usage() {
		System.out.println("ConcatFiles <inputdir> <outfile> <num>");
		System.out
				.println("  concatenates all files in <inputdir> into files <outfile-i>");
		System.out
				.println("  where each <outfile-i> is made out of <num> original input files.");
	}
}
