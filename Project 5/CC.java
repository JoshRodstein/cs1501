/*************************************************************************
 *  Compilation:  javac CC.java
 *  Dependencies: Graph.java 
 *
 *  Compute connected components using depth first search.
 *  Runs in O(E + V) time.
 *  --------------------------------------------------------------------
 *  Author: Sedgewick
 *  Modified Authors Code bye: Joshua Rodstein / jor94
 *
 *************************************************************************/

public class CC {
    private boolean[] marked;   // marked[v] = has vertex v been marked?
    private int[] id;           // id[v] = id of connected component containing v
    private int[] size;         // size[v] = number of vertices in component containing v
    private int count;          // number of connected components

    public CC(Graph G) {
        marked = new boolean[G.V()];
        id = new int[G.V()];
        size = new int[G.V()];
        for (int v = 0; v < G.V(); v++) {
            if (!marked[v]) {
                dfs(G, v);
                count++;
            }
        }
    }

    public int getCompCount(){
        return this.id.length;
    }

    // depth first search
    private void dfs(Graph G, int v) {
        marked[v] = true;
        id[v] = count;
        size[v]++;
        for (Edge w2 : G.adj[v]) {
            if (!marked[w2.either()]) {
                dfs(G, w2.either());
            }
            if (!marked[w2.other(w2.either())]) {
                dfs(G, w2.other(w2.either()));
            }

        }
    }

    // id of connected component containing v
    public int id(int v) {
        return id[v];
    }

    // size of connected component containing v
    public int size(int v) {
        return size[id[v]];
    }

    // number of connected components
    public int count() {
        if(count == 1){
            return count;
        }
        else{
            return count / 2;
        }

    }

    // are v and w in the same connected component?
    public boolean areConnected(int v, int w) {
        return id(v) == id(w);
    }

    // test client
    public static void main(String[] args) {
        int V = Integer.parseInt(args[0]);
        int E = Integer.parseInt(args[1]);
        Graph G = new Graph(V, E);
        StdOut.println(G);
        CC cc = new CC(G);

        StdOut.println("Number of connected components = " + cc.count());
        for (int v = 0; v < G.V(); v++) {
            StdOut.println(v + ": " + cc.id(v));
        }
    }


}
