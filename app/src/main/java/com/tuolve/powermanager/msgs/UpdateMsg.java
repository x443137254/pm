package com.tuolve.powermanager.msgs;

import com.tuolve.powermanager.service.CheckUpdateService;

/**
 * Created by wecc on 2018/9/24.
 *
 */

public class UpdateMsg {
    public CheckUpdateService.UpdateInfo info;
    public boolean isBig;
    public UpdateMsg(CheckUpdateService.UpdateInfo info, boolean isBig) {
        this.info = info;
        this.isBig = isBig;
    }
}
