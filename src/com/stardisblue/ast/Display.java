package com.stardisblue.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class Display {

    /**
     * Displays the title
     */
    public static void title(String title) {
        paragraph("# " + title);
    }

    /**
     * Displays the title
     */
    public static void title(String title, int level) {
        paragraph(String.join("", Collections.nCopies(level, "#")) + " " + title);
    }

    /**
     * Displays the content
     *
     * @param content string to display
     */
    public static void paragraph(String content) {
        System.out.println(content);
        System.out.println();
    }

    public static <T> void list(String name, List<T> array, Function<T, String> display) {
        title(name, 2);
        blockquote("Number of " + name.toLowerCase() + " : " + array.size());
        ul(array, display);
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
        list(percent + "% (" + filtered.size() + "/" + originalSize + ") of " + type + " With Most " + filter,
             filtered, display);
    }

    public static void blockquote(String bockquote) {
        paragraph("> " + bockquote);
    }

    public static <T> void ul(List<T> array, Function<T, String> display) {
        for (T item : array) {
            item(display.apply(item));
        }

        System.out.println();
    }

    public static void item(String item) {
        System.out.println("- " + item);
    }

    public static void json(ArrayList<String> nodes, List<String> links) {
        title("Json Graph", 2);
        System.out.println("{\"nodes\":[" + String.join(",", nodes) + "], " +
                                   "\"links\":[" + String.join(",", links) + "]}");
    }

    public static void newline() {
        System.out.println();
    }
}
