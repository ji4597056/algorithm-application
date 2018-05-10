package com.github.ji4597056;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.function.Consumer;

/**
 * @author Jeffrey
 * @since 2018/05/08 17:36
 */
public class DirectedGraph<T> {

    private Map<Integer, Vertex> graph;

    public DirectedGraph() {
        this.graph = new HashMap<>();
    }

    // 拓扑排序
    public List<Vertex> topoSort() {
        List<Vertex> result = new ArrayList<>();
        if (traverse(result::add)) {
            throw new RuntimeException("Graph has circle!");
        }
        return result;
    }

    // 判断是否有环
    public boolean hasCircle() {
        return traverse(null);
    }

    // 清空
    public void clear() {
        graph.clear();
    }

    // 遍历,返回是否有环,true=有环,false=无环
    private boolean traverse(Consumer<Vertex> consumer) {
        int count = 0;
        Queue<Vertex> queue = new PriorityQueue<>();
        // 扫描所有的顶点,将入度为0的顶点入队列
        Collection<Vertex> vertexs = getGraphValues();
        for (Vertex vertex : vertexs) {
            if (vertex.inDegree == 0) {
                queue.offer(vertex);
            }
        }
        Vertex v;
        while ((v = queue.poll()) != null) {
            if (consumer != null) {
                consumer.accept(v);
            }
            count++;
            for (Edge e : v.adjEdges) {
                if (--e.endVertex.inDegree == 0) {
                    queue.offer(e.endVertex);
                }
            }
        }
        return count != graph.size();
    }

    // 图中增加有向边
    public void add(GraphNode<T> from, GraphNode<T> to) {
        assert from != null;
        assert to != null;
        Vertex tVertex;
        Vertex fVertex;
        // update to-vertex
        if (!graph.containsKey(to.getId())) {
            tVertex = new Vertex(to.getId(), to.getWeight(), to.getContent());
            graph.put(tVertex.id, tVertex);
        }
        tVertex = graph.get(to.getId());
        tVertex.inDegree++;

        // update from-vertex
        if (!graph.containsKey(from.getId())) {
            fVertex = new Vertex(from.getId(), from.getWeight(), from.getContent());
            graph.put(fVertex.id, fVertex);
        }
        fVertex = graph.get(from.getId());
        addEdge(fVertex, tVertex);
    }

    // 图中增加顶点
    public void add(GraphNode<T> node) {
        assert node != null;
        if (!graph.containsKey(node.getId())) {
            graph.put(node.getId(), new Vertex(node.getId(), node.getWeight(), node.getContent()));
        }
    }

    // 顶点增加边
    private void addEdge(Vertex start, Vertex end) {
        Edge edge = new Edge(end);
        List<Edge> adjEdges = start.adjEdges;
        if (adjEdges == null) {
            adjEdges = new ArrayList<>();
        }
        adjEdges.add(edge);
    }

    // 获取图的浅拷贝
    private List<Vertex> getGraphValues() {
        return graph.values().stream()
            .collect(ArrayList::new, (vertices, vertex) -> {
                    try {
                        vertices.add(vertex.clone());
                    } catch (Exception e) {
                        throw new RuntimeException("Copy graph values error!");
                    }
                },
                ArrayList::addAll);
    }

    // 定点
    public class Vertex implements Cloneable, Comparable<Vertex> {

        // 顶点标识
        private int id;

        // 边
        private List<Edge> adjEdges;

        // 入度
        private int inDegree;

        // 权重
        private int weight;

        // 内容
        private T content;

        public Vertex(int id, T content) {
            this.id = id;
            this.inDegree = 0;
            this.weight = 0;
            this.adjEdges = new LinkedList<>();
            this.content = content;
        }

        public Vertex(int id, int weight, T content) {
            this.id = id;
            this.inDegree = 0;
            this.weight = weight;
            this.adjEdges = new LinkedList<>();
            this.content = content;
        }

        public int getId() {
            return id;
        }

        public List<Edge> getAdjEdges() {
            return adjEdges;
        }

        public int getInDegree() {
            return inDegree;
        }

        public int getWeight() {
            return weight;
        }

        public T getContent() {
            return content;
        }

        @Override
        public Vertex clone() throws CloneNotSupportedException {
            return (Vertex) super.clone();
        }

        @Override
        public int compareTo(Vertex o) {
            return o.weight - this.weight;
        }
    }

    // 边
    public class Edge {

        // 指向定点
        private Vertex endVertex;

        public Edge(Vertex endVertex) {
            this.endVertex = endVertex;
        }

        public Vertex getEndVertex() {
            return endVertex;
        }
    }
}
