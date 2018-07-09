package com.github.ofofs.jca.builder;

import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.light.LightMethodBuilder;

/**
 * @author kangyonggan
 * @since 7/9/18
 */
public class MethodBuilder extends LightMethodBuilder {

    private PsiCodeBlock body;

    public MethodBuilder(PsiManager manager, String name) {
        super(manager, name);
    }

    public void setBody(PsiCodeBlock body) {
        this.body = body;
    }

    @Override
    public PsiCodeBlock getBody() {
        return body;
    }
}
