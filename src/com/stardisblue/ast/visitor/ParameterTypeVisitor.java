package com.stardisblue.ast.visitor;

import com.stardisblue.ast.decorator.ParameterTypeDecorator;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.SimpleType;

public class ParameterTypeVisitor extends ASTVisitor {

    private ParameterTypeDecorator parameter;

    @Override
    public boolean preVisit2(ASTNode node) {
        if (parameter != null) {
            return false;
        }
        return super.preVisit2(node);
    }

    @Override
    public boolean visit(SimpleType node) {
        this.parameter = new ParameterTypeDecorator(node);
        return super.visit(node);
    }

    public ParameterTypeDecorator decorator() {
        return parameter;
    }
}
