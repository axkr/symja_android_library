// code adapted from https://github.com/datahaki/tensor
package org.matheclipse.core.tensor.img;

import java.io.Serializable;
import java.util.OptionalInt;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.tensor.api.TensorUnaryOperator;
import org.matheclipse.core.visit.VisitorLevelSpecification;

public class ImageCrop implements TensorUnaryOperator {
  /**
   * for grayscale images given value should be a scalar for RGBA images given value should be a
   * vector of length 4
   * 
   * @param value
   * @return operator that removes the boundary of images of given color value
   */
  @SuppressWarnings("unchecked")
  public static TensorUnaryOperator eq(IExpr value) {
    return new ImageCrop((Predicate<IExpr> & Serializable) value::equals);
  }

  // ---
  private final Predicate<IExpr> predicate;

  private ImageCrop(Predicate<IExpr> predicate) {
    this.predicate = predicate;
  }

  @Override
  public IAST apply(IAST image) {
    // int depth = 2;
    // TODO TENSOR IMG not as efficient as could be
    int dim0 = image.argSize();
    final IAST fimage = image;
    int d0lo = IntStream.range(0, dim0) //
        .filter(i -> !fimage.getAST(i + 1).stream().allMatch(predicate)) //
        .findFirst() //
        .orElse(dim0);
    int d0hi = IntStream.range(d0lo, dim0) //
        .map(i -> dim0 - i - 1) //
        .filter(i -> !fimage.getAST(i + 1).stream().allMatch(predicate)) //
        .findFirst() //
        .orElse(dim0);
    image = F.ListAlloc(image.stream().skip(d0lo).limit(d0hi - d0lo + 1));
    int dim1 = image.first().argSize();// .dimension1(image);
    IAST boole = tensorMap(image, entry -> F.booleInteger(predicate.test(entry)), 2);
    IAST vectorX = tensorMap(boole, x -> S.Total.of(x), 0);
    int fdim0 = image.argSize();
    IInteger dimS0 = F.ZZ(fdim0);
    OptionalInt d1min1 = IntStream.range(0, dim1) //
        .filter(index -> !vectorX.get(index + 1).equals(dimS0)).findFirst();
    OptionalInt d1max = IntStream.range(0, dim1) //
        .filter(index -> !vectorX.get(dim1 - index).equals(dimS0)).findFirst();
    int xmin = d1min1.orElse(0);
    int xmax = dim1 - d1max.orElse(0);
    return F.ListAlloc(image.stream().map(row -> ((IAST) row).slice(xmin + 1, xmax + 1)));
  }

  private static IAST tensorMap(IAST list, Function<IExpr, IExpr> function, int levelIndex) {
    VisitorLevelSpecification level =
        new VisitorLevelSpecification(x -> function.apply(x), levelIndex, false);
    return (IAST) list.accept(level).orElse(list);
  }

}
