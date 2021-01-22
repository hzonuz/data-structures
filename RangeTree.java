import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.StringTokenizer;

public class RangeTree {
    Node rooot;
    List<Node> nds;
    List<Node> results;

    public RangeTree(List<Node> pts) {
        nds = pts;
        // for (int i = 0; i < nds.size(); i++) {
        // System.out.print(nds.get(i).p.x + " ");
        // }
        // System.out.println();
        results = new ArrayList<Node>();
        Collections.sort(nds, new SortByX());
        // for (int i = 0; i < nds.size(); i++) {
        // System.out.print(nds.get(i).p.x + " ");
        // }
        // System.out.println();
        for (int i = 0; i < nds.size(); i++) {
            nds.get(i).min = i;
            nds.get(i).max = i;
        }
        rooot = makeTree(0, nds.size() - 1, true);
        Collections.sort(nds, new SortByX());
        // System.out.println(rooot.left_child.p.x);
        // for (int i = 0; i < nds.size(); i++) {
        // System.out.print(nds.get(i).p.x + " ");
        // }
        // System.out.println();
        // iterate(rooot);
        // System.out.println();
    }

    Node makeTree(int min_index, int max_index, boolean isX) {
        List<Node> sub_nds = nds.subList(min_index, max_index + 1);
        // if(isX) {
        // for (int i = 0; i < sub_nds.size(); i++) {
        // System.out.println(sub_nds.get(i).p.x);
        // }
        // System.out.println();
        // }
        if (!isX) {
            Collections.sort(sub_nds, new SortByY());
        }
        Queue<Node> nodes = new LinkedList<Node>(sub_nds);
        // if(isX) {
        // for (int i = 0; i < nodes.size(); i++) {
        // System.out.println(nodes.peek().p.x);
        // nodes.add(nodes.poll());
        // }
        // System.out.println();
        // }
        while (!nodes.isEmpty()) {
            boolean isOdd = false;
            int l = nodes.size();
            if (l % 2 == 1 && l != 1)
                isOdd = true;
            if (l == 1)
                return nodes.peek();
            for (int i = 0; i < l; i += 2) {
                if (isOdd && i == l - 1) {
                    nodes.add(nodes.poll());
                    continue;
                }
                Node tmp1 = nodes.poll();
                Node tmp2 = nodes.poll();
                Node tmp = new Node(tmp1, tmp2);
                if (isX) {
                    tmp.min = tmp1.min;
                    tmp.max = tmp2.max;
                }
                tmp.p = new Point();
                if (isX) {
                    tmp.y_tree = makeTree(tmp.min, tmp.max, false);
                    tmp.p.x = findNodeVal(tmp, isX);
                } else {
                    tmp.p.y = findNodeVal(tmp, isX);
                }
                nodes.add(tmp);
            }
        }
        return null;
    }

    void iterate(Node root) {
        Queue<Node> q = new LinkedList<Node>();
        q.add(root);
        while (!q.isEmpty()) {
            if (q.peek().left_child != null) {
                q.add(q.peek().left_child);
                q.add(q.peek().right_child);
                System.out.println(q.peek().min + " " + q.peek().max);
                iterate_y(q.poll().y_tree);
                System.out.println();
            } else
                q.poll();
        }
    }

    void iterate_y(Node root) {
        Queue<Node> q = new LinkedList<Node>();
        q.add(root);
        while (!q.isEmpty()) {
            if (q.peek().left_child != null) {
                q.add(q.peek().left_child);
                q.add(q.peek().right_child);
            }
            System.out.print(q.poll().p.y + " ");
        }
    }

    double findNodeVal(Node n, boolean isX) {
        n = n.left_child;
        while (n.right_child != null) {
            n = n.right_child;
        }
        if (isX)
            return n.p.x;
        return n.p.y;
    }

    void search_by_x(Rect rect, Node root) {
        if (root.p.x < rect.x1 && root.y_tree != null) {
            search_by_x(rect, root.right_child);
        }
        if (root.p.x > rect.x2 && root.y_tree != null) {
            search_by_x(rect, root.left_child);
        }
        if (root.p.x >= rect.x1 && root.p.x <= rect.x2) {
            if (root.y_tree == null) {
                if (root.p.y >= rect.y1 && root.p.y <= rect.y2) {
                    results.add(root);
                }
                return;
            }
            if (nds.get(root.min).p.x >= rect.x1 && nds.get(root.max).p.x <= rect.x2) {
                search_by_y(rect, root.y_tree);
            } else {
                search_by_x(rect, root.left_child);
                search_by_x(rect, root.right_child);
            }
        }
    }

