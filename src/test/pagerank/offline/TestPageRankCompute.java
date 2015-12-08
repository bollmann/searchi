package test.pagerank.offline;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class TestPageRankCompute {

	private static final String file = "/home/ishan/Git/Repos/searchi/hadoop-2.6.0/dummyinp_out/part-r-00000"; 
	
	@Test
	public void testResult() throws NumberFormatException, IOException {
		File f = new File(file);
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
		
		double totalScore = 0.0;
		int totalLines = 0;
		String line = "";
		
		while ((line = br.readLine()) != null) {
			String [] prData = line.trim().split("\t");
			String [] prData2 = prData[1].trim().split("###");
					
			totalScore += new Double(prData2[0].trim());
			totalLines++;
		}
		br.close();
		System.out.println(totalScore);
		System.out.println(totalLines);
		
	}

}
