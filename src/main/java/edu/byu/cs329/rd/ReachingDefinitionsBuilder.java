package edu.byu.cs329.rd;

import edu.byu.cs329.cfg.ControlFlowGraph;
import edu.byu.cs329.rd.ReachingDefinitions.Definition;
import net.bytebuddy.agent.ByteBuddyAgent.AttachmentProvider.Accessor.Simple;

import static org.mockito.ArgumentMatchers.shortThat;

import java.beans.Expression;
import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.Block;

public class ReachingDefinitionsBuilder {
  private List<ReachingDefinitions> rdList = null;
  private Map<Statement, Set<Definition>> entrySetMap = null;
  private Stack<Statement> worklist = null;
  private Map<Statement, Set<Definition>> exitSetMap = null;
  private Map<Statement, Set<Definition>> genSetMap = null;
  ControlFlowGraph cfgBuilder = null;

  /**
   * Computes the reaching definitions for each control flow graph.
   * 
   * @param cfgList the list of control flow graphs.
   * @return the coresponding reaching definitions for each graph.
   */
  public List<ReachingDefinitions> build(List<ControlFlowGraph> cfgList) {
    rdList = new ArrayList<ReachingDefinitions>();
    for (ControlFlowGraph cfg : cfgList) {
      ReachingDefinitions rd = computeReachingDefinitions(cfg);
      rdList.add(rd);
    }
    return rdList;
  }

  private ReachingDefinitions computeReachingDefinitions(ControlFlowGraph cfg) {
    Set<Definition> parameterDefinitions = createParameterDefinitions(cfg.getMethodDeclaration());
    // Algoritmo Part 1
    // Part A
    entrySetMap = new HashMap<Statement, Set<Definition>>();
    exitSetMap = new HashMap<Statement, Set<Definition>>();

    Statement start = cfg.getStart();
    entrySetMap.put(start, parameterDefinitions);

    worklist = new Stack<>();

    // Part B
    worklist.push(start);
    // entrySetMap.put(start, parameterDefinitions);
    worklistAlgorithm(cfg);

    return new ReachingDefinitions() {
      final Map<Statement, Set<Definition>> reachingDefinitions = Collections.unmodifiableMap(entrySetMap);

      @Override
      public Set<Definition> getReachingDefinitions(final Statement s) {
        Set<Definition> returnValue = null;
        if (reachingDefinitions.containsKey(s)) {
          returnValue = reachingDefinitions.get(s);
        }
        return returnValue;
      }
    };
  }

  private Set<Definition> createParameterDefinitions(MethodDeclaration methodDeclaration) {
    List<VariableDeclaration> parameterList = getParameterList(methodDeclaration.parameters());
    Set<Definition> set = new HashSet<Definition>();

    for (VariableDeclaration parameter : parameterList) {
      Definition definition = createDefinition(parameter.getName(), null);
      set.add(definition);
    }

    return set;
  }

  private Definition createDefinition(SimpleName name, Statement statement) {
    Definition definition = new Definition();
    definition.name = name;
    definition.statement = statement;
    return definition;
  }

  private List<VariableDeclaration> getParameterList(Object list) {
    @SuppressWarnings("unchecked")
    List<VariableDeclaration> statementList = (List<VariableDeclaration>) (list);
    return statementList;
  }

  private void createSetTables() {
    Statement node = cfgBuilder.getStart();
    entrySetMap.put(node, new HashSet<Definition>());
    exitSetMap.put(node, new HashSet<Definition>());
    Set<Definition> newSet = createSetDeSet(new HashSet<Definition>(), node);
    genSetMap.put(node, newSet);
    createSetTables(node);
  }

  private void createSetTables(Statement node) {
    Set<Statement> nextNodes = cfgBuilder.getSuccs(node);
    if (nextNodes == null || nextNodes.isEmpty()) {
      return;
    }
    for (Statement statement : nextNodes) {
      createSetTables(statement);
      if (!(statement instanceof Block)) {
        // initialized entry and exit Set
        entrySetMap.put(statement, new HashSet<Definition>());
        exitSetMap.put(statement, new HashSet<Definition>());

        // Gen Set creation
        if (genSetMap.containsKey(statement)) {
          Set<Definition> originalSet = genSetMap.get(statement);
          Set<Definition> newSet = createSetDeSet(originalSet, statement);
          genSetMap.replace(statement, newSet);
        } else {
          Set<Definition> newSet = createSetDeSet(new HashSet<Definition>(), statement);
          genSetMap.put(statement, newSet);
        }
      }
    }
  }

