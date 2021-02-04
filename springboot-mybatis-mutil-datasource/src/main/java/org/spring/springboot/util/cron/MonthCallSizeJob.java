package org.spring.springboot.util.cron;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.spring.springboot.dao.StsMonthDao;
import org.spring.springboot.domain.StsMonth;
import org.spring.springboot.service.CallResultService;
import org.spring.springboot.util.Constant;
import org.spring.springboot.util.DateUtil;
import org.spring.springboot.util.RedisUtil;
import org.spring.springboot.vo.StsMonthPatchReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class MonthCallSizeJob extends QuartzJobBean {

    @Autowired
    private CallResultService callResultService;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        callResultService.groupProvinceStsMonth();
    }

}