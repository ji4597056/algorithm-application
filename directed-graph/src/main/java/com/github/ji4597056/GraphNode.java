package com.github.ji4597056;

/**
 * @author Jeffrey
 * @since 2018/05/08 18:39
 */
public class GraphNode<T> {

    private int id;

    private T content;

    private int weight;

    public GraphNode(int id, T content, int weight) {
        this.id = id;
        this.content = content;
        this.weight = weight;
    }

    public GraphNode(int id, T content){
        this.id = id;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
