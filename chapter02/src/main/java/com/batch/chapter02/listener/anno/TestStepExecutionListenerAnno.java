package com.batch.chapter02.listener.anno;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;

@Slf4j
public class TestStepExecutionListenerAnno {
    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        log.info("TestStepExecutionListenerAnno.beforeStep");
    }

    //StepExecutionListener의 afterStep() 메서드가 ExitStatus를 반환하기 때문에 @AfterStep 어노테이션을 사용할 때는 이 반환 타입을 꼭 지켜줘야 한다.
    @AfterStep
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.info("TestStepExecutionListenerAnno.afterStep");
        return new ExitStatus("COMPLETED");
    }
}
