package com.stardisblue.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.ToIntFunction;

public class ListUtils {
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
     * @param intValue function used to get an item value
     * @param <T>   Type of an item
     * @return the sum of the array
     */
    public static <T> int sum(List<T> array, ToIntFunction<T> intValue) {
        int sum = 0;
        for (T t : array) {
            sum += intValue.applyAsInt(t);
        }

        return sum;
    }

    /**
     * Calculates the average of the array using a lambda function
     *
     * @param array     array of values
     * @param intValue function to get an item value
     * @param <T>       type of an item
     * @return the average of the array
     */
    public static <T> float average(List<T> array, ToIntFunction<T> intValue) {
        return sum(array, intValue) / array.size();
    }
}
