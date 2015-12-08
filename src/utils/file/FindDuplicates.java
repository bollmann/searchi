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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public final class FindDuplicates {
	
	private class PageBlob {
		String url;
		List<String> outgoingLinks;
	}
	
	public static void main(String [] args) throws JsonSyntaxException, IOException {
		
		if (args.length != 1) {
			System.err.println("ERROR");
			System.exit(-1);
		}
		
		File inputDir = new File(args[0].trim());

		Map<String, Integer> mp = new HashMap<String, Integer>();
		
		File[] files = inputDir.listFiles();		
		for (int i = 0; i < files.length; i++) {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(files[i])));
			
			String line = "";
			while ((line = br.readLine())!= null) {
				Gson gson = new Gson();
				PageBlob blob = gson.fromJson(line.trim().toString(), PageBlob.class);
				
				String url = blob.url.trim();
				if (mp.containsKey(url)) {
					mp.put(url, mp.get(url) + 1);
					System.out.println("Duplicate Key - " + url + " with count " + mp.get(url) + " file " + files[i].getName());
				}
				else {
					mp.put(url, 1);
				}
			}
			br.close();
				
		}
	}
}
