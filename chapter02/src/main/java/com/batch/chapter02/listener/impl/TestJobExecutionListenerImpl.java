package com.batch.chapter02.listener.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

@Slf4j
public class TestJobExecutionListenerImpl implements JobExecutionListener {
    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("TestJobExecutionListenerImpl.beforeJob");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info("TestJobExecutionListenerImpl.afterJob");
    }
}
