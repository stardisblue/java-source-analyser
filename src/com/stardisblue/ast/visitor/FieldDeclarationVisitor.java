package com.stardisblue.ast.visitor;

import com.stardisblue.ast.decorator.FieldDeclarationDecorator;
import com.stardisblue.ast.decorator.TypeDeclarationDecorator;
import com.stardisblue.logging.Logger;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;

import java.util.ArrayList;
import java.util.List;

public class FieldDeclarationVisitor extends ASTVisitor {
    private List<FieldDeclaration> fields = new ArrayList<>();
    private List<FieldDeclarationDecorator> decorators = new ArrayList<>();

    @Override
    public boolean visit(FieldDeclaration node) {
        fields.add(node);

        return super.visit(node);
    }

    /**
     * Used to create the decorators and resolve dependencies
     *
     * @param parent parent element
     * @return an array of decorators decorating all the elements found while visiting
     */
    public List<FieldDeclarationDecorator> decorators(TypeDeclarationDecorator parent) {
        // if the decorators are already set
        if (!decorators.isEmpty()) return decorators;

        decorators = new ArrayList<>(fields.size());

        Logger.println("Fields", "", Logger.DEBUG);

        for (FieldDeclaration field : fields) {
            // - creating decorator
            // x cyclic dependencies
            // - adding to the list of decorators
            decorators.add(new FieldDeclarationDecorator(parent, field));

        }

        // emptying once the decorator are created
        fields = null;

        return decorators;
    }

    /**
     * Returns the number of visited elements, used to pre-allocate space for decorators
     *
     * @return number of fields
     */
    public int size() {
        return fields.size();
    }
}
