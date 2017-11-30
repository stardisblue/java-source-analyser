package com.stardisblue.ast.decorator;


import com.stardisblue.logging.Logger;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import java.util.ArrayList;
import java.util.List;

/**
 * Decorating a Type (class)
 */
public class TypeDeclarationDecorator {
    private final CompilationUnit parent;
    private final TypeDeclaration node;
    private final List<FieldDeclarationDecorator> fieldDeclarationDecorators;
    private final List<MethodDeclarationDecorator> methodDeclarationDecorators;

    private String name;
    private String packageName;
    private String fullName;
    private int numberOfLines;

    public TypeDeclarationDecorator(CompilationUnit parent, TypeDeclaration node, int fieldsSize, int methodsSize) {
        // HAHA! saved it
        this.node = node;
        this.name = node.getName().toString();
        this.fieldDeclarationDecorators = new ArrayList<>(fieldsSize);
        this.methodDeclarationDecorators = new ArrayList<>(methodsSize);
        this.parent = parent;

        // Getting package name
        if (this.parent.getPackage() != null) {
            this.packageName = this.parent.getPackage().getName().toString();
            this.fullName = getPackageName() + "." + getName();
        } else {
            this.packageName = "";
            this.fullName = getName();
        }

        // counting line numbers
        int startLine = this.parent.getLineNumber(node.getStartPosition());
        // -1 for lenght correction
        int endLine = this.parent.getLineNumber(node.getStartPosition() + node.getLength() - 1);
        this.numberOfLines = endLine - startLine;

        // welp printing com.stardisblue.ast.logging
        Logger.printTitle(getFullName() + ": " + fieldsSize + " fields," +
                                  " " + methodsSize + " methods," +
                                  " " + numberOfLines() + " lines",
                          Logger.DEBUG);
    }

    /**
     * Need to be called once, used to resolve cyclic dependency injection
     *
     * @param fieldDeclarationDecorators  array of fields
     * @param methodDeclarationDecorators array of methods
     */
    public void setup(List<FieldDeclarationDecorator> fieldDeclarationDecorators,
                      List<MethodDeclarationDecorator> methodDeclarationDecorators) {
        this.fieldDeclarationDecorators.addAll(fieldDeclarationDecorators);
        this.methodDeclarationDecorators.addAll(methodDeclarationDecorators);
    }

    public int numberOfMethods() {
        return methodDeclarationDecorators.size();
    }

    public int numberOfFields() {
        return fieldDeclarationDecorators.size();
    }

    public String getPackageName() {
        return packageName;
    }

    public String getName() {
        return name;
    }

    public String getFullName() {
        return fullName;
    }

    public int numberOfLines() {
        return numberOfLines;
    }

    public TypeDeclaration getNode() {
        return node;
    }

    public List<MethodDeclarationDecorator> getMethods() {
        return methodDeclarationDecorators;
    }

    public CompilationUnit getParent() {
        return parent;
    }
}
