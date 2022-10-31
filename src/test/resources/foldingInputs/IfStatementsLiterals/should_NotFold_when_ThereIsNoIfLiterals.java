package foldingInputs.IfStatementsLiterals;

public class should_NotFold_when_ThereIsNoIfLiterals {
    public int name(final int y) {
        final int x = 3 + 5;
        final boolean b = ((true));
        final Integer i = (null);
        final char c = 'c';
        final String s = new String("Hello");
        final int z = 4 - 3;
        return x;
    }
}
