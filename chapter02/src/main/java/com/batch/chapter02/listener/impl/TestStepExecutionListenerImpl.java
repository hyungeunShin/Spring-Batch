package com.batch.chapter02.listener.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

@Slf4j
public class TestStepExecutionListenerImpl implements StepExecutionListener {
    @Override
    public void beforeStep(StepExecution stepExecution) {
        log.info("TestStepExecutionListenerImpl.beforeStep");
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.info("TestStepExecutionListenerImpl.afterStep");
        return StepExecutionListener.super.afterStep(stepExecution);
    }
}
