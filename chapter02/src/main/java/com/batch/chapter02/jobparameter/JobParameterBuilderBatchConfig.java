package com.batch.chapter02.jobparameter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
public class JobParameterBuilderBatchConfig {
    @Bean
    public Job jobParameterBuilderJob(JobRepository jobRepository, Step jobParameterBuilderStep) {
        return new JobBuilder("jobParameterBuilderJob", jobRepository)
                .start(jobParameterBuilderStep)
                .build();
    }

    @Bean
    public Step jobParameterBuilderStep(JobRepository jobRepository, PlatformTransactionManager transactionManager, Tasklet jobParameterBuilderTasklet) {
        return new StepBuilder("jobParameterBuilderStep", jobRepository)
                .tasklet(jobParameterBuilderTasklet, transactionManager)
                .build();
    }

    @Bean
    public Tasklet jobParameterBuilderTasklet() {
        return (contribution, chunkContext) -> {
            JobParameters jobParameters = chunkContext.getStepContext().getStepExecution().getJobParameters();
            String a = jobParameters.getString("a");
            Long b = jobParameters.getLong("b");

            log.info("a: {}", a);
            log.info("b: {}", b);

            return RepeatStatus.FINISHED;
        };
    }
}
