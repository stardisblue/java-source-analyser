package com.stardisblue.ast;

import com.stardisblue.ast.info.MethodDeclarationInfo;
import com.stardisblue.ast.info.MethodInvocationInfo;
import com.stardisblue.ast.info.TypeDeclarationInfo;
import com.stardisblue.ast.structure.Graph;
import com.stardisblue.logging.Logger;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.ToIntFunction;

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
     * Return all the elements of the array that are the same as the reference
     *
     * @param array      list of elements to compare
     * @param reference  the reference
     * @param comparison the function to compare with
     * @param <T>        Type
     * @return a list of all the elements in array that return true when using comparison with the reference
     */
    public static <T> List<T> getSameAs(List<T> array, T reference, BiFunction<T, T, Boolean> comparison) {
        List<T> sames = new ArrayList<>();
        for (T item : array) {
            if (comparison.apply(item, reference)) sames.add(item);
        }

        return sames;
    }

    /**
     * Will return the x% items of the array after sorting them using comparator.
     * The number of elements is a percentage of the original array.
     * Uses {@link Math#ceil(double)} to round up.
     *
     * @param array      the reference array
     * @param percentage the percentage of classes to display
     * @param comparator Used to sort the reference array
     * @param <T>        Type of the sublist
     * @return A list containing the first items in sorted order (using comparator),
     * it's length is a percentage of the original array
     */
    public static <T> List<T> sortedTopSubList(List<T> array, int percentage, Comparator<? super T> comparator) {
        // we copy
        List<T> cArray = new ArrayList<>(array);

        // we sort
        cArray.sort(comparator);

        // we count
        float floatCount = cArray.size() * (float) percentage / 100;
        int printableQte = (int) Math.ceil(floatCount);

        // we trim
        return cArray.subList(0, printableQte);
    }


    /**
     * Get the items having value &gt; minimalValue.
     *
     * @param array        list of items
     * @param minimalValue minimal value
     * @param value        value to check
     * @param <T>          type of the elements
     * @return a list containing all the elements having a value &gt; minimalValue
     */
    public static <T> List<T> hasMoreThan(List<T> array, int minimalValue, ToIntFunction<T> value) {
        List<T> filtered = new ArrayList<>();

        for (T element : array) {
            if (value.applyAsInt(element) > minimalValue) {
                filtered.add(element);
            }
        }

        return filtered;
    }

    /**
     * Sort and intersects the lists using the comparator
     *
     * @param first      list
     * @param second     list
     * @param comparator used to sort and compare the lists
     * @param <T>        List type
     * @return the intersection of the lists
     */
    public static <T> List<T> intersect(List<T> first, List<T> second, Comparator<T> comparator) {
        List<T> cFirst = new ArrayList<>(first);
        List<T> cSecond = new ArrayList<>(second);
        cFirst.sort(comparator);
        cSecond.sort(comparator);

        return sortedIntersect(cFirst, cSecond, comparator);
    }

    /**
     * Intersects using comparator
     *
     * @param first      sorted list
     * @param second     sorted list
     * @param comparator used to compare the lists
     * @param <T>        List type
     * @return the intersection of the lists
     */
    public static <T> List<T> sortedIntersect(List<T> first, List<T> second, Comparator<T> comparator) {
        Iterator<T> iterF = first.iterator();
        Iterator<T> iterS = second.iterator();

        List<T> intersection = new ArrayList<>();

        if (iterF.hasNext() && iterS.hasNext()) {
            T elemF = iterF.next();
            T elemS = iterS.next();
            while (true) {
                int comparison = Integer.signum(comparator.compare(elemF, elemS));
                if (comparison == 0) {
                    intersection.add(elemF);

                    if (iterF.hasNext() && iterS.hasNext()) {
                        elemF = iterF.next();
                        elemS = iterS.next();
                    } else {
                        break;
                    }

                } else if (comparison == 1) {
                    if (iterS.hasNext()) {
                        elemS = iterS.next();
                    } else {
                        break;
                    }

                } else if (comparison == -1) {
                    if (iterF.hasNext()) {
                        elemF = iterF.next();
                    } else {
                        break;
                    }

                } else {
                    break;
                }
            }
        }


        return intersection;
    }


    /**
     * Iterates over array and extracts the elements using the extractor, adds all in a list and returns it
     *
     * @param array     list to extract from
     * @param extractor the extraction protocol
     * @param <T>       Type of the owning item
     * @param <U>       Type of the owned item
     * @return a concatened list of all the extracted elements
     */
    public static <T, U> List<U> extract(List<T> array, Function<T, List<U>> extractor) {
        List<U> extracted = new ArrayList<>();

        for (T item : array) {
            extracted.addAll(extractor.apply(item));
        }

        return extracted;
    }

    /**
     * Calculates the sum of the array using a lambda function
     *
     * @param array array to sum
     * @param toSum function used to get an item value
     * @param <T>   Type of an item
     * @return the sum of the array
     */
    public static <T> int sum(List<T> array, ToIntFunction<T> toSum) {
        int sum = 0;
        for (T t : array) {
            sum += toSum.applyAsInt(t);
        }

        return sum;
    }

    /**
     * Calculates the average of the array using a lambda function
     *
     * @param array     array of values
     * @param toAverage function to get an item value
     * @param <T>       type of an item
     * @return the average of the array
     */
    public static <T> float average(List<T> array, ToIntFunction<T> toAverage) {
        return sum(array, toAverage) / array.size();
    }

    /**
     * Generates a an object representing the method call graph
     *
     * @param methods list of method decorators
     * @return an object representing a method call graph
     */
    public static Graph methodGraph(List<MethodDeclarationInfo> methods) {
        Graph graph = new Graph();

        for (MethodDeclarationInfo caller : methods) {
            // there are people who call
            String callerString = caller.getShortWithParamTypes();

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

            HashSet<Integer> callees = new HashSet<>();// list of called people

            for (MethodInvocationInfo callee : caller.getMethodCalls()) {
                // the ones who are called
                String calleeString = callee.getShortWithParamTypes();

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

            graph.link(callerId, callees);// we her and the people she called together
        }

        return graph; // end of it :)
    }

    /**
     * Returns a list of json objects representing nodes
     *
     * @param nodeIds          the ids of the methods
     * @param belongsToProject if the node belongs to the repository
     * @return
     */
    public static ArrayList<String> graphNodes(HashMap<String, Integer> nodeIds,
                                               ArrayList<Boolean> belongsToProject) {
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
    public static List<String> graphLinks(HashMap<Integer, HashSet<Integer>> linkIds,
                                          ArrayList<Integer> countParents) {
        ArrayList<String> links = new ArrayList<>();

        for (Map.Entry<Integer, HashSet<Integer>> linkEntry : linkIds.entrySet()) {
            int callerId = linkEntry.getKey();
            Logger.println(callerId, Logger.DEBUG);

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

    public static Graph classGraph(List<MethodDeclarationInfo> methods) {
        Graph graph = new Graph();

        for (MethodDeclarationInfo caller : methods) {
            // there are people who call
            String callerString = caller.getParent().getName();

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

            HashSet<Integer> callees;// list of called people
            if (graph.getLinkIds().containsKey(callerId)) {
                callees = graph.getLinkIds().get(callerId);
            } else {
                callees = new HashSet<>();
            }

            for (MethodInvocationInfo callee : caller.getMethodCalls()) {
                // the ones who are called
                String calleeString = callee.getClassType();
                Logger.println("  - " + calleeString, Logger.DEBUG);

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

            graph.link(callerId, callees);// we her and the people she called together
        }

        return graph;

    }
}
