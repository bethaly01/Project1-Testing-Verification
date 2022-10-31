package edu.byu.cs329.rd;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.beans.Expression;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.internal.util.MockUtil;

import edu.byu.cs329.cfg.ControlFlowGraph;
import edu.byu.cs329.cfg.StatementTracker;
import edu.byu.cs329.rd.ReachingDefinitions.Definition;

@DisplayName("Test For Mocking ReachingDefinition")
public class MockTests {
    ReachingDefinitionsBuilder unitUnderTest = null;

    @BeforeEach
    void beforeEach() {
        unitUnderTest = new ReachingDefinitionsBuilder();
    }

    @Test
    @Tag("Parameters")
    @DisplayName("Should have a definition for each parameter at start when the method declaration has parameters.")
    void should_HaveDefinitionForEachParameterAtStart_when_MethodDeclarationHasParameters() {
        ControlFlowGraph controlFlowGraph = MockUtils.newMockForEmptyMethodWithTwoParameters("a", "b");
        ReachingDefinitions reachingDefinitions = getReachingDefinitions(controlFlowGraph);
        Statement start = controlFlowGraph.getStart();
        Set<Definition> definitions = reachingDefinitions.getReachingDefinitions(start);
        assertEquals(2, definitions.size());
        assertAll("Parameters Defined at Start",
                () -> assertTrue(doesDefine("a", definitions)),
                () -> assertTrue(doesDefine("b", definitions)));
    }

    @Test
    @Tag("EmptyMethodDeclaration")
    @DisplayName("Should set start and end same when empty method declaration")
    void ReachingDefinition_for_EmptyMethodDeclaration() {
        ControlFlowGraph cfg = mock(ControlFlowGraph.class);
        MethodDeclaration methodDeclarion = mock(MethodDeclaration.class);
        List<VariableDeclaration> parameterList = new ArrayList<VariableDeclaration>();
        when(methodDeclarion.parameters()).thenReturn(parameterList);
        Block b0 = mock(Block.class);

        when(cfg.getMethodDeclaration()).thenReturn(methodDeclarion);
        when(cfg.getStart()).thenReturn(b0);
        when(cfg.getPreds(b0)).thenReturn(null);
        when(cfg.getSuccs(b0)).thenReturn(null);
        when(cfg.getEnd()).thenReturn(b0);

        ReachingDefinitions rdb = getReachingDefinitions(cfg);
        Statement start = cfg.getStart();
        Statement end = cfg.getEnd();
        Set<Definition> definitions = rdb.getReachingDefinitions(end);

        assertEquals(start, end);
        assertEquals(0, definitions.size());
        // assertThrows(NullPointerException.class, () -> definitions.size());

    }

    @Test
    @Tag("Sequential")
    @DisplayName("Reaching Definition test for return/sequential")
    void ReachingDefinition_for_ReturnOrSequential() {
        ControlFlowGraph cfg = mock(ControlFlowGraph.class);
        MethodDeclaration methodDeclarion = mock(MethodDeclaration.class);
        List<VariableDeclaration> parameterList = new ArrayList<VariableDeclaration>();
        when(methodDeclarion.parameters()).thenReturn(parameterList);

        VariableDeclarationStatement s1 = MockUtils.mockForVariableDeclarationStatement("i");
        VariableDeclarationStatement s2 = MockUtils.mockForVariableDeclarationStatement("b");
        VariableDeclarationStatement s3 = MockUtils.mockForVariableDeclarationStatement("a");
        Block b0 = MockUtils.mockForBlockStatement(s1, s2, s3);

        when(cfg.getMethodDeclaration()).thenReturn(methodDeclarion);

        when(cfg.getStart()).thenReturn(s1);
        when(cfg.getPreds(s1)).thenReturn(null);
        when(cfg.getSuccs(s1)).thenReturn(makeSet(s2));

        when(cfg.getPreds(s2)).thenReturn(makeSet(s1));
        when(cfg.getSuccs(s2)).thenReturn(makeSet(s3));

        when(cfg.getPreds(s3)).thenReturn(makeSet(s2));
        when(cfg.getSuccs(s3)).thenReturn(makeSet(b0));

        when(cfg.getPreds(b0)).thenReturn(makeSet(s1, s2, s3));
        when(cfg.getSuccs(b0)).thenReturn(null);

        when(cfg.getEnd()).thenReturn(b0);

        ReachingDefinitions rdb = getReachingDefinitions(cfg);
        Statement end = cfg.getEnd();
        Set<Definition> definitions = rdb.getReachingDefinitions(end);

        assertEquals(3, definitions.size());

    }

