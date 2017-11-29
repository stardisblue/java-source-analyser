package com.stardisblue.ast;

import com.stardisblue.ast.decorator.MethodDeclarationDecorator;
import com.stardisblue.ast.decorator.TypeDeclarationDecorator;
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
    public static String projectSourcePath = "C:\\Users\\stardisblue\\Documents\\M2_AIGLE\\HMIN306-Evolution_Restructuration\\TP2-Analyzer\\src";
    public static String jrePath = System.getProperty("java.home") + "\\lib\\rt.jar";

    public static void main(String[] args) {
        Logger.enable();
        final File folder = new File(projectSourcePath);

        Collection<File> javaFiles = FileUtils.listFiles(folder, new String[]{"java"}, true);

        ArrayList<TypeDeclarationDecorator> typeDecs = new ArrayList<>(javaFiles.size());

        try {

            for (File file : javaFiles) {
                String content = FileUtils.readFileToString(file);
                CompilationUnit compilationUnit = parse(content);

                TypeDeclarationVisitor typeDeclarationVisitor = new TypeDeclarationVisitor();
                compilationUnit.accept(typeDeclarationVisitor);

                typeDecs.addAll(typeDeclarationVisitor.decorators());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Logger.println(typeDecs, Logger.DEBUG);

        Display.title("Source Analyzer Statistics");

        // Packages
        List<String> packages = Compute.packages(typeDecs);
        Display.display("Package(s)", packages, String::toString);

        // Most methods
        int percentageMethods = 20;
        // - filter
        List<TypeDeclarationDecorator> mostMethods =
                Compute.sortedTopSubList(typeDecs, percentageMethods,
                                         (o1, o2) -> o2.numberOfMethods() - o1.numberOfMethods());
        // - display
        Display.withMost("Class(es)", "Method(s)", mostMethods, typeDecs.size(), percentageMethods,
                         t -> t.getFullName() + " : " + t.numberOfMethods() + " method(s)");

        // Most fields
        int percentageFields = 20;
        // - filter
        List<TypeDeclarationDecorator> mostFields =
                Compute.sortedTopSubList(typeDecs, percentageFields,
                                         (o1, o2) -> o2.numberOfFields() - o1.numberOfFields());
        // - display
        Display.withMost("Class(es)", "Field(s)", mostFields, typeDecs.size(), percentageFields,
                         t -> t.getFullName() + " : " + t.numberOfFields() + " field(s)");

        // Classes present in both
        // - intersection
        List<TypeDeclarationDecorator> mostMethodsFields =
                Compute.intersect(mostMethods, mostFields, Comparator.comparing(TypeDeclarationDecorator::getName));
        //  - sort
        mostMethodsFields.sort((o1, o2) -> o2.numberOfMethods() - o1.numberOfMethods()
                + o2.numberOfFields() - o1.numberOfFields());
        //  - calculate percentage
        int percentageTotal = (int) Math.ceil(100 * mostMethodsFields.size() / typeDecs.size());
        //  - display (name of class)
        Display.withMost("Class(es)", "Field(s) and Method(s)", mostMethodsFields, typeDecs.size(), percentageTotal,
                         t -> t.getFullName() + " : " +
                                 t.numberOfFields() + " field(s), " + t.numberOfMethods() + " method(s)");

        // Classes that have more than n methods
        int minimalValue = 3;
        // - filter
        List<TypeDeclarationDecorator> filteredByMethodNumber =
                Compute.hasMoreThan(typeDecs, minimalValue, TypeDeclarationDecorator::numberOfMethods);
        // - display
        Display.display("Class(es) with More than " + minimalValue + " Method(s)", filteredByMethodNumber,
                        t -> t.getFullName() + " : " + t.numberOfMethods() + " method(s)");


        // Get All Methods
        List<MethodDeclarationDecorator> methods = Compute.extract(typeDecs, TypeDeclarationDecorator::getMethods);
        // Get Method Most Line Number
        int percentageMethodLines = 20;
        // - filter
        List<MethodDeclarationDecorator> mostMethodLines =
                Compute.sortedTopSubList(methods, percentageMethodLines,
                                         (m1, m2) -> m2.numberOfLines() - m1.numberOfLines());
        // - display
        Display.withMost("Method(s)", "Line(s)", mostMethodLines, methods.size(), percentageMethodLines,
                         m -> m.getShortName() + " : " + m.numberOfLines() + " line(s)");

        // Get Method with highest number of parameters
        // - get max
        MethodDeclarationDecorator maxParamMethod =
                Collections.max(methods, Comparator.comparingInt(MethodDeclarationDecorator::numberOfParameters));
        // - get all maxes
        List<MethodDeclarationDecorator> maxParamMethods =
                Compute.getSameAs(methods, maxParamMethod,
                                  (m1, m2) -> m1.numberOfParameters() == m2.numberOfParameters());
        // - display
        Display.display("Method(s) With The Highest Number of Parameter(s)", maxParamMethods,
                        (t) -> t.getFullName() + " : " + t.numberOfParameters() + " parameters");

        // Method Call Graph
        // - creation of the graph
        Graph graph = Compute.methodGraph(methods);
        // - generate Json Structure
        ArrayList<String> nodes = Compute.graphNodes(graph.getIds(), graph.getIsNodeInProject());
        List<String> links = Compute.graphLinks(graph.getLinkIds(), graph.getSourceCount());
        // - display
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
