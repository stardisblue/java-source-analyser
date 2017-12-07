package com.stardisblue.ast.visitor;

import com.stardisblue.ast.info.MethodDeclarationInfo;
import com.stardisblue.ast.info.MethodInvocationInfo;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodInvocation;

import java.util.ArrayList;
import java.util.List;

public class MethodInvocationVisitor extends ASTVisitor {
    private List<MethodInvocation> methods = new ArrayList<>();
    private List<MethodInvocationInfo> decorators = new ArrayList<>();

    @Override
    public boolean visit(MethodInvocation node) {
        methods.add(node);

        return super.visit(node);
    }


    /**
     * Used to create the decorators and resolve dependencies
     *
     * @param parent parent element
     * @return an array of decorators decorating all the elements found while visiting
     */
    public List<MethodInvocationInfo> infos(MethodDeclarationInfo parent) {
        // if the decorators are already set
        if (!decorators.isEmpty()) return decorators;

        decorators = new ArrayList<>(methods.size());

        for (MethodInvocation node : methods) {
            // - creating info
            MethodInvocationInfo methodInvocationInfo = new MethodInvocationInfo(parent, node);

            // ? checking if the methodinvocation information has been found
            if (methodInvocationInfo.isBinded()) {
                // -  adding to the list of decorators
                decorators.add(methodInvocationInfo);
            }
        }

        // emptying once the info are created
        methods = null;

        return decorators;
    }

    /**
     * Returns the number of visited elements, used to pre-allocate space for decorators
     *
     * @return number of methods
     */
    public int size() {
        return methods.size();
    }
}
