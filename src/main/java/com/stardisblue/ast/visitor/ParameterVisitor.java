package com.stardisblue.ast.visitor;

import com.stardisblue.ast.info.MethodDeclarationInfo;
import com.stardisblue.ast.info.ParameterInfo;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;

import java.util.ArrayList;
import java.util.List;

public class ParameterVisitor extends ASTVisitor {

    private List<SingleVariableDeclarationWrapper> wrappers = new ArrayList<>();
    private List<ParameterInfo> decorators = new ArrayList<>();

    @Override
    public boolean visit(SingleVariableDeclaration node) {
        SingleVariableDeclarationWrapper w = new SingleVariableDeclarationWrapper();
        w.node = node;

        node.accept(w.parameterizedTypeVisitor);

        this.wrappers.add(w);

        return super.visit(node);
    }

    public List<ParameterInfo> infos(MethodDeclarationInfo parent) {
        // if the decorators are already set
        if (!decorators.isEmpty()) return decorators;

        decorators = new ArrayList<>(wrappers.size());

        for (SingleVariableDeclarationWrapper w : wrappers) {
            // - creating info
            // x cyclic dependency
            // - adding to the list of decorators
            decorators.add(new ParameterInfo(parent, w.node, w.parameterizedTypeVisitor.info()));
        }

        // emptying once the decorators are created
        wrappers = null;

        return decorators;
    }

    /**
     * Returns the number of visited elements, used to pre-allocate space for decorators
     *
     * @return number of method parameters
     */
    public int size() {
        return wrappers.size();
    }

    /**
     * Used for saving structures while visiting
     */
    private class SingleVariableDeclarationWrapper {
        SingleVariableDeclaration node;
        ParameterizedTypeVisitor parameterizedTypeVisitor = new ParameterizedTypeVisitor();
    }
}
