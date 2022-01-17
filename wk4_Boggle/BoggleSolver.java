import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdOut;

public class BoggleSolver {
    private static final int R = 26;
    private int n, m;
    private boolean[][] used;
    private SET<String> words;
    private String[][] board;
    private Node root;

    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        for (int i = 0; i < dictionary.length; i++)
            add(dictionary[i]);
    }

    private static class Node {
        private Node[] next = new Node[R];
        private boolean isString;
    }

    private boolean contains(String key) {
        if (key == null) throw new IllegalArgumentException("argument to contains() is null");
        Node x = get(root, key, 0);
        if (x == null) return false;
        return x.isString;
    }

    private Node get(Node x, String key, int d) {
        if (x == null) return null;
        if (d == key.length()) return x;
        char c = (char) (key.charAt(d) - 65);
        return get(x.next[c], key, d + 1);
    }

    private void add(String key) {
        if (key == null) throw new IllegalArgumentException("argument to add() is null");
        root = add(root, key, 0);
    }

    private Node add(Node x, String key, int d) {
        if (x == null) x = new Node();
        if (d == key.length()) {
            if (!x.isString) n++;
            x.isString = true;
        }
        else {
            char c = (char) (key.charAt(d) - 65);
            x.next[c] = add(x.next[c], key, d + 1);
        }
        return x;
    }

    private boolean keysWithPrefix(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException("calls keysWithPrefix() with null argument");
        }
        Node x = get(root, prefix, 0);
        if (x == null) return false;
        return true;
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        m = board.rows();
        n = board.cols();
        this.board = new String[m][n];
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++) {
                if (board.getLetter(i, j) == 'Q') this.board[i][j] = "QU";
                else this.board[i][j] = String.valueOf(board.getLetter(i, j));
            }
        words = new SET<String>();
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++) {
                used = new boolean[m][n];
                dfs(i, j, "");
            }
        return words;
    }

    private void dfs(int row, int col, String s) {
        s = s + board[row][col];
        if (!keysWithPrefix(s)) return;
        used[row][col] = true;
        for (int i = -1; i <= 1; i++)
            for (int j = -1; j <= 1; j++)
                if (!(i == 0 && j == 0) && ((col + j) >= 0) && ((col + j) < n) && ((row + i) >= 0)
                        && ((row + i) < m) && !used[row + i][col + j]) {
                    dfs(row + i, col + j, s);
                }
        if (s.length() > 2 && contains(s)) {
            words.add(s);
        }
        used[row][col] = false;
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        if (!contains(word)) return 0;
        switch (word.length()) {
            case 0:
            case 1:
            case 2:
                return 0;
            case 3:
            case 4:
                return 1;
            case 5:
                return 2;
            case 6:
                return 3;
            case 7:
                return 5;
        }
        return 11;
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        System.out.println(board);
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
        // System.out.println(solver.scoreOf("WET"));
    }
}
