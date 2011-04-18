package com.intellij.structuralsearch.equivalence.groovy;

import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.structuralsearch.equivalence.ChildRole;
import com.intellij.structuralsearch.equivalence.EquivalenceDescriptor;
import com.intellij.structuralsearch.equivalence.EquivalenceDescriptorBuilder;
import com.intellij.structuralsearch.equivalence.EquivalenceDescriptorProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.groovy.GroovyFileType;
import org.jetbrains.plugins.groovy.lang.lexer.GroovyTokenTypes;
import org.jetbrains.plugins.groovy.lang.lexer.TokenSets;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.GrStatement;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.GrVariable;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.GrVariableDeclaration;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.blocks.GrCodeBlock;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.clauses.GrForInClause;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.params.GrParameter;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.typedef.members.GrMethod;
import org.jetbrains.plugins.groovy.lang.psi.api.util.GrStatementOwner;

/**
 * @author Eugene.Kudelevsky
 */
public class GroovyEquivalenceDescriptorProvider extends EquivalenceDescriptorProvider {
  private static final IElementType[] VARIABLE_DELIMETERS = {GroovyTokenTypes.mCOMMA, GroovyTokenTypes.mSEMI};
  private static final TokenSet IGNORED_TOKENS = TokenSet.orSet(TokenSets.WHITE_SPACES_OR_COMMENTS, TokenSets.SEPARATORS);

  @Override
  public boolean isMyContext(@NotNull PsiElement context) {
    return context.getLanguage().isKindOf(GroovyFileType.GROOVY_LANGUAGE);
  }

  @Override
  public EquivalenceDescriptor buildDescriptor(@NotNull PsiElement e) {
    final EquivalenceDescriptorBuilder builder = new EquivalenceDescriptorBuilder();

    if (e instanceof GrVariableDeclaration) {
      return builder.elements(((GrVariableDeclaration)e).getVariables());
    }
    else if (e instanceof GrParameter) {
      final GrParameter p = (GrParameter)e;
      return builder
        .element(p.getNameIdentifierGroovy())
        .optionally(p.getTypeElementGroovy())
        .optionallyInPattern(p.getDefaultInitializer())
        .role(p.getNameIdentifierGroovy(), ChildRole.VARIABLE_NAME);
    }
    else if (e instanceof GrVariable) {
      final GrVariable v = (GrVariable)e;
      return builder
        .element(v.getNameIdentifierGroovy())
        .optionally(v.getTypeElementGroovy())
        .optionallyInPattern(v.getInitializerGroovy())
        .role(v.getNameIdentifierGroovy(), ChildRole.VARIABLE_NAME);
    }
    else if (e instanceof GrMethod) {
      final GrMethod m = (GrMethod)e;
      return builder
        .element(m.getNameIdentifierGroovy())
        .elements(m.getParameters())
        .optionally(m.getReturnTypeElementGroovy())
        .optionallyInPattern(m.getBlock())
        .role(m.getNameIdentifierGroovy(), ChildRole.FUNCTION_NAME);
    }
    else if (e instanceof GrForInClause) {
      final GrForInClause f = (GrForInClause)e;
      return builder
        .element(f.getDeclaredVariable())
        .element(f.getIteratedExpression());
    }
    else if (e instanceof GrCodeBlock) {
      return builder.codeBlock(((GrStatementOwner)e).getStatements());
    }

    // todo: support 'object method()' <-> 'object.method()'

    return null;
  }

  @NotNull
  @Override
  public IElementType[] getVariableDelimeters() {
    return VARIABLE_DELIMETERS;
  }

  @Override
  public TokenSet getLiterals() {
    return TokenSets.CONSTANTS;
  }

  @Override
  public int getNodeCost(@NotNull PsiElement element) {
    if (element instanceof GrStatement) {
      return 2;
    }
    return 0;
  }

  @Override
  public TokenSet getIgnoredTokens() {
    return TokenSets.WHITE_SPACES_OR_COMMENTS;
  }
}
