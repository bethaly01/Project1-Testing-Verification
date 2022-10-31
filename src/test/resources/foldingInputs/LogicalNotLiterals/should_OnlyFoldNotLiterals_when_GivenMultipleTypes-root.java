package foldingInputs.LogicalNotLiterals;

public class should_OnlyFoldNotLiterals_when_GivenMultipleTypes {
    public int name(final int y) {
        final int x = 3 + 5;
        final boolean b = !true;
        final Integer i = (null);
        final char c = ('c');
        final boolean bol = !false;
        final String s = new String(("Hello"));
        final int t = 6 + 7;
        if (6 < 10) {
            int z = 10;
        }
        return x;
    }
}
