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
public class EnumJobParameterBatchConfig {
    @Bean
    public Job enumJobParameterJob(JobRepository jobRepository, Step enumJobParameterStep) {
        return new JobBuilder("enumJobParameterJob", jobRepository)
                .start(enumJobParameterStep)
                .build();
    }

    @Bean
    public Step enumJobParameterStep(JobRepository jobRepository, PlatformTransactionManager transactionManager, Tasklet enumJobParameterTasklet) {
        return new StepBuilder("enumJobParameterStep", jobRepository)
                .tasklet(enumJobParameterTasklet, transactionManager)
                .build();
    }

    @Bean
    @StepScope
    public Tasklet enumJobParameterTasklet(@Value("#{jobParameters['testEnum']}") TestEnum testEnum) {
        return (contribution, chunkContext) -> {
            log.info("TestEnum: {}", testEnum);
            int enumValue = switch(testEnum) {
                case A -> 1;
                case B -> 2;
                case C -> 3;
                case D -> 4;
            };
            log.info("EnumValue: {}", enumValue);
            return RepeatStatus.FINISHED;
        };
    }

    public enum TestEnum {
        A, B, C, D
    }
}
