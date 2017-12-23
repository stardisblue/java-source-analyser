package com.stardisblue.ast.structure;

public class Cluster<T> {
    private Cluster<T> first;
    private Cluster<T> last;

    private int id;
    private T object;
    private double similarity;

    public Cluster(int id, T object) {
        this.id = id;
        first = null;
        last = null;
        this.object = object;
        similarity = -1;
    }

    Cluster(int id, Cluster<T> first, Cluster<T> last, int similarity) {
        this.id = id;
        this.first = first;
        this.last = last;
        object = null;
        this.similarity = similarity;
    }

    public boolean isLeaf() {
        return object != null;
    }

    public int getName() {
        return id;
    }

    public T getObject() {
        return object;
    }

    /**
     * Sets similarity only it has not been set before
     *
     * @param similarity the similarity value to set
     */
    public void initSimilarity(int similarity) {
        if (this.similarity == -1) {
            this.similarity = similarity;
        }
    }

    public Cluster<T> getLast() {
        return last;
    }

    public Cluster<T> getFirst() {
        return first;
    }

    public double getSimilarity() {
        return similarity;
    }
}
