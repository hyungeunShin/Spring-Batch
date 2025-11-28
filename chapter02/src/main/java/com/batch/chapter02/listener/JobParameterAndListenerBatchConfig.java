package com.batch.chapter02.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
public class JobParameterAndListenerBatchConfig {
    @Bean
    public Job jobParameterAndListenerJob(JobRepository jobRepository, Step jobParameterAndListenerStep) {
        return new JobBuilder("jobParameterAndListenerJob", jobRepository)
                .listener(jobParameterAndListener(null))
                .start(jobParameterAndListenerStep)
                .build();
    }

    @Bean
    public Step jobParameterAndListenerStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("jobParameterAndListenerStep", jobRepository)
                .tasklet((contribution, chunkContext) -> RepeatStatus.FINISHED, transactionManager)
                .build();
    }

    @Bean
    @JobScope
    public JobExecutionListener jobParameterAndListener(@Value("#{jobParameters['result']}") String result) {
        return new JobExecutionListener() {
            @Override
            public void beforeJob(JobExecution jobExecution) {
                log.info("jobExecutionListener.beforeJob: {}", result);
            }

            @Override
            public void afterJob(JobExecution jobExecution) {
                log.info("jobExecutionListener.afterJob: {}", result);
                log.info("jobExecutionListener.afterJob: {}", jobExecution.getStatus());
            }
        };
    }
}
