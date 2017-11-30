package com.stardisblue.ast.visitor;

import com.stardisblue.ast.decorator.MethodDeclarationDecorator;
import com.stardisblue.ast.decorator.TypeDeclarationDecorator;
import com.stardisblue.logging.Logger;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import java.util.ArrayList;
import java.util.List;

public class MethodDeclarationVisitor extends ASTVisitor {

    private List<MethodDeclarationWrapper> wrappers = new ArrayList<>();
    private List<MethodDeclarationDecorator> decorators = new ArrayList<>();

    @Override
    public boolean visit(MethodDeclaration node) {
        MethodDeclarationWrapper w = new MethodDeclarationWrapper();
        w.node = node;

        node.accept(w.parameterVisitor);
        node.accept(w.invocationVisitor);

        this.wrappers.add(w);

        return super.visit(node);
    }


    /**
     * Used to create the decorators and resolve dependencies
     *
     * @param parent parent element
     * @return an array of decorators decorating all the elements found while visiting
     */
    public List<MethodDeclarationDecorator> decorators(TypeDeclarationDecorator parent) {
        // if the decorators are already set
        if (!decorators.isEmpty()) return decorators;

        decorators = new ArrayList<>(wrappers.size());

        Logger.println("Methods", "", Logger.DEBUG);

        for (MethodDeclarationWrapper w : wrappers) {
            // - creating decorator
            MethodDeclarationDecorator method = new MethodDeclarationDecorator(parent, w.node,
                                                                               w.parameterVisitor.size(),
                                                                               w.invocationVisitor.size());
            // - resolving cyclic dependencies
            method.setup(w.parameterVisitor.decorators(method), w.invocationVisitor.decorators(method));
            // - adding to the list of decorators
            decorators.add(method);
        }

        // emptying once the decorator are created
        wrappers = null;

        return decorators;
    }

    /**
     * Returns the number of visited elements, used to pre-allocate space for decorators
     *
     * @return number of declared methods
     */
    public int size() {
        return wrappers.size();
    }

    /**
     * Used for saving structures while visiting
     */
    private class MethodDeclarationWrapper {
        MethodDeclaration node;
        ParameterVisitor parameterVisitor = new ParameterVisitor();
        InvocationVisitor invocationVisitor = new InvocationVisitor();
    }
}
