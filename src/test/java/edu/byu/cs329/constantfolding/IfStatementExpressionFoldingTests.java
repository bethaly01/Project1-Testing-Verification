package edu.byu.cs329.constantfolding;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;

import org.eclipse.jdt.core.dom.ASTNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import edu.byu.cs329.TestUtils;

@DisplayName("Tests for folding IfStatementExpression types")
public class IfStatementExpressionFoldingTests {
    IfStatementFolding folderUnderTest = null;

    @BeforeEach
    void beforeEach() {
        folderUnderTest = new IfStatementFolding();
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
    @DisplayName("Should return when If statement operands are not boolean type")
    void throwRuntimeException_when_operandsAreNotBooleanTypes() {
        String rootName = "foldingInputs/IfStatementsLiterals/should_NotFoldAnything_when_IfOperandsAreNotBool.java";
        ASTNode root = TestUtils.getAstNodeFor(this, rootName);
       folderUnderTest.fold(root) ;
    }

    @Test
    @DisplayName("Should not fold anything when there are no if statement operation literals")
    void should_NotFoldAnything_when_ThereAreNoIfStatementLiterals() {
        String rootName = "foldingInputs/IfStatementsLiterals/should_NotFold_when_ThereIsNoIfLiterals.java";
        String expectedName = "foldingInputs/IfStatementsLiterals/should_NotFold_when_ThereIsNoIfLiterals.java";
        TestUtils.assertDidNotFold(this, rootName, expectedName, folderUnderTest);
    }

    @Test
    @DisplayName("Should only fold numeric less than expression literals when given multiple types")
    void should_OnlyFoldIfStatementsLiterals_when_GivenMultipleTypes() {
        String rootName = "foldingInputs/IfStatementsLiterals/should_OnlyFoldIfStatementLiterals_when_GivenMultipleTypes-root.java";
        String expectedName = "foldingInputs/IfStatementsLiterals/should_OnlyFoldIfStatementLiterals_when_GivenMultipleTypes.java";
        TestUtils.assertDidFold(this, rootName, expectedName, folderUnderTest);
    }
}
