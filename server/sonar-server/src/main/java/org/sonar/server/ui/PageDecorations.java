/*
 * SonarQube
 * Copyright (C) 2009-2016 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.server.ui;

import org.sonar.api.server.ServerSide;
import org.sonar.api.web.PageDecoration;

import java.util.Collections;
import java.util.List;

/**
 * @since 3.3
 */
@ServerSide
public final class PageDecorations {

  private final PageDecoration[] decorations;

  public PageDecorations(List<PageDecoration> decorations) {
    this.decorations = decorations.toArray(new PageDecoration[decorations.size()]);
  }

  public PageDecorations() {
    this(Collections.<PageDecoration>emptyList());
  }

  public PageDecoration[] get() {
    return decorations;// NOSONAR expose internal representation
  }
}
