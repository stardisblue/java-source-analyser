package com.stardisblue.ast.decorator;

import org.eclipse.jdt.core.dom.SimpleType;

public class ParameterTypeDecorator {
    private final SimpleType node;

    public ParameterTypeDecorator(SimpleType node) {
        this.node = node;
    }

    public String getName() {
        return node.toString();
    }
}
