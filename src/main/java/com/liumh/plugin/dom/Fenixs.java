package com.liumh.plugin.dom;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;

import java.util.List;

/**
 * Description:
 *
 * @author liumianhong@xiao100.com
 * @date 2020-12-19
 * <p>
 * All rights Reserved, Designed www.xiao100.com
 */
public interface Fenixs extends DomElement {
    List<Fenix> getFenixs();

    GenericAttributeValue<String> getNamespace();
}
