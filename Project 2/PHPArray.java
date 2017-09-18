/***********************************************************************
 * 1501 - Prof. Ramirez - Project 2 - PHPArray                          *
 * Author: Joshua Rodstein                                              *
 * Email: jor94@pitt.edu                                                *
 * /////////////////////////////////////////////////////////////////////*
 * Compile: javac PHPArray.java                                         *
 * run: java PHPArray.java                                              *
 *                                                                      *
 * Warnings: Type check warnings for most generic type returns          *
 * when compiling from command line. 
 *                                                                      *    
 * ***************ALL TYPES ARE CONFIRMED SAFE***************************                                   
 *                                                                      *
 ************************************************************************/



import java.util.*;

/**
 * @author joshuarodstein
 * @param <V>
 */
public class PHPArray<V> implements Iterable<V> {
    /**
     * Global Variable:
     * M: current hashTable capacity
     * N: current of elements within table
     * reHash: True if put() args are for rehash
     * hashTable: underlying array structure for hashed Pair Objects
     * Nodes: root, tail : globals for linked list implementation
     *        next, prev : global for rehashing Node from middle of list
     * Iter: Iterator object returned for each() method
     */
    private int M;
    private int N;
    private boolean reHash;
    private Node[] hashTable;
    private Node root;
    private Node tail;
    private Node next;
    private Node prev;
    private Iterator<Pair<V>> iter;

    
    
    //PHPArray oOnstructors
    public PHPArray() {
    }

    // Intantiate new PHPArray with set capacity
    public PHPArray(int size) {

        this.M = size;
        this.reHash = false;
        hashTable = (Node[]) new Node[M];
    }

    // Instantiate new PHPArray with all data members explicitly set
    public PHPArray(int em, int en, Node[] table, Node r, Node t) {
        this.M = em;
        this.N = en;
        this.hashTable = table;
        this.root = r;
        this.tail = t;
        this.reHash = false;
    }

    
    /**
     * Returns iterator for Linked List values
     * @return Iterator<V> object 
     */
    @Override
    public Iterator<V> iterator() {

        Iterator<V> it = new Iterator<V>() {

            Node currentNode = root;

            @Override
            public boolean hasNext() {
                return currentNode != null;
            }

            @Override
            public V next() {
                Node tempNode = currentNode;
                currentNode = currentNode.next;
                return (V)tempNode.KeyVal.value;
            }

        };
        return it;

    }

    /**
     * Returns new Pair<V> object iterator
     * @return Iterator<Pair<V>> Object 
     */
   
    public Iterator<Pair<V>> pairIterator() {

        Iterator<Pair<V>> it = new Iterator<Pair<V>>() {

            Node currentNode = root;

            @Override
            public boolean hasNext() {
                return currentNode != null;
            }

            @Override
            public Pair<V> next() {
                Node tempNode = currentNode;
                currentNode = currentNode.next;
                return tempNode.KeyVal;
            }

        };
        return it;

    }

    /**
     * Uses Pair<V> iterator to iterate over Pair<V> objects and access
     * their Key Value data members
     * @return null if at end of table or table is empty
     */
    public Pair<V> each() {
        if (iter == null) {
            reset();
        }
        if (iter.hasNext()) {
            return (Pair<V>) iter.next();
        }
        return null;
    }

    public void reset() {
        iter = this.pairIterator();
    }

    ////////////////////////////////////////////////////////////////////
    // Holds Key Value pairs for each node. Public static inner class
    // allows it to be nested within PHPArray but it can still be publicly 
    // accessed .  Pair<V> has two public instance variables:  
    // 		key of type String and
    // 		value of type V
    public static class Pair<V> {

        public String key;
        public V value;

        Pair() {
            this(null, null);
        }

        Pair(String k, V v) {
            this.key = k;
            this.value = v;
        }

    }
///////////////////////////////////////////////////////////////////////////////
    
    /**
     * Node contains next & prev node references for doubly linked list
     * implementation. 
     * 
     * Contains Pair<V> object which holds corresponding Key val pair.
     * 
     * @implements Comparable<V> and compareTo() method
     * @param <V> generic type value that extends Comparable
     */
    private static class Node<V extends Comparable<V>> implements Comparable<V> {

