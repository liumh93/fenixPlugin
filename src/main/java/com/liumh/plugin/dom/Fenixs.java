package com.liumh.plugin.dom;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;

import java.util.List;

/**
 * Description:
 *
 * @author liumianhong
 * @date 2020-12-19
 * <p>
 */
public interface Fenixs extends DomElement {
    List<Fenix> getFenixs();

    GenericAttributeValue<String> getNamespace();
}
