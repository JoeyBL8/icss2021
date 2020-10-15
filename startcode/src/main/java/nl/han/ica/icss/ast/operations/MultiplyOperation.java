package nl.han.ica.icss.ast.operations;

import nl.han.ica.icss.ast.Operation;

public class MultiplyOperation extends Operation {

    public static final String LABEL = "Multiply";

    @Override
    public String getNodeLabel() {
        return LABEL;
    }
}
