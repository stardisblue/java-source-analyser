package com.stardisblue.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Display {

    /**
     * Displays the title
     */
    public static void title(String title) {
        markdown("# " + title);
    }

    /**
     * Displays a title and a content
     *
     * @param title   title of the content
     * @param content content
     */
    public static void markdown(String title, String content) {
        markdownTitle2(title);
        markdown(content);
    }

    /**
     * Displays the content
     *
     * @param content string to display
     */
    public static void markdown(String content) {
        System.out.println(content);
        System.out.println();
    }

    public static <T> void markdown(String name, List<T> array, Function<T, String> display) {
        markdownTitle2(name);
        markdownCount(name, array.size());
        markdownList(array, display);
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
        markdownTitle2(percent + "% (" + filtered.size() + "/" + originalSize +
                               ") of " + type + " With Most " + filter);
        markdownCount(type, filtered.size());
        markdownList(filtered, display);
    }


    private static void markdownTitle2(String title) {
        markdown("## " + title);
    }

    private static void markdownCount(String name, int count) {
        markdown("> Number of " + name.toLowerCase() + " : " + count);
    }

    private static <T> void markdownList(List<T> array, Function<T, String> display) {
        for (T item : array) {
            System.out.println("- " + display.apply(item));
        }

        System.out.println();
    }

    public static void json(ArrayList<String> nodes, List<String> links) {
        markdownTitle2("Json Graph");
        System.out.println("{\"nodes\":[" + String.join(",", nodes) + "], " +
                                   "\"links\":[" + String.join(",", links) + "]}");
    }
}
