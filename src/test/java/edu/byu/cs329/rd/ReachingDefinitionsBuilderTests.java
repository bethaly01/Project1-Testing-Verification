package edu.byu.cs329.rd;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Statement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import edu.byu.cs329.TestUtils;
import edu.byu.cs329.cfg.ControlFlowGraph;
import edu.byu.cs329.cfg.ControlFlowGraphBuilder;
import edu.byu.cs329.cfg.StatementTracker;
import edu.byu.cs329.rd.ReachingDefinitions.Definition;

@DisplayName("Tests for ReachingDefinitionsBuilder")
public class ReachingDefinitionsBuilderTests {

  ReachingDefinitionsBuilder unitUnderTest = null;
  ControlFlowGraphBuilder cfgTest = null;
  ControlFlowGraph controlFlowGraph1 = null;
  StatementTracker statementTracker = null;

  void init(String fileName) {
    ASTNode node = TestUtils.getASTNodeFor(this, fileName);
    List<ControlFlowGraph> cfgList = cfgTest.build(node);
    assertEquals(1, cfgList.size());
    controlFlowGraph1 = cfgList.get(0);
    statementTracker = new StatementTracker(node);
  }

  List<ControlFlowGraph> initList(String fileName) {
    ASTNode node = TestUtils.getASTNodeFor(this, fileName);
    List<ControlFlowGraph> cfgList = cfgTest.build(node);
    assertEquals(1, cfgList.size());
    controlFlowGraph1 = cfgList.get(0);
    return cfgList;
  }

  @BeforeEach
  void beforeEach() {
    unitUnderTest = new ReachingDefinitionsBuilder();
    cfgTest = new ControlFlowGraphBuilder();
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
  @Tag("IfStatement")
  @DisplayName("Reaching definition test for If")
  void ReachingDefinition_for_If() {
    String fileName = "rdInputs/IfStatement.java";
    init(fileName);
    ReachingDefinitions rdb = getReachingDefinitions(controlFlowGraph1);
    Statement statement = statementTracker.getVariableDeclarationStatement(2);
    Statement ifStatement = statementTracker.getIfStatement(0);
    Statement end = controlFlowGraph1.getEnd();

    Set<Definition> definitions = rdb.getReachingDefinitions(statement);
    Set<Definition> ifDefinitions = rdb.getReachingDefinitions(ifStatement);
    Set<Definition> totalDefinitions = rdb.getReachingDefinitions(end);

    assertEquals(2, definitions.size());
    assertEquals(1, ifDefinitions.size());
    assertTrue(doesDefine("a", totalDefinitions));
    assertTrue(doesDefine("b", totalDefinitions));
    assertTrue(doesDefine("c", totalDefinitions));
    // List<ReachingDefinitions> results = rdb.build(cfgList);
    // assertEquals(5, results.size());
  }

  @Test
  @Tag("ReturnSequential")
  @DisplayName("Reaching Definition test for return/sequential")
  void ReachingDefinition_for_ReturnOrSequential() {
    String fileName = "rdInputs/ReturnOrSequential.java";
    init(fileName);
    ReachingDefinitions rdb = getReachingDefinitions(controlFlowGraph1);
    Statement start = controlFlowGraph1.getEnd();
    // Statement theReturn = statementTracker.getReturnStatement(0);

    Set<Definition> definitions = rdb.getReachingDefinitions(start);

    assertEquals(3, definitions.size());
    assertTrue(doesDefine("i", definitions));
    assertTrue(doesDefine("b", definitions));
    assertTrue(doesDefine("a", definitions));
  }

  @Test
  @Tag("Loop")
  @DisplayName("Reaching definition test for loop")
  public void ReachingDefinition_for_loop() {
    String fileName = "rdInputs/WhileLoop.java";
    init(fileName);
    ReachingDefinitions rdb = getReachingDefinitions(controlFlowGraph1);
    Statement whileStatement = statementTracker.getWhileStatement(0);
    Statement end = controlFlowGraph1.getEnd();

    Set<Definition> definitions = rdb.getReachingDefinitions(whileStatement);
    Set<Definition> totalDefinitions = rdb.getReachingDefinitions(end);

    assertEquals(3, definitions.size());
    assertTrue(doesDefine("i", totalDefinitions));
    assertTrue(doesDefine("a", totalDefinitions));
    assertTrue(doesDefine("z", totalDefinitions));
  }

  @Test
  @Tag("LastInterestingTest")
  @DisplayName("Reaching definition test for loop")
  public void ReachingDefinition_for_LastInterestingTest() {
    String fileName = "rdInputs/Last.java";
    init(fileName);
    ReachingDefinitions rdb = getReachingDefinitions(controlFlowGraph1);
    Statement end = controlFlowGraph1.getEnd();

    Set<Definition> lastReachingDef = rdb.getReachingDefinitions(end);

    assertTrue(doesDefine("a", lastReachingDef));
    assertTrue(doesDefine("b", lastReachingDef));
    assertTrue(doesDefine("x", lastReachingDef));

    assertEquals(5, lastReachingDef.size());
  }

  private boolean doesDefine(String name, final Set<Definition> definitions) {
    for (Definition definition : definitions) {
      if (definition.name.getIdentifier().equals(name)) {
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
}
