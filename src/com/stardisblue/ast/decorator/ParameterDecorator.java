package com.stardisblue.ast.decorator;

import org.eclipse.jdt.core.dom.SingleVariableDeclaration;

public class ParameterDecorator {

    private final SingleVariableDeclaration singleVar;
    private final ParameterTypeDecorator parameterizedType;

    public ParameterDecorator(SingleVariableDeclaration siVar,
                              ParameterTypeDecorator decorators) {
        this.singleVar = siVar;
        this.parameterizedType = decorators;
    }

    public String getName() {
        return singleVar.getName().toString();
    }

    public String getType() {
        return singleVar.getType().toString();
    }

    public String getClassType() {
        if (parameterizedType == null) return getType();
        return parameterizedType.getName();
    }

    public String getShortName() {
        return getClassType();
    }

    public String getFullName() {
        return getType() + " " + getName();
    }
}
