package org.matheclipse.core.builtin;

import java.util.ArrayList;
import java.util.List;

import org.hipparchus.clustering.CentroidCluster;
import org.hipparchus.clustering.Cluster;
import org.hipparchus.clustering.DoublePoint;
import org.hipparchus.clustering.FuzzyKMeansClusterer;
import org.hipparchus.clustering.KMeansPlusPlusClusterer;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.ASTRealVector;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

public class ClusteringFuncrtions {
	/**
	 * 
	 * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation in static
	 * initializer</a>
	 */
	private static class Initializer {

		private static void init() {
			S.FindClusters.setEvaluator(new FindClusters());
		}
	}

	private static class FindClusters extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {

			if (ast.arg1().isList() && ast.arg1().size() > 1) {
				IAST listArg1 = (IAST) ast.arg1();
				int k = 3;
				if (ast.isAST2()) {
					k = ast.arg2().toIntDefault();
				}

				if (k > 0) {
					final List<DoublePoint> points = new ArrayList<DoublePoint>(listArg1.argSize());
					if (listArg1.isListOfLists()) {
						for (int j = 1; j < listArg1.size(); j++) {
							double[] values = listArg1.get(j).toDoubleVector();
							if (values == null) {
								return F.NIL;
							}
							final DoublePoint p = new DoublePoint(values);
							points.add(p);
						}
					} else {
						double[] values = listArg1.toDoubleVector();
						if (values == null) {
							return F.NIL;
						}
						for (int i = 0; i < values.length; i++) {
							final DoublePoint p = new DoublePoint(new double[] { values[i] });
							points.add(p);
						}
					}
					final KMeansPlusPlusClusterer<DoublePoint> transformer = new KMeansPlusPlusClusterer<DoublePoint>(k,
							100);
					// final FuzzyKMeansClusterer<DoublePoint> transformer = new FuzzyKMeansClusterer<DoublePoint>(k,
					// 2.0);
					final List<CentroidCluster<DoublePoint>> clusters = transformer.cluster(points);
					IASTAppendable result = F.ListAlloc(clusters.size());
					for (final Cluster<DoublePoint> cluster : clusters) {
						final List<DoublePoint> clusterPoints = cluster.getPoints();
						IASTAppendable list = F.ListAlloc(clusterPoints.size());
						if (listArg1.isListOfLists()) {
							for (int i = 0; i < clusterPoints.size(); i++) {
								double[] dVector = clusterPoints.get(i).getPoint().clone();
								list.append(new ASTRealVector(dVector, false));
							}
						} else {
							for (int i = 0; i < clusterPoints.size(); i++) {
								list.append(F.num(clusterPoints.get(i).getPoint()[0]));
							}
						}
						result.append(list);
					}
					return result;
				}
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize(IAST ast) {
			return IOFunctions.ARGS_1_2;
		}
	}

	public static void initialize() {
		Initializer.init();
	}

	private ClusteringFuncrtions() {

	}

}
