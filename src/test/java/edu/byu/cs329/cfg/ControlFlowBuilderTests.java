package edu.byu.cs329.cfg;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Statement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import edu.byu.cs329.TestUtils;

@DisplayName("Tests for ControlFlowBuilder")
public class ControlFlowBuilderTests {
  ControlFlowGraphBuilder unitUnderTest = null;
  ControlFlowGraph controlFlowGraph = null;
  StatementTracker statementTracker = null;

  @BeforeEach
  void beforeEach() {
    unitUnderTest = new ControlFlowGraphBuilder();
  }

  void init(String fileName) {
    ASTNode node = TestUtils.getASTNodeFor(this, fileName);
    List<ControlFlowGraph> cfgList = unitUnderTest.build(node);
    assertEquals(1, cfgList.size());
    controlFlowGraph = cfgList.get(0);
    statementTracker = new StatementTracker(node);
  }

  @Test
  @Tag("MethodDeclaration")
  @DisplayName("Should set start and end same when empty method declaration")
  void should_SetStartAndEndSame_when_EmptyMethodDeclaration() {
    String fileName = "cfgInputs/should_SetStartAndEndSame_when_EmptyMethodDeclaration.java";
    init(fileName);
    assertAll("Method declaration with empty block",
        () -> assertNotNull(controlFlowGraph.getMethodDeclaration()),
        () -> assertEquals(controlFlowGraph.getStart(), controlFlowGraph.getEnd()));
  }

  @Test
  @Tag("MethodDeclaration")
  @DisplayName("Should set start to first statement and end different when non-empty method declaration")
  void should_SetStartToFirstStatementAndEndDifferent_when_NonEmptyMethodDeclaration() {
    String fileName = "cfgInputs/should_SetStartToFirstStatementAndEndDifferent_when_NonEmptyMethodDeclaration.java";
    init(fileName);
    Statement start = controlFlowGraph.getStart();
    Statement end = controlFlowGraph.getEnd();
    Statement variableDeclStatement = statementTracker.getVariableDeclarationStatement(0);
    assertAll("Method declaration with non-empty block",
        () -> assertNotNull(controlFlowGraph.getMethodDeclaration()),
        () -> assertNotEquals(start, end),
        () -> assertTrue(start == variableDeclStatement),
        () -> assertTrue(hasEdge(variableDeclStatement, end)));
  }

  @Test
  @Tag("Block")
  @DisplayName("Should link all when block has no return")
  void should_LinkAll_when_BlockHasNoReturn() {
    String fileName = "cfgInputs/should_LinkAll_when_BlockHasNoReturn.java";
    init(fileName);
    Statement variableDeclaration = statementTracker.getVariableDeclarationStatement(0);
    Statement expressionStatement = statementTracker.getExpressionStatement(0);
    assertTrue(hasEdge(variableDeclaration, expressionStatement));
  }

  @Test
  @Tag("Block")
  @DisplayName("Should link to return when block has return")
  void should_LinkToReturn_when_BlockHasReturn() {
    String fileName = "cfgInputs/should_LinkToReturn_when_BlockHasReturn.java";
    init(fileName);
    Statement variableDeclaration = statementTracker.getVariableDeclarationStatement(0);
    Statement expressionStatement = statementTracker.getExpressionStatement(0);
    Statement returnStatement = statementTracker.getReturnStatement(0);
    assertAll(
        () -> assertTrue(hasEdge(variableDeclaration, returnStatement)),
        () -> assertFalse(hasEdge(returnStatement, expressionStatement)));
  }

  @Test
  @Tag("Return")
  @DisplayName("Should link to return when statements list has mutiple returns")
  void shold_LinkToReturn_when_StatmentsHasMultipleReturn() {
    String fileName = "cfgInputs/should_LinkToReturn_when_StatementsListHasMultipleReturns.java";
    init(fileName);
    Statement variableDeclaration = statementTracker.getVariableDeclarationStatement(0);
    Statement expressionStatement = statementTracker.getExpressionStatement(0);
    Statement returnStatement = statementTracker.getReturnStatement(0);
    assertTrue(hasEdge(expressionStatement, returnStatement));
    assertTrue(hasEdge(variableDeclaration, expressionStatement));
    assertNotNull(statementTracker.getReturnStatement(0));
    assertNotEquals(controlFlowGraph.getStart(), controlFlowGraph.getEnd());
  }

  @Test
  @Tag("Return")
  @DisplayName("Should link to return when statements list is empty/does not have return")
  void shold_LinkToReturn_when_StatmentsListReturnsNull() {
    String fileName = "cfgInputs/should_LinkToReturn_when_StatementsListDoesNotHaveReturn.java";
    init(fileName);
    assertEquals(controlFlowGraph.getStart(), controlFlowGraph.getEnd());
    // assertThrows(IndexOutOfBoundsException.class, () ->
    // statementTracker.getReturnStatement(0));
  }

  @Test
  @Tag("While")
  @DisplayName("Should link to all when block and has no return")
  void should_LinkAll_when_WhileHasNoReturn() {
    String fileName = "cfgInputs/should_LinkAll_when_WhileHasNoReturn.java";
    init(fileName);
    Statement last = statementTracker.getExpressionStatement(1);
    Statement whileDeclaration = statementTracker.getWhileStatement(0);
    Statement expressionStatement = statementTracker.getExpressionStatement(0);
    assertAll(
        () -> assertTrue(hasEdge(whileDeclaration, expressionStatement)),
        () -> assertTrue(hasEdge(last, whileDeclaration)));
  }