    @Test
    @Tag("Merging")
    @DisplayName("Reaching definition test for If")
    void ReachingDefinition_for_If() {
        ControlFlowGraph cfg = mock(ControlFlowGraph.class);
        MethodDeclaration methodDeclarion = mock(MethodDeclaration.class);
        List<VariableDeclaration> parameterList = new ArrayList<VariableDeclaration>();

        when(methodDeclarion.parameters()).thenReturn(parameterList);

        VariableDeclarationStatement s1 = MockUtils.mockForVariableDeclarationStatement("a");
        VariableDeclarationStatement s3 = MockUtils.mockForVariableDeclarationStatement("b");
        VariableDeclarationStatement s4 = MockUtils.mockForVariableDeclarationStatement("c");
        IfStatement i2 = MockUtils.newMockForIfStatement("a");
        Block b0 = MockUtils.mockForBlockStatement(s1, s3, s4);

        when(cfg.getMethodDeclaration()).thenReturn(methodDeclarion);

        when(cfg.getStart()).thenReturn(s1);
        when(cfg.getPreds(s1)).thenReturn(null);
        when(cfg.getSuccs(s1)).thenReturn(makeSet(i2));

        when(cfg.getPreds(i2)).thenReturn(makeSet(s1));
        when(cfg.getSuccs(i2)).thenReturn(makeSet(s3, s4));

        when(cfg.getPreds(s3)).thenReturn(makeSet(i2));
        when(cfg.getSuccs(s3)).thenReturn(makeSet(s4));

        when(cfg.getPreds(s4)).thenReturn(makeSet(i2, s3));
        when(cfg.getSuccs(s4)).thenReturn(makeSet(b0));

        when(cfg.getEnd()).thenReturn(b0);
        when(cfg.getPreds(b0)).thenReturn(makeSet(s4));
        when(cfg.getSuccs(b0)).thenReturn(null);

        ReachingDefinitions rdb = getReachingDefinitions(cfg);
        Set<Definition> definitions = rdb.getReachingDefinitions(s4);
        Set<Definition> ifDefinitions = rdb.getReachingDefinitions(i2);

        assertEquals(2, definitions.size());
        assertEquals(1, ifDefinitions.size());
    }

