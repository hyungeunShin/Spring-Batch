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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Configuration
public class DateJobParameterBatchConfig {
    @Bean
    public Job dateJobParameterJob(JobRepository jobRepository, Step dateJobParameterStep) {
        return new JobBuilder("dateJobParameterJob", jobRepository)
                .start(dateJobParameterStep)
                .build();
    }

    @Bean
    public Step dateJobParameterStep(JobRepository jobRepository, PlatformTransactionManager transactionManager, Tasklet dateJobParameterTasklet) {
        return new StepBuilder("dateJobParameterStep", jobRepository)
                .tasklet(dateJobParameterTasklet, transactionManager)
                .build();
    }

    //파라미터의 타입만 적절히 전달하면 DefaultJobParametersConverter가 알아서 변환
    @Bean
    @StepScope
    public Tasklet dateJobParameterTasklet(@Value("#{jobParameters['executionDate']}") LocalDate executionDate, @Value("#{jobParameters['startTime']}") LocalDateTime startTime) {
        return (contribution, chunkContext) -> {
            log.info("실행 일자: {}", executionDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")));
            log.info("시작 시간: {}", startTime.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초")));
            return RepeatStatus.FINISHED;
        };
    }
}
