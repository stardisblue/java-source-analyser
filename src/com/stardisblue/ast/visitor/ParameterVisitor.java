package com.stardisblue.ast.visitor;

import com.stardisblue.ast.decorator.ParameterDecorator;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;

import java.util.ArrayList;
import java.util.List;

public class ParameterVisitor extends ASTVisitor {

    private final List<ParameterDecorator> parameters = new ArrayList<>();

    @Override
    public boolean visit(SingleVariableDeclaration node) {
        ParameterTypeVisitor parameterTypeVisitor = new ParameterTypeVisitor();
        node.accept(parameterTypeVisitor);

        this.parameters.add(new ParameterDecorator(node, parameterTypeVisitor.decorator()));
        return super.visit(node);
    }

    public List<ParameterDecorator> decorators() {
        return parameters;
    }
}
