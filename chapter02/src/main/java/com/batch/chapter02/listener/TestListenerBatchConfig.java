package com.batch.chapter02.listener;

import com.batch.chapter02.listener.anno.TestJobExecutionListenerAnno;
import com.batch.chapter02.listener.anno.TestStepExecutionListenerAnno;
import com.batch.chapter02.listener.impl.TestJobExecutionListenerImpl;
import com.batch.chapter02.listener.impl.TestStepExecutionListenerImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
public class TestListenerBatchConfig {
    @Bean
    public Job testListenerJob(JobRepository jobRepository, Step testListenerStep) {
        return new JobBuilder("testListenerJob", jobRepository)
                .listener(new TestJobExecutionListenerImpl())
                //.listener(new TestJobExecutionListenerAnno())
                .start(testListenerStep)
                .build();
    }

    @Bean
    public Step testListenerStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("testListenerStep", jobRepository)
                .listener(new TestStepExecutionListenerImpl())
                //.listener(new TestStepExecutionListenerAnno())
                .tasklet((contribution, chunkContext) -> {
                    log.info("testListenerStep");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
}
