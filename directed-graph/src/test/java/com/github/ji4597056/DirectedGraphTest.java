package com.github.ji4597056;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Jeffrey
 * @since 2018/05/10 10:03
 */
public class DirectedGraphTest {

    private DirectedGraph<String> g1 = new DirectedGraph<>();
    private DirectedGraph<String> g2 = new DirectedGraph<>();
    private DirectedGraph<String> g3 = new DirectedGraph<>();

    @Before
    public void build() {
        buidG1();
        buidG2();
        buidG3();
    }

    // build g1(no weight)
    //   --> b -->
    // a           d  |  e  | f --> g
    //   --> c -->
    private void buidG1() {
        GraphNode<String> a = new GraphNode<>(1, "a");
        GraphNode<String> b = new GraphNode<>(2, "b");
        GraphNode<String> c = new GraphNode<>(3, "c");
        GraphNode<String> d = new GraphNode<>(4, "d");
        GraphNode<String> e = new GraphNode<>(5, "e");
        GraphNode<String> f = new GraphNode<>(6, "f");
        GraphNode<String> g = new GraphNode<>(7, "g");
        g1.add(a, b);
        g1.add(a, c);
        g1.add(b, d);
        g1.add(c, d);
        g1.add(e);
        g1.add(f, g);
    }

    // build g2(weight)
    //      --> b(1) -->
    // a(3)              d(1)  |  e(1)  | f(2) --> g(3)
    //      --> c(2) -->
    private void buidG2() {
        GraphNode<String> a = new GraphNode<>(1, "a", 3);
        GraphNode<String> b = new GraphNode<>(2, "b", 1);
        GraphNode<String> c = new GraphNode<>(3, "c", 2);
        GraphNode<String> d = new GraphNode<>(4, "d", 1);
        GraphNode<String> e = new GraphNode<>(5, "e", 1);
        GraphNode<String> f = new GraphNode<>(6, "f", 2);
        GraphNode<String> g = new GraphNode<>(7, "g", 3);
        g2.add(a, b);
        g2.add(a, c);
        g2.add(b, d);
        g2.add(c, d);
        g2.add(e);
        g2.add(f, g);
    }

    // build g3(has circle)
    //   --> b -->
    // a           d --> a  |  e  | f --> g
    //   --> c -->
    private void buidG3() {
        GraphNode<String> a = new GraphNode<>(1, "a");
        GraphNode<String> b = new GraphNode<>(2, "b");
        GraphNode<String> c = new GraphNode<>(3, "c");
        GraphNode<String> d = new GraphNode<>(4, "d");
        GraphNode<String> e = new GraphNode<>(5, "e");
        GraphNode<String> f = new GraphNode<>(6, "f");
        GraphNode<String> g = new GraphNode<>(7, "g");
        g3.add(a, b);
        g3.add(a, c);
        g3.add(b, d);
        g3.add(c, d);
        g3.add(d, a);
        g3.add(e);
        g3.add(f, g);
    }

    @Test
    public void topoSort() {
        System.out.println("==========print g1=========");
        g1.topoSort().forEach(vertex -> System.out.print(vertex.getContent() + " "));
        System.out.println();
        System.out.println("==========print g2=========");
        g2.topoSort().forEach(vertex -> System.out.print(vertex.getContent() + " "));
    }

    @Test
    public void hasCircle() {
        Assert.assertTrue(!g1.hasCircle());
        Assert.assertTrue(!g2.hasCircle());
        Assert.assertTrue(g3.hasCircle());
    }
}