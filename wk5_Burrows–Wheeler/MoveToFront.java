import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

import java.util.LinkedList;

public class MoveToFront {
    private static final int R = 256;

    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        LinkedList<Character> alpha = new LinkedList<Character>();
        for (char i = 0; i < R; i++)
            alpha.add(i);
        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            BinaryStdOut.write((char) alpha.indexOf(c));
            alpha.remove((Object) c);
            alpha.addFirst(c);

        }
        BinaryStdOut.close();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        char[] alpha = new char[R];
        for (char i = 0; i < R; i++)
            alpha[i] = i;
        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            BinaryStdOut.write(alpha[c]);
            char tmp = alpha[c];
            for (char i = c; i > 0; i--)
                alpha[i] = alpha[i - 1];
            alpha[0] = tmp;
        }
        BinaryStdOut.close();
    }

    // if args[0] is "-", apply move-to-front encoding
    // if args[0] is "+", apply move-to-front decoding
    public static void main(String[] args) {
        if (args[0].equals("-")) encode();
        if (args[0].equals("+")) decode();
    }
}
