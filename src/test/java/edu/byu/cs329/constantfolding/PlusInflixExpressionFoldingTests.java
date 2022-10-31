package edu.byu.cs329.constantfolding;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;

import org.eclipse.jdt.core.dom.ASTNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import edu.byu.cs329.TestUtils;

@DisplayName("Tests for folding PlusInfixExpression types")
public class PlusInflixExpressionFoldingTests {
    PlusInflixExpressionFolding folderUnderTest = null;

    @BeforeEach
    void beforeEach() {
        folderUnderTest = new PlusInflixExpressionFolding();
    }

    @Test
    @DisplayName("Should throw RuntimeException when root is null")
    void should_ThrowRuntimeException_when_RootIsNull() {
        assertThrows(RuntimeException.class, () -> {
            folderUnderTest.fold(null);
        });
    }

    @Test
    @DisplayName("Should throw RuntimeException when root is not a CompilationUnit and has no parent")
    void throwRuntimeException_when_RootIsNotACompilationUnitAndHasNoParent() {
        assertThrows(RuntimeException.class, () -> {
            URI uri = TestUtils.getUri(this, "");
            ASTNode compilationUnit = TestUtils.getCompilationUnit(uri);
            ASTNode root = compilationUnit.getAST().newNullLiteral();
            folderUnderTest.fold(root);
        });
    }

    @Test
    @DisplayName("Should throw RuntimeException when operands are not integers")
    void throwRuntimeException_when_operandsAreNotIntegers() {
        String rootName = "foldingInputs/numericPlusLiterals/should_NotFoldAnything_when_operandsAreNotIntegers.java";
        ASTNode root = TestUtils.getAstNodeFor(this, rootName);
        assertThrows(RuntimeException.class, () -> folderUnderTest.fold(root));
    }

    @Test
    @DisplayName("Should not fold anything when there are no numeric plus/addition operation literals")
    void should_NotFoldAnything_when_ThereAreNoParenthesizedLiterals() {
        String rootName = "foldingInputs/numericPlusLiterals/should_NotFoldAnything_when_ThereAreNoNumericPlusLiterals.java";
        String expectedName = "foldingInputs/numericPlusLiterals/should_NotFoldAnything_when_ThereAreNoNumericPlusLiterals.java";
        TestUtils.assertDidNotFold(this, rootName, expectedName, folderUnderTest);
    }

    @Test
    @DisplayName("Should only fold numeric plus expression literals when given multiple types")
    void should_OnlyFoldParenthesizedLiterals_when_GivenMultipleTypes() {
        String rootName = "foldingInputs/numericPlusLiterals/should_OnlyFoldPlusLiterals_when_GivenMultipleTypes-root.java";
        String expectedName = "foldingInputs/numericPlusLiterals/should_OnlyFoldPlusLiterals_when_GivenMultipleTypes.java";
        TestUtils.assertDidFold(this, rootName, expectedName, folderUnderTest);
    }
}
