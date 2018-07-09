package com.github.ofofs.jca.handler;

import com.github.ofofs.jca.annotation.Getter;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.util.PropertyUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author kangyonggan
 * @since 7/9/18
 */
public class GetterHandler extends BaseHandler {

    @Override
    protected void processClass(PsiClass psiClass) {
        final Map<PsiField, PsiMethod> fieldMethodMap = new HashMap<>();
        for (PsiField psiField : psiClass.getFields()) {
            PsiMethod propertySetter = PropertyUtil.findPropertyGetter(psiClass, psiField.getName(), psiField.hasModifierProperty(PsiModifier.STATIC), false);

            if (null != propertySetter) {
                fieldMethodMap.put(psiField, propertySetter);
            }
        }

        processIntern(fieldMethodMap, psiClass, Getter.class);
    }
}
