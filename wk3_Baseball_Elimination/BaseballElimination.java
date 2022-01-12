import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.HashMap;
import java.util.Map;

public class BaseballElimination {
    private final int num, t;
    private int s;
    private final Map<String, Integer> teams;
    private final String[] teambyid;
    private final int[] w;
    private final int[] l;
    private final int[] r;
    private final int[][] g;
    private FlowNetwork flow;
    private FordFulkerson ff;
    private int leadingteam;
    private final boolean[] trivials;

    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
        In file = new In(filename);
        num = file.readInt();
        teams = new HashMap<>();
        w = new int[num];
        l = new int[num];
        r = new int[num];
        g = new int[num][num];
        trivials = new boolean[num];
        teambyid = new String[num];
        leadingteam = -1;
        t = num + num * (num - 1) / 2 + 1;
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < num; i++) {
            teambyid[i] = file.readString();
            teams.put(teambyid[i], i);
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
        for (int i = 0; i < num; i++)
            if (w[leadingteam] > w[i] + r[i]) trivials[i] = true;
    }

    // number of teams
    public int numberOfTeams() {
        return num;
    }

    // all teams
    public Iterable<String> teams() {
        return teams.keySet();
    }

    // number of wins for given team
    public int wins(String team) {
        if (!teams.containsKey(team)) throw new IllegalArgumentException();
        return w[teams.get(team)];
    }

    // number of losses for given team
    public int losses(String team) {
        if (!teams.containsKey(team)) throw new IllegalArgumentException();
        return l[teams.get(team)];
    }

    // number of remaining games for given team
    public int remaining(String team) {
        if (!teams.containsKey(team)) throw new IllegalArgumentException();
        return r[teams.get(team)];
    }

    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        if (!teams.containsKey(team1) || !teams.containsKey(team2))
            throw new IllegalArgumentException();
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
        if (!teams.containsKey(team)) throw new IllegalArgumentException();
        return trivials[teams.get(team)];
    }

    // is given team eliminated?
    public boolean isEliminated(String team) {
        if (!teams.containsKey(team)) throw new IllegalArgumentException();
        if (trivalEliminated(team)) return true;
        int required = minwins(team);
        buildflownetwork(team);
        ff = new FordFulkerson(flow, teams.get(team), t);
        return (ff.value() < required);
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        if (!isEliminated(team)) return null;
        Bag<String> b = new Bag<String>();
        if (!teams.containsKey(team)) throw new IllegalArgumentException();
        if (trivalEliminated(team)) {
            for (int i = 0; i < num; i++)
                if (w[i] > w[teams.get(team)] + r[teams.get(team)]) b.add(teambyid[i]);
            return b;
        }
        for (String f : teams.keySet())
            if (!f.equals(team) && ff.inCut(teams.get(f))) b.add(f);
        return b;
    }

    private void buildflownetwork(String team) {
        int x = teams.get(team);
        flow = new FlowNetwork(num + num * (num - 1) / 2 + 2);
        // game vertices =n...n+(n-1)*n/2 ; team vertices = 0..n-1
        // s = x t = n+(n-1)*n/2+1
        s = x;
        int n = num;
        for (int i = 0; i < num; i++) {
            if (i == x) continue;
            // create team vertices to t
            flow.addEdge(new FlowEdge(i, t, w[x] + r[x] - w[i]));
            for (int j = i + 1; j < num; j++) {
                if (j == x) continue;
                // create s to game vertices
                flow.addEdge(new FlowEdge(s, n, g[i][j]));
                // create game vertices to team vertices
                flow.addEdge(new FlowEdge(n, i, Double.POSITIVE_INFINITY));
                flow.addEdge(new FlowEdge(n, j, Double.POSITIVE_INFINITY));
                n++;
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
