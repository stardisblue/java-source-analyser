package com.stardisblue.ast.decorator;


import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.Type;

import java.util.List;

public class FieldDecorator extends ASTVisitor {


    private TypeDeclarationDecorator parent;
    private final FieldDeclaration field;
    private final Type type;
    private final List fragments;

    public FieldDecorator(FieldDeclaration fieldDeclaration) {
        this.field = fieldDeclaration;

        this.type = field.getType();
        this.fragments = field.fragments();
    }

    public Type getType() {
        return type;
    }

    public List getFragments() {
        return fragments;
    }

    public void inject(TypeDeclarationDecorator node) {
        this.parent = node;
    }

    public TypeDeclarationDecorator getParent() {
        return parent;
    }
}
