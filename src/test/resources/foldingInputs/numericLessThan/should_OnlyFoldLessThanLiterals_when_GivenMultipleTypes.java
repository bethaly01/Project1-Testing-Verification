package foldingInputs.numericLessThan;

public class should_OnlyFoldLessThanLiterals_when_GivenMultipleTypes {
    public int name(final int y) {
        final int x = 2 + 3;
        final boolean b = true;
        final Integer i = (null);
        final char c = ('c');
        final boolean iBoolean = false;
        final String s = new String(("Hello"));
        final int t = 6 + 7;
        if (42 <= 42) {
            int z = 10;
        }
        return x;
    }
    }
}
