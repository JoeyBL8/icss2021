package nl.han.ica.icss.transforms;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.BoolLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.operations.*;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static nl.han.ica.icss.ast.types.ExpressionType.*;

public class EvalExpressions implements Transform {

    private IHANLinkedList<HashMap<String, Literal>> variableValues;

    public EvalExpressions() {
        //variableValues = new HANLinkedList<>();
    }

    @Override
    public void apply(AST ast) {
        variableValues = new HANLinkedList<>();
        applyBody(ast.root.body);
    }

    private void apply(ASTNode node) {
        if (node instanceof VariableAssignment) {
            VariableAssignment variableAssignment = (VariableAssignment) node;
            Literal literal = evalExpression(variableAssignment.expression);
            variableValues.get(0).put(variableAssignment.name.name, literal);
        } else if (node instanceof Stylerule) {
            Stylerule stylerule = (Stylerule) node;
            applyBody(stylerule.body);
        } else if (node instanceof IfClause) {
            IfClause ifClause = (IfClause) node;
            ifClause.conditionalExpression = evalExpression(ifClause.getConditionalExpression());

            applyBody(ifClause.body);
            if (ifClause.elseClause != null) {
                applyBody(ifClause.elseClause.body);
            }
        } else if (node instanceof Declaration) {
            Declaration declaration = (Declaration) node;
            declaration.expression = evalExpression(declaration.expression);
        } else {
            throw new RuntimeException("Unexpected node found: " + node);
        }
    }

    private void applyBody(List<ASTNode> body) {
        variableValues.addFirst(new HashMap<>());
        body.forEach(this::apply);
        variableValues.removeFirst();
    }

    private Literal evalExpression(Expression expression) {
        if (expression instanceof Literal) {
            return (Literal) expression;
        } else if (expression instanceof VariableReference) {
            return getLiteralFromVariable(((VariableReference) expression).name);
        } else {
            return evalOperation((Operation) expression);
        }
    }

    private Literal evalOperation(Operation operation) {
        Literal left = evalExpression(operation.lhs);
        Literal right = evalExpression(operation.rhs);
        int value = calculateValue(operation, left, right);
        return createLiteralFromTypeAndValue(left, right, value);
    }

    private int calculateValue(Operation operation, Literal left, Literal right) {
        int value;
        switch (operation.getNodeLabel()) {
            case AddOperation.LABEL:
                value = parseLiteral(left) + parseLiteral(right);
                break;
            case SubtractOperation.LABEL:
                value = parseLiteral(left) - parseLiteral(right);
                break;
            case MultiplyOperation.LABEL:
                value = parseLiteral(left) * parseLiteral(right);
                break;
            case DivisionOperation.LABEL:
                int rightValue = parseLiteral(right);
                if (rightValue == 0) {
                    operation.setError("You cannot divide by 0 dummy");
                    value = 0;
                } else {
                    value = parseLiteral(left) / rightValue;
                }
                break;
            case ExponentOperation.LABEL:
                int exponent = parseLiteral(right);
                value = (int)Math.pow(parseLiteral(left), exponent);
                break;
            default:
                throw new RuntimeException("Checker failed and let something through... This shouldn't be reached...");
        }
        return value;
    }

    private Literal createLiteralFromTypeAndValue(Literal left, Literal right, int value) {
        if (left.getExpressionType() == PIXEL || right.getExpressionType() == PIXEL) {
            return new PixelLiteral(value);
        } else if (left.getExpressionType() == PERCENTAGE || right.getExpressionType() == PERCENTAGE) {
            return new PercentageLiteral(value);
        } else {
            return new ScalarLiteral(value);
        }
    }

    private Literal getLiteralFromVariable(String name) {
        for (int i = 0; i < variableValues.getSize(); i++) {
            var current = variableValues.get(i);
            if (current.containsKey(name)) {
                return current.get(name);
            }
        }
        throw new RuntimeException("This shouldn't be reached. A variable was referenced out of scope or not declared.");
    }

    private int parseLiteral(Literal literal) {
        if (literal.getExpressionType() == PIXEL) return ((PixelLiteral) literal).value;
        if (literal.getExpressionType() == PERCENTAGE) return ((PercentageLiteral) literal).value;
        return ((ScalarLiteral) literal).value;
    }

}
