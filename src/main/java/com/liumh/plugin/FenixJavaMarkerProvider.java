package com.liumh.plugin;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomManager;
import com.liumh.plugin.dom.Fenix;
import com.liumh.plugin.dom.Fenixs;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

/**
 * Description:
 *
 * @author liumianhong@xiao100.com
 * @date 2020-12-18
 * <p>
 * All rights Reserved, Designed www.xiao100.com
 */
public class FenixJavaMarkerProvider extends RelatedItemLineMarkerProvider {
    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element,
                                            @NotNull Collection<? super RelatedItemLineMarkerInfo> result) {
        if (!(element instanceof PsiMethod)) {
            return;
        }
        PsiMethod method = (PsiMethod) element;
        if (!method.getContainingClass().isInterface()){
            return;
        }
        if (!method.hasAnnotation("com.blinkfox.fenix.jpa.QueryFenix")){
            return;
        }
        try {
            PsiIdentifier markElement = method.getNameIdentifier();
            PsiElement targetFenixXml = findTargetFenixXml(method);
            if (markElement != null && targetFenixXml != null){
                NavigationGutterIconBuilder<PsiElement> builder =
                        NavigationGutterIconBuilder.create(Icons.Right)
                                .setTargets(targetFenixXml)
                                .setTooltipText("link to xml");
                result.add(builder.createLineMarkerInfo(markElement));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private PsiElement findTargetFenixXml(PsiMethod method){
        Project project = method.getProject();
        String possibleFileName = method.getContainingClass().getName() + ".xml";
        String fenixNamespace =  method.getContainingClass().getQualifiedName();
        String fenixId = method.getNameIdentifier().getText();
        PsiElement targetElement = findTargetXmlLineByFileName(project, possibleFileName, fenixNamespace, fenixId);
        if (targetElement == null){
            targetElement = findTargetXmlLineByFileType(project, fenixNamespace, fenixId);
        }
        return targetElement;
    }

    public PsiElement findTargetXmlLineByFileName(Project project, String fileName, String classQualifiedName, String fenixId){
        PsiFile[] psiFiles = FilenameIndex.getFilesByName(project, fileName, GlobalSearchScope.allScope(project));
        for (PsiFile psiFile : psiFiles){
            XmlFile xmlFile = (XmlFile) psiFile;
            PsiElement targetElement = findFenixIdByXmlDom(project, xmlFile, classQualifiedName, fenixId);
            if (targetElement != null){
                return targetElement;
            }
        }
        return null;
    }

    public PsiElement findTargetXmlLineByFileType(Project project, String classQualifiedName, String fenixId){
        Collection<VirtualFile> virtualFiles = FileTypeIndex.getFiles(XmlFileType.INSTANCE, GlobalSearchScope.allScope(project));
        for (VirtualFile virtualFile : virtualFiles){
            XmlFile xmlFile = (XmlFile) PsiManager.getInstance(project).findFile(virtualFile);
            PsiElement targetElement = findFenixIdByXmlDom(project, xmlFile, classQualifiedName, fenixId);
            if (targetElement != null){
                return targetElement;
            }
        }
        return null;
    }

    public PsiElement findFenixIdByXmlDom(Project project, XmlFile xmlFile, String classQualifiedName, String fenixId){
        if (!xmlFile.getRootTag().getName().equals("fenixs")){
            return null;
        }
        DomManager domManager = DomManager.getDomManager(project);
        DomFileElement<Fenixs> domFileElement = domManager.getFileElement(xmlFile, Fenixs.class, "fenixs");
        if (domFileElement == null) {
            return null;
        }
        Fenixs fenixs = domFileElement.getRootElement();
        if (!classQualifiedName.equals(fenixs.getNamespace().getValue())) {
            return null;
        }
        List<Fenix> fenixList = fenixs.getFenixs();
        if (fenixList.isEmpty()) {
            return null;
        }
        for (Fenix fenixDom : fenixList){
            String id = fenixDom.getId().getValue();
            if (fenixId.equals(id)){
                return fenixDom.getXmlElement().getOriginalElement();
            }
        }
        return null;
    }
}
