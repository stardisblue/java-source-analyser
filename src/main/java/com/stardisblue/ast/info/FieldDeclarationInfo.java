package com.stardisblue.ast.info;


import com.stardisblue.logging.Logger;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.Type;

import java.util.List;

/**
 * Decorating FieldDeclaration
 */
public class FieldDeclarationInfo {


    private final TypeDeclarationInfo parent;
    private final Type type;
    private final List fragments;


    /**
     * Default constructer, the parent is injected using DI
     *
     * @param parent parent element
     * @param node   current decorated element
     */
    public FieldDeclarationInfo(TypeDeclarationInfo parent,
                                FieldDeclaration node) {
        this.parent = parent;
        this.type = node.getType();
        this.fragments = node.fragments();

        Logger.println("└─ " + getType() + " : " + getFragments(), Logger.DEBUG);

    }

    public Type getType() {
        return type;
    }

    public List getFragments() {
        return fragments;
    }
}