        private Node next;
        private Node prev;
        private Pair<V> KeyVal;

        Node() {
            this.next = null;
            this.prev = null;
            this.KeyVal = null;

        }

        Node(Pair<V> kv) {
            this.next = null;
            this.prev = null;
            this.KeyVal = new Pair<V>(kv.key, kv.value);
        }

        @Override
        public int compareTo(V o) {
            V thisVal = this.KeyVal.value;
            V val = o;

            if ((!(thisVal instanceof Comparable) || !(val instanceof Comparable))) {
                throw new IllegalArgumentException("List cannot be sorted");
            }
            return thisVal.compareTo(o);

        }
    }
///////////////////////////////////////////////////////////////////////////////

    /**
     * 
     * @return current hashTable capacity 
     */
    public int length() {
        return this.M;
    }

    /**
     * Accepts String parameter and generates unique hash value with is
     * bitwise anded and % by table size. 
     * 
     * @param key
     * @return unique hash value
     */
    private int hash(String key) {

        return (key.hashCode() & 0x7fffffff) % M;
    }

    /**
     * Prints raw hashTable values from index 0 to length -1
     */
    public void showTable() {
        String k;
        V v;
        System.out.println("\tRaw Hash Table Contents:");
        for (int i = 0; i < hashTable.length; i++) {
            if (hashTable[i] == null) {
                System.out.println(i + ": null");
            } else {
                k = hashTable[i].KeyVal.key;
                v = (V)hashTable[i].KeyVal.value;
                System.out.println(i + ": Key: " + k + " Value:" + v);
            }
        }

    }

    /**
     * creates new Node<V> object containing Pair<V> which contains the
     * passed Key Value pair. hashes object within the hashTable and adds to 
     * internal linked list. If values are for reHash, utilizes global next and 
     * prev references to replace into same spot in list. 
     * 
     * @param key
     * @param val 
     */
    public void put(String key, V val) {
        if (val == null) {
            unset(key);
        }

        // double table size if 50% full
        if (N >= M / 2) {
            resize(2 * M);
        }

        int i;
        for (i = hash(key); hashTable[i] != null; i = (i + 1) % M) {
            if (hashTable[i].KeyVal.key.equals(key)) {
                hashTable[i].KeyVal.value = val;
                return;

            }
        }

        Pair<V> newPair = new Pair<V>(key, val);
        hashTable[i] = new Node(newPair);

        if (reHash) {
            hashTable[i].next = this.next;
            if (hashTable[i].next != null) {
                hashTable[i].next.prev = hashTable[i];
            }
            hashTable[i].prev = this.prev;
            if (hashTable[i].prev != null) {
                hashTable[i].prev.next = hashTable[i];
            }
            reHash = false;
            return;
        }
        if (root == null) {
            root = hashTable[i];
            tail = root;
            return;
        }
        hashTable[i].prev = tail;
        hashTable[i].next = null;
        tail.next = hashTable[i];
        tail = hashTable[i];
        N++;
    }

    /**
     * overloaded put() method accepts integer key and converts to string
     * 
     * @param key of type int
     * @param val of generic type
    */
    public void put(int key, V val) {
        put(String.valueOf(key), val);
    }

    /**
     * returns value of specified key by grabbing hash value and comparing to 
     * corresponding table index
     * 
     * @param key
     * @return V value of specified Key, null if not found
     */
    public V get(String key) {
        for (int i = hash(key); hashTable[i] != null; i = (i + 1) % M) {
            if (hashTable[i].KeyVal.key.equals(key)) {
                return (V)hashTable[i].KeyVal.value;
            }
        }
        return null;
    }

     /**
     * returns key of specified value by grabbing hash value and comparing to 
     * corresponding table index if the key exists
     * 
     * @param key
     * @return V value of specified Key, null if not found
     */
    public V get(int key) {
        Integer strKey = new Integer(key);
        return (get(strKey.toString()));
    }

