
/*******************************************************************************
* CS 1501 Summer 2017                                                          * 
*                                                                              * 
* Author: Joshua Rodstein                                                      *
* Email: jor94@pitt.edu                                                        * 
* Description: Implementation of an anagram finding Algorithm.                 *  
* Execution from command line w/ specified input and output files              * 
* as well as * specified dictionary data structure.                            * 
* CLI format: "java Anagram <inputFile> <outputFile> <dictionaryDataStructure>"* 
******************************************************************************
 */

import java.io.*;
import java.util.*;

public class Anagram {

    // global declarations
    // Multiway Trie Data Structure to search Words/Prefixes.
    // DictInterface: provided for DLB and MyDictionary implementation.
 
    public static TrieST<Integer> myTrie;
    public static DictInterface D;

    public static void main(String[] args) throws IOException {
        // Scanner objects to store dictionary file and input file 
        // from command line args. 
        // PrintWriter object for writing solutions output to specified file
        // File object for reading output file from command line args 

        Scanner fileScan = null;
        Scanner input = null;
        PrintWriter printWriter = null;
        File file = new File(args[1]);

        // input verification try and catch blocks. 
        try {
            fileScan = new Scanner(new FileInputStream("dictionary.txt"));
        } catch (FileNotFoundException fnf) {
            System.err.println("Error: Specified dictionary file not found or "
                    + "not specified");
        }

        try {
            input = new Scanner(new FileInputStream(args[0]));
        } catch (FileNotFoundException fnf) {
            System.err.println("Error: Input file not found");
            System.exit(0);
        }

        // point to specified output file with printWriter obj
        // Will attempt to create the file and directory if none found
        // if file path exists, will be overwritten. 
        // If path or file do not exist, will print err and exit. 
        try {
            printWriter = new PrintWriter(file);
            file.getParentFile().mkdirs();
        } catch (NullPointerException np) {
            String outFile = "./";
            args[1] = outFile.concat(args[1]);
            file = new File(args[1]);
            file.getParentFile().mkdirs();
        } catch (FileNotFoundException fnf) {
            System.err.println("Error: Invalid output file path");
            System.exit(0);
        }

        // verfies that Dictionary data structure has been specified. 
        // If none specified, will print err
        try {
            if (args[2].equalsIgnoreCase("orig")) {
                D = new MyDictionary();
            } else if (args[2].equalsIgnoreCase("dlb")) {
                D = new DLB();
            } else {
                System.err.println("Error: Invalid Dictionary Data Structure "
                        + "Specified");
                System.exit(0);
            }
        } catch (ArrayIndexOutOfBoundsException ai) {
            System.err.println("Error: No Dictionary Data Structure Specified");
            System.exit(0);
        }
        
        ArrayList<String> collectList;
        String str, strP;
        StringBuilder sb;

        // build dictionary from specified file with 'D'  obj of corresponding 
        // Data Strucuture type
        while (fileScan.hasNext()) {
            str = fileScan.nextLine();
            D.add(str);
        }

        // while loop to iterate over input file one word at a time
        while (input.hasNext()) {
            // clear anagram collection list before a new word is started
            collectList = new ArrayList<>();
            
            char c;
            char[] chars = input.nextLine().toCharArray();
            strP = new String(chars);
            Arrays.sort(chars);
            str = new String(chars);

            myTrie = new TrieST<>();

            // 1st of three iterations of a recursive loop.  Iterates over input 
            // string to find anagrams starting from every char. this Is the
            // entry point to the recursive methods of my algorithm
            for (int i = 0; i < str.length(); i++) {
                // start with a blank prefix
                StringBuilder prefix = new StringBuilder();

                // remove white spaces from the input string by
                // converting to string first. place back
                // in a string builder object after trimmed
                sb = new StringBuilder(str);
                str = trimWhiteSpace(sb);
                sb = new StringBuilder(str);
                
                // c contains chracter representing first letter of possible
                // anagrams. Append to (empty at this point) prefix. 
                c = str.charAt(i);
                prefix.append(c);

                // pass prefix to searchPrefix method of Dictionary structure
                // and assign result to int variable 'ans'
                int ans = D.searchPrefix(prefix);

                // Switch statement controls entry into 2 recursive methods.
                // 
                switch (ans) {
                    case 0:
                        // not found: 
                        //  break switch and try next char as first letter
                        break;
                    case 1: 
                        // a prefix:
                        //  add prefix to Trie, enter into triBuilder() to 
                        //  attempt permutations. 
                        //  pass prefix and modified suffix as arguments
                        myTrie.put(prefix.toString(), 0);
                        trieBuilder(prefix, sb.deleteCharAt(i));
                        break;
                    case 2:
                        // a word:
                        //  if prefix represents a word but not a prefix
                        //  then if the string is only 1 char long, add
                        //  word to Trie and break;
                        //  else add as non word and pass to the multiWord 
                        //  method with white space appended
                        if (str.length() == 1) {
                            myTrie.put(prefix.toString(), 1);
                        } else {
                            myTrie.put(prefix.toString(), 0);
                            multiWordTrieBuilder(prefix.append(" "),
                                    new StringBuilder(), sb.deleteCharAt(i));
                        }
                        break;
                    case 3:
                        // a word and prefix:
                        //  once again, if the string is one char long, add &
                        //  break. 
                        //  If chars left then attempt one word solution with
                        //  trieBuilder, followed by multi-work solution with 
                        //  MultiWordTriebuilder
                        if (str.length() == 1) {
                            myTrie.put(prefix.toString(), 1);
                        } else {
                            myTrie.put(prefix.toString(), 0);
                            trieBuilder(prefix, sb.deleteCharAt(i));
                            multiWordTrieBuilder(prefix.append(" "),
                                    new StringBuilder(), sb);
                        }
                        break;

                    default:
                        break;
                }
            }

            
            /* key Collection Sort and Print */
            
            // iterate over Trie to collect and store all full anagrams 
            // into an ArrayLIst
            for (String key : myTrie.keys()) {
                if (myTrie.get(key) == 1) {
                    collectList.add(key);
                }
            }

            // custom function to remove all duplicate words from the List
            removeDuplicates(collectList);
            
            // Suppressed unchecked typeCast exception. All code is typsafe 
            // Initialize new Array of ArrayLists to store sorted anagrams
            @SuppressWarnings("unchecked")
            ArrayList<String>[] sortList = (ArrayList<String>[]) new ArrayList[collectList.size()];

            // initialize the Array of Lists
            for (int i = 0; i < sortList.length; i++) {
                sortList[i] = new ArrayList<String>();
            }

            // Print/ Fileout structure for sorted solutions
            printWriter.println("Here are the results for " + strP + ":");

            // Iterate over the list of collected string int he outer loop
            // innrer loop checks and counts white spaces, sorting/adding words
            // to lists at corresponding Array index positions
            for (String key : collectList) {
                boolean h = true;
                int y = 0;
                for (int i = 0; i < key.length(); i++) {
                    if (key.charAt(i) == ' ') {
                        y++;
                        // remove any words with adjacent white spaces.
                        // this is a work around for a bug I could not fix. 
                        // Seems to find a few extra incorrect words for a couple
                        // strings. 
                        if (i < key.length() - 2 && key.charAt(i + 1) == ' ') {
                            h = false;
                        }
                    }

                }
                if (h == true) {
                    sortList[y].add(key);
                }
            }

            // Print sorted solutions to file w/ PrintWriter
            for (int i = 0; i < sortList.length; i++) {
                if (!sortList[i].isEmpty()) {
                    printWriter.println((i + 1) + " word solutions:");

                    for (String key : sortList[i]) {
                        printWriter.println(key);
                    }

                }

            }
            printWriter.println();
        }
        printWriter.close();
        System.exit(0);
    }
    
