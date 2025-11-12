package by.it.group410901.kovalevich.lesson12;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedMap;

public class MySplayMap implements NavigableMap<Integer, String> {

    private static final class Node {
        int k;
        String v;
        Node l, r, p;
        Node(int k, String v) { this.k = k; this.v = v; }
    }

    private Node root;
    private int size;

    private static Node parent(Node n){ return n==null?null:n.p; }

    private void rotateLeft(Node x){
        Node y = x.r;
        x.r = y.l;
        if (y.l!=null) y.l.p = x;
        y.p = x.p;
        if (x.p==null) root = y;
        else if (x==x.p.l) x.p.l = y; else x.p.r = y;
        y.l = x;
        x.p = y;
    }

    private void rotateRight(Node x){
        Node y = x.l;
        x.l = y.r;
        if (y.r!=null) y.r.p = x;
        y.p = x.p;
        if (x.p==null) root = y;
        else if (x==x.p.r) x.p.r = y; else x.p.l = y;
        y.r = x;
        x.p = y;
    }

    private void splay(Node x){
        if (x==null) return;
        while (x.p!=null){
            Node p = x.p, g = p.p;
            if (g==null){
                if (x==p.l) rotateRight(p); else rotateLeft(p);
            } else if ((x==p.l) && (p==g.l)){
                rotateRight(g); rotateRight(p);
            } else if ((x==p.r) && (p==g.r)){
                rotateLeft(g); rotateLeft(p);
            } else if (x==p.r && p==g.l){
                rotateLeft(p); rotateRight(g);
            } else {
                rotateRight(p); rotateLeft(g);
            }
        }
    }

    private Node findNode(int key){
        Node cur = root, last = null;
        while (cur!=null){
            last = cur;
            if (key < cur.k) cur = cur.l;
            else if (key > cur.k) cur = cur.r;
            else return cur;
        }
        if (last!=null) splay(last);
        return null;
    }

    private Node subtreeMin(Node n){ while (n!=null && n.l!=null) n=n.l; return n; }
    private Node subtreeMax(Node n){ while (n!=null && n.r!=null) n=n.r; return n; }

    @Override
    public String put(Integer key, String value) {
        if (key==null) throw new NullPointerException();
        if (root==null){ root = new Node(key,value); size=1; return null; }
        Node cur = root, p = null;
        while (cur!=null){
            p = cur;
            if (key < cur.k) cur = cur.l;
            else if (key > cur.k) cur = cur.r;
            else { String old = cur.v; cur.v = value; splay(cur); return old; }
        }
        Node n = new Node(key,value);
        n.p = p;
        if (key < p.k) p.l = n; else p.r = n;
        splay(n);
        size++;
        return null;
    }

    @Override
    public String remove(Object key) {
        if (!(key instanceof Integer)) return null;
        Node n = findNode((Integer) key);
        if (n==null) return null;
        splay(n);
        String old = n.v;
        Node L = n.l, R = n.r;
        if (L!=null) L.p = null;
        if (R!=null) R.p = null;
        if (L==null) root = R;
        else {
            Node m = subtreeMax(L);
            splay(m);
            root.r = R;
            if (R!=null) R.p = root;
        }
        size--;
        return old;
    }

    @Override
    public String get(Object key) {
        if (!(key instanceof Integer)) return null;
        Node n = findNode((Integer) key);
        if (n!=null) splay(n);
        return n==null?null:n.v;
    }

    @Override
    public boolean containsKey(Object key) {
        if (!(key instanceof Integer)) return false;
        Node n = findNode((Integer) key);
        return n!=null && n.k==(Integer)key;
    }

    @Override
    public boolean containsValue(Object value) {
        return containsValue(root,(String)value);
    }

    private boolean containsValue(Node n, String v){
        if (n==null) return false;
        if (v==null? n.v==null : v.equals(n.v)) return true;
        return containsValue(n.l,v) || containsValue(n.r,v);
    }

    @Override
    public int size() { return size; }

    @Override
    public void clear() { root=null; size=0; }

    @Override
    public boolean isEmpty() { return size==0; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        inorder(root,sb);
        sb.append('}');
        return sb.toString();
    }

    private void inorder(Node n, StringBuilder sb){
        if (n==null) return;
        inorder(n.l,sb);
        if (sb.length()>1) sb.append(", ");
        sb.append(n.k).append('=').append(n.v);
        inorder(n.r,sb);
    }

    @Override
    public Comparator<? super Integer> comparator() { return null; }

    @Override
    public Integer firstKey() {
        if (root==null) throw new java.util.NoSuchElementException();
        Node m = subtreeMin(root);
        splay(m);
        return m.k;
    }

    @Override
    public Integer lastKey() {
        if (root==null) throw new java.util.NoSuchElementException();
        Node m = subtreeMax(root);
        splay(m);
        return m.k;
    }

    private Integer lowerKeyFrom(Node n, int k){
        Integer res = null;
        while (n!=null){
            if (k <= n.k){ n = n.l; }
            else { res = n.k; n = n.r; }
        }
        return res;
    }

