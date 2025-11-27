package com.batch.chapter02.listener.anno;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.batch.core.annotation.BeforeJob;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TestJobExecutionListenerAnno {
    @BeforeJob
    public void beforeJob(JobExecution jobExecution) {
        log.info("TestJobExecutionListenerAnno.beforeJob");
    }

    @AfterJob
    public void afterJob(JobExecution jobExecution) {
        log.info("TestJobExecutionListenerAnno.afterJob");
    }
}
