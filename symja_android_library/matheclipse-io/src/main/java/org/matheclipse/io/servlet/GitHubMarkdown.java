package org.matheclipse.io.servlet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.commonmark.Extension;
import org.commonmark.ext.github.GitHubExtensions;
import org.commonmark.ext.task.list.items.TaskListItemMarker;
import org.commonmark.node.ListBlock;
import org.commonmark.node.ListItem;
import org.commonmark.node.Node;
import org.commonmark.renderer.html.AttributeProvider;
import org.commonmark.renderer.html.AttributeProviderFactory;

/**
 * The Markdown dialect of the documentation pages and of the Markdown cells of a notebook: the
 * GitHub extensions - tables, strikethrough, autolinks, task lists, alerts and footnotes - together
 * with the <code>$</code> mathematics of {@link MarkdownTeX}.
 */
public final class GitHubMarkdown {

  private GitHubMarkdown() {}

  /**
   * The extensions for both the parser and the renderer.
   *
   * @param guardDollarSymbols see {@link MarkdownTeX#extension(boolean)}
   */
  public static List<Extension> extensions(boolean guardDollarSymbols) {
    List<Extension> extensions = new ArrayList<>();
    extensions.add(MarkdownTeX.extension(guardDollarSymbols));
    extensions.addAll(GitHubExtensions.create());
    return Collections.unmodifiableList(extensions);
  }

  /**
   * Mark task list items the way GitHub does: <code>class="task-list-item"</code> on an item that
   * starts with a check box and <code>class="contains-task-list"</code> on its list, so that the
   * style sheet can drop the bullet in front of the check box.
   */
  public static AttributeProviderFactory taskListAttributes() {
    return context -> TASK_LIST_ATTRIBUTES;
  }

  private static final AttributeProvider TASK_LIST_ATTRIBUTES = new AttributeProvider() {
    @Override
    public void setAttributes(Node node, String tagName, Map<String, String> attributes) {
      if (node instanceof ListItem && isTaskListItem(node)) {
        attributes.put("class", "task-list-item");
      } else if (node instanceof ListBlock) {
        for (Node item = node.getFirstChild(); item != null; item = item.getNext()) {
          if (isTaskListItem(item)) {
            attributes.put("class", "contains-task-list");
            return;
          }
        }
      }
    }
  };

  private static boolean isTaskListItem(Node listItem) {
    return listItem.getFirstChild() instanceof TaskListItemMarker;
  }
}
