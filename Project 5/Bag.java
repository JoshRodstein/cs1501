
/**
 * ***********************************************************************
 * Compilation: javac Bag.java Execution: java Bag
 *
 * A generic bag or multiset, implemented using a linked list.
 *
 ************************************************************************
 */
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * The <tt>Bag</tt> class represents a bag (or multiset) of generic items. It
 * supports insertion and iterating over the items in arbitrary order.
 * <p>
 * The <em>add</em>, <em>isEmpty</em>, and <em>size</em> operation take constant
 * time. Iteration takes time proportional to the number of items.
 * <p>
 * For additional documentation, see
 * <a href="http://algs4.cs.princeton.edu/13stacks">Section 1.3</a> of
 * <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 */
public class Bag<Item> implements Iterable<Item> {

    private int N;         // number of elements in bag
    private Node first;    // beginning of bag

    // helper linked list class
    private class Node {

        private Item item;
        private Node next;
        private Node prev;

        public Item getItem() {
            return this.item;
        }
    }

    /**
     * Create an empty stack.
     */
    public Bag() {
        first = null;
        N = 0;
    }

    public Node getFirst() {
        return this.first;
    }

    public Bag(Bag<Item> b) {
        if (b == null) {
            throw new IllegalArgumentException("Bag is empty sucka!");
        }
        Node bCurrent = b.getFirst();
        while (bCurrent != null) {
            this.add(bCurrent.getItem());
            bCurrent = bCurrent.next;
        }
    }

    /**
     * Is the BAG empty?
     */
    public boolean isEmpty() {
        return first == null;
    }

    /**
     * Return the number of items in the bag.
     */
    public int size() {
        return N;
    }

    /**
     * Add the item to the bag.
     */
    public void add(Item item) {
        Node oldfirst = first;
        first = new Node();
        first.item = item;
        first.next = oldfirst;
        N++;
    }

    /* Remove method written by: Joshua Rodstein
    *----------------------------------------------
    * traverse singly linked list to find and remove
    * Item from bag. Utilized temporary Node "prev" to
    * provide forward skip linking of nodes. Decrements
    * N upon successful removal of Node.
     */

    public boolean remove(Item item) {
        Node nextNode = first;
        Node prev = null;

        while (nextNode != null && nextNode.item != item) {
            prev = nextNode;
            nextNode = nextNode.next;
        }

        if (nextNode.item == item) {
            if (nextNode == first) {
                if (nextNode.next != null) {
                    first = nextNode.next;
                } else {
                    first = null;
                }
            } else if (nextNode.next == null) {
                prev.next = null;
            } else {
                prev.next = nextNode.next;
            }
            N--;
            return true;
        }

        return false;

    }

    /**
     * Return an iterator that iterates over the items in the bag.
     */
    public Iterator<Item> iterator() {
        return new LIFOIterator();
    }

    // an iterator, doesn't implement remove() since it's optional
    private class LIFOIterator implements Iterator<Item> {

        private Node current = first;

        public boolean hasNext() {
            return current != null;
        }

        public void remove() {

        }

        public Item next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            Item item = current.item;
            current = current.next;
            return item;
        }
    }

    /**
     * A test client.
     */
    public static void main(String[] args) {

        /**
         * *********************************************
         * A bag of strings *********************************************
         */
        Bag<String> bag = new Bag<String>();
        bag.add("Vertigo");
        bag.add("Just Lose It");
        bag.add("Pieces of Me");
        bag.add("Drop It Like It's Hot");
        for (String s : bag) {
            System.out.println(s);
        }
    }
}
