package pl.philbjern;

import java.io.*;
import java.util.*;

public class AirlineManagement {

    static class SegmentTree {
        int n;
        SegmentTreeNode[] tree;
        static boolean DEBUG = "true".equalsIgnoreCase(System.getenv("DEBUG"));

        SegmentTree(int size) {
            this.n = size;
            this.tree = new SegmentTreeNode[4 * n];
            for (int i = 0; i < tree.length; i++) {
                tree[i] = new SegmentTreeNode();
            }
        }

        void build(int[] passengers, int node, int start, int end) {
            if (start == end) {
                tree[node].sum = passengers[start];
                tree[node].active = true;
            } else {
                int mid = (start + end) / 2;
                build(passengers, 2 * node + 1, start, mid);
                build(passengers, 2 * node + 2, mid + 1, end);
                tree[node].sum = tree[2 * node + 1].sum + tree[2 * node + 2].sum;
                tree[node].active = true;
            }
        }

        void updateCapacity(int node, int start, int end, int index, int value, int day) {
            applyLazy(node, start, end, day);

            if (start == end) {
                // Update capacity for this specific flight
                tree[node].lazyCapacity = value;
                applyLazy(node, start, end, day); // Apply the change immediately
                return;
            }

            int mid = (start + end) / 2;
            if (index <= mid) {
                updateCapacity(2 * node + 1, start, mid, index, value, day);
            } else {
                updateCapacity(2 * node + 2, mid + 1, end, index, value, day);
            }

            // Recalculate sum for this node
            tree[node].sum = tree[2 * node + 1].sum + tree[2 * node + 2].sum;
        }

        void activate(int node, int start, int end, int index, int capacity, int day) {
            applyLazy(node, start, end, day);

            if (start == end) {
                // Activate this specific flight with the given capacity
                tree[node].lazyActive = true;
                tree[node].lazyCapacity = capacity;
                applyLazy(node, start, end, day); // Apply the changes immediately
                return;
            }

            int mid = (start + end) / 2;
            if (index <= mid) {
                activate(2 * node + 1, start, mid, index, capacity, day);
            } else {
                activate(2 * node + 2, mid + 1, end, index, capacity, day);
            }

            // Recalculate sum for this node
            tree[node].sum = tree[2 * node + 1].sum + tree[2 * node + 2].sum;
        }

        void deactivate(int node, int start, int end, int index, int day) {
            applyLazy(node, start, end, day);

            if (start == end) {
                // Deactivate this specific flight
                tree[node].lazyActive = false;
                applyLazy(node, start, end, day); // Apply the change immediately
                return;
            }

            int mid = (start + end) / 2;
            if (index <= mid) {
                deactivate(2 * node + 1, start, mid, index, day);
            } else {
                deactivate(2 * node + 2, mid + 1, end, index, day);
            }

            // Recalculate sum for this node
            tree[node].sum = tree[2 * node + 1].sum + tree[2 * node + 2].sum;
        }


        int query(int node, int start, int end, int l, int r, int day) {
            applyLazy(node, start, end, day);

            if (start > r || end < l) return 0; // Out of range

            if (start >= l && end <= r) return tree[node].sum; // Fully within range

            int mid = (start + end) / 2;
            return query(2 * node + 1, start, mid, l, r, day) +
                    query(2 * node + 2, mid + 1, end, l, r, day);
        }

        void applyLazy(int node, int start, int end, int day) {
            // Apply capacity updates
            if (tree[node].lazyCapacity != -1) {
                if (tree[node].active) {
                    // Update cumulative sum for the range [start, end] with the new capacity
                    int daysToUpdate = day - tree[node].lastUpdatedDay;
                    tree[node].sum += tree[node].capacity * daysToUpdate;

                    // Set the new capacity
                    tree[node].capacity = tree[node].lazyCapacity;
                }

                // Propagate lazy capacity overwrite to children
                if (start != end) {
                    tree[2 * node + 1].lazyCapacity = tree[node].lazyCapacity;
                    tree[2 * node + 2].lazyCapacity = tree[node].lazyCapacity;
                }
                tree[node].lazyCapacity = -1; // Clear lazy value
            }

            // Apply activation/deactivation
            if (tree[node].lazyActive != null) {
                tree[node].active = tree[node].lazyActive;

                // Propagate activation/deactivation to children
                if (start != end) {
                    tree[2 * node + 1].lazyActive = tree[node].lazyActive;
                    tree[2 * node + 2].lazyActive = tree[node].lazyActive;
                }
                tree[node].lazyActive = null; // Clear lazy value
            }

            // Update the sum based on the time elapsed since the last update
            if (tree[node].lastUpdatedDay < day) {
                int daysToUpdate = day - tree[node].lastUpdatedDay;
                if (tree[node].active) {
                    tree[node].sum += tree[node].capacity * daysToUpdate;
                }
                tree[node].lastUpdatedDay = day; // Mark as updated
            }
        }


