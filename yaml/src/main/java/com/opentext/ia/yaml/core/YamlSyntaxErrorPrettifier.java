/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import org.atteo.evo.inflector.English;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.error.MarkedYAMLException;


class YamlSyntaxErrorPrettifier  {

  private static final String SPACE = "space";


  public MarkedYAMLException apply(MarkedYAMLException e, String yaml) throws IOException {
    Mark problemMark = e.getProblemMark();
    Optional<YamlLine> prevLine = getLine(yaml, problemMark.getLine() - 1);
    Optional<YamlLine> line = getLine(problemMark.get_snippet(0, 100), 0);
    if (prevLine.isPresent() && line.isPresent()) {
      YamlLine prev = prevLine.get();
      YamlLine current = line.get();
      if (isIncorrectIndentation(prev, current)) {
        return incorrectIndentationException(prev, current, e);
      } else if (isItemOutsideSequence(prev, current)) {
        return itemOutsideSequence(e);
      }
    }
    return new YamlSyntaxErrorException(e.getMessage(), e);
  }

  private Optional<YamlLine> getLine(String text, int index) throws IOException {
    String result;
    int linesToGo = index;
    try (BufferedReader reader = new BufferedReader(new StringReader(text))) {
      do {
        result = reader.readLine();
      } while (linesToGo-- > 0);
    }
    return Optional.ofNullable(result).map(YamlLine::parse);
  }

  private boolean isIncorrectIndentation(YamlLine prev, YamlLine current) {
    if (prev.startsBlock() && current.indentation() != prev.indentation() + 2) {
      return true;
    }
    return prev.inSequence() && !current.inSequence() && prev.indentation() != current.indentation();
  }

  private MarkedYAMLException incorrectIndentationException(YamlLine prev, YamlLine current, MarkedYAMLException e) {
    Collection<Integer> validIndentations = new HashSet<>();
    if (prev.startsBlock()) {
      validIndentations.add(prev.indentation() + 2);
    } else {
      for (int indent = 0; indent <= prev.indentation(); indent += 2) {
        validIndentations.add(indent);
      }
    }
    int currentIndent = current.indentation();
    String message = String.format("Incorrect indentation of %d %s; expected %s", currentIndent,
        English.plural(SPACE, currentIndent), oneOfSpaces(validIndentations));
    return new YamlSyntaxErrorException(message, e);
  }

  private String oneOfSpaces(Collection<Integer> spaces) {
    switch (spaces.size()) {
      case 1:
        Integer expected = spaces.iterator().next();
        return String.format("%d %s", expected, English.plural(SPACE, expected));
      case 2:
        Iterator<Integer> values = spaces.iterator();
        return new StringBuilder()
            .append("either ")
            .append(values.next())
            .append(" or ")
            .append(values.next())
            .append(" spaces")
            .toString();
      default:
        return String.format("one of %s spaces", spaces);
    }
  }

  private boolean isItemOutsideSequence(YamlLine prev, YamlLine current) {
    return current.inSequence() && !prev.inSequence() && !prev.startsBlock();
  }

  private MarkedYAMLException itemOutsideSequence(MarkedYAMLException e) {
    return new YamlSyntaxErrorException("Item outside of sequence", e);
  }


  private static final class YamlLine {

    private static final Pattern YAML_LINE = Pattern.compile(
        "(?<indent>\\s*(-\\s*)?)(?<name>(\"[^:]+\")|[^:]+):\\s*(?<value>[^ ].*+)?");

    private final String indent;
    private final String name;
    private final String value;

    @Nullable
    static YamlLine parse(String line) {
      if (line == null) {
        return null;
      }
      Matcher matcher = YAML_LINE.matcher(line);
      if (matcher.matches()) {
        return new YamlLine(matcher.group("indent"), matcher.group("name"), matcher.group("value"));
      }
      return null;
    }

    private YamlLine(String indent, String name, String value) {
      this.indent = indent;
      this.name = name;
      this.value = value;
    }

    boolean inSequence() {
      return indent.contains("-");
    }

    int indentation() {
      return indent.length();
    }

    boolean startsBlock() {
      return value == null;
    }

    @Override
    public String toString() {
      StringBuilder result = new StringBuilder();
      result.append(indent).append(name).append(':');
      if (value != null) {
        result.append(" \"").append(value).append('"');
      }
      return result.toString();
    }

  }

}
