package com.stardisblue.ast.info;


import com.stardisblue.logging.Logger;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import java.util.ArrayList;
import java.util.List;

/**
 * Decorating MethodDeclaration
 */
public class MethodDeclarationInfo {

    private final TypeDeclarationInfo parent;
    private final List<ParameterInfo> parameters;
    private final List<MethodInvocationInfo> methodInvocations;

    private final String name;
    private String fullName;
    private int numberOfLines;
    private String strParameters;

    /**
     * Default constructer, elements are passed through via DI,
     * the arrays are preallocated using size parameters
     *
     * @param cu                    compilation unit used to get numberoflines
     * @param parent                parent element
     * @param node                  decorated element
     * @param parametersSize        number of parameters
     * @param methodInvocationsSize number of methodInvocations
     */
    public MethodDeclarationInfo(CompilationUnit cu, MethodDeclaration node, TypeDeclarationInfo parent,
                                 int parametersSize, int methodInvocationsSize) {
        // DI
        this.parent = parent;
        this.name = node.getName().toString();
        this.parameters = new ArrayList<>(parametersSize);
        this.methodInvocations = new ArrayList<>(methodInvocationsSize);

        // counting line numbers
        int startLine = cu.getLineNumber(node.getStartPosition());
        // -1 for lenght correction
        int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength() - 1);

        this.numberOfLines = endLine - startLine;

        Logger.println("└─ " + this.getShortName() + ": " + parametersSize + " parameters, "
                               + this.numberOfLines() + " lines",
                       Logger.DEBUG);
    }

    /**
     * Need to be called once, used to resolve cyclic dependency injection
     *
     * @param parameters        the array of parameters
     * @param methodInvocations the array of method invocations
     */
    public void setup(List<ParameterInfo> parameters, List<MethodInvocationInfo> methodInvocations) {
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

        for (ParameterInfo parameter : parameters) {
            st.append(parameter.getShortName()).append(", ");
        }

        st.delete(st.length() - 2, st.length());
        return st.toString();
    }

    private String getStringParameters() {
        if (strParameters == null) {
            StringBuilder st = new StringBuilder();

            for (ParameterInfo parameter : parameters) {
                st.append(parameter.getFullName()).append(", ");
            }
            // aand we remove the last ouane
            st.delete(st.length() - 2, st.length());

            return strParameters = st.toString();
        }

        return strParameters;
    }

    public List<MethodInvocationInfo> getMethodCalls() {
        return methodInvocations;
    }
}
