package com.stardisblue.ast.decorator;


import com.stardisblue.logging.Logger;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import java.util.List;

public class MethodDeclarationDecorator extends ASTVisitor {

    private final MethodDeclaration node;
    private final String name;
    private final List<ParameterDecorator> parameters;
    private final List<MethodInvocationDecorator> methodInvocations;
    private String fullName;
    private TypeDeclarationDecorator parent;
    private int numberOfLines;
    private String strParameters;

    public MethodDeclarationDecorator(MethodDeclaration node, List<ParameterDecorator> parameters,
                                      List<MethodInvocationDecorator> methodInvocations) {
        // DI
        this.node = node;
        this.name = node.getName().toString();
        this.parameters = parameters;
        this.methodInvocations = methodInvocations;
    }

    public void inject(TypeDeclarationDecorator node) {
        this.parent = node;
        CompilationUnit cu = this.parent.getParent();
        // counting line numbers
        int startLine = cu.getLineNumber(this.node.getStartPosition());
        // -1 for lenght correction
        int endLine = cu.getLineNumber(this.node.getStartPosition() + this.node.getLength() - 1);
        this.numberOfLines = endLine - startLine;

        Logger.println("└─ " + this.getShortWithParamTypes() + ": " + this.numberOfParameters() + " parameters, "
                               + this.numberOfLines() + " lines",
                       Logger.DEBUG);
        for (MethodInvocationDecorator methodInvocation : this.methodInvocations) {
            methodInvocation.inject(this);
        }
    }

    public int numberOfLines() {
        return numberOfLines;
    }

    public int numberOfParameters() {
        return parameters.size();
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        return parent.getName() + "." + getName();
    }

    public String getFullName() {
        if (fullName == null) {
            fullName = parent.getFullName() + "." + getName() + "(" + getStringParameters() + ")";
        }
        return fullName;
    }

    public String getShortWithParamTypes() {
        return getShortName() + "(" + getCleanStringParameters() + ")";
    }

    public String getFullWithParamTypes() {
        return parent.getFullName() + "." + getName() + "(" + getCleanStringParameters() + ")";
    }

    private String getCleanStringParameters() {
        StringBuilder st = new StringBuilder();

        if (parameters.size() == 0) {
            return "";
        }

        for (ParameterDecorator parameter : parameters) {
            st.append(parameter.getShortName()).append(", ");
        }

        st.delete(st.length() - 2, st.length());
        return st.toString();
    }

    private String getStringParameters() {
        if (strParameters == null) {
            StringBuilder st = new StringBuilder();

            for (ParameterDecorator parameter : parameters) {
                st.append(parameter.getFullName()).append(", ");
            }
            // aand we remove the last ouane
            st.delete(st.length() - 2, st.length());

            return strParameters = st.toString();
        }

        return strParameters;
    }

    public List<MethodInvocationDecorator> getMethodCalls() {
        return methodInvocations;
    }
}
