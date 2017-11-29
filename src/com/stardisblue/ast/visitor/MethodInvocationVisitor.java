package com.stardisblue.ast.visitor;

import com.stardisblue.ast.decorator.MethodInvocationDecorator;
import org.eclipse.jdt.core.dom.*;

import java.util.ArrayList;
import java.util.List;

public class MethodInvocationVisitor extends ASTVisitor {

    private List<MethodInvocationDecorator> methods = new ArrayList<>();

    @Override
    public boolean visit(MethodInvocation node) {
        MethodInvocationDecorator methodInvocationDecorator = new MethodInvocationDecorator(node);
        if (methodInvocationDecorator.isBinded()) {
            this.methods.add(methodInvocationDecorator);
        }

        return super.visit(node);
    }

    @Override
    public boolean visit(ClassInstanceCreation node) {
        System.out.println(node);
        return super.visit(node);
    }

    public List<MethodInvocationDecorator> decorators() {
        return methods;
    }
}
