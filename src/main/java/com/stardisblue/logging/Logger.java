/*
 * Copyright 2017 SourceAnalyzer
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.stardisblue.logging;

import java.text.DateFormat;
import java.util.Date;

/**
 * Class used to display Logs.
 * <p>
 * Has different display statuses :
 * <ul>
 * <li>{@link #DEBUG}</li>
 * <li>{@link #INFO}</li>
 * <li>{@link #WARNING} : displays all the text in orange</li>
 * <li>{@link #ERROR} : displays all the text in red</li>
 * </ul>
 * <p>
 * <b>note:</b> only {@link #DEBUG} is hidden if {@link #enable()} is not used. all the others statuses are displayed in the {@link System#out}
 * <p>
 *
 * @author stardisblue
 * @version 1.4.0
 * @see #enable()
 * @see #disable()
 * @see #print(Object, int)
 * @see #print(String, Object, int)
 * @see #println(Object, int)
 * @see #println(String, Object, int)
 * @see #printTitle(String, int)
 * @see #indent(String)
 * @see #now()
 * @since 0.2.0
 */
public final class Logger {
    public final static int DEBUG = 1;
    public final static int INFO = 2;
    public final static int WARNING = 4;
    public final static int ERROR = 8;

    private final static String DEBUG_STRING = "\033[0;32m[DEBUG]\033[0m ";
    private final static String INFO_STRING = "\033[0;34m[INFO ]\033[0m ";
    private final static String WARNING_STRING = "\033[0;33m[WARNG] ";//\033[0m ";
    private final static String ERROR_STRING = "\033[0;31m[ERROR] ";//"\033[0m ";

    private final static String METHOD_PREFIX = "--:";
    private final static String METHOD_SUFFIX = ":--";
    private final static String VARNAME_SEPARATOR = ": ";

    private static int displayDebug = 14;
    private static String severity = "";

    /**
     * Displays the object (uses <tt>o.toString()</tt>)
     *
     * @param o      the object to PrettyPrint
     * @param status status of the text
     */
    public static void print(Object o, int status) {
        if ((status & displayDebug) != 0) {
            severity = getSeverityString(status);
            System.out.print(concatAll(o));
        }
    }


    /**
     * Displays the object (uses <tt>o.toString()</tt>)
     *
     * @param o      the object to PrettyPrint
     * @param status status of the text
     */
    public static void println(Object o, int status) {
        if ((status & displayDebug) != 0) {
            severity = getSeverityString(status);
            System.out.println(concatAll(o));
        }
    }

    /**
     * Displays the object (uses <tt>o.toString()</tt>)
     *
     * @param name   name of the object
     * @param o      the object to PrettyPrint
     * @param status status of the text
     */
    public static void print(String name, Object o, int status) {
        if ((status & displayDebug) != 0) {
            severity = getSeverityString(status);
            System.out.print(concatAll(name, o));
        }
    }

    /**
     * Displays the object (uses <tt>o.toString()</tt>)
     *
     * @param name name of the object
     * @param o    Object to display
     */
    public static void println(String name, Object o, int status) {
        if ((status & displayDebug) != 0) {
            severity = getSeverityString(status);
            System.out.println(concatAll(name, o));
        }
    }

    /**
     * Displays a custom title in the given status
     *
     * @param title  text to display
     * @param status status of the title
     */
    public static void printTitle(String title, int status) {
        if ((status & displayDebug) != 0) {
            severity = getSeverityString(status);
            System.out.println(concatAll(createTitle(title)));
        }
    }

    /**
     * @return a String containing the current time up to milliseconds
     */
    public static String now() {
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);
        return df.format(new Date()) + "," + String.format("%03d", System.currentTimeMillis() % 1000);
    }

    /**
     * Enables {@link #DEBUG} statuses to be displayed.
     */
    public static void enable() {
        displayDebug = 15;
    }

    /**
     * @return if debug statuses are displayed.
     */
    public static boolean enabled() {
        return displayDebug == 15;
    }

    /**
     * Disables any display done with this class.
     */
    public static void disable() {
        displayDebug = 0;
    }


    /**
     * Indents the multiline String by one tabulation foreach <bold>new</bold> line
     * If the string is on oneline, nothing will be indented
     *
     * @param multiline text to be indented
     * @return the indented version of the text
     */
    public static String indent(String multiline) {
        return multiline.replaceAll("(?m)(\r?\n)", "$1\t");
    }

    private static String getSeverityString(int severity) {
        switch (severity) {
            case DEBUG:
                return DEBUG_STRING;
            case WARNING:
                return WARNING_STRING;
            case ERROR:
                return ERROR_STRING;
            default:
            case INFO:
                return INFO_STRING;
        }
    }

    /**
     * @param title le texte a transformer en titre
     * @return le titre sera prefixé et suffixé par {@link #METHOD_PREFIX} et {@link #METHOD_SUFFIX}
     */
    private static String createTitle(String title) {
        return METHOD_PREFIX + " \033[1;4m" + title + "\033[0m " + METHOD_SUFFIX;
    }

    private static String concatAll(Object obj) {
        return "\033[0;37m[" + now() + "]\033[0m" + severity + obj.toString();
    }

    private static String concatAll(String name, Object value) {
        return concatAll(("\033[1m") + name + "\033[0m" + VARNAME_SEPARATOR + value.toString());
    }
}
