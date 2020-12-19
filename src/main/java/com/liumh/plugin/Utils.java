package com.liumh.plugin;

import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomManager;
import com.liumh.plugin.dom.Fenixs;

/**
 * Description:
 *
 * @author liumianhong
 * @date 2020-12-19
 * <p>
 */
public class Utils {

    public static Fenixs getFenixsDomFromXml(XmlFile xmlFile){
        DomManager domManager = DomManager.getDomManager(xmlFile.getProject());
        DomFileElement<Fenixs> domFileElement = domManager.getFileElement(xmlFile, Fenixs.class, "fenixs");
        return domFileElement.getRootElement();
    }
}
