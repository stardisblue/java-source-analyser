package com.stardisblue.ast;

import com.stardisblue.ast.structure.Matrix;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class Display {

    private static PrintStream output;

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
        output.println(content);
        newline();
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

    public static void matrix(String title, Matrix matrix) {
        title(title, 2);
        table(matrix.getTable());
    }

    public static void table(String[][] table) {
        // displaying title line
        String[] titleLine = new String[table[0].length];
        row(table[0]);
        Arrays.fill(titleLine, "---");
        row(titleLine);

        for (int i = 1; i < table.length; i++) {
            row(table[i]);
        }
        newline();
    }

    public static void row(String[] row) {
        String strRow = "| " + String.join(" | ", row) + " |";

        System.out.println(strRow);

        output.println(strRow);
    }

    public static <T> void ul(List<T> array, Function<T, String> display) {
        ul(array, display, "");
        newline();
    }

    public static <T> void ul(List<T> array, Function<T, String> display, String prefix) {
        for (T item : array) {
            item(display.apply(item), prefix);
        }
    }

    public static void item(String item) {
        item(item, "");
    }

    public static void item(String item, String prefix) {
        String listItem = prefix + "- " + item;

        System.out.println(listItem);

        output.println(listItem);
    }

    public static void json(String title, List<String> nodes, List<String> links) {
        title(title, 2);
        codeBlock("json", "{\"nodes\":[" + String.join(",", nodes) + "], " +
                "\"links\":[" + String.join(",", links) + "]}");
    }

    public static void codeBlock(String language, String content) {
        String codeBlockMarker = "```";
        String beginCodeBlock = codeBlockMarker + language;

        System.out.println(beginCodeBlock);
        System.out.println(content);
        System.out.println(codeBlockMarker);

        output.println(beginCodeBlock);
        output.println(content);
        output.println(codeBlockMarker);
        newline();
    }

    public static void newline() {
        System.out.println();
        output.println();
    }

    public static void setOutput(String output) throws FileNotFoundException {
        Display.output = new PrintStream(new File(output));
    }

    public static void close() {
        output.close();
    }
}
