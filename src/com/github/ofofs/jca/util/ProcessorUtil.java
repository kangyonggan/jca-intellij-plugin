package com.github.ofofs.jca.util;

import com.github.ofofs.jca.AccessLevel;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * @author kangyonggan
 * @since 7/9/18
 */
public final class ProcessorUtil {

    private ProcessorUtil() {
    }

    private static final Map<Integer, AccessLevel> ACCESS_LEVEL_MAP = new HashMap<Integer, AccessLevel>() {{
        put(PsiUtil.ACCESS_LEVEL_PUBLIC, AccessLevel.PUBLIC);
        put(PsiUtil.ACCESS_LEVEL_PACKAGE_LOCAL, AccessLevel.PACKAGE);
        put(PsiUtil.ACCESS_LEVEL_PROTECTED, AccessLevel.PROTECTED);
        put(PsiUtil.ACCESS_LEVEL_PRIVATE, AccessLevel.PRIVATE);
    }};

    @Nullable
    public static String getMethodModifier(@NotNull PsiAnnotation psiAnnotation) {
        return getLevelVisibility(psiAnnotation, "value");
    }

    @Nullable
    public static String getAccessVisibility(@NotNull PsiAnnotation psiAnnotation) {
        return getLevelVisibility(psiAnnotation, "access");
    }

    @Nullable
    public static String getLevelVisibility(@NotNull PsiAnnotation psiAnnotation) {
        return getLevelVisibility(psiAnnotation, "level");
    }

    @Nullable
    public static String getLevelVisibility(@NotNull PsiAnnotation psiAnnotation, @NotNull String parameter) {
        return convertAccessLevelToJavaModifier(PsiAnnotationUtil.getStringAnnotationValue(psiAnnotation, parameter));
    }

    public static boolean isLevelVisible(@NotNull PsiAnnotation psiAnnotation) {
        return null != getLevelVisibility(psiAnnotation);
    }

    @NotNull
    public static Collection<String> getOnX(@NotNull PsiAnnotation psiAnnotation, @NotNull String parameterName) {
        PsiAnnotationMemberValue onXValue = psiAnnotation.findAttributeValue(parameterName);
        if (!(onXValue instanceof PsiAnnotation)) {
            return Collections.emptyList();
        }
        Collection<PsiAnnotation> annotations = PsiAnnotationUtil.getAnnotationValues((PsiAnnotation) onXValue, "value", PsiAnnotation.class);
        Collection<String> annotationStrings = new ArrayList<String>();
        for (PsiAnnotation annotation : annotations) {
            PsiAnnotationParameterList params = annotation.getParameterList();
            annotationStrings.add(PsiAnnotationSearchUtil.getSimpleNameOf(annotation) + params.getText());
        }
        return annotationStrings;
    }

    @Nullable
    private static String convertAccessLevelToJavaModifier(String value) {
        if (null == value || value.isEmpty()) {
            return PsiModifier.PUBLIC;
        }

        if ("PUBLIC".equals(value)) {
            return PsiModifier.PUBLIC;
        }
        if ("MODULE".equals(value)) {
            return PsiModifier.PACKAGE_LOCAL;
        }
        if ("PROTECTED".equals(value)) {
            return PsiModifier.PROTECTED;
        }
        if ("PACKAGE".equals(value)) {
            return PsiModifier.PACKAGE_LOCAL;
        }
        if ("PRIVATE".equals(value)) {
            return PsiModifier.PRIVATE;
        }
        if ("NONE".equals(value)) {
            return null;
        }
        return null;
    }

    @NotNull
    public static PsiAnnotation createAnnotationWithAccessLevel(@NotNull Class<? extends Annotation> annotationClass, @NotNull PsiModifierListOwner psiModifierListOwner) {
        String value = "";
        final PsiModifierList modifierList = psiModifierListOwner.getModifierList();
        if (null != modifierList) {
            final int accessLevelCode = PsiUtil.getAccessLevel(modifierList);

            final AccessLevel accessLevel = ACCESS_LEVEL_MAP.get(accessLevelCode);
            if (null != accessLevel && !AccessLevel.PUBLIC.equals(accessLevel)) {
                value = AccessLevel.class.getName() + "." + accessLevel;
            }
        }

        return PsiAnnotationUtil.createPsiAnnotation(psiModifierListOwner, annotationClass, value);
    }
}
