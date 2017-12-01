package com.stardisblue.ast.info;

import org.eclipse.jdt.core.dom.SingleVariableDeclaration;

/**
 * Decorating a MethodParameter
 */
public class ParameterInfo {

    private final MethodDeclarationInfo parent;
    private final ParameterizedTypeInfo parameterizedTypeDecoratorType;
    private final String name;
    private final String type;

    /**
     * Default constructer, elements are passed through via DI,
     *
     * @param parent                         parent element
     * @param node                           decorated element
     * @param parameterizedTypeDecoratorType parameter type
     */
    public ParameterInfo(MethodDeclarationInfo parent,
                         SingleVariableDeclaration node,
                         ParameterizedTypeInfo parameterizedTypeDecoratorType) {
        this.parent = parent;
        this.name = node.getName().toString();
        this.type = node.getType().toString();
        this.parameterizedTypeDecoratorType = parameterizedTypeDecoratorType;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
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

    @Override
    public String toString() {
        return getFullName();
    }
}
