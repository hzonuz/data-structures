import java.util.Scanner;

public class SkipList {
    Node head;
    Node tail;
    int MAX_LEVEL;
    double p;

    public SkipList(int n, double p) {
        head = new Node(Integer.MIN_VALUE);
        tail = new Node(Integer.MAX_VALUE);
        head.next = tail;
        tail.prev = head;
        MAX_LEVEL = n;
        this.p = p;
        for (int i = MAX_LEVEL; i > 0; i--) {
            head.up = new Node(Integer.MIN_VALUE);
            head.up.down = head;
            head = head.up;
            tail.up = new Node(Integer.MAX_VALUE);
            tail.up.down = tail;
            tail = tail.up;
            head.next = tail;
            tail.prev = head;
        }
    }

    private boolean isGoUp() {
        double tmp = Math.random();
        return tmp < p;
    }

    public void insert(int x) {
        Node tmp = new Node(x);
        Node curNode = head;
        for (int i = MAX_LEVEL; i > 0; i--) {
            while (curNode.next.key < tmp.key) {
                curNode = curNode.next;
            }
            if(i == 1)
                break;
            curNode = curNode.down;
        }
        curNode.next.prev = tmp;
        tmp.next = curNode.next;
        tmp.prev = curNode;       
        curNode.next = tmp;
        for (int i = 0; (i <= MAX_LEVEL) && isGoUp(); i++) {
            while (curNode.up == null) {
                curNode = curNode.prev;
            }
            tmp.up = new Node(x);
            tmp.up.down = tmp;
            tmp = tmp.up;
            curNode = curNode.up;
            curNode.next.prev = tmp;
            tmp.next = curNode.next;
            tmp.prev = curNode;            
            curNode.next = tmp;
        }
    }

    public Node search(int x) {
        Node curNode = head;
        for (int i = MAX_LEVEL; i > 0; i--) {
            while (curNode.next.key <= x) {
                curNode = curNode.next;
                if (x == curNode.key)
                    return curNode;
            }
            curNode = curNode.down;
        }
        return null;
    }

    public void delete(int x) {
        Node curNode = search(x);
        if (curNode == null) {
            System.out.println("error");
            return;
        }
        while (curNode != null) {
            while (curNode != null) {
                curNode.prev.next = curNode.next;
                curNode.next.prev = curNode.prev;
                curNode = curNode.down;
            }
            curNode = search(x);
        }
    }

    public void print() {
        Node tmp = head;
        for (int i = MAX_LEVEL; i > 1; i--) {
            tmp = tmp.down;
        }
        if (tmp.next.key == Integer.MAX_VALUE) {
            System.out.println("empty");
            return;
        }
        tmp = tmp.next;
        while (tmp.key != Integer.MAX_VALUE) {
            System.out.print(tmp.key + " ");
            tmp = tmp.next;
        }
        System.out.println();
    }
    public static void main(String[] args) {
        SkipList sl = new SkipList(10, 0.25);
        Scanner in = new Scanner(System.in);
        String cmd = "";
        int n = 0;
        while (in.hasNext()) {
            cmd = in.next();
            switch (cmd.charAt(0)) {
                case 'I':
                    n = in.nextInt();
                    sl.insert(n);
                    break;
                case 'S':
                    n = in.nextInt();
                    Node tmp = sl.search(n);
                    if(tmp == null)
                        System.out.println("false");
                    else
                        System.out.println("true"); 
                    break;
                case 'D':
                    n = in.nextInt();
                    sl.delete(n);
                    break;
                case 'P':
                    sl.print();
                    break;
                default:
                    break;
            }
        }
        in.close();
    }

}

class Node {
    Node up = null;
    Node down = null;
    Node next = null;
    Node prev = null;
    int key;

    Node(int x) {
        this.key = x;
    }
}
