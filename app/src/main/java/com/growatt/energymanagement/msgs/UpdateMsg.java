package com.growatt.energymanagement.msgs;


import com.growatt.energymanagement.service.CheckUpdateService;

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
