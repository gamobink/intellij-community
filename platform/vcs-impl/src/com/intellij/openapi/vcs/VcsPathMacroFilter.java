// Copyright 2000-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.openapi.vcs;

import com.intellij.openapi.application.PathMacroFilter;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

/**
 * @author yole
 */
public class VcsPathMacroFilter extends PathMacroFilter {
  @Override
  public boolean skipPathMacros(@NotNull Attribute attribute) {
    final Element parent = attribute.getParent();
    final String parentName = parent.getName();
    if ("MESSAGE".equals(parentName) && "value".equals(attribute.getName())) {
      return true;
    }
    if ("option".equals(parentName) && "LAST_COMMIT_MESSAGE".equals(parent.getAttributeValue("name"))) {
      return true;
    }

    // ignore comment field of ChangeListManager.list and in general any attribute named "comment" since in most cases value should be stored as is
    if ("comment".equals(attribute.getName())) {
      return true;
    }
    return false;
  }
}
