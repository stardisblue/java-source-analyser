package com.stardisblue.ast.info;

import com.stardisblue.logging.Logger;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;

import java.util.ArrayList;

/**
 * Decorating MethodInvocation
 */
public class MethodInvocationInfo {
    private final MethodDeclarationInfo parent;
    private final String name;
    private final String type;
    private final String packageName;
    private final String classType;
    private final boolean binded;
    private final ArrayList<String> parameters = new ArrayList<>();

    /**
     * Default constructer, elements are passed through via DI,
     *
     * @param parent parent element
     * @param node   decorated element
     */
    public MethodInvocationInfo(MethodDeclarationInfo parent,
                                MethodInvocation node) {
        this.parent = parent;

        IMethodBinding binding = node.resolveMethodBinding();

        if (binding != null) {
            this.binded = true;
            this.name = binding.getName();

            for (ITypeBinding iTypeBinding : binding.getParameterTypes()) {

                ITypeBinding typeDeclaration = iTypeBinding.getTypeDeclaration();

                parameters.add(typeDeclaration.getName());
            }

            this.type = binding.getDeclaringClass().getName();
            this.packageName = binding.getDeclaringClass().getPackage().getName();
            this.classType = binding.getDeclaringClass().getTypeDeclaration().getName();

            Logger.println("  └─ " + this.getShortWithParamTypes(), Logger.DEBUG);

        } else {
            this.binded = false;
            this.name = "";
            this.type = "";
            this.packageName = "";
            this.classType = "";
        }
    }

    public boolean isBinded() {
        return binded;
    }

    public String getShortName() {
        return getClassType() + "." + getName();
    }

    public String getFullName() {
        return getPackageName() + "." + getType() + "." + getName() + "(" + String.join(", ", parameters) + ")";
    }

    public String getClassType() {
        return classType;
    }

    public String getName() {
        return name;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getClassFullName(){
        return getPackageName() + "." + getType();
    }

    public String getType() {
        return type;
    }

    public String getShortWithParamTypes() {
        return getShortName() + "(" + String.join(", ", parameters) + ")";
    }
}
