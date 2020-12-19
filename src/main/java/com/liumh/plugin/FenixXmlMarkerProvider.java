package com.liumh.plugin;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlFile;
import com.liumh.plugin.dom.Fenix;
import com.liumh.plugin.dom.Fenixs;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Description:
 *
 * @author liumianhong
 * @date 2020-12-18
 * <p>
 */
public class FenixXmlMarkerProvider extends RelatedItemLineMarkerProvider {
    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element,
                                            @NotNull Collection<? super RelatedItemLineMarkerInfo> result) {
        if (!(element instanceof XmlFile)) {
            return;
        }
        XmlFile xmlFile = (XmlFile) element;
        if (!xmlFile.getRootTag().getName().equals("fenixs")){
            return;
        }
        try {
            Map<PsiElement, PsiElement> targetFileMap = findTargetFileMap(xmlFile);
            targetFileMap.forEach((sourceElement, targetElement) -> {
                NavigationGutterIconBuilder<PsiElement> builder = NavigationGutterIconBuilder.create(Icons.LEFT)
                        .setTargets(targetElement)
                        .setTooltipText("link to java method");
                result.add(builder.createLineMarkerInfo(sourceElement));
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private Map<PsiElement, PsiElement> findTargetFileMap(XmlFile xmlFile){
        Fenixs fenixs = Utils.getFenixsDomFromXml(xmlFile);
        List<Fenix> fenixList = fenixs.getFenixs();
        if (fenixList.isEmpty()){
            return null;
        }
        PsiJavaFile targetFile = findTargetFile(xmlFile, fenixs);
        if (targetFile == null){
            return null;
        }
        return createTargetMap(targetFile, fenixs.getNamespace().getValue(), fenixList);
    }

    private PsiJavaFile findTargetFile(XmlFile xmlFile, Fenixs fenixs){
        Project project = xmlFile.getProject();
        String[] splitName = StringUtils.split(fenixs.getNamespace().getValue(), ".");
        String targetClassName = splitName[splitName.length-1];
        String targetFileName = targetClassName + ".java";
        System.out.println(targetFileName);
        PsiFile[] psiFiles = FilenameIndex.getFilesByName(project, targetFileName, GlobalSearchScope.allScope(project));
        if (psiFiles.length == 0){
            return null;
        }
        for (PsiFile psiFile : psiFiles){
            PsiJavaFile psiJavaFile = (PsiJavaFile) psiFile;
            if (fenixs.getNamespace().getValue().equals(psiJavaFile.getPackageName() + "." + targetClassName)){
                return psiJavaFile;
            }
        }
        return null;
    }

    private Map<PsiElement, PsiElement> createTargetMap(PsiJavaFile targetFile, String qualifiedName, List<Fenix> fenixList){
        PsiClass[] classes = targetFile.getClasses();
        Map<PsiElement, PsiElement> targetMap = new HashMap<>();
        for (PsiClass psiClass : classes){
            if (!StringUtils.equals(psiClass.getQualifiedName(), qualifiedName)){
                continue;
            }
            for (Fenix fenix : fenixList){
                PsiMethod[] psiMethods = psiClass.findMethodsByName(fenix.getId().getValue(), true);
                if (psiMethods.length == 0) {
                    continue;
                }
                for (PsiMethod psiMethod : psiMethods){
                    if (psiMethod.hasAnnotation("com.blinkfox.fenix.jpa.QueryFenix")){
                        targetMap.put(fenix.getXmlElement().getOriginalElement(), psiMethods[0].getNameIdentifier());
                        break;
                    }
                }
            }
        }
        return targetMap;
    }
}
