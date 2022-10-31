package foldingInputs.LogicalNotLiterals;

public class should_NotFold_when_ThereAreNoNotLiterals {
    public int name(final int y) {
        final int x = 3 + 5;
        final char c = ('c');
        final String s = new String(("Hello"));
        final int t = 6 + 7;
        if (42 <= 42) {
            int z = 10;
        }
        return x;
    }
}
