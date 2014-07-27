import java.util.*;
import java.io.*;
/**
 * 
 * @author seanbucholtz
 *
 */
public class LexiconAnalyzer {
	
	private File lexiconFile;
	private ArrayList<String> lexicon;
	private int numberOfWords;
	private int maxWordLength;
	private int minWordLength;
	
	
	public LexiconAnalyzer(File f) throws FileNotFoundException {
		lexiconFile = f;
		numberOfWords = 0;
		maxWordLength = 0;
		minWordLength = 0;
		lexicon = readLexicon(lexiconFile);

	}
	
	public ArrayList<String> readLexicon(File f) throws FileNotFoundException {
		ArrayList<String> words = new ArrayList<String>();
		Scanner file = new Scanner(f);
		while(file.hasNextLine()) {
			String word = file.nextLine();
			numberOfWords ++;
			if(word.length() > maxWordLength) {
				maxWordLength = word.length();
			}
			else if (word.length() < minWordLength) {
				minWordLength = word.length();
			}
			// add word to the list
			words.add(word);
			}
		file.close();
		return words;
		}
	
	public void report() {
		System.out.println("Number of words: " + numberOfWords + "\nMax word length: " + maxWordLength 
				+ "\nMin word length: " + minWordLength);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String fileName = null;
		try {
			LexiconAnalyzer lA = new LexiconAnalyzer(new File(fileName));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * @return the lexiconFile
	 */
	public File getLexiconFile() {
		return lexiconFile;
	}

	/**
	 * @return the lexicon
	 */
	public ArrayList<String> getLexicon() {
		return lexicon;
	}

	/**
	 * @return the numberOfWords
	 */
	public int getNumberOfWords() {
		return numberOfWords;
	}

	/**
	 * @return the maxWordLength
	 */
	public int getMaxWordLength() {
		return maxWordLength;
	}

	/**
	 * @return the minWordLength
	 */
	public int getMinWordLength() {
		return minWordLength;
	}

}
