
/**
 * ***********************************************************************
 * Compilation: javac EdgeWeightedGraph.java Execution: java EdgeWeightedGraph V
 * E Dependencies: Bag.java Edge.java
 *
 * An edge-weighted undirected graph, implemented using adjacency lists.
 * Parallel edges and self-loops are permitted.
 *
 * Author: Sedgewick-
 * Modified By: Joshua Rodstein / jor94@pitt.edu
 * 1501 project 5
 * PS#: 4021607
 *
 ************************************************************************
 */
/**
 * The <tt>EdgeWeightedGraph</tt> class represents an undirected graph of
 * vertices named 0 through V-1, where each edge has a real-valued weight. It
 * supports the following operations: add an edge to the graph, in the graph,
 * iterate over all of the neighbors incident to a vertex.
 */

import java.util.*;

public class Graph {

    private final int V;
    private int E;
    public static Bag<Edge>[] adj;
    public static Bag<Edge>[] delAdj;
    public static int[] active;
    private static int[] removeArray;

    /**
     * Create an empty edge-weighted graph with V vertices.
     */
    public Graph(int V) {
        if (V < 0) {
            throw new RuntimeException("Number of vertices must be nonnegative");
        }
        this.V = V;
        this.E = 0;
        adj = (Bag<Edge>[]) new Bag[V];   // Initialize adjacency list for active nodes + corrsponding edged
        delAdj = (Bag<Edge>[]) new Bag[V]; // initialize adjacency list for disabled nodes and corresponding edges
        active = new int[V];              // int array for ease of checking active/inactive nodes
        for (int v = 0; v < V; v++) {
            adj[v] = new Bag<Edge>();
            delAdj[v] = new Bag<Edge>();
        }
    }

    /**
     * Create a random edge-weighted graph with V vertices and E edges. The
     * expected running time is proportional to V + E.
     */
    public Graph(int V, int E) {
        this(V);
        if (E < 0) {
            throw new RuntimeException("Number of edges must be nonnegative");
        }
        for (int i = 0; i < E; i++) {
            int v = (int) (Math.random() * V);
            int w = (int) (Math.random() * V);
            double weight = Math.round(100 * Math.random()) / 100.0;
            Edge e = new Edge(v, w, weight);
            addEdge(e);
        }
    }

    /**
     * Create a weighted graph from input stream.
     */
    public Graph(In in) {
        this(in.readInt());
        int E = in.readInt();
        for (int i = 0; i < E; i++) {
            int v = in.readInt();
            int w = in.readInt();
            double weight = in.readDouble();
            Edge e = new Edge(v, w, weight);
            addEdge(e);
        }
    }

    /**
     * Return the number of vertices in this graph.
     */
    public int V() {
        return V;
    }

    /**
     * Return the number of edges in this graph.
     */
    public int E() {
        return E;
    }
    /* Remove edge helper method
    *  updates E count.*/
    public boolean removeEdge(Edge e){
        int v = e.either();
        int w = e.other(v);
        boolean i, j;
        i = adj[v].remove(e);
        j = adj[w].remove(e);
        if(i && j) {
            E--;
            return true;
        } else{
            if(j){
                adj[w].add(e);
                return false;
            } else if(i){
                adj[v].add(e);
                return false;
            }
            else{
                return false;
            }
        }
    }

    /**
     * Add the edge e to this graph.
     */
    public void addEdge(Edge e) {
        int v = e.either();
        int w = e.other(v);
        adj[v].add(e);
        adj[w].add(e);
        E++;
    }

    /**
     * Return the edges incident to vertex v as an Iterable. To iterate over the
     * edges incident to vertex v, use foreach notation:
     * <tt>for (Edge e : graph.adj(v))</tt>.
     */
    public Iterable<Edge> adj(int v) {
        return adj[v];
    }

