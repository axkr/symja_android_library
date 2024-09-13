package org.matheclipse.core.visit;

import java.util.ArrayList;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ReturnException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IExpr.COMPARE_TERNARY;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.patternmatching.IPatternMatcher;

/** Visitor for the <code>ReplacePart()</code> function */
public class VisitorReplacePart extends AbstractVisitor {

  /**
   * A list of pattern matchers which should be matched against, for every possible position of an
   * {@link IAST} structure.
   */
  private ArrayList<IPatternMatcher> patternMatcherList;

  /**
   * <code>0</code> or <code>1</code>, depending if option <code>Heads->True</code> is set or the
   * <code>0</code> index position is used in the left-hand-side of a rule.
   */
  private int offset;

  /**
   * The current evaluation engine for this thread.
   */
  final private EvalEngine engine;

  /**
   * Create a new visitor which replaces parts of an expression.
   *
   * @param rule a rule or list-of-rules
   * @param heads if <code>TRUE</code> also replace the heads of expressions
   */
  public VisitorReplacePart(IAST rule, IExpr.COMPARE_TERNARY heads) {
    super();
    engine = EvalEngine.get();
    if (rule.isRuleAST()) {
      rule = F.list(rule);
    }
    if (rule.isListOfRules()) {
      IAST list = rule;
      this.patternMatcherList = new ArrayList<IPatternMatcher>(list.argSize() + 3);
      offset = heads == IExpr.COMPARE_TERNARY.TRUE ? 0 : 1;
      for (int i = 1; i < list.size(); i++) {
        rule = (IAST) list.get(i);
        initPatternMatcher(rule, heads);
      }
      if (heads == COMPARE_TERNARY.FALSE) {
        offset = 1;
      }
    }
  }

  private void initPatternMatcher(IAST rule, IExpr.COMPARE_TERNARY heads) {
    IExpr fromPositions = rule.arg1();
    try {
      // try extracting an int[] array of expressions
      if (fromPositions.isList()) {
        IAST list = (IAST) fromPositions;
        if (list.isListOfLists()) {
          for (int j = 1; j < list.size(); j++) {
            IAST subList = list.getAST(j);
            int[] positions = new int[subList.argSize()];
            for (int k = 1; k < subList.size(); k++) {
              positions[k - 1] = subList.get(k).toIntDefault();
              if (positions[k - 1] == Integer.MIN_VALUE) {
                throw ReturnException.RETURN_FALSE;
              }
              if (positions[k - 1] == 0) {
                offset = 0;
              }
            }
            IPatternMatcher evalPatternMatcher =
                engine.evalPatternMatcher(F.Sequence(positions), rule.arg2());
            this.patternMatcherList.add(evalPatternMatcher);
          }
        } else {
          if (list.argSize() > 0) {
            int[] positions = new int[list.argSize()];

            for (int j = 1; j < list.size(); j++) {
              positions[j - 1] = list.get(j).toIntDefault();
              if (positions[j - 1] == Integer.MIN_VALUE) {
                throw ReturnException.RETURN_FALSE;
              }
              if (positions[j - 1] == 0) {
                offset = 0;
              }
            }
            IPatternMatcher evalPatternMatcher =
                engine.evalPatternMatcher(F.Sequence(positions), rule.arg2());
            this.patternMatcherList.add(evalPatternMatcher);
          }
        }
      } else {
        int[] positions = new int[] {rule.arg1().toIntDefault()};
        if (positions[0] == Integer.MIN_VALUE) {
          throw ReturnException.RETURN_FALSE;
        }
        if (positions[0] == 0) {
          offset = 0;
        }
        IPatternMatcher evalPatternMatcher =
            engine.evalPatternMatcher(F.Sequence(positions), rule.arg2());
        this.patternMatcherList.add(evalPatternMatcher);

      }
    } catch (ReturnException rex) {
      if (fromPositions.isList()) {
        IAST list = ((IAST) fromPositions).apply(S.Sequence, 1);
        IPatternMatcher evalPatternMatcher = engine.evalPatternMatcher(list, rule.arg2());
        this.patternMatcherList.add(evalPatternMatcher);
      } else {
        IPatternMatcher evalPatternMatcher = engine.evalPatternMatcher(fromPositions, rule.arg2());
        this.patternMatcherList.add(evalPatternMatcher);
      }
    }
  }