    void search_by_y(Rect rect, Node root) {
        if (root.p.y < rect.y1 && root.left_child != null) {
            search_by_y(rect, root.right_child);
        }
        if (root.p.y > rect.y2 && root.left_child != null) {
            search_by_y(rect, root.left_child);
        }
        if (root.p.y >= rect.y1 && root.p.y <= rect.y2) {
            if (root.left_child == null && root.right_child == null) {
                results.add(root);
            } else {
                search_by_y(rect, root.left_child);
                search_by_y(rect, root.right_child);
            }
        }
    }

    public static void main(String[] args) {
        // Scanner in = new Scanner(System.in);
        FastIO in = new FastIO();
        PrintWriter pw = new PrintWriter(System.out);
        int n = in.nextInt();
        List<Node> pts = new ArrayList<Node>();
        List<Rect> rects = new ArrayList<Rect>();
        for (int i = 0; i < n; i++) {
            Point p = new Point();
            p.x = in.nextDouble();
            Node tmp = new Node(p);
            pts.add(tmp);
        }
        for (int i = 0; i < n; i++) {
            double y = in.nextDouble();
            pts.get(i).p.y = y;
        }
        int m = in.nextInt();
        for (int i = 0; i < m; i++) {
            Rect r = new Rect();
            r.x1 = in.nextDouble();
            r.y1 = in.nextDouble();
            r.x2 = in.nextDouble();
            r.y2 = in.nextDouble();
            rects.add(r);
        }
        RangeTree rt = new RangeTree(pts);
        for (int i = 0; i < rects.size(); i++) {
            rt.search_by_x(rects.get(i), rt.rooot);
            if (rt.results.isEmpty()) {
                pw.print("None");
            }
            Collections.sort(rt.results, new SortByY());
            for (int j = 0; j < rt.results.size(); j++) {
                if (rt.results.get(j).p.x % 1 == 0)
                    pw.print((int) rt.results.get(j).p.x + " ");
                else
                    pw.print(rt.results.get(j).p.x + " ");
            }
            if (!rt.results.isEmpty()) {
                pw.println();
            }
            for (int j = 0; j < rt.results.size(); j++) {
                if (rt.results.get(j).p.y % 1 == 0)
                    pw.print((int) rt.results.get(j).p.y + " ");
                else
                    pw.print(rt.results.get(j).p.y + " ");
            }
            rt.results.clear();
            pw.println();
        }
        pw.flush();
    }

}

class Node {
    Node left_child;
    Node right_child;
    Node y_tree;
    Point p;
    int min, max;

    Node(Node n1, Node n2) {
        left_child = n1;
        right_child = n2;
        y_tree = null;
    }

    Node(Point point) {
        p = point;
        left_child = null;
        right_child = null;
        y_tree = null;
    }
}

class Point {
    double x;
    double y;
}

class Rect {
    double x1, y1, x2, y2;
}

class SortByX implements Comparator<Node> {

    @Override
    public int compare(Node arg0, Node arg1) {
        if (arg0.p.x > arg1.p.x)
            return 1;
        if (arg0.p.x < arg1.p.x)
            return -1;
        return 0;
    }

}

class SortByY implements Comparator<Node> {

    @Override
    public int compare(Node arg0, Node arg1) {
        if (arg0.p.y > arg1.p.y)
            return 1;
        if (arg0.p.y < arg1.p.y)
            return -1;
        return 0;
    }

}

class FastIO {
    BufferedReader br;
    StringTokenizer st;

    public FastIO() {
        br = new BufferedReader(new InputStreamReader(System.in));
    }

    String next() {
        while (st == null || !st.hasMoreElements()) {
            try {
                st = new StringTokenizer(br.readLine());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return st.nextToken();
    }

    int nextInt() {
        return Integer.parseInt(next());
    }

    double nextDouble() {
        return Double.parseDouble(next());
    }

}