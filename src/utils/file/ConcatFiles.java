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

import org.apache.log4j.Logger;

import utils.nlp.LanguageDetector;
import utils.nlp.PornDetector;

import com.google.gson.Gson;

import crawler.dao.URLContent;

public class ConcatFiles {

	private static final Logger logger = Logger.getLogger(ConcatFiles.class);
	private String inDir;
	private String outFilePrefix;
	private int numFilesToCombine;

	public ConcatFiles(String inpDir, String outFilePrefix, int numFiles) {
		this.inDir = inpDir;
		this.outFilePrefix = outFilePrefix;
		this.numFilesToCombine = numFiles;
	}

	public void concat(int startInd) throws ArrayIndexOutOfBoundsException, IOException {
		File inputDir = new File(this.inDir);

		File[] files = inputDir.listFiles();
		int lineNr = startInd;
		int prefixCount = 1;
		int cntFile = 0;
		
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
			new FileOutputStream(String.format("%s-%d", outFilePrefix,
				prefixCount)), Charset.forName("UTF-8")));;
		for (int i = 0; i < files.length; ++i) {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(files[i])));
			String fileContent = br.readLine();

			URLContent content = null;
			try {
				content = new Gson().fromJson(fileContent,URLContent.class);
			} catch (Exception e) {
				logger.info(String.format(
					"Deleting file %s because JSON parse exception ",files[i].getName()), e);
				files[i].delete();
				br.close();
				continue;
			}
			
			if (content == null	|| !LanguageDetector.isEnglish(content.getContent())
					|| PornDetector.isPorn(content.getContent())) {
				files[i].delete();
				br.close();
				continue;
			}
			
			writer.write(Integer.toString(lineNr) + "\t" + fileContent + "\n");
			lineNr++;
			cntFile++;
			
			if (cntFile == numFilesToCombine) {
				cntFile = 0;
				prefixCount++;
				writer.close();
				writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(String.format("%s-%d", outFilePrefix,
						prefixCount)), Charset.forName("UTF-8")));
			}
			br.close();
		}
	}

	public static void main(String[] args) throws IOException {
		try {
			ConcatFiles concatUtil = new ConcatFiles(args[0], args[1],
					Integer.parseInt(args[2]));
			concatUtil.concat(Integer.parseInt(args[3].trim()));

		} catch (ArrayIndexOutOfBoundsException | FileNotFoundException
				| NumberFormatException e) {
			usage();
			System.out.println(e.getMessage());
			System.exit(1);
		}
	}

	private static void usage() {
		System.out.println("ConcatFiles <inputdir> <outfile> <num> <startInd>");
		System.out
				.println("  concatenates all files in <inputdir> into files <outfile-i>");
		System.out
				.println("  where each <outfile-i> is made out of <num> original input files.");
	}
}
