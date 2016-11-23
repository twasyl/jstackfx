package io.twasyl.jstackfx.beans;

/**
 * @author Thierry Wasylczenko
 * @since jStackFX @@NEXT-VERSION@@
 */
public class Pair<K, V> {
    protected K value1;
    protected V value2;

    public Pair() {
    }

    public Pair(K value1, V value2) {
        this.value1 = value1;
        this.value2 = value2;
    }

    public K getValue1() {
        return value1;
    }

    public void setValue1(K value1) {
        this.value1 = value1;
    }

    public V getValue2() {
        return value2;
    }

    public void setValue2(V value2) {
        this.value2 = value2;
    }
}
