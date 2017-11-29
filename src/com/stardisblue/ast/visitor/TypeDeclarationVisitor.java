package com.stardisblue.ast.visitor;

import com.stardisblue.ast.decorator.TypeDeclarationDecorator;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import java.util.ArrayList;

public class TypeDeclarationVisitor extends ASTVisitor {

    private ArrayList<TypeDeclarationDecorator> types = new ArrayList<>();

    @Override
    public boolean visit(TypeDeclaration node) {
        MethodDeclarationVisitor methodDeclarationVisitor = new MethodDeclarationVisitor();
        node.accept(methodDeclarationVisitor);
        FieldVisitor fieldVisitor = new FieldVisitor();
        node.accept(fieldVisitor);

        TypeDeclarationDecorator typeDeclarationDecorator = new
                TypeDeclarationDecorator(node, fieldVisitor.decorators(), methodDeclarationVisitor.decorators());

        types.add(typeDeclarationDecorator);


        return super.visit(node);
    }

    @Override
    public void endVisit(CompilationUnit node) {
        for (TypeDeclarationDecorator type : types) {
            type.inject(node);
        }

        super.endVisit(node);
    }

    public ArrayList<TypeDeclarationDecorator> decorators() {
        return types;
    }
}
