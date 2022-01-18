/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class BurrowsWheeler {

    // apply Burrows-Wheeler transform,
    // reading from standard input and writing to standard output
    public static void transform() {
        String s = BinaryStdIn.readString();
        CircularSuffixArray csa = new CircularSuffixArray(s);
        char[] c = s.toCharArray();
        char[] n = new char[c.length];
        int first = -1;
        for (int i = 0; i < c.length; i++) {
            if (csa.index(i) == 0) first = i;
            n[i] = c[Math.floorMod(csa.index(i) - 1, c.length)];
        }
        BinaryStdOut.write(first);
        BinaryStdOut.write(String.valueOf(n));
        BinaryStdOut.close();
    }

    // apply Burrows-Wheeler inverse transform,
    // reading from standard input and writing to standard output
    public static void inverseTransform() {
        int R = 256;
        int first = BinaryStdIn.readInt();
        String s = BinaryStdIn.readString();
        char[] t = s.toCharArray();
        int n = s.length();
        int[] count = new int[R + 1];
        int[] next = new int[n];

        // deduct next[]
        for (int i = 0; i < n; i++)
            count[t[i] + 1]++;
        for (int r = 0; r < R; r++)
            count[r + 1] += count[r];
        for (int i = 0; i < n; i++)
            next[count[t[i]]++] = i;

        // Inverting the message with given t[], first, next[]
        for (int i = 0; i < n; i++) {
            first = next[first];
            BinaryStdOut.write(String.valueOf(t[first]));
        }
        BinaryStdOut.close();
    }

    // if args[0] is "-", apply Burrows-Wheeler transform
    // if args[0] is "+", apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        if (args[0].equals("-")) transform();
        if (args[0].equals("+")) inverseTransform();
    }
}
