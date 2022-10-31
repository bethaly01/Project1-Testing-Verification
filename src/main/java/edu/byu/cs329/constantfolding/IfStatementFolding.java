package edu.byu.cs329.constantfolding;

import edu.byu.cs329.utils.ExceptionUtils;
import edu.byu.cs329.utils.TreeModificationUtils;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IfStatement;

public class IfStatementFolding implements Folding {
  class Visitor extends ASTVisitor {
    public boolean didFold = false;

    @Override
    public void endVisit(IfStatement node) {
      // if not boolean literal, then ignore
      Expression child = node.getExpression();
      if (!(child instanceof BooleanLiteral)) {
        return;
      }
      // current node belongs in top - foldable
      // get boolean value from IfStatement expression
      boolean literal = ((BooleanLiteral) child).booleanValue();
      AST ast = node.getAST();
      ASTNode exp;

      if (literal == true) {
        // adds then statement block
        exp = node.getThenStatement();
        ASTNode newExp = ASTNode.copySubtree(ast, exp);
        TreeModificationUtils.replaceChildInParent(node, newExp);
      } else {

        if (node.getElseStatement() == null) {
          // removes node
          TreeModificationUtils.removeChildInParent(node);
        } else {
          // adds else statement block
          exp = node.getElseStatement();
          ASTNode newExp = ASTNode.copySubtree(ast, exp);
          TreeModificationUtils.replaceChildInParent(node, newExp);
        }
      }
      didFold = true;
    }
  }

  /**
   * Replaces IfStatement literals in the tree with the literals.
   *
   * <p>Visits the root and any reachable nodes from the root to replace any
   * IfStatementExpression reachable node containing a literal with the literal
   * itself.
   *
   * <p>top := all nodes reachable from root such that each node is an outermost
   * ifStatement expression that ends in a literal
   *
   * <p>parents := all nodes such that each one is the parent of some node in top
   *
   * <p>isFoldable(n) := isIfStatementExpression(n) /\ ( isLiteral(expression(n))
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
   *
   * @param root the root of the tree to traverse.
   * @return true if ifStatement literals were replaced in the rooted tree
   */
  @Override
  public boolean fold(ASTNode root) {
    checkRequires(root);
    Visitor visitor = new Visitor();
    root.accept(visitor);
    return visitor.didFold;
  }

  private void checkRequires(final ASTNode root) {
    ExceptionUtils.requiresNonNull(root, "Null root passed to IfStatementInfixExpression.fold");
    if (!(root instanceof CompilationUnit) && root.getParent() == null) {
      ExceptionUtils.throwRuntimeException(
          "Non-CompilationUnit root with no parent passed"
              +
              "to IfStatementInfixExpression.fold");
    }
  }
}
