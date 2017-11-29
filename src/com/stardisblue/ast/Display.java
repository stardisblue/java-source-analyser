package com.stardisblue.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Display {

    /**
     * Displays the title
     */
    public static void title(String title) {
        display("# " + title);
    }

    /**
     * Displays a title and a content
     *
     * @param title   title of the content
     * @param content content
     */
    public static void display(String title, String content) {
        displayTitle2(title);
        display(content);
    }

    /**
     * Displays the content
     *
     * @param content string to display
     */
    public static void display(String content) {
        System.out.println(content);
        System.out.println();
    }

    public static <T> void display(String name, List<T> array, Function<T, String> display) {
        displayTitle2(name);
        displayCount(name, array.size());
        displayList(array, display);
    }


    /**
     * Will display the x% of classes with most filter, uses {@link Math#ceil(double)} to round up.
     *
     * @param type         Name of the Type
     * @param filter       name of the filter
     * @param filtered     filtered element list
     * @param originalSize original size of the list
     * @param percent      percentage wanted
     * @param display      value of the filtered element
     * @param <T>          Type of the item
     */
    public static <T> void withMost(String type, String filter, List<T> filtered,
                                    int originalSize, int percent,
                                    Function<T, String> display) {
        displayTitle2(percent + "% (" + filtered.size() + "/" + originalSize +
                              ") of " + type + " With Most " + filter);
        displayCount(type, filtered.size());
        displayList(filtered, display);
    }


    private static void displayTitle2(String title) {
        display("## " + title);
    }

    private static void displayCount(String name, int count) {
        display("> Number of " + name.toLowerCase() + " : " + count);
    }

    private static <T> void displayList(List<T> array, Function<T, String> display) {
        for (T item : array) {
            System.out.println("- " + display.apply(item));
        }

        System.out.println();
    }

    public static void json(ArrayList<String> nodes, List<String> links) {
        displayTitle2("Json Graph");
        System.out.println("{\"nodes\":[" + String.join(",", nodes) + "], " +
                                   "\"links\":[" + String.join(",", links) + "]}");
    }
}
