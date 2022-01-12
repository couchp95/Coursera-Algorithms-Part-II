import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.SeparateChainingHashST;
import edu.princeton.cs.algs4.StdOut;

public class BaseballElimination {
    private final int num, s, t;
    private final SeparateChainingHashST<String, Integer> teams;
    private final SeparateChainingHashST<Integer, String> teamsbyid;
    private final int[] w;
    private final int[] l;
    private final int[] r;
    private final int[][] g;
    private FlowNetwork flow;
    private int leadingteam;
    private final boolean[] trivials;

    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
        In file = new In(filename);
        num = file.readInt();
        teams = new SeparateChainingHashST<String, Integer>(num);
        teamsbyid = new SeparateChainingHashST<Integer, String>(num);
        w = new int[num];
        l = new int[num];
        r = new int[num];
        g = new int[num][num];
        trivials = new boolean[num];
        s = num * (num + 1);
        t = num * (num + 1) + 1;
        leadingteam = -1;
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < num; i++) {
            String tmp = file.readString();
            teams.put(tmp, i);
            teamsbyid.put(i, tmp);
            w[i] = file.readInt();
            l[i] = file.readInt();
            r[i] = file.readInt();
            for (int j = 0; j < num; j++)
                g[i][j] = file.readInt();
            if (w[i] > max) {
                max = w[i];
                leadingteam = i;
            }
        }
        for (String n : teams.keys())
            if (w[leadingteam] > w[teams.get(n)] + r[teams.get(n)]) trivials[teams.get(n)] = true;
    }

    // number of teams
    public int numberOfTeams() {
        return num;
    }

    // all teams
    public Iterable<String> teams() {
        return teams.keys();
    }

    // number of wins for given team
    public int wins(String team) {
        if (!teams.contains(team)) throw new IllegalArgumentException();
        return w[teams.get(team)];
    }

    // number of losses for given team
    public int losses(String team) {
        if (!teams.contains(team)) throw new IllegalArgumentException();
        return l[teams.get(team)];
    }

    // number of remaining games for given team
    public int remaining(String team) {
        if (!teams.contains(team)) throw new IllegalArgumentException();
        return r[teams.get(team)];
    }

    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        if (!teams.contains(team1) || !teams.contains(team2)) throw new IllegalArgumentException();
        return g[teams.get(team1)][teams.get(team2)];
    }

    private int minwins(String team) {
        int x = teams.get(team);
        int min = 0;
        for (int i = 0; i < num; i++) {
            if (i == x) continue;
            for (int j = i + 1; j < num; j++) {
                if (j == x) continue;
                min += g[i][j];
            }
        }
        return min;
    }

    private boolean trivalEliminated(String team) {
        if (!teams.contains(team)) throw new IllegalArgumentException();
        return trivials[teams.get(team)];
    }

    // is given team eliminated?
    public boolean isEliminated(String team) {
        if (!teams.contains(team)) throw new IllegalArgumentException();
        if (trivalEliminated(team)) return true;
        int required = minwins(team);
        buildflownetwork(team);
        FordFulkerson ff = new FordFulkerson(flow, s, t);
        return (ff.value() < required);
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        if (!isEliminated(team)) return null;
        Bag<String> b = new Bag<String>();
        if (!teams.contains(team)) throw new IllegalArgumentException();
        if (trivalEliminated(team)) {
            for (String n : teams.keys())
                if (w[teams.get(n)] > w[teams.get(team)] + r[teams.get(team)]) b.add(n);
            return b;
        }
        buildflownetwork(team);
        FordFulkerson ff = new FordFulkerson(flow, s, t);
        for (String f : teams.keys())
            if (ff.inCut(num * num + teams.get(f))) b.add(f);
        return b;
    }

    private void buildflownetwork(String team) {
        int x = teams.get(team);
        flow = new FlowNetwork(num * (num + 1) + 2);
        // game vertices =0-n*n-1 ; team vertices = n*n - n*(n+1)-1
        // s = n*(n+1) t = n*(n+1)+1
        for (int i = 0; i < num; i++) {
            if (i == x) continue;
            // create team vertices to t
            flow.addEdge(new FlowEdge(num * num + i, t, w[x] + r[x] - w[i]));
            for (int j = i + 1; j < num; j++) {
                if (j == x) continue;
                // create s to game vertices
                flow.addEdge(new FlowEdge(s, i * num + j, g[i][j]));
                // create game vertices to team vertices
                flow.addEdge(new FlowEdge(i * num + j, num * num + i, Double.POSITIVE_INFINITY));
                flow.addEdge(new FlowEdge(i * num + j, num * num + j, Double.POSITIVE_INFINITY));
            }
        }
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}
