package com.j15r.headsup.linker;

import com.google.gwt.core.ext.LinkerContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.linker.AbstractLinker;
import com.google.gwt.core.ext.linker.ArtifactSet;
import com.google.gwt.core.ext.linker.CompilationResult;
import com.google.gwt.core.ext.linker.LinkerOrder;
import com.google.gwt.core.ext.linker.SelectionProperty;
import com.google.gwt.core.ext.linker.SyntheticArtifact;
import com.google.gwt.core.ext.linker.LinkerOrder.Order;
import com.google.gwt.dev.util.Util;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

@LinkerOrder(Order.PRIMARY)
public class BookmarkletLinker extends AbstractLinker {

  private static class JoinBuffer {
    boolean needSeparator = false;

    private final StringBuffer buffer;

    private final String separator;

    public JoinBuffer(StringBuffer buffer, String separator) {
      this.buffer = buffer;
      this.separator = separator;
    }

    public void append(String item) {
      if (needSeparator) {
        buffer.append(separator);
      } else {
        needSeparator = true;
      }
      buffer.append(item);
    }
  }

  private static class Record {
    private final CompilationResult result;

    private final SyntheticArtifact artifact;

    private final String name;

    public Record(String name, CompilationResult result,
        SyntheticArtifact artifact) {
      this.result = result;
      this.artifact = artifact;
      this.name = name;
    }

    SyntheticArtifact getArtifact() {
      return artifact;
    }

    CompilationResult getResult() {
      return result;
    }

    String getName() {
      return name;
    }
  }

  @Override
  public String getDescription() {
    return "Droplet";
  }

  private static String generateScript(String callbackName, String[] jsArray) {
    // (function(){ /* js */ window['callback']();})();
    final StringBuffer buffer = new StringBuffer("(function(){");
    buffer.append("var $stats;\n");
    buffer.append("var $doc = document, $wnd = window;\n");
    for (String js : jsArray) {
      buffer.append(js);
    }
    buffer.append("\nwindow['").append(callbackName).append("'](gwtOnLoad);\n");
    buffer.append("})();");
    return buffer.toString();
  }

  private List<Record> emitResults(TreeLogger logger, LinkerContext context,
      Collection<CompilationResult> results) throws UnableToCompleteException {
    final List<Record> records = new LinkedList<Record>();
    for (CompilationResult result : results) {
      final byte[] js = generateScript(context.getModuleFunctionName(),
          result.getJavaScript()).getBytes();

      for (SortedMap<SelectionProperty, String> map : result.getPropertyMap()) {
        String name = buildName(map);
        records.add(new Record(name, result, emitBytes(logger, js, name + ".js")));
      }
    }
    return records;
  }

  private String buildName(SortedMap<SelectionProperty, String> map) {
    StringBuffer sb = new StringBuffer();
    JoinBuffer jb = new JoinBuffer(sb, "|");
    for (SelectionProperty prop : map.keySet()) {
      jb.append(map.get(prop));
    }
    return sb.toString();
  }

  private static List<SelectionProperty> getRuntimeProperties(
      LinkerContext context) {
    final List<SelectionProperty> properties = new LinkedList<SelectionProperty>();
    for (SelectionProperty property : context.getProperties()) {
      if (property.tryGetValue() == null) {
        properties.add(property);
      }
    }
    return properties;
  }

  private List<String> createResultKeys(List<SelectionProperty> properties,
      CompilationResult result) {
    final List<String> keys = new LinkedList<String>();
    for (Map<SelectionProperty, String> resultProps : result.getPropertyMap()) {
      final StringBuffer key = new StringBuffer();
      for (SelectionProperty property : properties) {
        key.append(resultProps.get(property));
      }
      keys.add(key.toString());
    }
    return keys;
  }

  private SyntheticArtifact emitSelection(TreeLogger logger,
      LinkerContext context, Collection<Record> records)
      throws UnableToCompleteException {
    final List<SelectionProperty> runtimeProperties = getRuntimeProperties(context);

    final StringBuffer buffer = new StringBuffer();

    buffer.append(Util.readStreamAsString(BookmarkletLinker.class.getResourceAsStream(BookmarkletLinker.class.getSimpleName()
        + ".js")));

    buffer.append("var name = '" + context.getModuleName() + "';\n");
    buffer.append("var base = '';\n");
    buffer.append("window['" + context.getModuleFunctionName()
        + "'] = loadCallback;\n");

    buffer.append("infect(");
    {
      final JoinBuffer joiner = new JoinBuffer(buffer, "+'|'+");
      for (SelectionProperty property : runtimeProperties) {
        joiner.append("(function()" + property.getPropertyProvider() + ")()");
      }
    }
    buffer.append(");");

    return emitString(logger, context.optimizeJavaScript(logger,
        buffer.toString()), context.getModuleName() + ".nocache.js");
  }

  @Override
  public ArtifactSet link(TreeLogger logger, LinkerContext context,
      ArtifactSet artifacts) throws UnableToCompleteException {
    final ArtifactSet newArtifacts = new ArtifactSet(artifacts);
    final List<Record> records = emitResults(logger, context,
        newArtifacts.find(CompilationResult.class));
    for (Record record : records) {
      newArtifacts.add(record.getArtifact());
    }
    newArtifacts.add(emitSelection(logger, context, records));
    return newArtifacts;
  }
}
