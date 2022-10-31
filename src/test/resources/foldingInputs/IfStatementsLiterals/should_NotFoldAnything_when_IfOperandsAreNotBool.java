package foldingInputs.IfStatementsLiterals;

public class should_NotFoldAnything_when_IfOperandsAreNotBool {
    public int name(final int y) {
        int x = 0;
        if (booleanFunc()) {
            x += 9;
        }

        return x;
    }

    public boolean booleanFunc() {
        return true;
    }
}
