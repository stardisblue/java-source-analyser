package com.stardisblue.ast.decorator;

import org.eclipse.jdt.core.dom.SingleVariableDeclaration;

/**
 * Decorating a MethodParameter
 */
public class ParameterDecorator {

    private final MethodDeclarationDecorator parent;
    private final SingleVariableDeclaration node;
    private final ParameterizedTypeDecorator parameterizedTypeDecoratorType;

    /**
     * Default constructer, elements are passed through via DI,
     *
     * @param parent                         parent element
     * @param node                           decorated element
     * @param parameterizedTypeDecoratorType parameter type
     */
    public ParameterDecorator(MethodDeclarationDecorator parent,
                              SingleVariableDeclaration node,
                              ParameterizedTypeDecorator parameterizedTypeDecoratorType) {
        this.parent = parent;
        this.node = node;
        this.parameterizedTypeDecoratorType = parameterizedTypeDecoratorType;
    }

    public String getName() {
        return node.getName().toString();
    }

    public String getType() {
        return node.getType().toString();
    }

    public String getClassType() {
        if (parameterizedTypeDecoratorType == null) return getType();
        return parameterizedTypeDecoratorType.getName();
    }

    public String getShortName() {
        return getClassType();
    }

    public String getFullName() {
        return getType() + " " + getName();
    }
}
