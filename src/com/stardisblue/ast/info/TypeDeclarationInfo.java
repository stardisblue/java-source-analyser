package com.stardisblue.ast.info;


import com.stardisblue.logging.Logger;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import java.util.ArrayList;
import java.util.List;

/**
 * Decorating a Type (class)
 */
public class TypeDeclarationInfo {
    private final List<FieldDeclarationInfo> fieldDeclarationInfos;
    private final List<MethodDeclarationInfo> methodDeclarationInfos;

    private String name;
    private String packageName;
    private String fullName;
    private int numberOfLines;

    public TypeDeclarationInfo(CompilationUnit parent, TypeDeclaration node, int fieldsSize, int methodsSize) {
        this.name = node.getName().toString();
        this.fieldDeclarationInfos = new ArrayList<>(fieldsSize);
        this.methodDeclarationInfos = new ArrayList<>(methodsSize);

        // Getting package name
        if (parent.getPackage() != null) {
            this.packageName = parent.getPackage().getName().toString();
            this.fullName = getPackageName() + "." + getName();
        } else {
            this.packageName = "";
            this.fullName = getName();
        }

        // counting line numbers
        int startLine = parent.getLineNumber(node.getStartPosition());
        // -1 for lenght correction
        int endLine = parent.getLineNumber(node.getStartPosition() + node.getLength() - 1);
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
     * @param fieldDeclarationInfos array of fields
     * @param methodDeclarationInfos     array of methods
     */
    public void setup(List<FieldDeclarationInfo> fieldDeclarationInfos,
                      List<MethodDeclarationInfo> methodDeclarationInfos) {
        this.fieldDeclarationInfos.addAll(fieldDeclarationInfos);
        this.methodDeclarationInfos.addAll(methodDeclarationInfos);
    }

    public int numberOfMethods() {
        return methodDeclarationInfos.size();
    }

    public int numberOfFields() {
        return fieldDeclarationInfos.size();
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

    public List<MethodDeclarationInfo> getMethods() {
        return methodDeclarationInfos;
    }
}
