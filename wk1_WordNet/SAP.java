/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class SAP {
    private final Digraph G;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null) throw new IllegalArgumentException();
        this.G = new Digraph(G.V());
        for (int i = 0; i < G.V(); i++)
            for (int j : G.adj(i))
                this.G.addEdge(i, j);
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        if (v < 0 || v >= G.V() || w < 0 || w >= G.V()) throw new IllegalArgumentException();
        BreadthFirstDirectedPaths bfsv = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths bfsw = new BreadthFirstDirectedPaths(G, w);
        return lengthSearch(bfsv, bfsw);
    }

    private int lengthSearch(BreadthFirstDirectedPaths bfsv, BreadthFirstDirectedPaths bfsw) {
        int min = Integer.MAX_VALUE;
        int[] v = new int[G.V()];
        boolean hasAncestor = false;
        for (int i = 0; i < v.length; i++) {
            if (!bfsv.hasPathTo(i))
                v[i] = -1;
            else {
                v[i] = bfsv.distTo(i);
                if (bfsw.hasPathTo(i)) {
                    hasAncestor = true;
                    int distance = v[i] + bfsw.distTo(i);
                    if (distance < min) {
                        min = distance;
                    }
                }
            }
        }
        if (!hasAncestor) {
            min = -1;
        }
        return min;
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        if (v < 0 || v >= G.V() || w < 0 || w >= G.V()) throw new IllegalArgumentException();
        BreadthFirstDirectedPaths bfsv = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths bfsw = new BreadthFirstDirectedPaths(G, w);
        return ancestorSearch(bfsv, bfsw);
    }

    private int ancestorSearch(BreadthFirstDirectedPaths bfsv, BreadthFirstDirectedPaths bfsw) {
        int min = Integer.MAX_VALUE;
        int[] v = new int[G.V()];
        int ancestorV = -1;
        boolean hasAncestor = false;
        for (int i = 0; i < v.length; i++) {
            if (!bfsv.hasPathTo(i))
                v[i] = -1;
            else {
                v[i] = bfsv.distTo(i);
                if (bfsw.hasPathTo(i)) {
                    hasAncestor = true;
                    int distance = v[i] + bfsw.distTo(i);
                    if (distance < min) {
                        min = distance;
                        ancestorV = i;
                    }
                }
            }
        }
        if (!hasAncestor) {
            ancestorV = -1;
        }
        return ancestorV;
    }

    private int checkIterableIllegal(Iterable<Integer> x) {
        if (x == null) throw new IllegalArgumentException();
        int counter = 0;
        for (Object i : x) {
            if (i == null) throw new IllegalArgumentException();
            if ((int) i < 0 || (int) i >= G.V()) throw new IllegalArgumentException();
            counter++;
        }
        if (counter == 0) return -1;
        else return 0;

    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        if (checkIterableIllegal(v) == -1 || checkIterableIllegal(w) == -1) return -1;

        BreadthFirstDirectedPaths bfsv = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths bfsw = new BreadthFirstDirectedPaths(G, w);
        return lengthSearch(bfsv, bfsw);
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        if (checkIterableIllegal(v) == -1 || checkIterableIllegal(w) == -1) return -1;
        BreadthFirstDirectedPaths bfsv = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths bfsw = new BreadthFirstDirectedPaths(G, w);
        return ancestorSearch(bfsv, bfsw);
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);

        }
    }
}
