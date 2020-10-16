package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.operations.*;
import nl.han.ica.icss.ast.types.ExpressionType;


import java.util.ArrayList;
import java.util.HashMap;

import static nl.han.ica.icss.ast.types.ExpressionType.*;


public class Checker {

    private IHANLinkedList<HashMap<String, ExpressionType>> variableTypes;

    public void check(AST ast) {
        variableTypes = new HANLinkedList<>();
        checkStyleSheet(ast.root);
    }

    private void checkStyleSheet(Stylesheet stylesheet) {
        variableTypes.addFirst(new HashMap<>());
        stylesheet.body.forEach((node) -> {
            if (node instanceof VariableAssignment) {
                checkVariableAssignment((VariableAssignment) node);
            }
            if (node instanceof Stylerule) {
                checkStyleRule((Stylerule) node);
            }
        });
        variableTypes.removeFirst();
    }

    private void checkStyleRule(Stylerule stylerule) {
        checkBody(stylerule.body);
    }

    private void checkBody(ArrayList<ASTNode> body) {
        variableTypes.addFirst(new HashMap<>());
        body.forEach((node) -> {
            if (node instanceof Declaration) {
                checkDeclaration((Declaration) node);
            }
            if (node instanceof VariableAssignment) {
                checkVariableAssignment((VariableAssignment) node);
            }
            if (node instanceof IfClause) {
                checkIfClause((IfClause) node);
            }
        });
        variableTypes.removeFirst();
    }

    private void checkDeclaration(Declaration node) {
        ExpressionType expressionType = checkExpression(node.expression);
        if (!typeMatchesProperty(node.property.name, expressionType)) {
            node.setError("Invalid value assigned to property, got: " + expressionType.name());
        }
    }

    private ExpressionType checkExpression(Expression expression) {
        if (expression instanceof Operation) {
            return checkOperation((Operation) expression);
        }
        if (expression instanceof VariableReference) {
            VariableReference reference = (VariableReference) expression;
            ExpressionType type = getVariableType(reference.name);
            if (type == UNDEFINED) {
                reference.setError("Variable was referenced but not yet declared");
            }
            return type;
        }
        if (expression instanceof Literal) {
            Literal literal = (Literal) expression;
            return literal.getExpressionType();
        }
        return null;
    }

    private ExpressionType checkOperation(Operation operation) {
        ExpressionType leftType = checkExpression(operation.lhs);
        ExpressionType rightType = checkExpression(operation.rhs);
        // if a previous error occurred, keep throwing up the expressionType
        if (leftType == UNDEFINED || rightType == UNDEFINED) {
            return UNDEFINED;
        } else if (leftType == COLOR || rightType == COLOR) {
            operation.setError("You cannot use a color inside an operation");
            return UNDEFINED;
        } else if (leftType == BOOL || rightType == BOOL) {
            operation.setError("You cannot use a boolean value inside an operation");
            return UNDEFINED;
        } else if (operation instanceof AddOperation || operation instanceof SubtractOperation) {
            if (leftType != rightType) {
                operation.setError("Add and subtract operations should always be of the same type.");
                return UNDEFINED;
            } else {
                return leftType;
            }
        } else if (operation instanceof MultiplyOperation) {
            if (leftType != SCALAR && rightType != SCALAR) {
                operation.setError("Multiplication Operations should always have at least 1 scalar expression");
                return UNDEFINED;
            } else {
                if (leftType == SCALAR) {
                    return rightType;
                } else {
                    return leftType;
                }
            }
        } else if (operation instanceof DivisionOperation) {
            if (rightType != SCALAR) {
                operation.setError("Division can only been done through a scalar.");
                return UNDEFINED;
            } else {
                return leftType;
            }
        } else if (operation instanceof ExponentOperation) {
            if (rightType != SCALAR) {
                operation.setError("The exponent can only be a scalar value");
                return UNDEFINED;
            } else if (leftType != SCALAR) {
                operation.setError("The base can only be a scalar value");
                return UNDEFINED;
            } else {
                return leftType;
            }
        }
        return UNDEFINED;
    }

    private ExpressionType getVariableType(String name) {
        // way too high of a complexity (O(N^2)). But the depth is not expected to be too great. At a scope depth of 10, the running time will be 50.
        for (int i = 0; i < variableTypes.getSize(); i++) {
            var current = variableTypes.get(i);
            if (current.containsKey(name)) {
                return current.get(name);
            }
        }
        return UNDEFINED;
    }

    private boolean variableDefined(String name) {
        for (int i = 0; i < variableTypes.getSize(); i++) {
            var current = variableTypes.get(i);
            if (current.containsKey(name)) {
                return true;
            }
        }
        return false;
    }

    private void checkIfClause(IfClause ifClause) {
        var type = checkExpression(ifClause.getConditionalExpression());
        if (type != BOOL) {
            ifClause.setError("Expected a boolean expression, got " + type.name());
        }
        checkBody(ifClause.body);
        if (ifClause.elseClause != null) {
            checkBody(ifClause.elseClause.body);
        }
    }

    private void checkVariableAssignment(VariableAssignment variableAssignment) {
        ExpressionType expressionType = checkExpression(variableAssignment.expression);
        if (expressionType == UNDEFINED) {
            variableAssignment.setError("Attempting to assign a unsupported expression to this variable");
            return;
        }
        if (variableDefined(variableAssignment.name.name)) {
            ExpressionType oldType = getVariableType(variableAssignment.name.name);
            if (expressionType != oldType) {
                variableAssignment.setError("Variable was already declared as " + oldType.name() + " and cannot be declared as " + expressionType.name());
                return;
            }
        }
        variableTypes.get(0).put(variableAssignment.name.name, expressionType);
    }

    private boolean typeMatchesProperty(String propertyName, ExpressionType type) {
        if (propertyName.equals("color") || propertyName.equals("background-color")) {
            return type == COLOR;
        }
        if (propertyName.equals("width") || propertyName.equals("height")) {
            return type == PIXEL || type == PERCENTAGE;
        }
        return false;
    }
}
