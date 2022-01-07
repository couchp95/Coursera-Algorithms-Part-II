/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
    // constructor takes a WordNet object
    private final WordNet wordnet;

    public Outcast(WordNet wordnet) {
        this.wordnet = wordnet;
    }

    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {
        int[] d = new int[nouns.length];
        for (int i = 0; i < nouns.length; i++)
            for (int j = 0; j < nouns.length; j++)
                d[i] += wordnet.distance(nouns[i], nouns[j]);
        int index = 0;
        int max = -1;
        for (int i = 0; i < d.length; i++) {
            //  System.out.println("d[" + i + "]:" + d[i] + "\t nouns:" + nouns[i]);
            if (d[i] > max) {
                max = d[i];
                index = i;
            }
        }
        return nouns[index];
    }

    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
        System.out.println(wordnet.distance("table", "horse"));
        System.out.println(wordnet.distance("cat", "horse"));
        System.out.println(wordnet.distance("cat", "cat"));
    }
}
