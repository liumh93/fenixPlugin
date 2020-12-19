package com.liumh.plugin;

import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomManager;
import com.liumh.plugin.dom.Fenixs;

/**
 * Description:
 *
 * @author liumianhong@xiao100.com
 * @date 2020-12-19
 * <p>
 * All rights Reserved, Designed www.xiao100.com
 */
public class Utils {

    public static Fenixs getFenixsDomFromXml(XmlFile xmlFile){
        DomManager domManager = DomManager.getDomManager(xmlFile.getProject());
        DomFileElement<Fenixs> domFileElement = domManager.getFileElement(xmlFile, Fenixs.class, "fenixs");
        return domFileElement.getRootElement();
    }
}
