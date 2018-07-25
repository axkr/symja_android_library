package ch.ethz.idsc.tensor.qty;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.regex.Pattern;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

/** associates strings with instances of unit */
/* package */ enum UnitHelper {
	MEMO;
	// ---
	private static final int SIZE = 500;
	private static final Pattern PATTERN = Pattern.compile("[a-zA-Z]+");
	// ---
	private final Map<String, IUnit> map = new LinkedHashMap<String, IUnit>(SIZE * 4 / 3, 0.75f, true) {
		@Override
		protected boolean removeEldestEntry(Map.Entry<String, IUnit> eldest) {
			return size() > SIZE;
		}
	};

	/**
	 * @param string,
	 *            for instance "A*kg^-1*s^2"
	 * @return unit
	 */
	/* package */ IUnit lookup(String string) {
		IUnit unit = map.get(string);
		if (Objects.isNull(unit)) {
			unit = create(string);
			map.put(string, unit);
		}
		return unit;
	}

	/* package */ static String requireValid(String key) {
		if (!PATTERN.matcher(key).matches())
			throw new IllegalArgumentException(key);
		return key;
	}

	// helper function
	private static IUnit create(String string) {
		NavigableMap<String, IExpr> map = new TreeMap<>();
		StringTokenizer stringTokenizer = new StringTokenizer(string, IUnit.JOIN_DELIMITER);
		while (stringTokenizer.hasMoreTokens()) {
			String token = stringTokenizer.nextToken();
			int index = token.indexOf('^');
			final String unit;
			IExpr exponent;
			if (0 <= index) {
				unit = token.substring(0, index);
				exponent = F.fromString(token.substring(index + 1));
				if (exponent.isOne()) {
					exponent = F.C1;
				}
			} else {
				unit = token;
				exponent = F.C1;
			}
			String key = requireValid(unit.trim());
			if (map.containsKey(key)) { // exponent exists
				IExpr sum = map.get(key).add(exponent);
				if (sum.isZero())
					map.remove(key); // exponents cancel
				else
					map.put(key, sum); // update total exponent
			} else //
			if (!exponent.isZero()) // introduce exponent
				map.put(key, exponent);
		}
		return new UnitImpl(map);
	}
}
