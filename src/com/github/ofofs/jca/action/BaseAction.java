package com.github.ofofs.jca.action;

import com.intellij.codeInsight.CodeInsightActionHandler;
import com.intellij.codeInsight.generation.actions.BaseGenerateAction;

/**
 * @author kangyonggan
 * @since 7/9/18
 */
public abstract class BaseAction extends BaseGenerateAction {
    public BaseAction(CodeInsightActionHandler handler) {
        super(handler);
    }
}
