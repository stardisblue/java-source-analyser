package com.stardisblue.ast.visitor;

import com.stardisblue.ast.info.ParameterizedTypeInfo;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.SimpleType;

public class ParameterizedTypeVisitor extends ASTVisitor {

    private ParameterizedTypeInfo parameter;

    @Override
    public boolean preVisit2(ASTNode node) {
        if (parameter != null) {
            return false;
        }
        return super.preVisit2(node);
    }

    @Override
    public boolean visit(SimpleType node) {
        // - creating info
        // x cyclic dependencies
        // x list
        this.parameter = new ParameterizedTypeInfo(node);
        return super.visit(node);
    }


    /**
     * Used to create the decorators and resolve dependencies
     *
     * @return the type decorating info
     */
    public ParameterizedTypeInfo info() {
        return parameter;
    }
}
