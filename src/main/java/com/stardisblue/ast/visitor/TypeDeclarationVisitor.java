package com.stardisblue.ast.visitor;

import com.stardisblue.ast.info.TypeDeclarationInfo;
import org.eclipse.jdt.core.dom.*;

import java.util.ArrayList;

public class TypeDeclarationVisitor extends ASTVisitor {

    private ArrayList<TypeDeclarationInfo> decorators = new ArrayList<>();
    private ArrayList<TypeDeclarationWrapper> wrappers = new ArrayList<>();

    @Override
    public boolean visit(TypeDeclaration node) {
        TypeDeclarationWrapper w = new TypeDeclarationWrapper();
        w.node = node;

        for (MethodDeclaration methodDeclaration : node.getMethods()) {
            methodDeclaration.accept(w.methodDeclarationVisitor);
        }

        for (FieldDeclaration fieldDeclaration : node.getFields()) {
            fieldDeclaration.accept(w.fieldDeclarationVisitor);
        }

        wrappers.add(w);

        return super.visit(node);
    }

    /**
     * Used to create the decorators and resolve dependencies
     *
     * @param parent parent element
     * @return an array of decorators decorating all the elements found while visiting
     */
    public ArrayList<TypeDeclarationInfo> infos(CompilationUnit parent) {
        // if the decorators are already set
        if (!decorators.isEmpty()) return decorators;

        decorators = new ArrayList<>(wrappers.size());

        for (TypeDeclarationWrapper w : wrappers) {
            // - creating info
            TypeDeclarationInfo type = new TypeDeclarationInfo(parent, w.node,
                                                               w.fieldDeclarationVisitor.size(),
                                                               w.methodDeclarationVisitor.size());
            // - resolving cyclic dependency injection
            type.setup(w.fieldDeclarationVisitor.infos(type), w.methodDeclarationVisitor.infos(parent, type));

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
