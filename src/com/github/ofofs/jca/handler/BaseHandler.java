package com.github.ofofs.jca.handler;

import com.intellij.codeInsight.CodeInsightActionHandler;
import com.intellij.codeInsight.generation.OverrideImplementUtil;
import com.intellij.openapi.command.undo.UndoUtil;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * @author kangyonggan
 * @since 7/9/18
 */
public abstract class BaseHandler implements CodeInsightActionHandler {

    @Override
    public void invoke(@NotNull Project project, @NotNull Editor editor, @NotNull PsiFile psiFile) {
        if (psiFile.isWritable()) {
            PsiClass psiClass = OverrideImplementUtil.getContextClass(project, editor, psiFile, false);
            if (null != psiClass) {
                processClass(psiClass);

                UndoUtil.markPsiFileForUndo(psiFile);
            }
        }
    }

    /**
     * 处理类
     *
     * @param psiClass 类
     */
    protected abstract void processClass(@NotNull PsiClass psiClass);

    protected void processIntern(@NotNull Map<PsiField, PsiMethod> fieldMethodMap, @NotNull PsiClass psiClass, @NotNull Class<? extends Annotation> annotationClass) {


        for (Map.Entry<PsiField, PsiMethod> fieldMethodEntry : fieldMethodMap.entrySet()) {
            final PsiField propertyField = fieldMethodEntry.getKey();
            final PsiMethod propertyMethod = fieldMethodEntry.getValue();

            if (null != propertyField) {
                propertyMethod.delete();
            }
        }
    }

}
