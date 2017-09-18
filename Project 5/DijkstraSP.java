/*************************************************************************
 *  Compilation:  javac DijkstraSP.java
 *  Execution:    java DijkstraSP V E
 *  Dependencies: EdgeWeightedgraph.java IndexMinPQ.java Stack.java Edge.java
 *
 *  Dijkstra's algorithm. Computes the shortest path tree.
 *  Assumes all weights are nonnegative.
 *
 *  Author: Sedgewick
 *  Modified by: Joshua Rodstein / jor94@pitt.edu
 *
 *  Modified to handle undirected edges.
 *
 *************************************************************************/

public class DijkstraSP {
    private double[] distTo;          // distTo[v] = distance  of shortest s->v path
    private Edge[] edgeTo;    // edgeTo[v] = last edge on shortest s->v path
    private IndexMinPQ<Double> pq;    // priority queue of vertices
    Graph graph;

    public DijkstraSP(Graph G, int s, int en) {
        graph = G;
        distTo = new double[2 * G.V()];
        edgeTo = new Edge[2 * G.V()];

        distTo = new double[2 * G.V()];
        edgeTo = new Edge[ 2* G.V()];
        edgeTo = new Edge[ 2* G.V()];



        for (int v = 0; v < 2 * G.V(); v++)
            distTo[v] = Double.POSITIVE_INFINITY;


        distTo[s] = 0.0;

        // relax vertices in order of distance from s
        pq = new IndexMinPQ<Double>(2 * G.V());
        pq.insert(s, distTo[s]);
        while (!pq.isEmpty()) {
            int v = pq.delMin();
            for (Edge e : G.adj(v)) {
                relax(e);
                relax(e.inverse());
            }


        }

        // check optimality conditions
        assert check(G, s);
    }

    // relax edge e and update pq if changed
    private void relax(Edge e) {
        int v = e.either(), w = e.other(e.either());
        if (distTo[w] > distTo[v] + e.weight()) {
            distTo[w] = distTo[v] + e.weight();
            edgeTo[w] = e;
            if (pq.contains(w)) pq.change(w, distTo[w]);
            else                pq.insert(w, distTo[w]);
        }
    }

    // length of shortest path from s to v
    public double distTo(int v) {
        return distTo[v];
    }

    // is there a path from s to v?
    public boolean hasPathTo(int v) {
        return distTo[v] < Double.POSITIVE_INFINITY;
    }

    // shortest path from s to v as an Iterable, null if no such path
    public Iterable<Edge> pathTo(int v) {
        if (!hasPathTo(v)) return null;
        Stack<Edge> path = new Stack<Edge>();
        for (Edge e = edgeTo[v]; e != null; e = edgeTo[e.either()]) {
            path.push(e);
        }
        return path;
    }


    // check optimality conditions:
    // (i) for all edges e:            distTo[e.to()] <= distTo[e.from()] + e.weight()
    // (ii) for all edge e on the SPT: distTo[e.to()] == distTo[e.from()] + e.weight()
    private boolean check(Graph G, int s) {

        // check that edge weights are nonnegative
        for (Edge e : G.edges()) {
            if (e.weight() < 0) {
                System.err.println("negative edge weight detected");
                return false;
            }
        }

        // check that distTo[v] and edgeTo[v] are consistent
        if (distTo[s] != 0.0 || edgeTo[s] != null) {
            System.err.println("distTo[s] and edgeTo[s] inconsistent");
            return false;
        }
        for (int v = 0; v < G.V(); v++) {
            if (v == s) continue;
            if (edgeTo[v] == null && distTo[v] != Double.POSITIVE_INFINITY) {
                System.err.println("distTo[] and edgeTo[] inconsistent");
                return false;
            }
        }

        // check that all edges e = v->w satisfy distTo[w] <= distTo[v] + e.weight()
        for (int v = 0; v < G.V(); v++) {
            for (Edge e : G.adj(v)) {
                int w = e.other(e.either());
                if (distTo[v] + e.weight() < distTo[w]) {
                    System.err.println("edge " + e + " not relaxed");
                    return false;
                }
            }
        }

        // check that all edges e = v->w on SPT satisfy distTo[w] == distTo[v] + e.weight()
        for (int w = 0; w < G.V(); w++) {
            if (edgeTo[w] == null) continue;
            Edge e = edgeTo[w];
            int v = e.either();
            if (w != e.other(e.either())) return false;
            if (distTo[v] + e.weight() != distTo[w]) {
                System.err.println("edge " + e + " on shortest path not tight");
                return false;
            }
        }
        return true;
    }

    public String toString(int start, int end)
    {
        String NEWLINE = System.getProperty("line.separator");
        StringBuilder s = new StringBuilder();
        String header = "Dijkstra's  ";


        s.append(header + "PATH from " + start + " to " + end + NEWLINE);
        s.append("----------------------------------------------------");

        // print shortest path
        if (hasPathTo(end))
        {
            s.append(NEWLINE + "Shortest distance from " + start + " to " + end+ " is " + distTo(end) + NEWLINE);
            s.append("Path with edges: " + NEWLINE);
            if (hasPathTo(end))
            {
                for (Edge e : pathTo(end))
                {
                    double weight = e.weight();


                    s.append(e.either() + " -> " + e.other(e.either()) + " " + weight + NEWLINE);
                }
            }
        }
        else
        {
            s.append(NEWLINE + start + " to " + end + "          no path\n");
        }

        return s.toString();
    }
}
