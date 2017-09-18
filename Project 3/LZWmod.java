
/**
 * ***********************************************************************
 * Author: Joshua Rodstein
 * Project 3: LZW Variable coeword compression
 * jor94@pitt.edu
 * 
 * Compilation: javac LZW.java Execution: java LZW - < input.txt (compress)
 * Execution: java LZW + < input.txt (expand) Dependencies: BinaryIn.java
 * BinaryOut.java
 *
 * Compress or expand binary input from standard input using LZW.
 *
 *
 ************************************************************************
 */

import java.io.*;
public class LZWmod {

    private static final int R = 256;        // number of input chars
    private static final int L = 65536;       // number of codewords = 2^W
    private static final int W = 12;         // codeword width
    private static int W2 = 9;
    private static int L2 = 512;//(int) Math.pow(2, W2);

    public static void compress() {
        //StirngBuilder to build chars and search for prefix
        StringBuilder sbInput;
        // t will hold sing char to be appended
        StringBuilder t = new StringBuilder();
        // TrieST symbol table
        TrieST<Integer> st = new TrieST<Integer>();
        
        // Load symbol table with extended ACII char set
        for (int i = 0; i < R; i++) {
            st.put(new StringBuilder().append((char) i), i);
        }
        
        int code = R + 1;  // R is codeword for EOF

        // tryByte returns next char/byte as a StringBuilder
        sbInput = tryByte();
        
        // if sbInput is null, file is empty
        while (sbInput != null) {
            // search for longest prefix of current form of sbinput
            StringBuilder s = st.longestPrefixOf(sbInput);

            // if s.length == snInput.length then we matched an entire
            // prefix and need to search for the first non-math
            while (s.length() == sbInput.length()) {
                t = tryByte();
                sbInput.append(t);
                s = st.longestPrefixOf(sbInput);
            }
            
            // here t will either be null or a character that did not match
            // we then write to file
            BinaryStdOut.write(st.get(s), W2);  // Print s's encoding.

            // if EOF not reached and we have not reached max codewords for 
            // width then add to symbol table and imcrement code
            // if code == max codewords for width then increase width and
            // max codewords
            if (t != null && code < L2) {
                st.put(sbInput, code++);
                if (code == L2) {
                    if (W2 < 16) {
                        L2 = (int) Math.pow(2, ++W2);
                    }
                }
            }

            // if t is null, then EOF reached and loop will end. 
            sbInput = t;
        }

        BinaryStdOut.write(R, W2);
        BinaryStdOut.close();
    }

    public static void expand() {
        String[] st = new String[L];
        int i; // next available codeword value

        // initialize symbol table with all 1-character strings
        for (i = 0; i < R; i++) {
            st[i] = "" + (char) i;
        }
        st[i++] = "";                        // (unused) lookahead for EOF

        int codeword = BinaryStdIn.readInt(W2);
        String val = st[codeword];

        while (true) {

            BinaryStdOut.write(val);
            codeword = BinaryStdIn.readInt(W2);
            if (codeword == R) {
                break;
            }
            String s = st[codeword];
            if (i == codeword) {
                s = val + val.charAt(0);   // special case hack
            }

            // i + 1 to sync with compress and add to dict array
            if (i + 1 < L) {
                st[i++] = val + s.charAt(0);
            }

            // if after i increment we appreach max codewords
            // and we have not reached max width, then increase
            // both
            if (i + 1 == L2 && W2 < 16) {
                L2 = (int) Math.pow(2, ++W2);
            }

            // if i == L then we will cease to add codewords and will print 
            // reamining 16 bit chunks until end of compressed file is reached. 
            if (i == L) {
                BinaryStdOut.write(val);
                codeword = BinaryStdIn.readInt(W2);
                while (codeword != R) {
                    BinaryStdOut.write(st[codeword]);
                    codeword = BinaryStdIn.readInt(W2);
                }
                break;
            }

            val = s;
        }

        BinaryStdOut.close();
    }

    //tryByte method wraps BinaryStdIn call to
    // reachChar in tryCatch block and returns
    //  next char as a stirng builder.
    // if EOF reached, then return null
    private static StringBuilder tryByte() {
        char ci;
        StringBuilder sbi = new StringBuilder();

        try {
            ci = BinaryStdIn.readChar();
            sbi.append((char) ci);
        } catch (Exception e) {
            sbi = null;
        }

        return sbi;

    }

    public static void main(String[] args) {
        if (args[0].equals("-")) {
            compress();
        } else if (args[0].equals("+")) {
            expand();
        } else {
            throw new RuntimeException("Illegal command line argument");
        }

        System.exit(0);
    }

}