    @Test
    @Tag("Branching")
    @DisplayName("Reaching defintion test for branching")
    public void ReachingDefinition_for_branching() {
        ControlFlowGraph cfg = mock(ControlFlowGraph.class);
        MethodDeclaration methodDeclarion = mock(MethodDeclaration.class);
        List<VariableDeclaration> parameterList = new ArrayList<VariableDeclaration>();

        when(methodDeclarion.parameters()).thenReturn(parameterList);

        VariableDeclarationStatement s1 = MockUtils.mockForVariableDeclarationStatement("a");
        VariableDeclarationStatement s3 = MockUtils.mockForVariableDeclarationStatement("b");
        VariableDeclarationStatement s4 = MockUtils.mockForVariableDeclarationStatement("c");
        VariableDeclarationStatement s5 = MockUtils.mockForVariableDeclarationStatement("d");

        IfStatement i2 = MockUtils.newMockForIfStatement("a");
        Block b0 = MockUtils.mockForBlockStatement(s1, i2, s3, s4, s5);

        when(cfg.getMethodDeclaration()).thenReturn(methodDeclarion);

        when(cfg.getStart()).thenReturn(s1);
        when(cfg.getPreds(s1)).thenReturn(null);
        when(cfg.getSuccs(s1)).thenReturn(makeSet(i2));

        when(cfg.getPreds(i2)).thenReturn(makeSet(s1));
        when(cfg.getSuccs(i2)).thenReturn(makeSet(s3, s4));

        when(cfg.getPreds(s3)).thenReturn(makeSet(i2));
        when(cfg.getSuccs(s3)).thenReturn(makeSet(s5));

        when(cfg.getPreds(s4)).thenReturn(makeSet(i2));
        when(cfg.getSuccs(s4)).thenReturn(makeSet(s5));

        when(cfg.getPreds(s5)).thenReturn(makeSet(s3, s4));
        when(cfg.getSuccs(s5)).thenReturn(makeSet(b0));

        when(cfg.getPreds(b0)).thenReturn(makeSet(s1, s3, s4, s5));
        when(cfg.getSuccs(b0)).thenReturn(null);

        when(cfg.getEnd()).thenReturn(b0);

        ReachingDefinitions rdb = getReachingDefinitions(cfg);
        Statement end = cfg.getEnd();
        Set<Definition> definitions = rdb.getReachingDefinitions(end);

        assertEquals(5, definitions.size());
    }

    @Test
    @Tag("Loop")
    @DisplayName("Reaching definition test for loop")
    public void ReachingDefinition_for_loop() {
        ControlFlowGraph cfg = mock(ControlFlowGraph.class);
        MethodDeclaration methodDeclarion = mock(MethodDeclaration.class);

        List<VariableDeclaration> parameterList = new ArrayList<VariableDeclaration>();
        when(methodDeclarion.parameters()).thenReturn(parameterList);

        VariableDeclarationStatement s1 = MockUtils.mockForVariableDeclarationStatement("i");
        WhileStatement w2 = MockUtils.mockForWhileStatement("i");
        VariableDeclarationStatement s3 = MockUtils.mockForVariableDeclarationStatement("a");
        ExpressionStatement s4 = MockUtils.newMockForExpressionStatement("i");
        Block b0 = mock(Block.class);

        when(cfg.getMethodDeclaration()).thenReturn(methodDeclarion);

        when(cfg.getStart()).thenReturn(s1);
        when(cfg.getPreds(s1)).thenReturn(null);
        when(cfg.getSuccs(s1)).thenReturn(makeSet(w2));

        when(cfg.getPreds(w2)).thenReturn(makeSet(s1, s3));
        when(cfg.getSuccs(w2)).thenReturn(makeSet(s3, s4));

        when(cfg.getPreds(s3)).thenReturn(makeSet(w2));
        when(cfg.getSuccs(s3)).thenReturn(makeSet(w2));

        when(cfg.getPreds(s4)).thenReturn(makeSet(w2));
        when(cfg.getSuccs(s4)).thenReturn(makeSet(b0));

        when(cfg.getPreds(b0)).thenReturn(makeSet(s1, w2, s3, s4));
        when(cfg.getSuccs(b0)).thenReturn(null);

        when(cfg.getEnd()).thenReturn(b0);

        ReachingDefinitions rdb = getReachingDefinitions(cfg);
        Statement end = cfg.getEnd();
        Set<Definition> definitions = rdb.getReachingDefinitions(end);

        assertEquals(4, definitions.size());

    }

