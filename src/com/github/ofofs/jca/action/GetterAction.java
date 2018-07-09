package com.github.ofofs.jca.action;

import com.github.ofofs.jca.builder.MethodBuilder;
import com.github.ofofs.jca.util.PsiMethodUtil;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.impl.source.PsiClassImpl;
import org.jetbrains.annotations.NotNull;

/**
 * @author kangyonggan
 * @since 7/6/18
 */
public class GetterAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getProject();

        // 遍历所有文件
        for (VirtualFile file : project.getBaseDir().getChildren()) {
            processFile(project, file);
        }

    }

    /**
     * 处理单个文件
     *
     * @param project 项目
     * @param file    文件
     */
    private void processFile(Project project, VirtualFile file) {
        if (file.isDirectory()) {
            // 如果是文件夹，逐个处理子文件
            for (VirtualFile childFile : file.getChildren()) {
                processFile(project, childFile);
            }
        } else if (file.getFileType() instanceof JavaFileType) {
            // 只处理java文件
            PsiFile psiFile = PsiManager.getInstance(project).findFile(file);

            if ("GetterTest3.java".equals(psiFile.getName())) {
                psiFile.accept(new PsiElementVisitor() {
                    @Override
                    public void visitElement(PsiElement element) {
                        for (PsiElement child : element.getChildren()) {
                            if (child instanceof PsiClassImpl) {
                                PsiClassImpl psiClass = (PsiClassImpl) child;

                                MethodBuilder methodBuilder = new MethodBuilder(psiClass.getManager(), "getName");
                                methodBuilder.addModifier(PsiModifier.PUBLIC);
                                methodBuilder.setMethodReturnType(PsiType.BOOLEAN);
                                methodBuilder.setContainingClass(psiClass);

                                PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(project);
                                PsiCodeBlock body = elementFactory.createCodeBlockFromText("{return name;}", psiClass);
                                methodBuilder.setBody(body);

                                System.out.println(methodBuilder instanceof PsiMethod);

                                WriteCommandAction.runWriteCommandAction(project, new Runnable() {
                                    @Override
                                    public void run() {
                                        //do something
                                        PsiMethod psiMethod = rebuildMethod(project, methodBuilder);
                                        psiClass.add(psiMethod);

                                        psiMethod.delete();
                                    }
                                });

                            }
                        }
                    }
                });
            }

        }
    }


    private PsiMethod rebuildMethod(@NotNull Project project, @NotNull PsiMethod fromMethod) {
        final PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(project);

        final PsiMethod resultMethod;
        final PsiType returnType = fromMethod.getReturnType();
        if (null == returnType) {
            resultMethod = elementFactory.createConstructor(fromMethod.getName());
        } else {
            resultMethod = elementFactory.createMethod(fromMethod.getName(), returnType);
        }

        rebuildTypeParameter(fromMethod, resultMethod);

        final PsiClassType[] referencedTypes = fromMethod.getThrowsList().getReferencedTypes();
        if (referencedTypes.length > 0) {
            PsiJavaCodeReferenceElement[] refs = new PsiJavaCodeReferenceElement[referencedTypes.length];
            for (int i = 0; i < refs.length; i++) {
                refs[i] = elementFactory.createReferenceElementByType(referencedTypes[i]);
            }
            resultMethod.getThrowsList().replace(elementFactory.createReferenceList(refs));
        }

        for (PsiParameter parameter : fromMethod.getParameterList().getParameters()) {
            PsiParameter param = elementFactory.createParameter(parameter.getName(), parameter.getType());
            if (parameter.getModifierList() != null) {
                PsiModifierList modifierList = param.getModifierList();
                for (PsiAnnotation originalAnnotation : parameter.getModifierList().getAnnotations()) {
                    final PsiAnnotation annotation = modifierList.addAnnotation(originalAnnotation.getQualifiedName());
                    for (PsiNameValuePair nameValuePair : originalAnnotation.getParameterList().getAttributes()) {
                        annotation.setDeclaredAttributeValue(nameValuePair.getName(), nameValuePair.getValue());
                    }
                }
            }
            resultMethod.getParameterList().add(param);
        }

        final PsiModifierList fromMethodModifierList = fromMethod.getModifierList();
        final PsiModifierList resultMethodModifierList = resultMethod.getModifierList();
        copyModifiers(fromMethodModifierList, resultMethodModifierList);
        for (PsiAnnotation psiAnnotation : fromMethodModifierList.getAnnotations()) {
            final PsiAnnotation annotation = resultMethodModifierList.addAnnotation(psiAnnotation.getQualifiedName());
            for (PsiNameValuePair nameValuePair : psiAnnotation.getParameterList().getAttributes()) {
                annotation.setDeclaredAttributeValue(nameValuePair.getName(), nameValuePair.getValue());
            }
        }

        PsiCodeBlock body = fromMethod.getBody();
        if (null != body) {
            resultMethod.getBody().replace(body);
        }

        return (PsiMethod) CodeStyleManager.getInstance(project).reformat(resultMethod);
    }


    private void rebuildTypeParameter(@NotNull PsiTypeParameterListOwner listOwner, @NotNull PsiTypeParameterListOwner resultOwner) {
        final PsiTypeParameterList fromMethodTypeParameterList = listOwner.getTypeParameterList();
        if (listOwner.hasTypeParameters() && null != fromMethodTypeParameterList) {
            PsiTypeParameterList typeParameterList = PsiMethodUtil.createTypeParameterList(fromMethodTypeParameterList);
            if (null != typeParameterList) {
                final PsiTypeParameterList resultOwnerTypeParameterList = resultOwner.getTypeParameterList();
                if (null != resultOwnerTypeParameterList) {
                    resultOwnerTypeParameterList.replace(typeParameterList);
                }
            }
        }
    }

    private void copyModifiers(PsiModifierList fromModifierList, PsiModifierList resultModifierList) {
        for (String modifier : PsiModifier.MODIFIERS) {
            resultModifierList.setModifierProperty(modifier, fromModifierList.hasModifierProperty(modifier));
        }
    }
}
