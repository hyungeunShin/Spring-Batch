package com.batch.chapter02.jobparameter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.converter.JobParametersConverter;
import org.springframework.batch.core.converter.JsonJobParametersConverter;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
public class JsonJobParameterBatchConfig {
    @Bean
    public Job jsonJobParameterJob(JobRepository jobRepository, Step jsonJobParameterStep) {
        return new JobBuilder("jsonJobParameterJob", jobRepository)
                .start(jsonJobParameterStep)
                .build();
    }

    @Bean
    public Step jsonJobParameterStep(JobRepository jobRepository, PlatformTransactionManager transactionManager, Tasklet jsonJobParameterTasklet) {
        return new StepBuilder("jsonJobParameterStep", jobRepository)
                .tasklet(jsonJobParameterTasklet, transactionManager)
                .build();
    }

    @Bean
    @StepScope
    public Tasklet jsonJobParameterTasklet(@Value("#{jobParameters['a']}") String a) {
        return (contribution, chunkContext) -> {
            String[] arr = a.split(",");
            log.info("arr[0]: {}", arr[0]);
            log.info("arr[1]: {}", arr[1]);
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    @Profile("json")
    public JobParametersConverter jobParametersConverter() {
        return new JsonJobParametersConverter();
    }
}
