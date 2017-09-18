import java.io.*;
import java.util.*;

/**
 * 
 * 
 */
public class TrieST<Value> {
    private static final int R = 256;      //extended ASCII
    
    private Node root;
    
    private static class Node {
        private Object val;
        private Node[] next = new Node[R];
    }
    
    /***************************************
     * Is the Key in the symbol table?
     **************************************/
    
    public boolean contains (String key){
        return get(key) != null;
    }
    
    public Value get(String key){
        Node x = get(root, key, 0);
        if(x == null) return null;
        return (Value) x.val;
    }
    
    private Node get(Node x, String key, int d) {
        if (x == null) return null;
        if (d == key.length()) return x;
        char c = key.charAt(d);
        return get(x.next[c], key, d+1);
    }
    
    /**********************************************
     * Insert key-value pair into the symbol table
     **********************************************/
    public void put(String key, Value val){
        root = put(root, key, val, 0);
    }
    
    private Node put(Node x, String key, Value val, int d){
        if (x == null) 
            x = new Node();
        if (d == key.length()) {
            x.val = val;
            return x;
        }
        char c = key.charAt(d);
        x.next[c] = put(x.next[c], key, val, d+1);
        return x;
    }
    
    //find the key that is longest prefix of s
    public String longestPrefixOf(String query) {
        int length = longestPrefixOf(root, query, 0, 0);
        return query.substring(0, length);
    }
    
    // find the key in the subtrie rooted at x that is the longest 
    // prefix of the quesry string, starting at the dth character
    private int longestPrefixOf(Node x, String query, int d, int length) {
        if (x == null) return length;
        if (x.val != null) length = d;
        if (d == query.length()) return length;
        char c = query.charAt(d);
        return longestPrefixOf(x.next[c], query, d+1, length);
    }
    
    public Iterable<String> keys() {
        return keysWithPrefix("");
    }
    
    public Iterable<String> keysWithPrefix(String prefix) {
        PriorityQueue<String> queue = new PriorityQueue<String>();
        Node x = get(root, prefix, 0);
        collect(x, prefix, queue);
        return queue;
    }
    
    private void collect(Node x, String key, Queue<String> queue) {
        if (x == null) return;
        if (x.val != null) queue.add(key);
        for (int c = 0; c < R; c++)
            collect(x.next[c], key + (char) c, queue);
    }
    
    public Iterable<String> keysThatMatch(String pat) {
        PriorityQueue<String> q = new PriorityQueue<String>();
        collect(root, "", pat, q);
        return q;
    }
    
    public void collect(Node x, String prefix, String pat, Queue<String> q){
        if (x == null) return;
        if (prefix.length() == pat.length() && x.val != null) q.add(prefix);
        if (prefix.length() == pat.length()) return;
        char next = pat.charAt(prefix.length());
        for (int c = 0; c < R; c++)
            if (next == '.' || next == c)
                collect(x.next[c], prefix + (char) c, pat, q);
    }
    
    public void delete(String key) {
        root = delete(root, key, 0);
    }

    private Node delete(Node x, String key, int d){
        if (x == null) return null;
        if (d == key.length()) x.val = null;
        else {
            char c = key.charAt(d);
            x.next[c] = delete(x.next[c], key, d+1);
        }
        if (x.val != null) return x;
        for (int c = 0; c < R; c++)
            if(x.next[c] != null)
                return x;
        return null;
    }
    
    // test client
    public static void main(String[] args) throws FileNotFoundException {
        
        //build symbol table form standard input
        TrieST<Integer> st = new TrieST<Integer>();
        Scanner fileScan = new Scanner(new FileInputStream("test1.txt"));
        for (int i = 0; fileScan.hasNext(); i++) {
            String key = fileScan.nextLine();
            st.put(key, i);
        }
        
        //print results
        for (String key : st.keys()) {
            System.out.println(key + " " + st.get(key));
        }
    }
    
    
} // end
    

