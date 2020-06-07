package src.main.java.org.matheclipse.core.interfaces;

@FunctionalInterface
public interface IExprProcessor {

	boolean process(IExpr min, IExpr max, IASTMutable result, int index);

}
