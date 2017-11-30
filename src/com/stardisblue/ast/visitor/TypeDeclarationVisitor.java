package com.stardisblue.ast.visitor;

import com.stardisblue.ast.decorator.TypeDeclarationDecorator;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import java.util.ArrayList;

public class TypeDeclarationVisitor extends ASTVisitor {

    private ArrayList<TypeDeclarationDecorator> decorators = new ArrayList<>();
    private ArrayList<TypeDeclarationWrapper> wrappers = new ArrayList<>();

    @Override
    public boolean visit(TypeDeclaration node) {
        TypeDeclarationWrapper w = new TypeDeclarationWrapper();
        w.node = node;

        node.accept(w.methodDeclarationVisitor);
        node.accept(w.fieldDeclarationVisitor);

        wrappers.add(w);

        return super.visit(node);
    }

    /**
     * Used to create the decorators and resolve dependencies
     *
     * @param parent parent element
     * @return an array of decorators decorating all the elements found while visiting
     */
    public ArrayList<TypeDeclarationDecorator> decorators(CompilationUnit parent) {
        // if the decorators are already set
        if (!decorators.isEmpty()) return decorators;

        decorators = new ArrayList<>(wrappers.size());

        for (TypeDeclarationWrapper w : wrappers) {
            // - creating decorator
            TypeDeclarationDecorator type = new TypeDeclarationDecorator(parent, w.node,
                                                                         w.fieldDeclarationVisitor.size(),
                                                                         w.methodDeclarationVisitor.size());
            // - resolving cyclic dependency injection
            type.setup(w.fieldDeclarationVisitor.decorators(type), w.methodDeclarationVisitor.decorators(type));

            // - adding to the list of decorators
            decorators.add(type);
        }

        // emptying once the decorators are created
        wrappers = null;

        return decorators;
    }

    /**
     * Used for saving structures while visiting
     */
    private class TypeDeclarationWrapper {
        TypeDeclaration node;
        FieldDeclarationVisitor fieldDeclarationVisitor = new FieldDeclarationVisitor();
        MethodDeclarationVisitor methodDeclarationVisitor = new MethodDeclarationVisitor();
    }
}
