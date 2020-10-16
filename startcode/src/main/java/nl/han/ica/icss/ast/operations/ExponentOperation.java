package nl.han.ica.icss.ast.operations;

import nl.han.ica.icss.ast.Operation;

public class ExponentOperation extends Operation {
    public static final String LABEL = "Exponent";

    @Override
    public String getNodeLabel() {
        return LABEL;
    }
}
