//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.extend.dingtalk;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DingDingSyncTask implements IDingDingSyncTask {
    private static Logger logger = Logger.getLogger(DingDingUtil.class);
    @Autowired
    public DingDingService dingDingService;
    @Value("${dingding.syncDing:false}")
    private String syncDing;

    public DingDingSyncTask() {
    }

    public void syncOrg() {
        if (this.syncDing.equals("true")) {
            logger.debug("开始同步组织");
            String message = this.dingDingService.copyDingDepartmentToSys(this.dingDingService.getDingDeparts());
            logger.debug("组织同步结束");
            this.syncUser();
        } else {
            logger.debug("已禁止组织同步");
        }

    }

    public void syncUser() {
        logger.debug("开始同步用户");
        String message = this.dingDingService.copyDingUsersToSysByDingDepartId(1);
        logger.debug("同步用户结束");
    }
}
