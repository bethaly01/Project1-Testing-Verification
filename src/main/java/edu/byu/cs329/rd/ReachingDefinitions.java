package edu.byu.cs329.rd;

import java.util.Set;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Statement;

public interface ReachingDefinitions {

  public static class Definition {
    public SimpleName name;
    public Statement statement;

    @Override
    public boolean equals(Object o) {
      Definition definition = (Definition) o;

      String currentName = name.getIdentifier();
      String nextName = definition.name.getIdentifier();

      String currentStatement = statement == null ? "null" : statement.toString();
      String nextStatement = definition.statement == null ? "null" : definition.statement.toString();

      boolean name = currentName.equals(nextName);
      boolean nameStatement = currentStatement.equals(nextStatement);

      return name && nameStatement;
    }
  }

  public Set<Definition> getReachingDefinitions(final Statement s);
}
