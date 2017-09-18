

/**
 * DLB Dictionary Data Structure
 * Implements DictInterface and 2 of its 3 abstract methods. 
 * CS 1501 Summer 2017  
 * @author Joshua Rodstein
 */
public class DLB implements DictInterface {
    // root node
    DLBnode root;

    // constructor
    // created new rootNode upon instantiation
    public DLB() {
        root = new DLBnode();
    }

    // public inner class 
    // contains data members for DLBnode objects
    public class DLBnode {

        // Char value of the node
        char value;
        // has the node more recently been created or did it already exist
        boolean created;
        // holds reference to child node (one level down)
        DLBnode child;
        // holds reference to sibling (one node to the right)
        DLBnode sibling;

        public DLBnode() {
        }

        // constructor 
        // recieved a char as param
        // node referneces set to null
        // created set to false as default
        public DLBnode(char v) {
            this.value = v;
            this.child = null;
            this.sibling = null;
            this.created = false;
        }

    }

    @Override
    /* Add method of DictInterface Implemented
    *
    * concat end of word char "$" to end of s
    * create reference to the root node of the dictionary. 
    * iterate over the string and pass one char at a time to
    * the addChild method with a reference to the root of the Dict. 
    *
    * @param s string to be added to dictionary
    * @return boolean specifying whether created or already existed
    *
    */
    public boolean add(String s) {
        String str = s.concat("$");
        DLBnode current = root;

        for (int i = 0; i < str.length(); i++) {
            current = addChild(current, str.charAt(i));
        }

        return current.created;

    }

    /**
    * Adds/returns child node with char value c
    *
    * <p>
    * addChild receives a reference to the dict root and a char value 
    * as parameters. If currents child reference is null, it adds a child node
    * and sets value to c. If current has a child, the node and value are
    * passed to the addSibling method. If node created, created data member is
    * set to true.
    * 
    * @param current reference to the root node
    * @param value to be added
    * @return If node created that node is returned. If not, return the found
    * node with the value c. 
    *
    */
    private DLBnode addChild(DLBnode current, char c) {
        if (current.child != null) {
            return addSibling(current.child, c);
        } else {
            current.child = new DLBnode(c);
            current.child.created = true;
            return current.child;
        }
    }

    /**
     * Adds/returns a sibling node with char value c
     * 
     * <p>
     * addSibling is called by addChild and receives the same root reference
     * and char value c. Traverse the sibling nodes with a loop. If the value
     * does not match c, and if the current node has a siblings.. move to that 
     * sibling.
     * 
     * <p>
     * When loop exits, either we have matched the value or we have found a 
     * sibling without a right sibling. If value matched, then created is false
     * and we return the found node. Otherwise we create a node, set created to
     * true and return the created node.
     * 
     * 
     * @param current reference to root node
     * @param c value of node to be added
     * @return If node created that node is returned. If not, return the found
     * node with the value c. 
     */
    private DLBnode addSibling(DLBnode current, char c) {

        while (current.value != c && current.sibling != null) {
            current = current.sibling;
        }

        if (current.value == c) {
            current.created = false;
            return current;
        }

        current.sibling = new DLBnode(c);
        current.sibling.created = true;
        return current.sibling;

    }
    
    /**
     * Finds childNode of current with the specified char value
     * 
     * <p>
     * findChild receives reference to root node and a char value. 
     * If the child of the current node is NOT null and has the value
     * c, then return that child node. Otherwise pass the child of current
     * and the value c to findSibling()
     * 
     * @param current reference to a root DLBnode 
     * @param c value of node to search for
     * @return node with matching value c, null if not found
     */

    private DLBnode findChild(DLBnode current, char c) {
        if (current.child != null && current.child.value == c) {
            return current.child;
        }
        return findSibling(current.child, c);
    }

    /**
     * Finds node with specified char value 
     * 
     * <p>
     * findSibling() receives root reference and a char value as parameters. 
     * traverses siblings while the current node is not null and the value
     * of the current node does not match c. once loop exits, either current 
     * id the matching node or current is null and the node does not exist
     * 
     * @param current reference to a root DLBnode
     * @param c value of node to search for
     * @return node with matching value c, null if not found
     */
    private DLBnode findSibling(DLBnode current, char c) {
        
        // allows search for the end of word character "$"
        // if not found then current is null and easily checked for
        // in searchPrefix() 
        while (current != null && current.value != c) {
            current = current.sibling;
        }

        return current;

    }

    /**
     * Searches the dictionary for the sequence of chars represented by s
     * 
     * <P>
     * Created a reference to root of dictionary. Uses a while loop to pass
     * each character of s to findCHild(). If current is not null after loop 
     * then the characters were found. pass the current node to findChild with 
     * the end of word character "$".
     * 
     * @param s Sequence of characters to check against dictionary
     * @return 0 if not found, 1 if prefix, 2 if word, 3 if prefix & word
     * 
     */
    @Override
    public int searchPrefix(StringBuilder s) {

        DLBnode current = root;
        int i = 0;
        
        while (current != null && i < s.length()){
            current = findChild(current, s.charAt(i));
            i++;
        }

        // if current is not null then chars were found. 
        if (current != null) {
            // find node with end of word char "$"
            current = findChild(current, '$');
            //if current is null then "$" not found, and s is prefix only
            if (current == null) {
                return 1;
            }
            // if current is not null but tis sibling is, then "$" found with no
            // sibling and s is a word only
            if (current.sibling == null) {
                return 2;
            }
            // if current is not null and "$" is found with a sibling, then
            // s is a word AND a prefix
            return 3;
        } else {
            // if after the loop current is null, then s is niether a word nor 
            // a prefix. 
            return 0;
        }

        

    }

     /**
      * 
      * Not supported in this implementation
      * 
      * @param s   unsupported
      * @param start unsupported
      * @param end unsupported
      * @return  unsupported
      */
    @Override
    public int searchPrefix(StringBuilder s, int start, int end) {
        throw new UnsupportedOperationException("Not supported."); //To change body of generated methods, choose Tools | Templates.
    }

}
