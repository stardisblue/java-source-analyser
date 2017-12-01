package com.stardisblue.ast;

import com.stardisblue.ast.info.MethodDeclarationInfo;
import com.stardisblue.ast.info.TypeDeclarationInfo;
import com.stardisblue.ast.structure.Graph;
import com.stardisblue.ast.visitor.TypeDeclarationVisitor;
import com.stardisblue.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Main {
    public static String projectSourcePath = "C:\\Users\\stardisblue\\Documents\\M2_AIGLE\\HMIN306-Evolution_Restructuration\\TP2-Source-Analyser\\src";
    public static String jrePath = System.getProperty("java.home") + "\\lib\\rt.jar";

    public static void main(String[] args) {
        Logger.enable();
        final File folder = new File(projectSourcePath);

        Collection<File> javaFiles = FileUtils.listFiles(folder, new String[]{"java"}, true);

        List<TypeDeclarationInfo> classes = new ArrayList<>(javaFiles.size());

        try {

            for (File file : javaFiles) {
                String content = FileUtils.readFileToString(file);
                CompilationUnit compilationUnit = parse(content);

                TypeDeclarationVisitor typeDeclarationVisitor = new TypeDeclarationVisitor();
                compilationUnit.accept(typeDeclarationVisitor);

                classes.addAll(typeDeclarationVisitor.infos(compilationUnit));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Logger.println(classes, Logger.DEBUG);

        // All classes :
        // List<TypeDeclarationInfo> classes // already set up
        // All Methods
        List<MethodDeclarationInfo> methods = Compute.extract(classes, TypeDeclarationInfo::getMethods);

        Display.title("Source Analyzer Statistics");

        /*
         * General Information
         */
        Display.title("General Information", 2);

        Display.item("Number of classes : " + classes.size());
        Display.item("Total number of lines : " +
                             Compute.sum(classes, TypeDeclarationInfo::numberOfLines));
        Display.item("Average number of methods/class : " +
                             Compute.average(classes, TypeDeclarationInfo::numberOfMethods));
        Display.item("Average number of lines/method : " +
                             Compute.average(methods, MethodDeclarationInfo::numberOfLines));
        Display.item("Average number of fields/class : " +
                             Compute.average(classes, TypeDeclarationInfo::numberOfFields));
        Display.newline();

        /*
         * Packages
         */
        List<String> packages = Compute.packages(classes);
        Display.list("Package(s)", packages, String::toString);

        /*
         * Percent of classes with the most methods
         */
        int percentageMethods = 10;
        // filter
        List<TypeDeclarationInfo> mostMethods =
                Compute.sortedTopSubList(classes, percentageMethods,
                                         (o1, o2) -> o2.numberOfMethods() - o1.numberOfMethods());
        // display
        Display.withMost("Class(es)", "Method(s)", mostMethods, classes.size(), percentageMethods,
                         t -> t.getFullName() + " : " + t.numberOfMethods() + " method(s)");

        /*
         * Percent of classes with the most fields
         */
        int percentageFields = 10;
        // filter
        List<TypeDeclarationInfo> mostFields =
                Compute.sortedTopSubList(classes, percentageFields,
                                         (o1, o2) -> o2.numberOfFields() - o1.numberOfFields());
        // display
        Display.withMost("Class(es)", "Field(s)", mostFields, classes.size(), percentageFields,
                         t -> t.getFullName() + " : " + t.numberOfFields() + " field(s)");


        /*
         * Classes present in both of them
         */
        // intersection
        List<TypeDeclarationInfo> mostMethodsFields =
                Compute.intersect(mostMethods, mostFields, Comparator.comparing(TypeDeclarationInfo::getName));
        //  sort
        mostMethodsFields.sort((o1, o2) -> o2.numberOfMethods() - o1.numberOfMethods()
                + o2.numberOfFields() - o1.numberOfFields());
        //  calculate percentage
        int percentageTotal = (int) Math.ceil(100 * mostMethodsFields.size() / classes.size());
        //  display (name of class)
        Display.withMost("Class(es)", "Field(s) and Method(s)", mostMethodsFields, classes.size(), percentageTotal,
                         t -> t.getFullName() + " : " +
                                 t.numberOfFields() + " field(s), " + t.numberOfMethods() + " method(s)");


        /*
         * Classes that have more than n methods
         */
        int minimalValue = 3;
        // filter
        List<TypeDeclarationInfo> filteredByMethodNumber =
                Compute.hasMoreThan(classes, minimalValue, TypeDeclarationInfo::numberOfMethods);
        // display
        Display.list("Class(es) with More than " + minimalValue + " Method(s)", filteredByMethodNumber,
                     t -> t.getFullName() + " : " + t.numberOfMethods() + " method(s)");

        /*
         * Get Methods with most Lines
         */
        int percentageMethodLines = 10;
        // filter
        List<MethodDeclarationInfo> mostMethodLines =
                Compute.sortedTopSubList(methods, percentageMethodLines,
                                         (m1, m2) -> m2.numberOfLines() - m1.numberOfLines());
        // display
        Display.withMost("Method(s)", "Line(s)", mostMethodLines, methods.size(), percentageMethodLines,
                         m -> m.getShortName() + " : " + m.numberOfLines() + " line(s)");

        /*
         * Get Method with highest number of parameters
         */
        // get max
        MethodDeclarationInfo maxParamMethod =
                Collections.max(methods, Comparator.comparingInt(MethodDeclarationInfo::numberOfParameters));
        // get same as max
        List<MethodDeclarationInfo> maxParamMethods =
                Compute.getSameAs(methods, maxParamMethod,
                                  (m1, m2) -> m1.numberOfParameters() == m2.numberOfParameters());
        // display
        Display.list("Method(s) With The Highest Number of Parameter(s)", maxParamMethods,
                     (t) -> t.getFullName() + " : " + t.numberOfParameters() + " parameters");

        /*
         * Method call graph
         */
        // creation of the graph
        Graph graph = Compute.methodGraph(methods);
        // generate Json Structure
        ArrayList<String> nodes = Compute.graphNodes(graph.getIds(), graph.getIsNodeInProject());
        List<String> links = Compute.graphLinks(graph.getLinkIds(), graph.getSourceCount());
        // display
        Display.json(nodes, links);
    }


    /**
     * @param fileContent the content of a javafile
     * @return The apporpriate CompilationUnit
     */
    private static CompilationUnit parse(String fileContent) {
        ASTParser parser = ASTParser.newParser(AST.JLS4);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setResolveBindings(true); // we need bindings later on
        parser.setBindingsRecovery(true); // we need bindings later on

        parser.setCompilerOptions(JavaCore.getOptions());

        parser.setUnitName("stardisblue");

        String[] sources = {projectSourcePath};
        String[] classpath = {jrePath};

        parser.setEnvironment(classpath, sources, new String[]{"UTF-8"}, true);
        parser.setSource(fileContent.toCharArray()); // set source
        return (CompilationUnit) parser.createAST(null /* IProgressMonitor */); // parse
    }
}
