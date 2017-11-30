package com.stardisblue.ast.visitor;

import com.stardisblue.ast.decorator.MethodDeclarationDecorator;
import com.stardisblue.ast.decorator.MethodInvocationDecorator;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.MethodInvocation;

import java.util.ArrayList;
import java.util.List;

public class InvocationVisitor extends ASTVisitor {

    private List<ConstructorInvocation> constructors = new ArrayList<>();
    private List<MethodInvocation> methods = new ArrayList<>();
    private List<MethodInvocationDecorator> decorators = new ArrayList<>();

    @Override
    public boolean visit(MethodInvocation node) {
        methods.add(node);

        return super.visit(node);
    }


    /**
     * Used to create the decorators and resolve dependencies
     *
     * @param parent parent element
     * @return an array of decorators decorating all the elements found while visiting
     */
    public List<MethodInvocationDecorator> decorators(MethodDeclarationDecorator parent) {
        // if the decorators are already set
        if (!decorators.isEmpty()) return decorators;

        decorators = new ArrayList<>(methods.size());

        for (MethodInvocation node : methods) {
            // - creating decorator
            MethodInvocationDecorator methodInvocationDecorator = new MethodInvocationDecorator(parent, node);

            // ? checking if the methodinvocation information has been found
            if (methodInvocationDecorator.isBinded()) {
                // -  adding to the list of decorators
                decorators.add(methodInvocationDecorator);
            }
        }

        // emptying once the decorator are created
        methods = null;

        return decorators;
    }

    /**
     * Returns the number of visited elements, used to pre-allocate space for decorators
     *
     * @return number of methods
     */
    public int size() {
        return methods.size();
    }
}
