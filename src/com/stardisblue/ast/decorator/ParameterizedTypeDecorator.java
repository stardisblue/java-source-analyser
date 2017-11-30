package com.stardisblue.ast.decorator;

import org.eclipse.jdt.core.dom.SimpleType;

/**
 * Decorating a parameter type
 */
public class ParameterizedTypeDecorator {
    private final SimpleType node;

    public ParameterizedTypeDecorator(SimpleType node) {
        this.node = node;
    }

    public String getName() {
        return node.toString();
    }
}
