package com.stardisblue.ast;

import com.stardisblue.ast.info.MethodDeclarationInfo;
import com.stardisblue.ast.info.MethodInvocationInfo;
import com.stardisblue.ast.info.TypeDeclarationInfo;
import com.stardisblue.ast.structure.Cluster;
import com.stardisblue.ast.structure.ClusterManager;
import com.stardisblue.ast.structure.Graph;
import com.stardisblue.ast.structure.Matrix;
import com.stardisblue.functional.TriConsumer;
import com.stardisblue.logging.Logger;

import java.util.*;
import java.util.function.Function;

public class Compute {
    /**
     * Get all the existing packages of the array of packages.
     * <p>
     * Note that the list of {@link TypeDeclarationInfo} needs to be in a sorted order (eg: a.A, a.b.I, a.c.O, a.c.J)
     *
     * @param typeDeclarationInfos array of objects decorating a {@link TypeDeclarationInfo}
     * @return the list of packages
     */
    public static List<String> packages(List<TypeDeclarationInfo> typeDeclarationInfos) {
        String lastPackageName = "";
        List<String> packages = new ArrayList<>();
        for (TypeDeclarationInfo typeDeclarationInfo : typeDeclarationInfos) {
            String packageName = typeDeclarationInfo.getPackageName();

            if (!packageName.equals(lastPackageName)) {
                packages.add(packageName);
                lastPackageName = packageName;
            }
        }

        return packages;
    }


    /**
     * Generates a an object representing the method call graph
     *
     * @param methods list of method decorators
     * @return an object representing a method call graph
     */
    public static Graph methodGraph(List<MethodDeclarationInfo> methods) {
        return graph(methods,
                     MethodDeclarationInfo::getMethodCalls,
                     MethodDeclarationInfo::getShortWithParamTypes,
                     MethodInvocationInfo::getShortWithParamTypes);
    }

    /**
     * Generates an object representing the class call graph
     *
     * @param methods list of methodDecorators
     * @return an object representing a class call graph
     */
    public static Graph classGraph(List<MethodDeclarationInfo> methods) {
        return graph(methods,
                     MethodDeclarationInfo::getMethodCalls,
                     (m) -> m.getParent().getName(),
                     MethodInvocationInfo::getClassType);
    }

    /**
     * Creates a graph structure iterating over parent and child using keynode and valuenode as references
     *
     * @param parent       Parent list
     * @param child        invoked foreach parent to retrieve the childrens
     * @param parentString invoked foreach parent to get the parent string value
     * @param childString  invoked foreach child to get the child string value
     * @param <T>
     * @param <U>
     * @return
     */
    public static <T, U> Graph graph(List<T> parent,
                                     Function<T, List<U>> child,
                                     Function<T, String> parentString,
                                     Function<U, String> childString) {
        Graph graph = new Graph();

        for (T caller : parent) {
            // there are people who call
            String callerString = parentString.apply(caller);

            int callerId;
            if (graph.has(callerString)) {// we have her in our phonebook
                callerId = graph.get(callerString);// we get her number
                graph.belongs(callerId, true); // she belongs to us because she's the caller
            } else {// she does not exist in our phonebook
                callerId = graph.nextId();// so we give her a number
                graph.belongs(true);// she belongs to us because she's the caller
                graph.beginCount(0); // we can begin to count the number of callees she has
                graph.save(callerString, callerId); // we add her to our phonebook
            }

            // we her and the people she called together
            HashSet<Integer> callees = graph.getLinkIds().computeIfAbsent(callerId, (k) -> new HashSet<>());

            for (U callee : child.apply(caller)) {
                // the ones who are called
                String calleeString = childString.apply(callee);

                int calleeId;
                if (graph.has(calleeString)) { // we have him in our phonebook
                    calleeId = graph.get(calleeString); // so we get the phone number
                    graph.incrementCount(calleeId); // the called is being called once more
                } else {// we do not have him in our phonebook
                    calleeId = graph.nextId(); // so we get him a number
                    graph.belongs(false); // he does not belong to us
                    graph.beginCount(1); // he was at least called by the caller
                    graph.save(calleeString, calleeId); // we save this guy's number
                }

                callees.add(calleeId); // we add this person to the list of called people
            }
        }

        return graph;
    }

    /**
     * Returns a list of json objects representing nodes
     *
     * @param nodeIds          the ids of the methods
     * @param belongsToProject if the node belongs to the repository
     * @return an array containing a node represented as a json object
     */
    public static ArrayList<String> graphNodes(HashMap<String, Integer> nodeIds, ArrayList<Boolean> belongsToProject) {
        ArrayList<String> nodes = new ArrayList<>(nodeIds.size());

        for (Map.Entry<String, Integer> nodeEntry : nodeIds.entrySet()) {
            // the json has an id, a name and if he belongs to the project
            nodes.add("{\"id\":" + nodeEntry.getValue() + ", " +
                              "\"name\": \"" + nodeEntry.getKey() + "\", " +
                              "\"own\": " + (belongsToProject.get(nodeEntry.getValue()) ? "true" : "false") + "}");
        }

        return nodes;
    }

