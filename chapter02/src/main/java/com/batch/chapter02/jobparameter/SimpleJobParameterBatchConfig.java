package com.batch.chapter02.jobparameter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
public class SimpleJobParameterBatchConfig {
    @Bean
    public Job simpleJobParameterJob(JobRepository jobRepository, Step simpleJobParameterStep) {
        return new JobBuilder("simpleJobParameterJob", jobRepository)
                .start(simpleJobParameterStep)
                .build();
    }

    @Bean
    public Step simpleJobParameterStep(JobRepository jobRepository, PlatformTransactionManager transactionManager, Tasklet simpleJobParameterTasklet) {
        return new StepBuilder("simpleJobParameterStep", jobRepository)
                .tasklet(simpleJobParameterTasklet, transactionManager)
                .build();
    }

    @Bean
    @StepScope
    public Tasklet simpleJobParameterTasklet(@Value("#{jobParameters['a']}") String a, @Value("#{jobParameters['b']}") Integer b) {
        return (contribution, chunkContext) -> {
            log.info("a: {}", a);
            log.info("b: {}", b);
            return RepeatStatus.FINISHED;
        };
    }
}
