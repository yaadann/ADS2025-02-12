package by.it.group410901.kovalevich.lesson12;

import java.util.*;

public class MyRbMap implements SortedMap<Integer, String> {

    private static final boolean RED = true, BLACK = false;

    private static final class Node {
        int k;
        String v;
        Node l, r, p;
        boolean c = RED;
        Node(int k, String v) { this.k = k; this.v = v; }
    }

    private Node root;
    private int size;

    private static boolean color(Node n) { return n == null ? BLACK : n.c; }
    private static void setColor(Node n, boolean c) { if (n != null) n.c = c; }
    private static Node parent(Node n) { return n == null ? null : n.p; }
    private static Node grand(Node n) { Node p = parent(n); return p == null ? null : p.p; }
    private static Node sibling(Node n) {
        Node p = parent(n);
        if (p == null) return null;
        return n == p.l ? p.r : p.l;
    }
    private static Node min(Node n) { while (n.l != null) n = n.l; return n; }
    private static Node max(Node n) { while (n.r != null) n = n.r; return n; }

    private void rotateLeft(Node x) {
        Node y = x.r;
        x.r = y.l;
        if (y.l != null) y.l.p = x;
        y.p = x.p;
        if (x.p == null) root = y;
        else if (x == x.p.l) x.p.l = y; else x.p.r = y;
        y.l = x;
        x.p = y;
    }

    private void rotateRight(Node x) {
        Node y = x.l;
        x.l = y.r;
        if (y.r != null) y.r.p = x;
        y.p = x.p;
        if (x.p == null) root = y;
        else if (x == x.p.r) x.p.r = y; else x.p.l = y;
        y.r = x;
        x.p = y;
    }

    private Node find(int k) {
        Node n = root;
        while (n != null) {
            if (k < n.k) n = n.l;
            else if (k > n.k) n = n.r;
            else return n;
        }
        return null;
    }

    @Override
    public String put(Integer key, String value) {
        if (key == null) throw new NullPointerException();
        if (root == null) {
            root = new Node(key, value);
            root.c = BLACK;
            size = 1;
            return null;
        }
        Node cur = root, p = null;
        int cmp = 0;
        while (cur != null) {
            p = cur;
            if (key < cur.k) { cur = cur.l; cmp = -1; }
            else if (key > cur.k) { cur = cur.r; cmp = 1; }
            else { String old = cur.v; cur.v = value; return old; }
        }
        Node n = new Node(key, value);
        n.p = p;
        if (cmp < 0) p.l = n; else p.r = n;
        insertFix(n);
        size++;
        return null;
    }

    private void insertFix(Node x) {
        while (color(parent(x)) == RED) {
            Node p = parent(x), g = grand(x);
            if (p == g.l) {
                Node u = g.r;
                if (color(u) == RED) {
                    setColor(p, BLACK); setColor(u, BLACK); setColor(g, RED);
                    x = g;
                } else {
                    if (x == p.r) { x = p; rotateLeft(x); p = parent(x); g = grand(x); }
                    setColor(p, BLACK); setColor(g, RED); rotateRight(g);
                }
            } else {
                Node u = g.l;
                if (color(u) == RED) {
                    setColor(p, BLACK); setColor(u, BLACK); setColor(g, RED);
                    x = g;
                } else {
                    if (x == p.l) { x = p; rotateRight(x); p = parent(x); g = grand(x); }
                    setColor(p, BLACK); setColor(g, RED); rotateLeft(g);
                }
            }
        }
        setColor(root, BLACK);
    }

    @Override
    public String remove(Object key) {
        if (!(key instanceof Integer)) return null;
        Node z = find((Integer) key);
        if (z == null) return null;
        String old = z.v;
        delete(z);
        size--;
        return old;
    }

    private void transplant(Node u, Node v) {
        if (u.p == null) root = v;
        else if (u == u.p.l) u.p.l = v;
        else u.p.r = v;
        if (v != null) v.p = u.p;
    }

    private void delete(Node z) {
        Node y = z, x;
        boolean yOrigColor = y.c;
        if (z.l == null) {
            x = z.r;
            transplant(z, z.r);
        } else if (z.r == null) {
            x = z.l;
            transplant(z, z.l);
        } else {
            y = min(z.r);
            yOrigColor = y.c;
            x = y.r;
            if (y.p == z) {
                if (x != null) x.p = y;
            } else {
                transplant(y, y.r);
                y.r = z.r; y.r.p = y;
            }
            transplant(z, y);
            y.l = z.l; y.l.p = y;
            y.c = z.c;
        }
        if (yOrigColor == BLACK) deleteFix(x, parent(x));
    }

