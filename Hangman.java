import java.io.*;
import java.util.*;

import javax.swing.JOptionPane;
/**
 * This application plays a modified version of the game Hangman.
 * The game dynamically chooses the word being guessed based upon 
 * the user's current alphabetic input.
 * 
 * @author Sean Bucholtz
 * @version Assignment 4
 */
public class Hangman {
 
 // groups words according to positions of guessed letter
 private Map<String, List<String>> wordPartitions;
 // remaining possible words that fit the information given so far
 private List<String> currentList;
 // holds all the "wrong" guesses so far
 private Set<Character> wrongGuesses;
 // current state of the word being guessed
 private StringBuilder guessString;
 // for checking whether a guess was "wrong"
 private String justHyphens;
 // the number of tries it takes to guess the word
 private int numberOfAttempts;
 
 /**
  * The parameterized constructor.
  * @param wordLength the length of the word to be guessed
  * @throws FileNotFoundException if the lexicon file is not present
  * @throws IllegalArgumentException if wordLength argument is < 1, or > the longest word in the lexicon file
  */
 public Hangman(int wordLength) throws FileNotFoundException {
  // the application will not behave properly if the wordLength argument is < 1
  if(wordLength < 1) {
   throw new IllegalArgumentException("Word length must be greater-than 0.");
  }
  wordPartitions = new HashMap<String,List<String>>();
  currentList = new ArrayList<String>();
  wrongGuesses = new HashSet<Character>();
  guessString = new StringBuilder();
  justHyphens = "";
  numberOfAttempts = 0;
  // fill with hyphens
  for(int i = 1; i <= wordLength; i ++) {
   guessString.append("-");
   justHyphens += "-";
  }
  // the lexicon file
  File f = new File("Lexicon.txt");
  // scanner object for lexicon file
  Scanner file = new Scanner(f);
  // longest word
  int maxLength = 0;
  // scan file
  while(file.hasNextLine()) {
   String word = file.nextLine();
   // if current word length is greater-than the longest word found so far
   if(word.length() > maxLength) {
	 // set new max length
     maxLength = word.length();
   }
   // if word is equal to the desired word length
   if(word.length() == wordLength) {
    // add word to the list
    currentList.add(word);
   }
  }
  // if desired word length is greater than the longest word
  if(wordLength > maxLength) {
	// close resource
	file.close();
	// throw exception
    throw new IllegalArgumentException("Word length must be less-than " + maxLength);
  }
  // close resource
  file.close();
 }
 
 /**
  * Commences the Hangman game
  */
 public void play() {
  char choice;
  do {
   choice = getUserChoice();
   partition(choice); 
   updateCurrentList(choice);
   } 
  while (!gameOver());
   endMessage();
 }
 
 /**
  * Prompts the user for an alphabetic input
  * @return the user's input
  */
 private char getUserChoice() {
  // storage/sentinel for user input
  String input = "";
  // while input is empty
  while(input.length() == 0) {
   // prompt user for input while also displaying the current state of the game
   input = JOptionPane.showInputDialog("Word: " + guessString + 
     "\nIncorrect Guesses: " + wrongGuesses + 
     "\nPlease enter a letter from A-Z:");
   if(input == null) {
	 // reset sentinel
     input = "";
     // prompt user if they would like to quit
     int quit = JOptionPane.showConfirmDialog(null,"Would you like to quit?","Quit?",
    		 JOptionPane.YES_NO_OPTION);
     // if quit is yes
     if(quit == 0) {
       // end application
       System.exit(quit);
     }
   }
   // if input is a valid letter (A-Z)
   else if(input.length() > 0 && Character.isLetter(input.charAt(0))) {
    // exit input loop
    break;
   }
   // if not a valid letter
   else {
    // reset sentinel 
    input = "";
   }
  }
  // return the input character in lower case
  return Character.toLowerCase(input.charAt(0));
 }
 
 /**
  * Partitions the word possibilities based upon the current letter.
  * @param choice the current letter being guessed
  */
 private void partition(char choice) {
  // declare and instantiate list iterator
  Iterator<String> itr = currentList.iterator();
  while(itr.hasNext()) {
   // next word in the list
   String word = itr.next();
   String key = getPartitionKey(word, choice);
   // if partition does not contain key
   List<String> list = new ArrayList<String>();
   if(wordPartitions.containsKey(key)) {
    list = wordPartitions.get(key);
   }
   list.add(word);
   wordPartitions.put(key,list);
  }
 }
 
 /**
  * Generates a partition key based upon the the letter's position in the word.
  * @param s the word to be key'd
  * @param c the letter being searched for
  * @return
  */
 private String getPartitionKey(String s, char c) {
  // the key to be returned
  String key = "";
  // convert the word to a char array
  char[] word = s.toCharArray();
  for(char letter : word) {
   // if the letter matches the letter at the current location in the word
   if(letter == c) {
 // concatenate the letter on to the key
    key += String.valueOf(letter);
   }
   // if the letter does not match the letter at the current location in the word
   else {
 // concatenate a dash on to the key   
    key += "-";
   }
  }
  return key;
 }
 
 /**
  * Updates the current word list to contain the words from the largest partition,
  * and determines if the current letter is contained in the new word list.
  * @param choice the letter being searched for
  */
 private void updateCurrentList(char choice) {
  // increment the number of tries
  numberOfAttempts ++;
  // the set of keys
  Set<String> keys = wordPartitions.keySet();
  // the longest partition in the map
  List<String> longestPartition = new ArrayList<String>();
  // the key to the longest partition
  String longestPartitionKey = "";
  // validate each key
  for(String key : keys) {
   // if the current list size is larger than the previous
   if(wordPartitions.get(key).size() > longestPartition.size()) {
    // current list becomes the longest partition
    longestPartition = wordPartitions.get(key);
    // current key becomes the longest partition key
    longestPartitionKey = key;
   }
  }
  // the current list gets the longest partition
  currentList = longestPartition;
  // clear all partitions for next guess
  wordPartitions.clear();
  // if the longest key is composed only of hyphens
  if(longestPartitionKey.equals(justHyphens)) {
   // add letter to the set of wrong guesses
   wrongGuesses.add(choice);
  }
  // if the longest key has letters
  else {
   // add the letter to the current state of the word being guesses
   addLetterToGuessString(choice, longestPartitionKey);
  }
 }
 
 /**
  * Adds a letter to the word being guessed.
  * @param letter the letter to be added
  * @param key the letter placement model
  */
 private void addLetterToGuessString(char letter, String key) {
  // iterate through each index value of the key
  for(int i = 0; i < key.length(); i++) {
   // if the value of the character at the current index position of the key
   // matches the letter to be added
   if(key.charAt(i) == letter) {
 // replace hyphen at the current location with the letter
    guessString.replace(i, i+1, Character.toString(letter));
   }
  }
 }
 
 /**
  * Determines if the games is over.
  * @return true if the game is over, and false otherwise
  */
 private boolean gameOver() {
  // if the word being guessed does not contain any hyphens
  if(guessString.toString().indexOf('-') == -1) {
   return true;
  }
  else {
   return false;
  }
 }
 
 /**
  * Displays the details of the game.
  */
 private void endMessage() {
  JOptionPane.showMessageDialog(null,"Congratulations!!\nThe word is: " + guessString 
    + "\nIncorrect guesses: " + wrongGuesses + "\nNumber of tries: " + numberOfAttempts);
 }
 
 public static void main(String[] args) {
	 Hangman h = null;
	try {
		h = new Hangman(7);
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	 h.play();
 }
 // END
}
