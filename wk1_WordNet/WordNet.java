import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.RedBlackBST;
import edu.princeton.cs.algs4.SET;

import java.util.ArrayList;

public class WordNet {
    private final Digraph wordnet;
    private final ArrayList<Bag<String>> synsets; //  index - words
    private final RedBlackBST<String, SET<Integer>> nouns; // word - SET of indexes
    private final SAP sap;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        this.synsets = new ArrayList<Bag<String>>();
        // this.nouns = new RedBlackBST<String, Integer>();
        this.nouns = new RedBlackBST<String, SET<Integer>>();
        if (synsets == null || hypernyms == null) throw new IllegalArgumentException();
        In s = new In(synsets);
        while (!s.isEmpty()) {
            String[] lineSynsets = s.readLine().split(",");
            int ilineSynsets = Integer.parseInt(lineSynsets[0]);
            Bag<String> b = new Bag<String>();
            String[] s1 = lineSynsets[1].split(" ");
            // System.out.println("Synset " + ilineSynsets + "\t:" + lineSynsets[1]);
            for (int i = 0; i < s1.length; i++) {
                b.add(s1[i]);
                // nouns.put(s1[i], ilineSynsets);
                SET<Integer> set = nouns.get(s1[i]);
                if (set == null) set = new SET<Integer>();
                set.add(ilineSynsets);
                nouns.put(s1[i], set);
            }
            this.synsets.add(ilineSynsets, b);
        }
        this.wordnet = new Digraph(this.synsets.size());
        In h = new In(hypernyms);
        while (!h.isEmpty()) {
            String[] lineHypernyms = h.readLine().split(",");
            int iHypernyms = Integer.parseInt(lineHypernyms[0]);
            for (int i = 1; i < lineHypernyms.length; i++) {
                // System.out.println("i: " + i + "\tlength: " + lineHypernyms.length);
                // System.out.println("lineHypernyms[" + i + "]: " + lineHypernyms[i]);
                wordnet.addEdge(iHypernyms, Integer.parseInt(lineHypernyms[i]));
            }
        }

        DirectedCycle dc = new DirectedCycle(wordnet);
        if (dc.hasCycle()) throw new IllegalArgumentException();
        int root = 0;
        for (int i = 0; i < wordnet.V(); i++)
            if (wordnet.outdegree(i) == 0) root++;
        if (root > 1) throw new IllegalArgumentException();
        sap = new SAP(wordnet);
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return nouns.keys();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null) throw new IllegalArgumentException();
        return nouns.contains(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (nounA == null || nounB == null) throw new IllegalArgumentException();
        if (!isNoun(nounA) || !isNoun(nounB)) throw new IllegalArgumentException();
        return sap.length(nouns.get(nounA), nouns.get(nounB));
    }


    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (nounA == null || nounB == null) throw new IllegalArgumentException();
        if (!isNoun(nounA) || !isNoun(nounB)) throw new IllegalArgumentException();
        StringBuilder result = new StringBuilder();
        for (String s : synsets.get(sap.ancestor(nouns.get(nounA), nouns.get(nounB))))
            result.append(s + " ");
        return result.toString();
    }

    // do unit testing of this class
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);

        System.out.println("nouns size:" + wordnet.nouns.size());
        // System.out.println(wordnet.nouns.get("horse"));

    }
}
