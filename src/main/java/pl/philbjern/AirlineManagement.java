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

        void updateCapacity(int node, int start, int end, int l, int r, int value) {
            applyLazy(node, start, end);

            if (start > r || end < l) {
                return;
            }

            if (start >= l && end <= r) {
                tree[node].lazy += value;
                applyLazy(node, start, end);
                if (DEBUG) {
                    System.out.println("Updated capacity: Node=" + node + ", Range=[" + start + "," + end + "]");
                    System.out.println(this);
                }
                return;
            }

            int mid = (start + end) / 2;
            updateCapacity(2 * node + 1, start, mid, l, r, value);
            updateCapacity(2 * node + 2, mid + 1, end, l, r, value);

            tree[node].sum = calculateSum(node);
        }

        void updateActivation(int node, int start, int end, int l, int r, boolean activate) {
            applyLazy(node, start, end);

            if (start > r || end < l) {
                return;
            }

            if (start >= l && end <= r) {
                tree[node].lazyActive = activate;
                applyLazy(node, start, end);
                if (DEBUG) {
                    System.out.println("Updated activation: Node=" + node + ", Range=[" + start + "," + end + "]");
                    System.out.println(this);
                }
                return;
            }

            int mid = (start + end) / 2;
            updateActivation(2 * node + 1, start, mid, l, r, activate);
            updateActivation(2 * node + 2, mid + 1, end, l, r, activate);

            tree[node].sum = calculateSum(node);
        }

        int query(int node, int start, int end, int l, int r) {
            applyLazy(node, start, end);

            if (start > r || end < l) {
                return 0;
            }

            if (start >= l && end <= r) {
                if (DEBUG) {
                    System.out.println("Query: Node=" + node + ", Range=[" + start + "," + end + "] -> Sum=" + (tree[node].active ? tree[node].sum : 0));
                }
                return tree[node].active ? tree[node].sum : 0;
            }

            int mid = (start + end) / 2;
            int leftSum = query(2 * node + 1, start, mid, l, r);
            int rightSum = query(2 * node + 2, mid + 1, end, l, r);
            return leftSum + rightSum;
        }

        void applyLazy(int node, int start, int end) {
            if (tree[node].lazyActive != null) {
                tree[node].active = tree[node].lazyActive;
                tree[node].lazyActive = null;
            }

            if (tree[node].lazy != 0) {
                if (tree[node].active) {
                    tree[node].sum += tree[node].lazy * (end - start + 1);
                }

                if (start != end) {
                    tree[2 * node + 1].lazy += tree[node].lazy;
                    tree[2 * node + 2].lazy += tree[node].lazy;
                }

                tree[node].lazy = 0;
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
                            .append("active=").append(tree[i].active).append(", ")
                            .append("lazy=").append(tree[i].lazy).append(", ")
                            .append("lazyActive=").append(tree[i].lazyActive).append("\n");
                }
            }
            return sb.toString();
        }
    }

    static class SegmentTreeNode {
        int sum;
        boolean active;
        int lazy;
        Boolean lazyActive;

        SegmentTreeNode() {
            this.sum = 0;
            this.active = true;
            this.lazy = 0;
            this.lazyActive = null;
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter pw = new PrintWriter(System.out);

        String[] firstLine = br.readLine().split(" ");
        int n = Integer.parseInt(firstLine[0]);
        int q = Integer.parseInt(firstLine[1]);

        String[] passengersLine = br.readLine().split(" ");
        int[] passengers = Arrays.stream(passengersLine).mapToInt(Integer::parseInt).toArray();

        SegmentTree segmentTree = new SegmentTree(n);
        segmentTree.build(passengers, 0, 0, n - 1);

        for (int i = 0; i < q; i++) {
            String[] query = br.readLine().split(" ");
            char type = query[0].charAt(0);
            int l = Integer.parseInt(query[1]) - 1;

            switch (type) {
                case 'P':
                    int p = Integer.parseInt(query[2]);
                    int r = Integer.parseInt(query[3]) - 1;
                    segmentTree.updateCapacity(0, 0, n - 1, l, r, p);
                    break;
                case 'C':
                    int t = Integer.parseInt(query[2]) - 1;
                    segmentTree.updateActivation(0, 0, n - 1, l, t, false);
                    break;
                case 'Q':
                    int rQuery = Integer.parseInt(query[2]) - 1;
                    int result = segmentTree.query(0, 0, n - 1, l, rQuery);
                    pw.println(result);
                    break;
            }

            if (SegmentTree.DEBUG) {
                System.out.println("After operation " + type + ":\n" + segmentTree);
            }
        }

        pw.flush();
        pw.close();
    }
}
