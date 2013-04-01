package org.matheclipse.parser.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.expression.F;
import org.matheclipse.parser.client.Parser;
import org.matheclipse.parser.client.ast.ASTNode;

public class ParseRubiFiles {
	public static List<ASTNode> parseFileToList(String fileName) {
		try {
			File file = new File(fileName);
			final BufferedReader f = new BufferedReader(new FileReader(file));
			final StringBuffer buff = new StringBuffer(1024);
			String line;
			while ((line = f.readLine()) != null) {
				buff.append(line);
				buff.append('\n');
			}
			f.close();
			String inputString = buff.toString();
			Parser p = new Parser(false, true);
			return p.parseList(inputString);
			// assertEquals(obj.toString(),
			// "Plus[Plus[Times[-1, a], Times[-1, Times[b, Factorial2[c]]]], d]");
		} catch (Exception e) {
			e.printStackTrace();
			// assertEquals("", e.getMessage());
		}
		return null;
	}

	public static void main(String[] args) {
		String[] fileNames = { "C:\\temp\\RationalFunctionIntegrationRules.m", "C:\\temp\\AlgebraicFunctionIntegrationRules.m",
				"C:\\temp\\ExponentialFunctionIntegrationRules.m", "C:\\temp\\TrigFunctionIntegrationRules.m",
				"C:\\temp\\HyperbolicFunctionIntegrationRules.m", "C:\\temp\\LogarithmFunctionIntegrationRules.m",
				"C:\\temp\\InverseTrigFunctionIntegrationRules.m", "C:\\temp\\InverseHyperbolicFunctionIntegrationRules.m",
				"C:\\temp\\ErrorFunctionIntegrationRules.m", "C:\\temp\\IntegralFunctionIntegrationRules.m",
				"C:\\temp\\SpecialFunctionIntegrationRules.m", "C:\\temp\\GeneralIntegrationRules.m" };
		for (int i = 0; i < fileNames.length; i++) {
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>");
			System.out.println(">>>>> File name: " + fileNames[i]);
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>");
			List<ASTNode> list = parseFileToList(fileNames[i]);
			if (list != null) {
				for (ASTNode astNode : list) {
					System.out.println(astNode);
				}
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>");
				System.out.println(">>>>> Number of entries: " + list.size());
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>");
			}
		}
	}
}
