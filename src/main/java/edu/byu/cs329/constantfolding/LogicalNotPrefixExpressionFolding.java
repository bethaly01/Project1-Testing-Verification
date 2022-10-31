package edu.byu.cs329.constantfolding;

import edu.byu.cs329.utils.ExceptionUtils;
import edu.byu.cs329.utils.TreeModificationUtils;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression.Operator;

public class LogicalNotPrefixExpressionFolding implements Folding {
  class Visitor extends ASTVisitor {
    public boolean didFold = false;

    @Override
    public void endVisit(PrefixExpression node) {

      Operator operator = node.getOperator();
      if (operator != PrefixExpression.Operator.NOT) {
        return;
      }

      Expression childExpression = node.getOperand();
      if (!(childExpression instanceof BooleanLiteral)) {
        return;
      }

      boolean literal = ((BooleanLiteral) childExpression).booleanValue();
      literal = !literal;
      AST ast = node.getAST();
      ASTNode newNode = ast.newBooleanLiteral(literal);
      TreeModificationUtils.replaceChildInParent(node, newNode);
      didFold = true;
    }

  }

  /**
   * Replaces Not Logical Prefix Expression literals in the tree with the
   * literals.
   * 
   * <p>Visits the root and any reachable nodes from the root to replace
   * any LocgicalNotPrefixExpression reachable node containing a literal
   * with the literal itself.
   *
   * <p>top := all nodes reachable from root such that each node
   * is an outermost parenthesized expression that ends
   * in a literal
   * 
   * <p>parents := all nodes such that each one is the parent
   * of some node in top
   * 
   * <p>isFoldable(n) := isLocgicalNotPrefixExpression(n)
   * /\ ( isLiteral(expression(n))
   * || isFoldable(expression(n)))
   * 
   * <p>literal(n) := if isLiteral(n) then n else literal(expression(n))
   * 
   * @modifies nodes in parents
   * 
   * @requires root != null
   * @requires (root instanceof CompilationUnit) \/ parent(root) != null
   * 
   * @ensures fold(root) == (old(top) != emptyset)
   * @ensures forall n in old(top), exists n' in nodes
   *          fresh(n')
   *          /\ isLiteral(n')
   *          /\ value(n') == value(literal(n))
   *          /\ parent(n') == parent(n)
   *          /\ children(parent(n')) == (children(parent(n)) setminus {n}) union
   *          {n'}
   * 
   * @param root the root of the tree to traverse.
   * @return true if not Logical literals were replaced in the rooted tree
   */
  @Override
  public boolean fold(ASTNode root) {
    checkRequires(root);
    Visitor visitor = new Visitor();
    root.accept(visitor);
    return visitor.didFold;
  }

  private void checkRequires(final ASTNode root) {
    ExceptionUtils.requiresNonNull(root, "Null root passed to EqualityInfixExpressionFolding.fold");
    if (!(root instanceof CompilationUnit) && root.getParent() == null) {
      ExceptionUtils.throwRuntimeException(
          "Non-CompilationUnit root with no parent passed to LogicalNotPrefixExpression.fold");
    }
  }

}