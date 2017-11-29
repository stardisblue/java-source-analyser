package com.stardisblue.ast.decorator;


import com.stardisblue.logging.Logger;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import java.util.ArrayList;
import java.util.List;

public class TypeDeclarationDecorator {
    private String name;
    private String packageName;
    private String fullName;
    private int numberOfLines;

    private TypeDeclaration node;
    private List<FieldDecorator> fieldDecorators = new ArrayList<>();
    private List<MethodDeclarationDecorator> methodDeclarationDecorators = new ArrayList<>();
    private CompilationUnit parent;

    public TypeDeclarationDecorator(TypeDeclaration node,
                                    List<FieldDecorator> fieldDecorators,
                                    List<MethodDeclarationDecorator> methodDeclarationDecorators) {
        // HAHA! saved it
        this.node = node;
        this.name = node.getName().toString();
        this.fieldDecorators = fieldDecorators;
        this.methodDeclarationDecorators = methodDeclarationDecorators;

    }

    public void inject(CompilationUnit cu) {
        // Getting package name
        this.parent = cu;
        if (cu.getPackage() != null) {
            this.packageName = cu.getPackage().getName().toString();
            this.fullName = getPackageName() + "." + getName();
        } else {
            this.packageName = "";
            this.fullName = getName();
        }

        // counting line numbers
        int startLine = cu.getLineNumber(node.getStartPosition());
        // -1 for lenght correction
        int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength() - 1);
        this.numberOfLines = endLine - startLine;

        // welp printing com.stardisblue.ast.logging
        Logger.printTitle(getFullName() + ": " + fieldDecorators.size() + " fields, " + methodDeclarationDecorators
                .size() + " methods, " + numberOfLines() + " lines", Logger.DEBUG);

        Logger.println("Fields", "", Logger.DEBUG);
        for (FieldDecorator fieldDecorator : fieldDecorators) {
            fieldDecorator.inject(this);

            Logger.println("└─ " + fieldDecorator.getType() + " : " + fieldDecorator.getFragments(), Logger.DEBUG);
        }


        Logger.println("Methods", "", Logger.DEBUG);
        for (MethodDeclarationDecorator methodDeclarationDecorator : methodDeclarationDecorators) {
            methodDeclarationDecorator.inject(this);
        }
    }

    public int numberOfMethods() {
        return methodDeclarationDecorators.size();
    }

    public int numberOfFields() {
        return fieldDecorators.size();
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
