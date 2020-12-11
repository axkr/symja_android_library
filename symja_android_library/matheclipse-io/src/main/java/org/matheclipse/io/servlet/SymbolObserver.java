package org.matheclipse.io.servlet;

import java.util.HashSet;
import java.util.Set;

import org.matheclipse.core.expression.ISymbolObserver;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.FEConfig;
import org.matheclipse.parser.client.math.MathException;

/** */
public class SymbolObserver implements ISymbolObserver {
  private static final Set<String> LOADED_PACKAGE_SET = new HashSet<String>();

  public SymbolObserver() {
    super();
  }

  /**
   * Load an associated package from the Appengines datastore, if a new &quot;symbol starting with
   * an uppercase character&quot; is defined. Doesn't load a package multiple times, if it's already
   * loaded.
   *
   * @return <code>true</code> if a new package was loaded.
   */
  @Override
  public synchronized boolean createPredefinedSymbol(String symbolName) {
    try {
      // SymbolEntity symbolEntity = SymbolService.findByName(symbolName);
      // if (symbolEntity != null) {
      // String packageName = symbolEntity.getPackageName();
      // if (LOADED_PACKAGE_SET.contains(packageName)) {
      // return false;
      // }
      // LOADED_PACKAGE_SET.add(packageName);
      // PackageEntity packageEntity = PackageService
      // .findByName(packageName);
      // if (packageEntity != null) {
      // PackageLoader.loadPackage(EvalEngine.get(), packageEntity);
      // return true;
      // }
      // }
    } catch (Exception ex) {
      if (FEConfig.SHOW_STACKTRACE) {
        ex.printStackTrace();
      }
    }
    return false;
  }

  @Override
  public void createUserSymbol(ISymbol symbol) {
    //		// get the user-defined symbol from the datastore
    //		UserService userService = UserServiceFactory.getUserService();
    //		User user = userService.getCurrentUser();
    //		if (user != null) {
    //			// String symbolName = symbol.toString();
    //			// UserSymbolEntity symbolEntity = UserSymbolService
    //			// .findByUserIdSymbol(user.getUserId(), symbolName);
    //			// if (symbolEntity != null) {
    //			// String source = symbolEntity.getSource();
    //			// int attributes = symbolEntity.getAttributes();
    //			// if (attributes != ISymbol.NOATTRIBUTE) {
    //			// symbol.setAttributes(attributes);
    //			// // ISymbol sym = EvalEngine.get().getUserVariable(
    //			// // symbolEntity.getSymbolName());
    //			// // if (sym != null) {
    //			// // sym.setAttributes(attributes);
    //			// // }
    //			// }
    //			// if (source.trim().length() > 0) {
    //			// try {
    //			// EvalEngine evalEngine = EvalEngine.get();
    //			// IExpr parsedExpression = evalEngine.parse(symbolEntity
    //			// .getSource());
    //			// if (parsedExpression != null) {
    //			// F.eval(parsedExpression);
    //			// return;
    //			// }
    //			// } catch (RuntimeException ex) {
    //			// return;
    //			// }
    //			// throw new MathException("Invalid user defined variable: "+symbolName);
    //			// }
    //			// }
    //			return;
    //		}
    throw new MathException("You must be logged in\nto use persistent '$'-variables!");
  }
}
