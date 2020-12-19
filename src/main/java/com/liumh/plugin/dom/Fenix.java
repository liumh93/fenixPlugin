package com.liumh.plugin.dom;

import com.intellij.psi.PsiClass;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;

/**
 * Description:
 *
 * @author liumianhong
 * @date 2020-12-19
 * <p>
 */
public interface Fenix extends DomElement {
    GenericAttributeValue<String> getId();
}
