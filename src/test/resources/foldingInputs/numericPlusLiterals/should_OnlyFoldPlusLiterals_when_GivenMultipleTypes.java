package foldingInputs.numericPlusLiterals;

public class should_OnlyFoldParenthesizedLiterals_when_GivenMultipleTypes {
    public int name(final int y) {
        final int x = 8;
        final boolean b = ((true));
        final Integer i = (null);
        final char c = ('c');
        final String s = new String(("Hello"));
        final int t = 13;
        if (42 <= 42) {
            int z = 10;
        }
        return x;
    }
}
