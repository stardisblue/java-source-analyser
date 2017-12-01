package com.stardisblue.ast.info;

import org.eclipse.jdt.core.dom.SimpleType;

/**
 * Decorating a parameter type
 */
public class ParameterizedTypeInfo {
    private final String name;

    public ParameterizedTypeInfo(SimpleType node) {
        this.name = node.toString();
    }

    public String getName() {
        return name;
    }
}
