package com.stardisblue.ast.visitor;

import com.stardisblue.ast.decorator.FieldDecorator;
import com.stardisblue.ast.decorator.TypeDeclarationDecorator;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;

import java.util.ArrayList;
import java.util.List;

public class FieldVisitor extends ASTVisitor {
    List<FieldDecorator> fields = new ArrayList<>();

    @Override
    public boolean visit(FieldDeclaration node) {
        fields.add(new FieldDecorator(node));

        return super.visit(node);
    }

    public List<FieldDecorator> decorators() {
        return fields;
    }
}
