package com.stardisblue.ast.visitor;

import com.stardisblue.ast.decorator.MethodDeclarationDecorator;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import java.util.ArrayList;
import java.util.List;

public class MethodDeclarationVisitor extends ASTVisitor {

    private List<MethodDeclarationDecorator> methods = new ArrayList<>();

    public boolean visit(MethodDeclaration node) {
        ParameterVisitor parameterVisitor = new ParameterVisitor();
        MethodInvocationVisitor methodInvocationVisitor = new MethodInvocationVisitor();

        node.accept(parameterVisitor);
        node.accept(methodInvocationVisitor);

        this.methods.add(new MethodDeclarationDecorator(node, parameterVisitor.decorators(),
                                                        methodInvocationVisitor.decorators()));

        return super.visit(node);
    }

    public List<MethodDeclarationDecorator> decorators() {
        return methods;
    }
}
