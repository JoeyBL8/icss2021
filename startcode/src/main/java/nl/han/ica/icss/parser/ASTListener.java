package nl.han.ica.icss.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Stack;


import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.HANStack;
import nl.han.ica.datastructures.IHANStack;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.*;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;

/**
 * This class extracts the ICSS Abstract Syntax Tree from the Antlr Parse tree.
 */
public class ASTListener extends ICSSBaseListener {
	
	//Accumulator attributes:
	private AST ast;

	//Use this to keep track of the parent nodes when recursively traversing the ast
	private IHANStack<ASTNode> currentContainer;

	public ASTListener() {
		ast = new AST();
		currentContainer = new HANStack<>();
	}

    public AST getAST() {
        return ast;
    }

	@Override
	public void exitStylesheet(ICSSParser.StylesheetContext ctx) {
		Stylesheet stylesheet = new Stylesheet();
		while (currentContainer.peek() != null) {
			stylesheet.body.add(currentContainer.pop());
		}
		Collections.reverse(stylesheet.body);
		ast.root = stylesheet;
	}

	@Override
	public void exitStylerule(ICSSParser.StyleruleContext ctx) {
		Stylerule stylerule = new Stylerule();
		stylerule.body = ((Body)currentContainer.pop()).body;
		var selectors = new ArrayList<Selector>();
		selectors.add((Selector) currentContainer.pop());
		stylerule.selectors = selectors;
		currentContainer.push(stylerule);
	}

	@Override
	public void exitIdSelector(ICSSParser.IdSelectorContext ctx) {
		IdSelector idSelector = new IdSelector(ctx.getChild(0).getText());
		currentContainer.push(idSelector);
	}

	@Override
	public void exitClassSelector(ICSSParser.ClassSelectorContext ctx) {
		ClassSelector classSelector = new ClassSelector(ctx.getChild(0).getText());
		currentContainer.push(classSelector);
	}

	@Override
	public void exitTagSelector(ICSSParser.TagSelectorContext ctx) {
		TagSelector tagSelector = new TagSelector(ctx.getChild(0).getText());
		currentContainer.push(tagSelector);
	}

	@Override
	public void exitBody(ICSSParser.BodyContext ctx) {
		Body body = new Body();
		var bodyList = new ArrayList<ASTNode>();
		while(currentContainer.peek() != null) {
			if (currentContainer.peek() instanceof Declaration
					|| currentContainer.peek() instanceof IfClause
					|| currentContainer.peek() instanceof VariableAssignment) {
				bodyList.add(currentContainer.pop());
			} else {
				break;
			}
		}
		Collections.reverse(bodyList);
		body.body = bodyList;
		currentContainer.push(body);
	}

	@Override
	public void exitProperty(ICSSParser.PropertyContext ctx) {
		Declaration declaration = new Declaration();
		declaration.expression = (Expression) currentContainer.pop();
		declaration.property = new PropertyName(ctx.getChild(0).getText());
		currentContainer.push(declaration);
	}

	@Override
	public void exitIfClause(ICSSParser.IfClauseContext ctx) {
		IfClause ifClause = new IfClause();
		if (currentContainer.peek() instanceof ElseClause) {
			ifClause.elseClause = (ElseClause) currentContainer.pop();
		}
		ifClause.body = ((Body)currentContainer.pop()).body;
		ifClause.conditionalExpression = (Expression) currentContainer.pop();
		currentContainer.push(ifClause);
	}

	@Override
	public void exitElseClause(ICSSParser.ElseClauseContext ctx) {
		ElseClause elseClause = new ElseClause();
		elseClause.body = ((Body) currentContainer.pop()).body;
		currentContainer.push(elseClause);
	}

	@Override
	public void exitAddSubOperation(ICSSParser.AddSubOperationContext ctx) {
		var operator = ctx.getChild(1).getText();
		Operation operation;
		if (operator.equals("+")) {
			operation = new AddOperation();
		} else {
			operation = new SubtractOperation();
		}
		operation.rhs = (Expression) currentContainer.pop();
		operation.lhs = (Expression) currentContainer.pop();
		currentContainer.push(operation);
	}

	@Override
	public void exitMulDivOperation(ICSSParser.MulDivOperationContext ctx) {
		var operator = ctx.getChild(1).getText();
		Operation operation;
		if (operator.equals("*")) {
			operation = new MultiplyOperation();
		} else {
			operation = new DivisionOperation();
		}
		operation.rhs = (Expression) currentContainer.pop();
		operation.lhs = (Expression) currentContainer.pop();
		currentContainer.push(operation);
	}

	@Override
	public void exitExponentOperation(ICSSParser.ExponentOperationContext ctx) {
		Operation operation = new ExponentOperation();
		operation.rhs = (Expression) currentContainer.pop();
		operation.lhs = (Expression) currentContainer.pop();
		currentContainer.push(operation);
	}

	@Override
	public void exitVariableAssignment(ICSSParser.VariableAssignmentContext ctx) {
		VariableAssignment variableAssignment = new VariableAssignment();
		variableAssignment.expression = (Expression) currentContainer.pop();
		variableAssignment.name = (VariableReference) currentContainer.pop();
		currentContainer.push(variableAssignment);
	}

	@Override
	public void exitColorLiteral(ICSSParser.ColorLiteralContext ctx) {
		ASTNode colorLiteral = new ColorLiteral(ctx.getChild(0).getText());
		currentContainer.push(colorLiteral);
	}

	@Override
	public void exitPixelLiteral(ICSSParser.PixelLiteralContext ctx) {
		ASTNode pixelLiteral = new PixelLiteral(ctx.getChild(0).getText());
		currentContainer.push(pixelLiteral);
	}

	@Override
	public void exitPercentageLiteral(ICSSParser.PercentageLiteralContext ctx) {
		ASTNode percentageLiteral = new PercentageLiteral(ctx.getChild(0).getText());
		currentContainer.push(percentageLiteral);
	}

	@Override
	public void exitScalarLiteral(ICSSParser.ScalarLiteralContext ctx) {
		ASTNode scalarLiteral = new ScalarLiteral(ctx.getChild(0).getText());
		currentContainer.push(scalarLiteral);
	}

	@Override
	public void exitVariableReference(ICSSParser.VariableReferenceContext ctx) {
		ASTNode variableReference = new VariableReference(ctx.getChild(0).getText());
		currentContainer.push(variableReference);
	}

	@Override
	public void exitBool(ICSSParser.BoolContext ctx) {
		ASTNode boolLiteral = new BoolLiteral(ctx.getChild(0).getText());
		currentContainer.push(boolLiteral);
	}
}