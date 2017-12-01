package com.stardisblue.ast.visitor;

import com.stardisblue.ast.info.MethodDeclarationInfo;
import com.stardisblue.ast.info.TypeDeclarationInfo;
import com.stardisblue.logging.Logger;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;

import java.util.ArrayList;
import java.util.List;

public class MethodDeclarationVisitor extends ASTVisitor {

    private List<MethodDeclarationWrapper> wrappers = new ArrayList<>();
    private List<MethodDeclarationInfo> decorators = new ArrayList<>();

    @Override
    public boolean visit(MethodDeclaration node) {
        MethodDeclarationWrapper w = new MethodDeclarationWrapper();
        w.node = node;

        for (Object obj : node.parameters()) {
            SingleVariableDeclaration singleVar = (SingleVariableDeclaration) obj;
            w.parameterVisitor.visit(singleVar);
        }

        node.accept(w.methodInvocationVisitor);

        this.wrappers.add(w);

        return super.visit(node);
    }

    /**
     * Used to create the decorators and resolve dependencies
     *
     * @param compilationUnit used to calculate number of lines
     * @param parent          parent element
     * @return an array of decorators decorating all the elements found while visiting
     */
    public List<MethodDeclarationInfo> infos(CompilationUnit compilationUnit, TypeDeclarationInfo parent) {
        // if the decorators are already set
        if (!decorators.isEmpty()) return decorators;

        decorators = new ArrayList<>(wrappers.size());

        Logger.println("Methods", "", Logger.DEBUG);

        for (MethodDeclarationWrapper w : wrappers) {
            // - creating decorator
            MethodDeclarationInfo method = new MethodDeclarationInfo(compilationUnit, w.node, parent,
                                                                     w.parameterVisitor.size(),
                                                                     w.methodInvocationVisitor.size());
            // - resolving cyclic dependencies
            method.setup(w.parameterVisitor.infos(method), w.methodInvocationVisitor.infos(method));
            // - adding to the list of decorators
            decorators.add(method);
        }

        // emptying once the info are created
        wrappers = null;

        return decorators;
    }

    /**
     * Returns the number of visited elements, used to pre-allocate space for decorators
     *
     * @return number of declared methods
     */
    public int size() {
        return wrappers.size();
    }

    /**
     * Used for saving structures while visiting
     */
    private class MethodDeclarationWrapper {
        MethodDeclaration node;
        ParameterVisitor parameterVisitor = new ParameterVisitor();
        MethodInvocationVisitor methodInvocationVisitor = new MethodInvocationVisitor();
    }
}