    /**
     * Returns an array representing all the links as json objects
     *
     * @param linkIds      optimized hashmap representing all the links
     * @param countParents number of source nodes for a given targetNodeId
     * @return an array containing a links represented as json object
     */
    public static List<String> graphLinks(HashMap<Integer, HashSet<Integer>> linkIds, ArrayList<Integer> countParents) {
        ArrayList<String> links = new ArrayList<>();

        for (Map.Entry<Integer, HashSet<Integer>> linkEntry : linkIds.entrySet()) {
            int callerId = linkEntry.getKey();

            for (int calleeId : linkEntry.getValue()) {
                int parentCount = countParents.get(calleeId);
                // the more the callee was called, the less he weights
                float weight = (float) (1 / (0.5 * parentCount + 0.5));

                links.add("{\"source\":" + callerId + ", " +
                                  "\"target\": " + calleeId + ", " +
                                  "\"str\": " + weight + "}");
            }
        }

        return links;
    }


    /**
     * Group clusters using hierarchicClustering and the Matrix to detect the closest entities.
     *
     * @param defaultMatrix the reference matrix
     * @return the clusterTree;
     */
    public static Cluster<String> hierarchicClustering(Matrix defaultMatrix) {

        // reindexing the clusters
        ArrayList<Cluster<String>> clusters = new ArrayList<>(defaultMatrix.getNames().length);
        for (String className : defaultMatrix.getNames()) {
            clusters.add(new Cluster<>(clusters.size(), className));
        }

        //setting up the clusterManager
        ClusterManager<String> clusterManager =
                new ClusterManager<>(clusters, defaultMatrix.getMatrix(), defaultMatrix.getTotal());

        // making the tree
        while (clusterManager.getClusters().size() > 1) {
            int[] closestPair = clusterManager.getClosestPairId();
            clusterManager.initSimilarity(closestPair[0], closestPair[1]);
            clusterManager.updateMatrix(closestPair[0], closestPair[1]);
        }

        return clusterManager.getClusters().get(0);
    }

    /**
     * Iterates over methods to extract all the classnames present in classes.
     * <p>
     * Counts the coupling ratio between two classes.
     * <p>
     * For classes named A and B, takes all the method invocations of A in B and B in A, sum it and returns a matrix representing this association.
     *
     * @param classes list of classes
     * @param methods list of methods
     * @return a matrix representing all the coupling between the classes
     */
    public static Matrix classCoupling(List<TypeDeclarationInfo> classes,
                                       List<MethodDeclarationInfo> methods) {

        ArrayList<String> classNames = new ArrayList<>(classes.size());

        for (TypeDeclarationInfo aClass : classes) {
            classNames.add(aClass.getFullName());
        }

        // we extract classnames
        Matrix matrix = new Matrix(classNames);

        for (MethodDeclarationInfo callee : methods) {
            for (MethodInvocationInfo caller : callee.getMethodCalls()) {
                matrix.increment(callee.getParent().getFullName(), caller.getClassFullName());
            }
        }

        matrix.generateTable();

        return matrix;
    }

    public static List<String> dendrogramNodes(int nodeQuantity, Cluster<String> cluster) {
        LinkedList<Cluster<String>> inputList = new LinkedList<>();
        inputList.add(cluster);

        return queueIteration(inputList, (queue, item, list) -> {
            int id = item.getName();
            String name;

            if (item.isLeaf()) {
                name = item.getObject();
            } else {
                name = String.valueOf(item.getSimilarity());

                queue.add(item.getFirst());
                queue.add(item.getLast());
            }

            list.add("{\"id\":" + id + ", " + "\"name\": \"" + name + "\", " + "\"own\": " + true + "}");
        });
    }

    public static List<String> dendrogramLinks(Cluster<String> cluster) {
        LinkedList<Cluster<String>> inputList = new LinkedList<>();
        inputList.add(cluster);

        return queueIteration(inputList, (queue, item, output) -> {
            if (item.isLeaf()) {
                return;
            }

            Cluster<String> first = item.getFirst();
            Cluster<String> last = item.getLast();

            queue.add(first);
            queue.add(last);

            output.add("{\"source\":" + item.getName() + ", " +
                               "\"target\": " + first.getName() + ", " + "\"str\": " + 1 + "}");
            output.add("{\"source\":" + item.getName() + ", " +
                               "\"target\": " + last.getName() + ", " + "\"str\": " + 1 + "}");
        });
    }

    /**
     * Separes the cluster into partition depending on the similarity between parent and child clusters
     *
     * @param cluster the cluster to partition
     * @return a list of partitions
     */
    public static List<Cluster<String>> clusterSelection(Cluster<String> cluster) {
        LinkedList<Cluster<String>> inputList = new LinkedList<>();
        inputList.add(cluster);

        return queueIteration(inputList, (queue, item, list) -> {
            if (item.isLeaf()) {
                return;
            }

            Cluster<String> first = item.getFirst();
            Cluster<String> last = item.getLast();

            if (item.getSimilarity() > (first.getSimilarity() + last.getSimilarity()) / 2) {
                list.add(item);
            } else {
                if (!first.isLeaf()) {
                    queue.add(first);
                }
                if (!last.isLeaf()) {
                    queue.add(last);
                }
            }
        });
    }

    public static <T, R> List<R> queueIteration(Queue<T> queue, TriConsumer<Queue<T>, T, List<R>> operation) {
        List<R> output = new ArrayList<>();

        while (!queue.isEmpty()) {
            T itemQueue = queue.remove();

            operation.apply(queue, itemQueue, output);
        }

        return output;
    }
}