        int calculateSum(int node) {
            return tree[node].active ? tree[2 * node + 1].sum + tree[2 * node + 2].sum : 0;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < tree.length; i++) {
                if (tree[i] != null) {
                    sb.append("Node ").append(i).append(": ")
                            .append("sum=").append(tree[i].sum).append(", ")
                            .append("capacity=").append(tree[i].capacity).append(", ")
                            .append("active=").append(tree[i].active).append(", ")
                            .append("lazyCapacity=").append(tree[i].lazyCapacity).append(", ")
                            .append("lazyActive=").append(tree[i].lazyActive).append(", ")
                            .append("lastUpdatedDay").append(tree[i].lastUpdatedDay).append("\n");
                }
            }
            return sb.toString();
        }
    }

    static class SegmentTreeNode {
        int sum; // Cumulative passenger count up to the last updated day
        int capacity; // Current capacity of the plane (set by P or A operations)
        boolean active; // Whether the plane is active
        int lazyCapacity; // Lazy value for capacity updates (overwrite behavior)
        Boolean lazyActive; // Lazy value for activation/deactivation
        int lastUpdatedDay; // The last day this node was updated

        SegmentTreeNode() {
            this.sum = 0;
            this.capacity = 0;
            this.active = false; // Planes start deactivated
            this.lazyCapacity = -1; // -1 indicates no pending capacity update
            this.lazyActive = null;
            this.lastUpdatedDay = 0;
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter pw = new PrintWriter(System.out);

        // Read the number of flights and operations
        String[] firstLine = br.readLine().split(" ");
        int n = Integer.parseInt(firstLine[0]);
        int q = Integer.parseInt(firstLine[1]);

        // Read initial passenger capacities for flights
        String[] passengersLine = br.readLine().split(" ");
        int[] passengers = Arrays.stream(passengersLine).mapToInt(Integer::parseInt).toArray();

        // Initialize and build the segment tree
        SegmentTree segmentTree = new SegmentTree(n);
        segmentTree.build(passengers, 0, 0, n - 1);

        for (int i = 0; i < q; i++) {
            String[] query = br.readLine().split(" ");
            char type = query[0].charAt(0);

            // Parse the first parameter (always flight index or range start)
            int l = Integer.parseInt(query[1]) - 1;

            switch (type) {
                case 'A': { // Activate a flight with a specific plane capacity
                    int p = Integer.parseInt(query[2]);
                    int t = Integer.parseInt(query[3]);
                    segmentTree.activate(0, 0, n - 1, l, p, t);
                    break;
                }
                case 'C': { // Deactivate a flight from a specific day
                    int t = Integer.parseInt(query[2]);
                    segmentTree.deactivate(0, 0, n - 1, l, t);
                    break;
                }
                case 'P': { // Change the capacity of a flight
                    int t = Integer.parseInt(query[2]);
                    int v = Integer.parseInt(query[3]);
                    segmentTree.updateCapacity(0, 0, n - 1, l, v, t);
                    break;
                }
                case 'Q': { // Query total passengers for a range of flights
                    int rQuery = Integer.parseInt(query[2]) - 1;
                    int day = Integer.parseInt(query[3]);
                    int result = segmentTree.query(0, 0, n - 1, l, rQuery, day);
                    pw.println(result);
                    break;
                }
                default:
                    throw new IllegalArgumentException("Unknown operation: " + type);
            }

            // Debug information (if enabled in SegmentTree class)
            if (SegmentTree.DEBUG) {
                System.out.println("After operation " + type + ":\n" + segmentTree);
            }
        }

        pw.flush();
        pw.close();
    }
}