  private IExpr visitPatternIndexList(IAST ast, IASTAppendable positions) {
    IASTAppendable result = F.NIL;
    for (int i = offset; i < ast.size(); i++) {
      final IInteger position = F.ZZ(i);
      for (int j = 0; j < patternMatcherList.size(); j++) {
        IPatternMatcher matcher = patternMatcherList.get(j);
        IASTAppendable positionsToMatch = positions.copyAppendable();
        positionsToMatch.append(position);

        IASTAppendable temp = patternIndexRecursive(matcher, ast, positionsToMatch, i, result);
        if (temp.isPresent()) {
          result = temp;
          break;
        }

        IInteger negativePart = F.ZZUniqueReference(i - ast.size());
        positionsToMatch.set(positionsToMatch.size() - 1, negativePart);
        temp = patternIndexRecursive(matcher, ast, positionsToMatch, i, result);
        if (temp.isPresent()) {
          IExpr ex = temp.replaceAll(x -> {
            if (x == negativePart) {
              // compare unique reference from F.ZZUniqueReference(); don't use equals() method!
              return position;
            }
            return F.NIL;
          });
          if (ex.isPresent() && (ex instanceof IASTAppendable)) {
            result = (IASTAppendable) ex;
          } else {
            result = temp;
          }
          break;
        }

        if (ast.isAssociation() && i > 0) {
          // for associations the key instead of the position can also match
          positionsToMatch.set(positionsToMatch.size() - 1, ast.getRule(i).first());
          temp = patternIndexRecursive(matcher, ast, positionsToMatch, i, result);
          if (temp.isPresent()) {
            result = temp;
            break;
          }
        }

      }

    }
    return result;
  }

  /**
   * @param matcher
   * @param ast
   * @param positionsToMatch the list of positions which should be matched
   * @param position
   * @param result
   * @return {@link F#NIL} if no match was found
   */
  private IASTAppendable patternIndexRecursive(IPatternMatcher matcher, IAST ast,
      IASTAppendable positionsToMatch, int position, IASTAppendable result) {
    if (matcher.getLHS().isSequence()) {
      IExpr temp = matcher.eval(positionsToMatch, engine);
      if (temp.isPresent()) {
        if (position == 0 && ast.isAssociation()) {
          result = ((IAssociation) ast).copyAST();
        } else if (result.isNIL()) {
          result = ast.copyAppendable();
        }
        result.setValue(position, temp);
        return result;
      } else {
        if (ast.get(position).isASTOrAssociation()) {
          temp = visitPatternIndexList((IAST) ast.get(position), positionsToMatch);
          if (temp.isPresent()) {
            if (position == 0 && ast.isAssociation()) {
              result = ((IAssociation) ast).copyAST();
            } else if (result.isNIL()) {
              result = ast.copyAppendable();
            }

            result.setValue(position, temp);
            return result;
          }
        }
      }
    } else {
      IExpr temp = matcher.eval(F.ZZ(position), engine);
      if (temp.isPresent()) {
        if (result.isNIL()) {
          result = ast.copyAppendable();
        }
        result.setValue(position, temp);
        return result;
      }
    }
    return F.NIL;
  }

  @Override
  public IExpr visit(IASTMutable ast) {
    IASTAppendable positionsToMatch = F.ast(S.Sequence);
    for (int j = 0; j < patternMatcherList.size(); j++) {
      IPatternMatcher matcher = patternMatcherList.get(j);
      IExpr lhs = matcher.getLHS();
      if (lhs.isAST(S.Sequence, 1)) {
        // empty sequence matches with complete expression
        return matcher.getRHS();
      }
    }
    return visitPatternIndexList(ast, positionsToMatch);
  }
}
