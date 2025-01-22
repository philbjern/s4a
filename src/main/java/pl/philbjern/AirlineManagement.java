package pl.philbjern;

import java.io.*;
import java.util.*;

public class AirlineManagement {

    static class Flight {
        int maxPassengers;
        int activeUntil;

        Flight(int maxPassengers, int activeUntil) {
            this.maxPassengers = maxPassengers;
            this.activeUntil = activeUntil;
        }
    }

    static class SegmentTree {
        private final int[] tree;
        private final int size;

        SegmentTree(int n) {
            size = n;
            tree = new int[4 * n];
        }

        void update(int idx, int value, int node, int start, int end) {
            if (start == end) {
                tree[node] = value;
            } else {
                int mid = (start + end) / 2;
                int leftNode = 2 * node + 1;
                int rightNode = 2 * node + 2;
                if (idx <= mid) {
                    update(idx, value, leftNode, start, mid);
                } else {
                    update(idx, value, rightNode, mid + 1, end);
                }
                tree[node] = tree[leftNode] + tree[rightNode];
            }
        }

        void update(int idx, int value) {
            update(idx, value, 0, 0, size - 1);
        }

        int query(int l, int r, int node, int start, int end) {
            if (start > r || end < l) {
                return 0;
            }
            if (l <= start && end <= r) {
                return tree[node];
            }
            int mid = (start + end) / 2;
            int leftQuery = query(l, r, 2 * node + 1, start, mid);
            int rightQuery = query(l, r, 2 * node + 2, mid + 1, end);
            return leftQuery + rightQuery;
        }

        int query(int l, int r) {
            return query(l, r, 0, 0, size - 1);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("SegmentTree: [");
            for (int i = 0; i < tree.length; i++) {
                sb.append(tree[i]);
                if (i < tree.length - 1) {
                    sb.append(", ");
                }
            }
            sb.append("]");
            return sb.toString();
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter pw = new PrintWriter(System.out);

        String[] firstLine = br.readLine().split(" ");
        int n = Integer.parseInt(firstLine[0]);
        int q = Integer.parseInt(firstLine[1]);

        String[] passengersLine = br.readLine().split(" ");

        Flight[] flights = initializeFlights(n, passengersLine);
        SegmentTree segmentTree = initializeSegmentTree(n, flights);

        processQueries(br, pw, q, flights, segmentTree);

        pw.flush();
    }

    private static Flight[] initializeFlights(int n, String[] passengersLine) {
        Flight[] flights = new Flight[n];
        for (int i = 0; i < n; i++) {
            int maxPassengers = Integer.parseInt(passengersLine[i]);
            flights[i] = new Flight(maxPassengers, Integer.MAX_VALUE);
        }
        return flights;
    }

    private static SegmentTree initializeSegmentTree(int n, Flight[] flights) {
        SegmentTree segmentTree = new SegmentTree(n);
        for (int i = 0; i < n; i++) {
            segmentTree.update(i, flights[i].maxPassengers);
        }
        return segmentTree;
    }

    private static void processQueries(BufferedReader br, PrintWriter pw, int q, Flight[] flights, SegmentTree segmentTree) throws IOException {
        while (q-- > 0) {
            String[] query = br.readLine().split(" ");

            char type = query[0].charAt(0);
            int i = Integer.parseInt(query[1]) - 1;

            switch (type) {
                case 'P':
                    processUpdatePassengerQuery(query, flights, segmentTree, i);
                    break;
                case 'C':
                    processDeactivateFlightQuery(query, flights, segmentTree, i);
                    break;
                case 'A':
                    processAssignNewFlightQuery(query, flights, segmentTree, i);
                    break;
                case 'Q':
                    processSumQuery(query, pw, flights, segmentTree, i);
                    break;
            }
        }
    }

    private static void processUpdatePassengerQuery(String[] query, Flight[] flights, SegmentTree segmentTree, int i) {
        int p = Integer.parseInt(query[2]);
        int t = Integer.parseInt(query[3]);
        if (flights[i].activeUntil > t) {
            flights[i].maxPassengers = p;
            segmentTree.update(i, p);
        }
    }

    private static void processDeactivateFlightQuery(String[] query, Flight[] flights, SegmentTree segmentTree, int i) {
        int t = Integer.parseInt(query[2]);
        if (flights[i].activeUntil > t) {
            flights[i].activeUntil = t;
        }
    }

    private static void processAssignNewFlightQuery(String[] query, Flight[] flights, SegmentTree segmentTree, int i) {
        int p = Integer.parseInt(query[2]);
        flights[i] = new Flight(p, Integer.MAX_VALUE);
        segmentTree.update(i, p);
    }

    private static void processSumQuery(String[] query, PrintWriter pw, Flight[] flights, SegmentTree segmentTree, int i) {
        int j = Integer.parseInt(query[2]) - 1;
        int t = Integer.parseInt(query[3]);
        int sum = calculateSumForActiveFlights(flights, segmentTree, i, j, t);
        pw.println(sum);
    }

    private static int calculateSumForActiveFlights(Flight[] flights, SegmentTree segmentTree, int i, int j, int t) {
        // Rebuild the segment tree temporarily to account for activeUntil
        int sum = 0;
        for (int idx = i; idx <= j; idx++) {
            if (flights[idx].activeUntil <= t) {
                segmentTree.update(idx, 0); // Mark inactive flights
            }
        }
        sum = segmentTree.query(i, j); // Query the active range
        for (int idx = i; idx <= j; idx++) {
            if (flights[idx].activeUntil > t) {
                segmentTree.update(idx, flights[idx].maxPassengers); // Restore active flights
            }
        }

        return sum;
    }
}