  private Set<Definition> createSetDeSet(Set<Definition> originalSet, Statement statement) {

    if (statement instanceof VariableDeclarationStatement || (statement instanceof ExpressionStatement
        && ((ExpressionStatement) statement).getExpression() instanceof Assignment)) {
      // create Set of Definition for Variable Declaration
      Definition definition = null;
      if (statement instanceof VariableDeclarationStatement) {
        VariableDeclaration vd = (VariableDeclaration) ((VariableDeclarationStatement) statement).fragments().get(0);
        definition = createDefinition(vd.getName(), statement);
      } else {
        SimpleName simpleName = (SimpleName) ((Assignment) ((ExpressionStatement) statement).getExpression())
            .getLeftHandSide();
        definition = createDefinition(simpleName, statement);
      }

      originalSet.add(definition);
    }
    return originalSet;
  }

  private Set<Definition> calculateGenSet(Statement statement) {
    Set<Definition> genSet = new HashSet<>();

    if (statement instanceof VariableDeclarationStatement) {
      VariableDeclarationStatement variableDeclaration = (VariableDeclarationStatement) statement;
      @SuppressWarnings("unchecked")
      List<VariableDeclarationFragment> fragments = variableDeclaration.fragments();
      SimpleName simpleName = fragments.get(0).getName();
      genSet.add(createDefinition(simpleName, statement));
    } else if (statement instanceof ExpressionStatement) {
      ExpressionStatement expressionStatement = (ExpressionStatement) statement;
      Assignment expression = (Assignment) expressionStatement.getExpression();
      SimpleName simpleName = (SimpleName) expression.getLeftHandSide();
      genSet.add(createDefinition(simpleName, statement));
    }

    return genSet;
  }

  private Set<Definition> calculateKillSet(Set<Definition> genSet) {
    Set<Definition> killSet = new HashSet<>();

    for (Definition def : genSet) {
      Definition killDefinition = createDefinition(def.name, null);
      killSet.add(killDefinition);
    }

    return killSet;
  }

  private Set<Definition> calculateExitSet(Set<Definition> entrySet, Set<Definition> killSet, Set<Definition> genSet) {
    Set<Definition> exitSet = new HashSet<>(entrySet);
    Set<Definition> removeSet = new HashSet<>();

    for (Definition def : exitSet) {
      for (Definition kill : killSet) {
        if (def.name.toString().equals(kill.name.toString())) {
          removeSet.add(def);
        }
      }
    }

    exitSet.removeAll(removeSet);
    exitSet.addAll(genSet);

    return exitSet;
  }

  private boolean calculateEntryExitSetsChanges(Statement statement, Set<Definition> entrySet,
      Set<Definition> exitSet) {
    Set<Definition> oldEntrySet = entrySetMap.get(statement);
    Set<Definition> oldExitSet = exitSetMap.get(statement);

    if (oldEntrySet == null) {
      return true;
    }

    if (oldExitSet == null) {
      return true;
    }

    boolean entrySetChanged = compareDefinitionSets(entrySet, oldEntrySet);
    boolean exitSetChanged = compareDefinitionSets(exitSet, oldExitSet);

    return entrySetChanged || exitSetChanged;
  }

  private boolean compareDefinitionSets(Set<Definition> firstInputSet, Set<Definition> secondInputSet) {
    for (Definition def : firstInputSet) {
      boolean containsDef = findDefinitionInSet(def, secondInputSet);
      if (!containsDef) {
        return true;
      }
    }
    return false;
  }

  private boolean findDefinitionInSet(Definition def, Set<Definition> definition) {
    for (Definition d : definition) {
      if (def.equals(d)) {
        return true;
      }
    }
    return false;
  }

  private void worklistAlgorithm(ControlFlowGraph cfg) {
    while (!worklist.isEmpty()) {
      Statement currentStatement = worklist.pop();
      Set<Statement> predecessors = cfg.getPreds(currentStatement) == null ? new HashSet<>()
          : cfg.getPreds(currentStatement);
      Set<Definition> tempEntryHolder = entrySetMap.getOrDefault(currentStatement, new HashSet<>());
      Set<Definition> predsExitSet = new HashSet<>(tempEntryHolder);

      for (Statement preds : predecessors) {
        Set<Definition> predsExit = exitSetMap.getOrDefault(preds, new HashSet<>());
        predsExitSet.addAll(predsExit);
      }

      Set<Definition> currentEntrySet = new HashSet<>(predsExitSet);
      Set<Definition> currentGenSet = calculateGenSet(currentStatement);
      Set<Definition> currentKillSet = calculateKillSet(currentGenSet);
      Set<Definition> currentExitSet = calculateExitSet(currentEntrySet, currentKillSet, currentGenSet);

      boolean isChanged = calculateEntryExitSetsChanges(currentStatement, currentEntrySet, currentExitSet);

      if (isChanged) {
        entrySetMap.put(currentStatement, currentEntrySet);
        exitSetMap.put(currentStatement, currentExitSet);

        Set<Statement> successsors = cfg.getSuccs(currentStatement);

        if (successsors != null) {
          for (Statement sucessor : successsors) {
            worklist.push(sucessor);
          }
        }
      }
    }
  }
}