    /**
     * Return all edges in this graph as an Iterable. To iterate over the edges,
     * use foreach notation:
     * <tt>for (Edge e : graph.edges())</tt>.
     */
    public Iterable<Edge> edges() {
        Bag<Edge> list = new Bag<Edge>();
        for (int v = 0; v < V; v++) {
            int selfLoops = 0;
            for (Edge e : adj(v)) {
                if (e.other(v) > v) {
                    list.add(e);
                } // only add one copy of each self loop
                else if (e.other(v) == v) {
                    if (selfLoops % 2 == 0) {
                        list.add(e);
                    }
                    selfLoops++;
                }
            }
        }
        return list;
    }
    /*
    * modWeight helper method handles removing node if weight set to 0, adding node
    * if does not currently exist, and changing weight if node exists and
    * weight is > 0
    */
    private void modWeight(int arg1, int arg2, int arg3) {
        int i = arg1;
        int j = arg2;
        int w = arg3;
        boolean found = false;

        // modify weight of first edge occurance in graph
        for (Edge e : adj[i]) {
            if (e.other(i) == j) {
                found = true;
                if (w <= 0) {
                    if(adj[i].remove(e) == true){
                        E--;
                    }
                } else {
                    e.changeWeight(w);
                }
                break;
            }
        }
        // modify weight of resiprocal edge occurences in graph
        for (Edge e : adj[j]) {
            if (e.either() == i) {
                found = true;
                if (w <= 0) {
                    if(adj[j].remove(e) == true){
                        E--;
                    }
                } else {
                    e.changeWeight(w);
                }
                break;
            }
        }

        // if NOT found, add edge wiht passed attributes
        if (found == false) {
            Edge e = new Edge(i, j, w);
            addEdge(e);
        }

    }
    /**
     * Return a string representation of this graph.
     */
    public String toString() {
        String NEWLINE = System.getProperty("line.separator");
        StringBuilder s = new StringBuilder();
        s.append(V + " " + E + NEWLINE);
        for (int v = 0; v < V; v++) {
            if(adj[v].isEmpty()){
                if(!delAdj[v].isEmpty())
                    continue;
            }
            s.append(v + ": ");
            for (Edge e : adj[v]) {
                s.append(e + "  ");
            }
            s.append(NEWLINE);
        }
        return s.toString();
    }