    private void deleteFix(Node x, Node px) {
        while ((x != root) && color(x) == BLACK) {
            Node p = px != null ? px : parent(x);
            if (x == (p != null ? p.l : null)) {
                Node w = p.r;
                if (color(w) == RED) { setColor(w, BLACK); setColor(p, RED); rotateLeft(p); w = p.r; }
                if (color(w.l) == BLACK && color(w.r) == BLACK) {
                    setColor(w, RED); x = p; px = parent(x);
                } else {
                    if (color(w.r) == BLACK) { setColor(w.l, BLACK); setColor(w, RED); rotateRight(w); w = p.r; }
                    setColor(w, color(p)); setColor(p, BLACK); setColor(w.r, BLACK); rotateLeft(p); x = root; break;
                }
            } else {
                Node w = p.l;
                if (color(w) == RED) { setColor(w, BLACK); setColor(p, RED); rotateRight(p); w = p.l; }
                if (color(w.l) == BLACK && color(w.r) == BLACK) {
                    setColor(w, RED); x = p; px = parent(x);
                } else {
                    if (color(w.l) == BLACK) { setColor(w.r, BLACK); setColor(w, RED); rotateLeft(w); w = p.l; }
                    setColor(w, color(p)); setColor(p, BLACK); setColor(w.l, BLACK); rotateRight(p); x = root; break;
                }
            }
        }
        if (x != null) setColor(x, BLACK);
    }

    @Override
    public String get(Object key) {
        if (!(key instanceof Integer)) return null;
        Node n = find((Integer) key);
        return n == null ? null : n.v;
    }

    @Override
    public boolean containsKey(Object key) {
        if (!(key instanceof Integer)) return false;
        return find((Integer) key) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        return containsValue(root, (String) value);
    }

    private boolean containsValue(Node n, String v) {
        if (n == null) return false;
        if (v == null ? n.v == null : v.equals(n.v)) return true;
        return containsValue(n.l, v) || containsValue(n.r, v);
    }

    @Override
    public int size() { return size; }

    @Override
    public void clear() { root = null; size = 0; }

    @Override
    public boolean isEmpty() { return size == 0; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        inorder(root, sb);
        sb.append('}');
        return sb.toString();
    }

    private void inorder(Node n, StringBuilder sb) {
        if (n == null) return;
        inorder(n.l, sb);
        if (sb.length() > 1) sb.append(", ");
        sb.append(n.k).append('=').append(n.v);
        inorder(n.r, sb);
    }

    @Override
    public SortedMap<Integer, String> headMap(Integer toKey) {
        MyRbMap m = new MyRbMap();
        fillHead(root, toKey, m);
        return m;
    }

    private void fillHead(Node n, int to, MyRbMap m) {
        if (n == null) return;
        if (n.k < to) { fillHead(n.l, to, m); m.put(n.k, n.v); fillHead(n.r, to, m); }
        else fillHead(n.l, to, m);
    }

    @Override
    public SortedMap<Integer, String> tailMap(Integer fromKey) {
        MyRbMap m = new MyRbMap();
        fillTail(root, fromKey, m);
        return m;
    }

    private void fillTail(Node n, int from, MyRbMap m) {
        if (n == null) return;
        if (n.k >= from) { fillTail(n.l, from, m); m.put(n.k, n.v); fillTail(n.r, from, m); }
        else fillTail(n.r, from, m);
    }

    @Override
    public Integer firstKey() {
        if (root == null) throw new NoSuchElementException();
        return min(root).k;
    }

    @Override
    public Integer lastKey() {
        if (root == null) throw new NoSuchElementException();
        return max(root).k;
    }

    @Override
    public Comparator<? super Integer> comparator() { return null; }

    @Override
    public SortedMap<Integer, String> subMap(Integer fromKey, Integer toKey) {
        MyRbMap m = new MyRbMap();
        fillRange(root, fromKey, toKey, m);
        return m;
    }

    private void fillRange(Node n, int from, int to, MyRbMap m) {
        if (n == null) return;
        if (n.k >= from) fillRange(n.l, from, to, m);
        if (n.k >= from && n.k < to) m.put(n.k, n.v);
        if (n.k < to) fillRange(n.r, from, to, m);
    }

    @Override
    public Set<Entry<Integer, String>> entrySet() { throw new UnsupportedOperationException(); }

    @Override
    public Set<Integer> keySet() { throw new UnsupportedOperationException(); }

    @Override
    public Collection<String> values() { throw new UnsupportedOperationException(); }
}

