package nl.han.ica.icss.ast.operations;

import nl.han.ica.icss.ast.Operation;

public class SubtractOperation extends Operation {

    public static final String LABEL = "Subtract";

    @Override
    public String getNodeLabel() {
        return LABEL;
    }
}
