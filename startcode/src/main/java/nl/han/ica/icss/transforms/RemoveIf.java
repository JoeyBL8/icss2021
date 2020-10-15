package nl.han.ica.icss.transforms;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.BoolLiteral;

import java.util.ArrayList;
import java.util.List;

public class RemoveIf implements Transform {

    @Override
    public void apply(AST ast) {
        removeUnspupportedCSSStructures(ast.root.body);
        ast.root.body.forEach(this::apply);
    }

    private void apply(ASTNode node) {
        if (node instanceof Stylerule) {
            Stylerule stylerule = (Stylerule) node;
            stylerule.body = evaluateIf(stylerule.body);
            removeUnspupportedCSSStructures(stylerule.body);
        } else throw new RuntimeException("There only should be StyleRules at this point...");
    }

    private ArrayList<ASTNode> evaluateIf(List<ASTNode> body) {
        ArrayList<ASTNode> toReturn = new ArrayList<>();
        for (ASTNode astNode : body) {
            if (astNode instanceof IfClause) {
                IfClause ifClause = (IfClause) astNode;
                if (((BoolLiteral)ifClause.conditionalExpression).value) {
                    toReturn.addAll(evaluateIf(ifClause.body));
                } else if (ifClause.elseClause != null) {
                    toReturn.addAll(evaluateIf(ifClause.elseClause.body));
                }
            } else {
                toReturn.add(astNode);
            }
        }
        return toReturn;
    }

    private void removeUnspupportedCSSStructures(List<ASTNode> body) {
        body.removeIf((node) -> node instanceof VariableAssignment || node instanceof IfClause);
    }
}
