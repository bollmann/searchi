package test.indexer;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import indexer.offline.Tokenizer;
import indexer.offline.Tokenizer.TokenType;
import junit.framework.TestCase;

public class TestTokenizer extends TestCase {
	
	@Test
	public void testGetTokenTypeFromNGramSize() {		
		assertEquals(TokenType.UNIGRAM, Tokenizer.getTokenTypeFromNGramSize(1));
		assertEquals(TokenType.BIGRAM, Tokenizer.getTokenTypeFromNGramSize(2));
		assertEquals(TokenType.TRIGRAM, Tokenizer.getTokenTypeFromNGramSize(3));
		
	}
	
	@Test
	public void testGetTokens() {
		
		String text = "the match is a awesome timepass";
		Tokenizer tokenizer = new Tokenizer(text, TokenType.UNIGRAM);
		
		List<String>expectedUnigram = Arrays.asList(text.split(" "));		
		assertEquals(expectedUnigram, tokenizer.getTokens());
		
		tokenizer = new Tokenizer(text, TokenType.BIGRAM);
		List<String>expectedBigram = Arrays.asList("match", "is", "awesome", "timepass");		
		assertEquals(expectedBigram, tokenizer.getTokens());
		
	}


}