    public static void main(String[] args) {

        In in = new In(args[0]);
        Graph G = new Graph(in);
        Scanner scan = new Scanner(System.in);

        StdOut.println("\nINPUT FILE " + args[0] + ":");
        StdOut.println("----------------------------");

        while (true) {

            System.out.print("\nEnter Command: ");
            // parse user input
            String[] cmd = scan.nextLine().split("\\s"); // split at white spaces
            int[] cArgs = new int[cmd.length-1];
            for (int i = 0; i < cArgs.length; i++) {
                //StdOut.println(cmd[i]);
                cArgs[i] = Integer.parseInt(cmd[i+1]);
            }
            System.out.println();
            System.out.print("Command");
            for(int i = 0; i < cmd.length; i++){
                StdOut.print(" " + cmd[i]);
            }
            System.out.println(":\n-------------");

            switch (cmd[0]) {
                case "R":   // Report Status

                    CC connectG = new CC(G);

                    if(connectG.count() == 1) {
                        StdOut.println("The network is currently connected\n");
                    }
                    else{
                        StdOut.println("The network is currently disconnected\n");
                    }

                    StdOut.println("The following nodes are currently up: ");
                    for(int i = 0; i < adj.length; i++){
                        if(active[i] == 0){
                            StdOut.print(i + " ");
                        }
                    }
                    StdOut.println("\n\nThe following nodes are currently down: ");
                    for(int i = 0; i < adj.length; i++){
                        if(active[i] == 1){
                            StdOut.print(i + " ");
                        }
                    }

                    // Print connected components by acquiring id's and occurnces of id's of
                    // connected components
                    int[] con = new int[adj.length];
                    for(int i = 0; i < con.length; i++){
                        con[connectG.id(i)]++;
                    }
                    for(int i = 0; i < adj.length; i++){

                    }

                    System.out.println();

                    // print connected components by searching array for
                    // id occurances > 1 (which signifies connected component)
                    // and pringint the edges in the component.
                    StdOut.println("\nThe connected components are:");
                    int count = 0;
                    for(int v = 0; v < con.length; v++) {
                        if(con[v] > 1){
                            count++;
                            StdOut.println("Component " + (count-1) + ":");
                            for(int i = 0; i < adj.length; i++){
                                if(connectG.id(i) == v){
                                    StdOut.print(i + ": ");
                                 for(Edge e : adj[i]){
                                     StdOut.print(e.toString() + " ");
                                 }
                                 StdOut.println();
                                }
                            }
                        }
                    }
                    break;
                case "M": // Print MST of of graph including all connected sub graphs.
                    // utilized author provided PrimMST algorithim with slight modification
                    System.out.println("The edges in the MST follow:");
                    PrimMST MST = new PrimMST(G);
                    for (Edge e : MST.edges()) {
                        StdOut.println(e);
                    }
                    break;
                case "S": // find and print shortest path with Modified Dijkstra's algorithm
                    if(cmd.length < 2){
                        StdOut.println("Invalid Command!\n\n");
                        break;
                    }
                    int s = cArgs[0];
                    int j = cArgs[1];
                    DijkstraSP sp = new DijkstraSP(G, s, j);
                    // print shortest path
                    StdOut.print("Shortest paths from " + s);
                   // for (int v = 0; v < G.V(); v++) {
                        if (sp.hasPathTo(cArgs[1])) {
                            StdOut.printf("%d to %d (%.2f)  ", s, cArgs[1], sp.distTo(cArgs[1]));
                            for (Edge e : sp.pathTo(cArgs[1])) {
                                StdOut.print(e + "   ");
                            }
                            StdOut.println();
                        }
                        else {
                            StdOut.printf("%d to %d         no path\n", s, cArgs[1]);
                        }

                    break;
                case "P":
                    StdOut.println("\nUnsupported Operation: \n\t Due to poor life choices on " +
                            "behalf of the author, this operation is not yet functional. Please direct any questions " +
                            "or concerns to the author at:\n\n jor94@pitt.edu / 2098145290");
                    break;
                case "D": // take down node: removes node and palces in delAdj as inactive. updated active[]
                    if(cmd.length < 2){
                        StdOut.println("Invalid Command!\n\n");
                        break;
                    }
                    int v, w;
                    if(active[cArgs[0]] == 0) {
                        delAdj[cArgs[0]] = new Bag<Edge>(adj[cArgs[0]]);
                        for(Edge e : adj[cArgs[0]]){
                                G.removeEdge(e);
                        }
                        active[cArgs[0]] = 1;

                        StdOut.println("Vertex " + cArgs[0] + " is down");
                    }

                    break;
                case "U": // 90% functional case to bring up an inactive node.
                    // when taking down node(s) results in a disconnected, active node.. nodes
                    // must be re-uped in reverse of the order they were taken down.
                    if(cmd.length < 2){
                        System.err.println("Invalid Command!\n\n");
                        break;
                    }
                    if(active[cArgs[0]] == 1) {
                        adj[cArgs[0]] = new Bag<Edge>();
                        for (Edge e : delAdj[cArgs[0]]) {
                            if(active[e.other(e.either())] == 1 || active[e.either()] == 1) {
                                //adj[cArgs[0]] = new Bag<Edge>(delAdj[cArgs[0]]);
                                G.addEdge(e);
                            }
                        }
                        active[cArgs[0]] = 0;
                        StdOut.println("Vertex " + cArgs[0] + " is up");
                    }

                    break;
                case "C": // change weight of an existing edge. If no edge exists, create. If weight is 0, delete edge.
                    if(cmd.length < 2){
                        StdOut.println("Invalid Command!\n\n");
                        break;
                    }
                    System.out.println("Weight of edge " + cArgs[0] + "->" + cArgs[1] + "changed to "
                            + cArgs[2]);
                    if (cArgs.length == 3) {
                        G.modWeight(cArgs[0], cArgs[1], cArgs[2]);
                    }
                    break;
                case "Q": // quit and exit program.
                    System.exit(0);
                default:
                    break;
            }

        }

    }

}