    /*
     * Iterates over all characters in string and attempts all permutations
     * of one word solutions. 
     *
     * <p>
     * trieBuilder is a recursive method that accepts a prefix and a suffix. 
     * For loop iterates over remaining characters in the suffix, and each time
     * a recursive call is made the newly appended prefix and newly amended 
     * suffix are passed as parameters. trieBuilder also calls its multiWord
     * version. in case 2 and 3 fo the switch. This structure allows for 
     * recursive backtracking. 
     * 
     * @param prefix current solution build
     * @param suffix remaining character to iterate over
     * @return 0 if backtrack and iterates over all chars in suffix
    */
    public static int trieBuilder(StringBuilder prefix, StringBuilder suffix) {
        StringBuilder p, s;

        for (int i = 0; i < suffix.length(); i++) {
            p = new StringBuilder(prefix);
            s = new StringBuilder(suffix);

            p.append(s.charAt(i));
            int ans = D.searchPrefix(p);

            switch (ans) {
                case 0:
                    //not found
                    break;
                case 1:
                    //a prefix
                    //  make recursive call to trieBuilder to continue building 
                    // one word solution. 
                    myTrie.put(p.toString(), 0);
                    trieBuilder(p, s.deleteCharAt(i));
                    break;
                case 2:
                    //a word
                    //  if s is 1 char long, then we are out of letters and 
                    // solution is full. Add as a word to the Trie and backtrack
                    if (s.length() == 1) {
                        myTrie.put(p.toString(), 1);
                    } else {
                        // id s > 1, then there are chars left and we must
                        // make another recursive call. Append a white space to
                        // p and pass as builder to the multi word method. 
                        myTrie.put(p.toString(), 0);
                        multiWordTrieBuilder(p.append(" "), new StringBuilder(),
                                s.deleteCharAt(i));
                    }
                    break;
                case 3:
                    //a word and prefix
                    //  If chars left then attempt one word solution with
                    //  trieBuilder
                    //  Since also a prefix, append a space to end of prefix and 
                    //  pass ass BUILD. 
                    if (s.length() == 1) {
                        myTrie.put(p.toString(), 1);
                    } else {
                        myTrie.put(p.toString(), 0);
                        trieBuilder(p, s.deleteCharAt(i));
                        // since s.deleteChar at called as arg in prev method 
                        // call,,, pass only s to multiWOrdTrieBuilder
                        multiWordTrieBuilder(p.append(" "), new StringBuilder(),
                                s);
                    }
                    break;
                default:
                    break;
            }

        }

        return 0;
    }

    
    /*
     * Iterates over all characters in string and attempts all permutations
     * of multi-word solutions. 
     *
     * <p>
     * multiWordTrieBuilder is a recursive method that accepts a build,prefix 
     * and a suffix.
     *
     * <p>
     * For loop iterates over remaining characters in the suffix, and each time
     * a recursive call is made the newly appended prefix is added to the build. 
     * The build, the prefix, and remaining suffix are passed in recursion. 
     *
     * @param build  current partial solution build
     * @param prefix current word permutation.
     * @param suffix remaining characters to iterate over
     * @return 0 if backtrack and iterates over all chars in suffix
    */
    public static int multiWordTrieBuilder(StringBuilder build,
            StringBuilder prefix, StringBuilder suffix) {
        StringBuilder b, p, s;

        for (int i = 0; i < suffix.length(); i++) {
            p = new StringBuilder(prefix);
            s = new StringBuilder(suffix);
            b = new StringBuilder(build);

            p.append(s.charAt(i));
            int ans = D.searchPrefix(p);

            switch (ans) {
                case 0:
                    //not found
                    break;
                case 1:
                    //a prefix  
                    myTrie.put(p.toString(), 0);
                    multiWordTrieBuilder(b, p, s.deleteCharAt(i));
                    break;
                case 2:
                    //a word
                    if (s.length() == 1) {
                        myTrie.put(b.append(p).toString(), 1);
                    } else {
                        myTrie.put(p.toString(), 0);
                        
                        // if prefix is a word and NOT a prefix, append a space.
                        // and pass with a new empty string builder to start
                        // the next word in the solution. pass amended suffix
                        multiWordTrieBuilder(b.append(" "), new StringBuilder(),
                                s.deleteCharAt(i));
                    }
                    break;
                case 3:
                    //a word and a prefix
                    if (s.length() == 1) {
                        myTrie.put(b.append(p).toString(), 1);
                    } else {
                        myTrie.put(p.toString(), 0);
                        // if a word and a prefix, we must make two recursive 
                        // calls in succesion. First call is treated like a one
                        // word solution with no spaces amended and the current
                        // prefix being passed. 2nd call appends a white space 
                        // to the solution build and a blank prefix to start a 
                        // new word. 
                        multiWordTrieBuilder(b, p, s.deleteCharAt(i));
                        multiWordTrieBuilder(b.append(p).append(" "),
                                new StringBuilder(), s);
                    }
                    break;
                default:
                    break;
            }

        }

        return 0;
    }

    /*
    * removes duplicate solutions from ArrayList of String
    * 
    * @param l ArrayList of string to be stripped duplicates
    * @return void
    */
    private static void removeDuplicates(ArrayList<String> l) {
        String sAry[] = l.toArray(new String[l.size()]);

        for (int i = 0; i < l.size(); i++) {
            for (int y = 0; y < l.size(); y++) {
                String s1 = l.get(i);
                String s2 = l.get(y);
                if (i == y) {
                    continue;
                } else if (s1.equals(s2)) {
                    l.remove(s2);
                }
            }
        }

    }

    
    /*
    * Removes whitespace characters from StringBuilder
    *
    * @param sb Sequence of characters to be stripped of white space
    * @return sb.toString() string representation of sb
    */
    public static String trimWhiteSpace(StringBuilder sb) {
        while (sb.length() > 0 && Character.isWhitespace(sb.charAt(0))) {
            sb.deleteCharAt(0);
        }
        while (sb.length() > 0 && Character.isWhitespace(sb.charAt(sb.length()
                - 1))) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }
}
