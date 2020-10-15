package nl.han.ica.icss.ast.operations;

import nl.han.ica.icss.ast.Operation;

public class DivisionOperation extends Operation {

    public static final String LABEL = "Division";

    @Override
    public String getNodeLabel() {
        return LABEL;
    }
}
