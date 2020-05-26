package org.matheclipse.api;

import java.util.Collections;
import java.util.Set;

import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.form.Documentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class DocumentationPod implements IPod {
	String word;

	public DocumentationPod(String word) {
		this.word = word;
	}

	public short podType() {
		return DOCUMENTATION;
	}

	public String keyWord() {
		return word;
	}

	public int addJSON(ObjectMapper mapper, ArrayNode podsArray, int formats, EvalEngine engine) {
		StringBuilder buf = new StringBuilder();
		if (Documentation.getMarkdown(buf, word)) {
			addDocumentationPod(mapper, podsArray, buf, formats);
			return 1;
		}
		return -1;
	}

	protected static void addDocumentationPod(ObjectMapper mapper, ArrayNode podsArray, StringBuilder buf, int formats) {
		ArrayNode temp = mapper.createArrayNode();
		ObjectNode subpodsResult = mapper.createObjectNode();
		subpodsResult.put("title", "documentation");
		subpodsResult.put("scanner", "help");
		subpodsResult.put("error", "false");
		subpodsResult.put("numsubpods", 1);
		subpodsResult.putPOJO("subpods", temp);
		podsArray.add(subpodsResult);

		ObjectNode node = mapper.createObjectNode();
		// if ((formats & HTML) != 0x00) {
		temp.add(node);
		// node.put("html", generateHTMLString(buf.toString()));
		node.put("markdown", buf.toString());
		if ((formats & Pods.HTML) != 0x00) {
			node.put("html", generateHTMLString(buf.toString()));
		}
	}

	private static String generateHTMLString(final String markdownStr) {
		Set<Extension> EXTENSIONS = Collections.singleton(TablesExtension.create());
		Parser parser = Parser.builder().extensions(EXTENSIONS).build();
		Node document = parser.parse(markdownStr);
		HtmlRenderer renderer = HtmlRenderer.builder().extensions(EXTENSIONS).build();
		return renderer.render(document); // "<p>This is <em>Sparta</em></p>\n"
	}
}
