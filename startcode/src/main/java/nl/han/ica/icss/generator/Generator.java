package nl.han.ica.icss.generator;


import nl.han.ica.icss.ast.AST;
import nl.han.ica.icss.ast.Declaration;
import nl.han.ica.icss.ast.Literal;
import nl.han.ica.icss.ast.Stylerule;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.StringJoiner;

public class Generator {

    public String generate(AST ast) {
        StringJoiner joiner = new StringJoiner("\n\n");
        ast.root.body.stream().map((node) -> displayStylerule((Stylerule) node)).forEach(joiner::add);
        return joiner.toString();
    }

    private String displayStylerule(Stylerule stylerule) {
        StringBuilder stringBuilder = new StringBuilder();
        stylerule.selectors.forEach((selector -> stringBuilder.append(selector).append(" ")));
        stringBuilder.append("{\n");
        stylerule.body.stream().map((node) -> displayProperty((Declaration) node)).forEach((s) -> stringBuilder.append("  ").append(s).append("\n"));
        stringBuilder.append("}");
        return stringBuilder.toString();
    }

    private String displayProperty(Declaration declaration) {
        return declaration.property.name +
                ": " +
                getExpressionValue((Literal) declaration.expression) +
                ";";
    }

    private String getExpressionValue(Literal literal) {
        if (literal.getExpressionType() == ExpressionType.PIXEL) {
            return ((PixelLiteral) literal).value + "px";
        } else if (literal.getExpressionType() == ExpressionType.PERCENTAGE) {
            return ((PercentageLiteral) literal).value + "%";
        } else if (literal.getExpressionType() == ExpressionType.COLOR) {
            return ((ColorLiteral) literal).value;
        } else
            throw new RuntimeException("Something must have really gotten wrong, as all possible options were reached...");
    }


}