    /**
     * removes key value pair and node from table and ArrayList if the 
     * Node containing the Key exists. If gap created in cluster, rehashes
     * any following nodes to eliminate hole in table. 
     * 
     * @param key of Node to be removed
     */
    public void unset(String key) {
        if (!contains(key)) {
            return;
        }

        // find position i of key
        int i = hash(key);
        while (!(key.equals(hashTable[i].KeyVal.key))) {
            i = (i + 1) % M;
        }

        // delete key and associated value
        if (root == tail) {
            root = null;
        } else if (root == hashTable[i]) {

            root = root.next;
            root.prev = null;
        } else if (tail == hashTable[i]) {
            System.out.println(tail.KeyVal.key);
            tail = hashTable[i].prev;
        } else {
            hashTable[i].next.prev = hashTable[i].prev;
            hashTable[i].prev.next = hashTable[i].next;
        }
        hashTable[i] = null;
        // rehash all keys in same cluster
        i = (i + 1) % M;
        while (hashTable[i] != null) {
            // delete keys[i] an vals[i] and reinsert
            System.out.println("\tKey " + hashTable[i].KeyVal.key + " rehashed...\n");
            String keyToRehash = hashTable[i].KeyVal.key;
            V valToRehash = (V)hashTable[i].KeyVal.value;
            this.next = hashTable[i].next;
            this.prev = hashTable[i].prev;
            hashTable[i] = null;
            N--;
            reHash = true;  //set gloabl reHash boolean to True to correctly link
            put(keyToRehash, valToRehash);
            i = (i + 1) % M;
        }

        N--;

        // halves size of array if it's 12.5% full or less
        if (N > 0 && N <= M / 8) {
            resize(M / 2);
        }

        assert check();
    }

    /**
     * doubles table size by rehashing all Nodes within temp PHPArray object
     * and copying to current PHPArray object
     * 
     * @param capacity of new PHPArray
     */
    private void resize(int capacity) {
        System.out.println("\t\tSize: "+N+" -- resizing array from "+M+" to "+capacity);
        PHPArray<V> temp = new PHPArray<V>(capacity);
        Node rePair = root;
        // rehash in insertion order by iterating over linked pair objects
        // starting at the root
        while (rePair != null) {
            temp.put(rePair.KeyVal.key, (V)rePair.KeyVal.value);
            rePair = rePair.next;
        }

        this.M = capacity;
        this.N = temp.N;
        this.root = temp.root;
        this.tail = temp.tail;
        this.hashTable = Arrays.copyOf(temp.hashTable, capacity);

    }

    /**
     * Overloaded unset() method accepts int key
     * and converts to Integer object which is converted
     * to a string and passed to unset(String Key)
     * 
     * @param key 
     */
    public void unset(int key) {
        Integer strKey = new Integer(key);
        unset(strKey.toString());
    }

    /**
     * 
     * @param key to search for
     * @return true if key contained within table, false if not
     */
    public boolean contains(String key) {
        return get(key) != null;
    }

    /**
     * confirms load ratio of hashTable
     * 
     * @return true if within spec,false if not 
     */
    private boolean check() {
        // check that hash table is at most 50% full
        if (M < 2 * N) {
            System.err.println("Hash table size M = " + M + "; array size N = " + N);
            return false;
        }

        // check that each key in table can be found by get()
        for (int i = 0; i < M; i++) {
            if (hashTable[i].KeyVal.key == null) {
                continue;
            } else if (get(hashTable[i].KeyVal.key) != hashTable[i].KeyVal.value) {
                System.err.println("get(" + hashTable[i].KeyVal.key + ") = " + get(hashTable[i].KeyVal.key) + "; hashTable[" + i + "].value = " + hashTable[i].KeyVal.value);
                return false;
            }
        }
        return true;
    }

    /**
     * transposes key and value of all Pair<V> objects of all Nodes
     * 
     * @return transposed PHPArray
     */
    public PHPArray<String> array_flip() {
        PHPArray<String> flipArray = new PHPArray<>(M);
        Node flipNode = root;

        while (flipNode != null) {

            flipArray.put((String) flipNode.KeyVal.value,
                    flipNode.KeyVal.key);
            flipNode = flipNode.next;
        }

        return flipArray;
    }

