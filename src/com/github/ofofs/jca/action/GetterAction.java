package com.github.ofofs.jca.action;

import com.github.ofofs.jca.handler.GetterHandler;

/**
 * @author kangyonggan
 * @since 7/6/18
 */
public class GetterAction extends BaseAction {

    public GetterAction() {
        super(new GetterHandler());
    }
}
