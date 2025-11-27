package com.batch.chapter02.scope;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@StepScope
public class ScopeTasklet implements Tasklet {
    private final String jobParameter;

    public ScopeTasklet(@Value("#{jobParameters['jobParameter']}") String jobParameter) {
        this.jobParameter = jobParameter;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        log.info("jobParameter: {}", jobParameter);
        return RepeatStatus.FINISHED;
    }
}