    /**
     * returns array list of all keys of all nodes in insertion order. 
     * 
     * @return keyList ArrayList of keys 
     */
    public ArrayList<String> keys() {
        Node list = root;
        ArrayList<String> keyList = new ArrayList<>();

        while (list != null) {
            keyList.add(list.KeyVal.key);
            list = list.next;
        }

        return keyList;
    }

    /**
     * returns array list of all value of all nodes in insertion order. 
     * 
     * @return valList ArrayList of values 
     */
    public ArrayList<V> values() {
        Node list = root;
        ArrayList<V> valList = new ArrayList<>();

        while (list != null) {
            valList.add((V)list.KeyVal.value);
            list = list.next;
        }

        return valList;
    }

    /**
     * Sorts insertion order linked nodes from smallest to largest. 
     * Utilizes merg_sort() which is worst case O(NlgN). reHashes in insertion
     * order with new keys from 0 -> (length of list) - 1
     * 
     */
    public void sort() {
        try {
            root = merge_sort(root);
        } catch (IllegalArgumentException AR) {
            System.out.println("PHPArray values are not Comparable -- cannot be sorted");
        }
        Node reKey = root;
        PHPArray<V> sortAr = new PHPArray<V>(M);

        for (int i = 0; reKey != null; i++) {
            sortAr.put(String.valueOf(i), (V)reKey.KeyVal.value);
            reKey = reKey.next;
        }

        this.M = sortAr.M;
        this.N = sortAr.N;
        this.root = sortAr.root;
        this.tail = sortAr.tail;
        this.hashTable = Arrays.copyOf(sortAr.hashTable, sortAr.M);
    }

    /**
     * asort() sorts table in insertion order with merge_sort() by iterating over nodes as in
     * sort(). rehashed with ORIGINAL keys
     */
    public void asort() {
        try {
            root = merge_sort(root);
        } catch (IllegalArgumentException AR) {
            System.out.println("PHPArray values are not Comparable -- cannot be sorted");
        }
        root = merge_sort(root);
        Node reKey = root;
        PHPArray<V> sortAr = new PHPArray<>(M);

        for (int i = 0; reKey != null; i++) {
            sortAr.put(reKey.KeyVal.key, (V)reKey.KeyVal.value);
            reKey = reKey.next;
        }

        this.M = sortAr.M;
        this.N = sortAr.N;
        this.root = sortAr.root;
        this.tail = sortAr.tail;
        this.hashTable = Arrays.copyOf(sortAr.hashTable, sortAr.M);

    }

    /**
     * Merge Sort is a stable sort that sorts table via Linked nodes in O(NlgN)
     * 
     * @param n
     * @return 
     */
    private Node merge_sort(Node n) {
        Node nd = n;
        if (nd == null || nd.next == null) {
            return nd;
        }
        Node mid = getMid(nd);
        Node right = mid.next;
        mid.next = null;

        return merge(merge_sort(nd), merge_sort(right));
    }
    
    /**
     * merges/sorts on the way up from bottom of recursion tree
     * 
     * @param l left half of list
     * @param r right half of list
     * @return returns merged portion of list
     */
    private Node merge(Node l, Node r) {
        Node temp, curr;
        temp = new Node<>();
        curr = temp;

        while (l != null && r != null) {
            if (l.compareTo((Comparable)r.KeyVal.value) < 1) {
                curr.next = l;
                l = l.next;
            } else {
                curr.next = r;
                r = r.next;
            }
            curr = curr.next;
        }
        if (l == null) {
            curr.next = r;
        } else {
            curr.next = l;
        }

        return temp.next;
    }

    /**
     * finds middle node of linked list for splitting list 
     * 
     * @param n list to search for mid
     * @return middle node
     */
    private Node getMid(Node n) {
        if (n == null) {
            return n;
        }
        Node big, small;
        small = n;
        big = small;
        while (small.next != null && small.next.next != null) {
            big = big.next;
            small = small.next.next;
        }
        return big;
    }

}