    @Test
    @Tag("Complex")
    @DisplayName("Reaching definition test for complex structure")
    public void ReachingDefinition_for_LastInterestingTest() {
        ControlFlowGraph cfg = mock(ControlFlowGraph.class);
        Statement statement = mock(Statement.class);
        when(cfg.getStart()).thenReturn(statement);
        MethodDeclaration methodDeclarion = mock(MethodDeclaration.class);
        VariableDeclaration firstParameter = MockUtils.newMockForVariableDeclaration("a");
        VariableDeclaration secondParameter = MockUtils.newMockForVariableDeclaration("b");
        List<VariableDeclaration> parameterList = new ArrayList<VariableDeclaration>();
        parameterList.add(firstParameter);
        parameterList.add(secondParameter);
        when(methodDeclarion.parameters()).thenReturn(parameterList);

        VariableDeclarationStatement s1 = MockUtils.mockForVariableDeclarationStatement("x");
        ExpressionStatement s2 = MockUtils.newMockForExpressionStatement("x");
        IfStatement i3 = MockUtils.newMockForIfStatement("a");
        WhileStatement w4 = MockUtils.mockForWhileStatement("x");
        ExpressionStatement s5 = MockUtils.newMockForExpressionStatement("x");
        ExpressionStatement s6 = MockUtils.newMockForExpressionStatement("x");
        ReturnStatement r7 = MockUtils.mockForReturnStatement("x");
        Block b0 = MockUtils.mockForBlockStatement(s1, s2, i3, w4, s5, s6, r7);

        when(cfg.getMethodDeclaration()).thenReturn(methodDeclarion);

        when(cfg.getPreds(statement)).thenReturn(null);
        when(cfg.getSuccs(statement)).thenReturn(makeSet(s1));

        when(cfg.getPreds(s1)).thenReturn(makeSet(statement));
        when(cfg.getSuccs(s1)).thenReturn(makeSet(s2));

        when(cfg.getPreds(s2)).thenReturn(makeSet(s1));
        when(cfg.getSuccs(s2)).thenReturn(makeSet(i3));

        when(cfg.getPreds(i3)).thenReturn(makeSet(s2));
        when(cfg.getSuccs(i3)).thenReturn(makeSet(s6, w4));

        when(cfg.getPreds(w4)).thenReturn(makeSet(i3));
        when(cfg.getSuccs(w4)).thenReturn(makeSet(r7, s5));

        when(cfg.getPreds(s5)).thenReturn(makeSet(w4));
        when(cfg.getSuccs(s5)).thenReturn(makeSet(w4));

        when(cfg.getPreds(s6)).thenReturn(makeSet(i3));
        when(cfg.getSuccs(s6)).thenReturn(makeSet(r7));

        when(cfg.getPreds(r7)).thenReturn(makeSet(s6, w4));
        when(cfg.getSuccs(r7)).thenReturn(makeSet(b0));

        when(cfg.getPreds(b0)).thenReturn(makeSet(s1, s2, i3, w4, s5, s6, r7));
        when(cfg.getSuccs(b0)).thenReturn(null);
        when(cfg.getEnd()).thenReturn(b0);

        ReachingDefinitions rdb = getReachingDefinitions(cfg);
        Statement end = cfg.getEnd();

        Set<Definition> definitions = rdb.getReachingDefinitions(end);

        assertEquals(3, definitions.size());

    }

    private boolean doesDefine(String name, final Set<Definition> definitions) {
        for (Definition definition : definitions) {
            if (definition.name.getIdentifier().equals(name) && definition.statement == null) {
                return true;
            }
        }
        return false;
    }

    private ReachingDefinitions getReachingDefinitions(ControlFlowGraph controlFlowGraph) {
        List<ControlFlowGraph> list = new ArrayList<ControlFlowGraph>();
        list.add(controlFlowGraph);
        List<ReachingDefinitions> reachingDefinitionsList = unitUnderTest.build(list);
        assertEquals(1, reachingDefinitionsList.size());
        return reachingDefinitionsList.get(0);
    }

    private Set<Statement> makeSet(Statement... theStatement) {
        Set<Statement> statements = new HashSet<Statement>();
        for (Statement statement : theStatement) {
            statements.add(statement);
        }
        return statements;
    }

}