    private Integer floorKeyFrom(Node n, int k){
        Integer res = null;
        while (n!=null){
            if (k < n.k){ n = n.l; }
            else { res = n.k; n = n.r; }
        }
        return res;
    }

    private Integer ceilingKeyFrom(Node n, int k){
        Integer res = null;
        while (n!=null){
            if (k > n.k){ n = n.r; }
            else { res = n.k; n = n.l; }
        }
        return res;
    }

    private Integer higherKeyFrom(Node n, int k){
        Integer res = null;
        while (n!=null){
            if (k >= n.k){ n = n.r; }
            else { res = n.k; n = n.l; }
        }
        return res;
    }

    @Override public Integer lowerKey(Integer key) { return lowerKeyFrom(root,key); }
    @Override public Integer floorKey(Integer key) { return floorKeyFrom(root,key); }
    @Override public Integer ceilingKey(Integer key) { return ceilingKeyFrom(root,key); }
    @Override public Integer higherKey(Integer key) { return higherKeyFrom(root,key); }

    @Override
    public SortedMap<Integer, String> headMap(Integer toKey) {
        MySplayMap m = new MySplayMap();
        collectHead(root,toKey,m);
        return m;
    }

    private void collectHead(Node n, int to, MySplayMap m){
        if (n==null) return;
        if (n.k < to){ collectHead(n.l,to,m); m.put(n.k,n.v); collectHead(n.r,to,m); }
        else collectHead(n.l,to,m);
    }

    @Override
    public SortedMap<Integer, String> tailMap(Integer fromKey) {
        MySplayMap m = new MySplayMap();
        collectTail(root,fromKey,m);
        return m;
    }

    private void collectTail(Node n, int from, MySplayMap m){
        if (n==null) return;
        if (n.k >= from){ collectTail(n.l,from,m); m.put(n.k,n.v); collectTail(n.r,from,m); }
        else collectTail(n.r,from,m);
    }

    @Override
    public NavigableMap<Integer, String> subMap(Integer fromKey, boolean fromInclusive, Integer toKey, boolean toInclusive) {
        MySplayMap m = new MySplayMap();
        collectRange(root,fromKey,fromInclusive,toKey,toInclusive,m);
        return m;
    }

    private void collectRange(Node n, int from, boolean fi, int to, boolean ti, MySplayMap m){
        if (n==null) return;
        if (n.k > from || (fi && n.k==from)) collectRange(n.l,from,fi,to,ti,m);
        boolean okL = n.k > from || (fi && n.k==from);
        boolean okR = n.k < to || (ti && n.k==to);
        if (okL && okR) m.put(n.k,n.v);
        if (n.k < to || (ti && n.k==to)) collectRange(n.r,from,fi,to,ti,m);
    }

    @Override
    public NavigableMap<Integer, String> headMap(Integer toKey, boolean inclusive) {
        return subMap(Integer.MIN_VALUE,true,toKey,inclusive);
    }

    @Override
    public NavigableMap<Integer, String> tailMap(Integer fromKey, boolean inclusive) {
        return subMap(fromKey,inclusive,Integer.MAX_VALUE,true);
    }

    @Override
    public NavigableMap<Integer, String> subMap(Integer fromKey, Integer toKey) {
        return subMap(fromKey,true,toKey,false);
    }

    @Override
    public Map.Entry<Integer, String> firstEntry() { throw new UnsupportedOperationException(); }
    @Override
    public Map.Entry<Integer, String> lastEntry() { throw new UnsupportedOperationException(); }
    @Override
    public Map.Entry<Integer, String> lowerEntry(Integer key) { throw new UnsupportedOperationException(); }
    @Override
    public Map.Entry<Integer, String> floorEntry(Integer key) { throw new UnsupportedOperationException(); }
    @Override
    public Map.Entry<Integer, String> ceilingEntry(Integer key) { throw new UnsupportedOperationException(); }
    @Override
    public Map.Entry<Integer, String> higherEntry(Integer key) { throw new UnsupportedOperationException(); }
    @Override
    public Map.Entry<Integer, String> pollFirstEntry() { throw new UnsupportedOperationException(); }
    @Override
    public Map.Entry<Integer, String> pollLastEntry() { throw new UnsupportedOperationException(); }
    @Override
    public NavigableSet<Integer> navigableKeySet() { throw new UnsupportedOperationException(); }
    @Override
    public NavigableSet<Integer> descendingKeySet() { throw new UnsupportedOperationException(); }
    @Override
    public NavigableMap<Integer, String> descendingMap() { throw new UnsupportedOperationException(); }

    @Override
    public Set<Entry<Integer, String>> entrySet() { throw new UnsupportedOperationException(); }
    @Override
    public Set<Integer> keySet() { throw new UnsupportedOperationException(); }
    @Override
    public Collection<String> values() { throw new UnsupportedOperationException(); }

    @Override
    public void putAll(Map<? extends Integer, ? extends String> m) {
        if (m==null) return;
        for (Entry<? extends Integer, ? extends String> e : m.entrySet()) put(e.getKey(), e.getValue());
    }
}

