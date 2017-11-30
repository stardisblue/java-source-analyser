package com.stardisblue.ast.decorator;


import com.stardisblue.logging.Logger;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import java.util.ArrayList;
import java.util.List;

/**
 * Decorating MethodDeclaration
 */
public class MethodDeclarationDecorator {

    private final TypeDeclarationDecorator parent;
    private final MethodDeclaration node;
    private final List<ParameterDecorator> parameters;
    private final List<MethodInvocationDecorator> methodInvocations;

    private final String name;
    private String fullName;
    private int numberOfLines;
    private String strParameters;

    /**
     * Default constructer, elements are passed through via DI,
     * the arrays are preallocated using size parameters
     *
     * @param parent                parent element
     * @param node                  decorated element
     * @param parametersSize        number of parameters
     * @param methodInvocationsSize number of methodInvocations
     */
    public MethodDeclarationDecorator(TypeDeclarationDecorator parent, MethodDeclaration node,
                                      int parametersSize, int methodInvocationsSize) {
        // DI
        this.node = node;
        this.name = node.getName().toString();
        this.parameters = new ArrayList<>(parametersSize);
        this.methodInvocations = new ArrayList<>(methodInvocationsSize);
        this.parent = parent;

        CompilationUnit cu = this.parent.getParent();
        // counting line numbers
        int startLine = cu.getLineNumber(this.node.getStartPosition());
        // -1 for lenght correction
        int endLine = cu.getLineNumber(this.node.getStartPosition() + this.node.getLength() - 1);

        this.numberOfLines = endLine - startLine;

        Logger.println("└─ " + this.getShortWithParamTypes() + ": " + parametersSize + " parameters, "
                               + this.numberOfLines() + " lines",
                       Logger.DEBUG);
    }

    /**
     * Need to be called once, used to resolve cyclic dependency injection
     *
     * @param parameters        the array of parameters
     * @param methodInvocations the array of method invocations
     */
    public void setup(List<ParameterDecorator> parameters, List<MethodInvocationDecorator> methodInvocations) {
        this.parameters.addAll(parameters);
        this.methodInvocations.addAll(methodInvocations);
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
