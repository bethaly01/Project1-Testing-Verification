package edu.byu.cs329.constantfolding;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.jdt.core.dom.ASTMatcher;
import org.eclipse.jdt.core.dom.ASTNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import edu.byu.cs329.utils.JavaSourceUtils;



@DisplayName("Tests for constant folding with all expression classes")
public class ConstantFoldingTests {
    static final Logger log = LoggerFactory.getLogger(ConstantFolding.class);
    ConstantFolding folderUnderTest = null;

    @BeforeEach
    void beforeEach() {
        folderUnderTest = new ConstantFolding();
    }

    @Test
    @DisplayName("Should fold with all expressions classes")
    void should_Fold_With_All_Expressions_Classes() {
        String rootName = "foldingInputs/constantFoldingInput/should_Fold_With_All_Expressions_Classes-root.java";
        String expectedName = "foldingInputs/constantFoldingInput/should_Fold_With_All_Expressions_Classes.java";
        assertDidFoldConstant(this, rootName, expectedName);
    }

    public void assertDidFoldConstant(final Object t, String rootName, String expectedName) {
        ASTNode root = getASTNodeFor(t, rootName);
        Boolean didFold = folderUnderTest.fold(root);
        log.debug(root.toString());
        assertTrue(didFold);
        ASTNode expected = getASTNodeFor(t, expectedName);
        assertTrue(expected.subtreeMatch(new ASTMatcher(), root));
    }

    // static Logger log = LoggerFactory.getLogger(TestUtils.class);
    public static ASTNode getASTNodeFor(final Object t, String name) {
        URI uri = JavaSourceUtils.getUri(t, name);
        assertNotNull(uri);
        ASTNode root = JavaSourceUtils.getCompilationUnit(uri);
        return root;
    }
}