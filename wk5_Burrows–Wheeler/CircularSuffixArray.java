/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import java.util.Arrays;
import java.util.Comparator;

public class CircularSuffixArray {
    private final int n;
    private final char[] string;
    private final Integer[] index;
    
    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null) throw new IllegalArgumentException();
        n = s.length();
        string = s.toCharArray();
        index = new Integer[n];
        for (int i = 0; i < n; i++) {
            index[i] = i;
        }
        Arrays.sort(index, new Comparator<Integer>() {
            @Override
            public int compare(Integer a, Integer b) {
                for (int i = 0; i < n; i++) {
                    char c1 = string[(i + a) % n];
                    char c2 = string[(i + b) % n];
                    if (c1 < c2) return -1;
                    if (c1 > c2) return 1;
                }
                return 0;
            }
        });
    }

    // length of s
    public int length() {
        return n;
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if (i < 0 || (i > length() - 1)) throw new IllegalArgumentException();
        return index[i];
    }

    // unit testing (required)
    public static void main(String[] args) {
        CircularSuffixArray csa = new CircularSuffixArray("ABRACADABRA!");
        System.out.println("length: " + csa.length());
        for (int i = 0; i < csa.length(); i++)
            System.out.print(csa.index(i) + "\t");
        System.out.println();
    }
}
