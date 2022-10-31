package foldingInputs.constantFoldingInput;

public class should_Fold_With_All_Expressions_Classes {
    public int name(final int y) {
        int x = 2;
        int i = 1 + 2;
        if (!(3 < 3)) {
            x = 5;
        }

        return x;
    }
}
