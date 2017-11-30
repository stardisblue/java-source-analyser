package com.stardisblue.ast;

import com.stardisblue.ast.decorator.MethodDeclarationDecorator;
import com.stardisblue.ast.decorator.MethodInvocationDecorator;
import com.stardisblue.ast.decorator.TypeDeclarationDecorator;
import com.stardisblue.ast.structure.Graph;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.ToIntFunction;

public class Compute {
    /**
     * Get all the existing packages of the array of packages.
     * <p>
     * Note that the list of {@link TypeDeclarationDecorator} needs to be in a sorted order (eg: a.A, a.b.I, a.c.O, a.c.J)
     *
     * @param typeDeclarationDecorators array of objects decorating a {@link TypeDeclarationDecorator}
     * @return the list of packages
     */
    public static List<String> packages(ArrayList<TypeDeclarationDecorator> typeDeclarationDecorators) {
        String lastPackageName = "";
        ArrayList<String> packages = new ArrayList<>();
        for (TypeDeclarationDecorator typeDeclarationDecorator : typeDeclarationDecorators) {
            String packageName = typeDeclarationDecorator.getPackageName();

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
     * Generates a an object representing the method call graph
     *
     * @param methods list of method decorators
     * @return an object representing a method call graph
     */
    public static Graph methodGraph(List<MethodDeclarationDecorator> methods) {
        Graph graph = new Graph();

        for (MethodDeclarationDecorator caller : methods) {
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

            for (MethodInvocationDecorator callee : caller.getMethodCalls()) {
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
}
