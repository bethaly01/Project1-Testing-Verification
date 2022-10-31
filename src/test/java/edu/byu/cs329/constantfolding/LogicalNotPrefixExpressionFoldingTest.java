package edu.byu.cs329.constantfolding;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.net.URI;

import org.eclipse.jdt.core.dom.ASTNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import edu.byu.cs329.TestUtils;

@DisplayName("Tests for folding LogicalNotPrefixExpression types")
public class LogicalNotPrefixExpressionFoldingTest {
  LogicalNotPrefixExpressionFolding folderUnderTest = null;
  final String prefix = "foldingInputs/LogicalNotPrefixExpression";

  @BeforeEach
  void beforeEach() {
    folderUnderTest = new LogicalNotPrefixExpressionFolding();
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
  void should_ThrowRuntimeException_when_RootIsNotACompilationUnitAndHasNoParent() {
    assertThrows(RuntimeException.class, () -> {
      URI uri = TestUtils.getUri(this, "");
      ASTNode compilationUnit = TestUtils.getCompilationUnit(uri);
      ASTNode root = compilationUnit.getAST().newNullLiteral();
      folderUnderTest.fold(root);
    });
  }

  @Test
  @DisplayName("Should return when Logical Not operands are not boolean type")
  void throwRuntimeException_when_operandsAreNotBooleanTypes() {
    String rootName = "foldingInputs/LogicalNotLiterals/should_NotFoldAnything_when_NotOperandsAreNotBool.java";
    ASTNode root = TestUtils.getAstNodeFor(this, rootName);
    folderUnderTest.fold(root);
    }

  @Test
  @DisplayName("Should not fold anything when there are no logical not literals")
  void should_NotFoldAnything_when_ThereAreNoIfStatementLiterals() {
    String rootName = "foldingInputs/LogicalNotLiterals/should_NotFold_when_ThereAreNoNotLiterals.java";
    String expectedName = "foldingInputs/LogicalNotLiterals/should_NotFold_when_ThereAreNoNotLiterals.java";
    TestUtils.assertDidNotFold(this, rootName, expectedName, folderUnderTest);
  }

  @Test
  @DisplayName("Should only fold logical not expression literals when given multiple types")
  void should_OnlyFoldIfStatementsLiterals_when_GivenMultipleTypes() {
    String rootName = "foldingInputs/LogicalNotLiterals/should_OnlyFoldNotLiterals_when_GivenMultipleTypes-root.java";
    String expectedName = "foldingInputs/LogicalNotLiterals/should_OnlyFoldNotLiterals_when_GivenMultipleTypes.java";
    TestUtils.assertDidFold(this, rootName, expectedName, folderUnderTest);
  }

}