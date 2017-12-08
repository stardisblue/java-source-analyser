package com.stardisblue.ast;

import com.stardisblue.ast.info.MethodDeclarationInfo;
import com.stardisblue.ast.info.TypeDeclarationInfo;
import com.stardisblue.ast.structure.Cluster;
import com.stardisblue.ast.structure.Graph;
import com.stardisblue.ast.structure.Matrix;
import com.stardisblue.ast.visitor.TypeDeclarationVisitor;
import com.stardisblue.logging.Logger;
import com.stardisblue.utils.ListUtils;
import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Main {
    private static String projectSourcePath = System.getProperty("user.dir") + "\\src\\main\\java\\";
    private static String jrePath = System.getProperty("java.home") + "\\lib\\rt.jar";

    public static void main(String[] args) {
        Logger.enable();

        /*
         * Setting up AST
         */
        final File folder = new File(projectSourcePath);

        Collection<File> javaFiles = FileUtils.listFiles(folder, new String[]{"java"}, true);

        List<TypeDeclarationInfo> classes = new ArrayList<>(javaFiles.size());

        // constructing class structure
        try {
            for (File file : javaFiles) {
                String content = FileUtils.readFileToString(file, UTF_8);
                CompilationUnit compilationUnit = parse(content);

                TypeDeclarationVisitor typeDeclarationVisitor = new TypeDeclarationVisitor();
                compilationUnit.accept(typeDeclarationVisitor);

                classes.addAll(typeDeclarationVisitor.infos(compilationUnit));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // All classes :
        // List<TypeDeclarationInfo> classes // already set up
        // All Methods
        List<MethodDeclarationInfo> methods = ListUtils.extract(classes, TypeDeclarationInfo::getMethods);



        Display.title("Source Analyzer Statistics");

        /*
         * TP3
         *
         * General Information
         */
        Display.title("General Information", 2);

        Display.item("Number of classes : " + classes.size());
        Display.item("Total number of lines : " +
                             ListUtils.sum(classes, TypeDeclarationInfo::numberOfLines));
        Display.item("Average number of methods/class : " +
                             ListUtils.average(classes, TypeDeclarationInfo::numberOfMethods));
        Display.item("Average number of lines/method : " +
                             ListUtils.average(methods, MethodDeclarationInfo::numberOfLines));
        Display.item("Average number of fields/class : " +
                             ListUtils.average(classes, TypeDeclarationInfo::numberOfFields));
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
                ListUtils.sortedTopSubList(classes, percentageMethods,
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
                ListUtils.sortedTopSubList(classes, percentageFields,
                                           (o1, o2) -> o2.numberOfFields() - o1.numberOfFields());
        // display
        Display.withMost("Class(es)", "Field(s)", mostFields, classes.size(), percentageFields,
                         t -> t.getFullName() + " : " + t.numberOfFields() + " field(s)");


        /*
         * Classes present in both of them
         */
        // intersection
        List<TypeDeclarationInfo> mostMethodsFields =
                ListUtils.intersect(mostMethods, mostFields, Comparator.comparing(TypeDeclarationInfo::getName));
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
                ListUtils.hasMoreThan(classes, minimalValue, TypeDeclarationInfo::numberOfMethods);
        // display
        Display.list("Class(es) with More than " + minimalValue + " Method(s)", filteredByMethodNumber,
                     t -> t.getFullName() + " : " + t.numberOfMethods() + " method(s)");

        /*
         * Get Methods with most Lines
         */
        int percentageMethodLines = 10;
        // filter
        List<MethodDeclarationInfo> mostMethodLines =
                ListUtils.sortedTopSubList(methods, percentageMethodLines,
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
                ListUtils.getSameAs(methods, maxParamMethod,
                                    (m1, m2) -> m1.numberOfParameters() == m2.numberOfParameters());
        // display
        Display.list("Method(s) With The Highest Number of Parameter(s)", maxParamMethods,
                     (t) -> t.getFullName() + " : " + t.numberOfParameters() + " parameters");

        /*
         * Class list and method
         */
        Display.title("Class List");
        for (TypeDeclarationInfo item : classes) {
            Display.item(item.getFullName());
            Display.ul(item.getMethods(), MethodDeclarationInfo::getMethod, "  ");
        }
        Display.newline();


        /*
         * Method call graph
         */
        // creation of the graph
        Graph graph = Compute.methodGraph(methods);
        // generate Json Structure
        List<String> nodes = Compute.graphNodes(graph.getIds(), graph.getIsNodeInProject());
        List<String> links = Compute.graphLinks(graph.getLinkIds(), graph.getSourceCount());
        // display
        Display.json("MethodCall Json graph", nodes, links);

        /*
         * Class call graph
         */
        Graph classGraph = Compute.classGraph(methods);
        // generate Json Structure
        List<String> classNodes = Compute.graphNodes(classGraph.getIds(), classGraph.getIsNodeInProject());
        List<String> classLinks = Compute.graphLinks(classGraph.getLinkIds(), classGraph.getSourceCount());
        Display.json("ClassCall Json graph", classNodes, classLinks);

        /*
         * TP4
         *
         * class coupling matrix
         */
        Matrix matrix = Compute.classCoupling(classes, methods);
        Display.matrix("Class coupling matrix", matrix);


        /*
         * Hierarchic Clustering
         */
        Cluster<String> cluster = Compute.hierarchicClustering(matrix);
        List<String> dendrogramNodes = Compute.dendrogramNodes(classes.size(), cluster);
        List<String> dendrogramLinks = Compute.dendrogramLinks(cluster);
        Display.json("Dendrogram Cluster graph", dendrogramNodes, dendrogramLinks);

        /*
         * Cluster Selection
         */
        List<Cluster<String>> partitions = Compute.clusterSelection(cluster);
        List<String> partitionedLinks = new ArrayList<>();
        for (Cluster<String> partition : partitions) {
            partitionedLinks.addAll(Compute.dendrogramLinks(partition));
        }
        Display.json("Dendrogram Partition graph", dendrogramNodes, partitionedLinks);

    }


    /**
     * @param fileContent the content of a javafile
     * @return The apporpriate CompilationUnit
     */
    private static CompilationUnit parse(String fileContent) {
        ASTParser parser = ASTParser.newParser(AST.JLS9);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setResolveBindings(true); // we need bindings later on
        parser.setBindingsRecovery(true); // we need bindings later on

        //parser.setCompilerOptions(JavaCore.getOptions());

        parser.setUnitName("stardisblue");

        String[] sources = {projectSourcePath};
        String[] classpath = {jrePath};

        parser.setEnvironment(classpath, sources, new String[]{"UTF-8"}, true);
        parser.setSource(fileContent.toCharArray()); // set source
        return (CompilationUnit) parser.createAST(null /* IProgressMonitor */); // parse
    }
}
