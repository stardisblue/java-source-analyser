package com.stardisblue.ast.decorator;

import com.stardisblue.logging.Logger;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;

import java.util.ArrayList;

public class MethodInvocationDecorator {


    private final MethodInvocation node;
    private final String name;
    private final String type;
    private final String packageName;
    private final String classType;
    private final boolean binded;
    private final ArrayList<String> parameters = new ArrayList<>();
    private MethodDeclarationDecorator parent;

    public MethodInvocationDecorator(MethodInvocation node) {
        this.node = node;
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
        } else {
            this.binded = false;
            this.name = "";
            this.type = "";
            this.packageName = "";
            this.classType = "";
        }
    }

    public void inject(MethodDeclarationDecorator node) {
        this.parent = node;
        Logger.println("  └─ " + this.getFullName(), Logger.DEBUG);
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

    public String getType() {
        return type;
    }

    public MethodInvocation getNode() {
        return node;
    }

    public String getShortWithParamTypes() {
        return getShortName()+ "(" + String.join(", ", parameters) + ")";
    }
}
