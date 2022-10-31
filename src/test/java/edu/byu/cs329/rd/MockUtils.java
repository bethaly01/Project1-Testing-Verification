package edu.byu.cs329.rd;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;
import org.eclipse.jdt.core.dom.InfixExpression.Operator;
import org.eclipse.jdt.core.dom.InfixExpression;

import edu.byu.cs329.cfg.ControlFlowGraph;
import edu.byu.cs329.cfg.StatementTracker;
import net.bytebuddy.agent.ByteBuddyAgent.AttachmentProvider.Accessor.Simple;

public class MockUtils {
  public static ControlFlowGraph newMockForEmptyMethodWithTwoParameters(String first, String second) {
    ControlFlowGraph cfg = mock(ControlFlowGraph.class);
    Statement statement = mock(Statement.class);
    when(cfg.getStart()).thenReturn(statement);
    MethodDeclaration methodDeclarion = mock(MethodDeclaration.class);
    VariableDeclaration firstParameter = newMockForVariableDeclaration(first);
    VariableDeclaration secondParameter = newMockForVariableDeclaration(second);
    List<VariableDeclaration> parameterList = new ArrayList<VariableDeclaration>();
    parameterList.add(firstParameter);
    parameterList.add(secondParameter);
    when(methodDeclarion.parameters()).thenReturn(parameterList);
    when(cfg.getMethodDeclaration()).thenReturn(methodDeclarion);
    return cfg;
  }

  public static Set<Statement> makeSet(Statement... theStatement) {
    Set<Statement> statements = new HashSet<Statement>();
    for (Statement statement : theStatement) {
      statements.add(statement);
    }

    return statements;
  }

  public static IfStatement newMockForIfStatement(String name) {
    IfStatement ifStatement = mock(IfStatement.class);
    InfixExpression infixExpression = mock(InfixExpression.class);
    SimpleName simpleName = mock(SimpleName.class);

    when(ifStatement.getExpression()).thenReturn(infixExpression);
    when(infixExpression.getLeftOperand()).thenReturn(simpleName);
    when(simpleName.getIdentifier()).thenReturn(name);

    return ifStatement;
  }

  public static VariableDeclaration newMockForVariableDeclaration(String name) {
    VariableDeclaration declaration = mock(VariableDeclaration.class);
    SimpleName simpleName = mock(SimpleName.class);
    when(simpleName.getIdentifier()).thenReturn(name);
    when(declaration.getName()).thenReturn(simpleName);
    return declaration;
  }

  public static VariableDeclarationStatement mockForVariableDeclarationStatement(String name) {
    VariableDeclarationStatement declaration = mock(VariableDeclarationStatement.class);
    VariableDeclarationFragment s1Frag = mock(VariableDeclarationFragment.class);
    SimpleName simpleName = mock(SimpleName.class);
    List<VariableDeclarationFragment> fragments = new ArrayList<>();

    fragments.add(s1Frag);

    when(declaration.fragments()).thenReturn(fragments);
    when(s1Frag.getName()).thenReturn(simpleName);
    when(simpleName.getIdentifier()).thenReturn(name);

    return declaration;
  }

  public static ExpressionStatement newMockForExpressionStatement(String name) {
    SimpleName simpleName = mock(SimpleName.class);
    ExpressionStatement expression = mock(ExpressionStatement.class);
    InfixExpression infixExpression = mock(InfixExpression.class);
    Assignment assignment = mock(Assignment.class);

    when(simpleName.getIdentifier()).thenReturn(name);
    when(assignment.getLeftHandSide()).thenReturn(simpleName);
    when(assignment.getRightHandSide()).thenReturn(infixExpression);
    when(expression.getExpression()).thenReturn(assignment);

    return expression;
  }

  public static WhileStatement mockForWhileStatement(String name) {
    WhileStatement whileStatement = mock(WhileStatement.class);
    SimpleName simpleName = mock(SimpleName.class);
    when(simpleName.getIdentifier()).thenReturn(name);
    when(whileStatement.getExpression()).thenReturn(simpleName);

    return whileStatement;
  }

  public static Block mockForBlockStatement(Statement... statement) {
    Block block = mock(Block.class);
    when(block.statements()).thenReturn(Arrays.asList(statement));

    return block;
  }

  public static ReturnStatement mockForReturnStatement(String name) {
    ReturnStatement returnStatement = mock(ReturnStatement.class);
    SimpleName simpleName = mock(SimpleName.class);
    when(simpleName.getIdentifier()).thenReturn(name);
    when(returnStatement.getExpression()).thenReturn(simpleName);

    return returnStatement;
  }

}
