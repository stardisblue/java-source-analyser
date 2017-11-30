package com.stardisblue.ast.decorator;


import com.stardisblue.logging.Logger;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.Type;

import java.util.List;

/**
 * Decorating FieldDeclaration
 */
public class FieldDeclarationDecorator {


    private final TypeDeclarationDecorator parent;
    private final FieldDeclaration node;
    private final Type type;
    private final List fragments;


    /**
     * Default constructer, the parent is injected using DI
     *
     * @param parent parent element
     * @param node   current decorated element
     */
    public FieldDeclarationDecorator(TypeDeclarationDecorator parent,
                                     FieldDeclaration node) {
        this.parent = parent;
        this.node = node;
        this.type = this.node.getType();
        this.fragments = this.node.fragments();

        Logger.println("└─ " + getType() + " : " + getFragments(), Logger.DEBUG);

    }

    public Type getType() {
        return type;
    }

    public List getFragments() {
        return fragments;
    }

    public TypeDeclarationDecorator getParent() {
        return parent;
    }
}
