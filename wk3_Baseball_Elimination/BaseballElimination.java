import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BaseballElimination {
    private final int num;
    private final Map<String, Integer> teams;
    private final int[] w;
    private final int[] l;
    private final int[] r;
    private final int[][] g;

    private final ArrayList<Bag<String>> subset;

    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
        In file = new In(filename);
        num = file.readInt();
        teams = new HashMap<>();
        w = new int[num];
        l = new int[num];
        r = new int[num];
        g = new int[num][num];
        String[] teambyid = new String[num];
        int leadingteam = -1;
        subset = new ArrayList<Bag<String>>();
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < num; i++) {
            subset.add(null);
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
        for (int current = 0; current < num; current++)
            if (w[leadingteam] > w[current] + r[current]) {
                Bag<String> b = new Bag<String>();
                for (int j = 0; j < num; j++)
                    if (w[j] > w[current] + r[current]) b.add(teambyid[j]);
                subset.add(current, b);
            }
            else {
                int required = minwins(current);
                FlowNetwork flow = new FlowNetwork(num + (num - 2) * (num - 1) / 2 + 1);
                // game vertices =n...n+(n-2)*(n-1)/2-1 ; team vertices = 0..n-1
                // s = x t = num + (num - 2) * (num - 1) / 2
                int s = current;
                int t = num + (num - 2) * (num - 1) / 2;
                int n = num;
                for (int i = 0; i < num; i++) {
                    if (i == s) continue;
                    // create team vertices to t
                    flow.addEdge(new FlowEdge(i, t, w[s] + r[s] - w[i]));
                    for (int j = i + 1; j < num; j++) {
                        if (j == s) continue;
                        // create s to game vertices
                        flow.addEdge(new FlowEdge(s, n, g[i][j]));
                        // create game vertices to team vertices
                        flow.addEdge(new FlowEdge(n, i, Double.POSITIVE_INFINITY));
                        flow.addEdge(new FlowEdge(n, j, Double.POSITIVE_INFINITY));
                        n++;
                    }
                }
                FordFulkerson ff = new FordFulkerson(flow, current, t);
                if (ff.value() < required) {
                    Bag<String> b = new Bag<String>();
                    for (int i = 0; i < num; i++)
                        if (i != current && ff.inCut(i)) b.add(teambyid[i]);
                    // for (String f : teams.keySet())
                    //    if (!f.equals(teambyid[current]) && ff.inCut(teams.get(f))) b.add(f);
                    subset.add(current, b);
                }
            }
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

    private int minwins(int x) {
        // int x = teams.get(team);
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

    // is given team eliminated?
    public boolean isEliminated(String team) {
        if (!teams.containsKey(team)) throw new IllegalArgumentException();
        return (subset.get(teams.get(team)) != null);
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        if (!isEliminated(team)) return null;
        if (!teams.containsKey(team)) throw new IllegalArgumentException();
        return subset.get(teams.get(team));
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
