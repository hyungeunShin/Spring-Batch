package com.batch.chapter02.listener.anno;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TestStepExecutionListenerAnno {
    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        log.info("TestStepExecutionListenerAnno.beforeStep");
    }

    @AfterStep
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.info("TestStepExecutionListenerAnno.afterStep");
        return new ExitStatus("COMPLETED");
    }
}
