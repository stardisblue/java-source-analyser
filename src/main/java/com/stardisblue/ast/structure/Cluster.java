package com.stardisblue.ast.structure;

import java.util.ArrayList;
import java.util.List;

public class Cluster<T> {
    private Cluster first;
    private Cluster last;

    private int id;
    private List<T> objects;

    public Cluster(int id, T object) {
        this.id = id;
        first = null;
        last = null;
        this.objects = new ArrayList<>(1);
        this.objects.add(object);
    }

    Cluster(int id, Cluster<T> first, Cluster<T> last) {
        this.id = id;
        this.first = first;
        this.last = last;
        objects = null;
    }

    public boolean isLeaf() {
        return objects != null;
    }

    public int getName() {
        return id;
    }

    public List<T> getObjects() {
        return objects;
    }
}