  @Test
  @Tag("While")
  @DisplayName("Should link to return when while has return")
  void should_LinkToReturn_when_WhileHasReturn() {
    String fileName = "cfgInputs/should_LinkToReturn_when_WhileHasReturn.java";
    init(fileName);
    Statement returnStatement = statementTracker.getReturnStatement(0);
    Statement whileDeclaration = statementTracker.getWhileStatement(0);
    Statement expressionStatement = statementTracker.getExpressionStatement(0);
    assertAll(
        () -> assertTrue(hasEdge(whileDeclaration, expressionStatement)),
        () -> assertFalse(hasEdge(returnStatement, whileDeclaration)));
  }

  @Test
  @Tag("While")
  @DisplayName("Should link to while when statement list is empty")
  void should_LinkToWhile_when_WhileIsEmpty() {
    String fileName = "cfgInputs/should_LinkToWhile_when_WhileIsEmpty.java";
    init(fileName);
    Statement whileDeclaration = statementTracker.getWhileStatement(0);
    assertAll(
        () -> assertTrue(hasEdge(whileDeclaration, whileDeclaration)));
  }

  @Test
  @Tag("While")
  @DisplayName("Should link to while when statement list is not empty")
  void should_LinkToWhile_when_WhileIsNotEmpty() {
    String fileName = "cfgInputs/should_LinkToFirstStatement_when_WhileIsNotEmpty.java";
    init(fileName);
    Statement whileDeclaration = statementTracker.getWhileStatement(0);
    Statement expressionStatement = statementTracker.getExpressionStatement(0);
    Statement last = statementTracker.getExpressionStatement(1);
    assertAll(
        () -> assertTrue(hasEdge(whileDeclaration, expressionStatement)),
        () -> assertTrue(hasEdge(last, whileDeclaration)));
  }

  @Test
  @Tag("IfStatement")
  @DisplayName("Should link to first statement after If when statement list is empty")
  void should_LinkToFirstStatement_when_IfIsEmpty() {
    String fileName = "cfgInputs/should_LinkToFirstStatement_when_IfIsEmpty.java";
    init(fileName);
    Statement ifDeclaration = statementTracker.getIfStatement(0);
    Statement expressionStatement = statementTracker.getExpressionStatement(0);
    assertTrue(hasEdge(ifDeclaration, expressionStatement));
  }

  @Test
  @Tag("IfStatement")
  @DisplayName("Should link to If to first statement in If when statement list is not empty and has no return")
  void should_LinkToIf_when_IfIsNotEmpty() {
    String fileName = "cfgInputs/should_LinkToIf_when_IfIsNotEmpty.java";
    init(fileName);
    Statement ifDeclaration = statementTracker.getIfStatement(0);
    Statement first = statementTracker.getExpressionStatement(0);
    Statement last = statementTracker.getExpressionStatement(1);
    assertAll(
        () -> assertTrue(hasEdge(ifDeclaration, first)),
        () -> assertTrue(hasEdge(first, last)));
  }

  @Test
  @Tag("IfStatement")
  @DisplayName("Should link Else to first statement in Else when else statement list is not empty and has no return")
  void should_LinkToElse_when_ElseIsNotEmpty() {
    String fileName = "cfgInputs/should_LinkToElse_when_ElseIsNotEmpty.java";
    init(fileName);
    Statement ifDeclaration = statementTracker.getIfStatement(0);
    Statement first = statementTracker.getExpressionStatement(2);
    Statement last = statementTracker.getExpressionStatement(3);
    assertAll(
        () -> assertTrue(hasEdge(ifDeclaration, first)),
        () -> assertTrue(hasEdge(first, last)));
  }

  @Test
  @Tag("IfStatement")
  @DisplayName("Should link to return if If or Else statement has return")
  void should_LinkToReturn_when_IfElseHaveReturn() {
    String fileName = "cfgInputs/should_LinkToReturn_when_IfElseIsHasReturn.java";
    init(fileName);
    Statement ifDeclaration = statementTracker.getIfStatement(0);
    Statement ifFirst = statementTracker.getExpressionStatement(0);
    Statement ifLast = statementTracker.getExpressionStatement(1);
    Statement elseFirst = statementTracker.getExpressionStatement(2);
    Statement elseLast = statementTracker.getExpressionStatement(3);
    assertAll(
        () -> assertTrue(hasEdge(ifDeclaration, ifFirst)),
        () -> assertFalse(hasEdge(ifLast, ifDeclaration)),
        () -> assertTrue(hasEdge(ifDeclaration, elseFirst)),
        () -> assertFalse(hasEdge(ifDeclaration, elseLast)));
  }

  private boolean hasEdge(Statement source, Statement dest) {
    Set<Statement> successors = controlFlowGraph.getSuccs(source);
    Set<Statement> predecessors = controlFlowGraph.getPreds(dest);
    return successors != null && successors.contains(dest)
        && predecessors != null && predecessors.contains(source);
  }
}
