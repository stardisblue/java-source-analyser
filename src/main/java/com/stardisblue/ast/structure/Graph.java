package com.stardisblue.ast.structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Graph {
    private HashMap<String, Integer> nodeIds = new HashMap<>();
    private ArrayList<Boolean> belongsToProject = new ArrayList<>();
    private ArrayList<Integer> countParents = new ArrayList<>();
    private HashMap<Integer, HashSet<Integer>> linkIds = new HashMap<>();

    public boolean has(String node) {
        return nodeIds.containsKey(node);
    }

    public int get(String node) {
        return nodeIds.get(node);
    }

    public int nextId() {
        return nodeIds.size();
    }

    public HashMap<String, Integer> getIds() {
        return nodeIds;
    }

    public ArrayList<Boolean> getIsNodeInProject() {
        return belongsToProject;
    }

    public ArrayList<Integer> getSourceCount() {
        return countParents;
    }

    public HashMap<Integer, HashSet<Integer>> getLinkIds() {
        return linkIds;
    }

    public void belongs(boolean toProject) {
        belongsToProject.add(toProject);
    }

    public void belongs(int node, boolean setTo) {
        belongsToProject.set(node, setTo);
    }

    public void save(String node, int id) {
        nodeIds.put(node, id);
    }

    public void beginCount(int at) {
        countParents.add(at);
    }

    public void incrementCount(int calleeId) {
        countParents.set(calleeId, countParents.get(calleeId) + 1);
    }
}
