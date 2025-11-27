package com.batch.chapter02.tasklet;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DeleteOldFilesBatchConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final JdbcTemplate jdbcTemplate;

    @Bean
    public Job deleteOldFilesJob() {
        return new JobBuilder("deleteOldFilesJob", jobRepository)
                .start(deleteOldFilesStep())
                .build();
    }

    @Bean
    public Step deleteOldFilesStep() {
        return new StepBuilder("deleteOldFilesStep", jobRepository)
                .tasklet(deleteOldFilesTasklet(), transactionManager)
                .build();
    }

    @Bean
    public Tasklet deleteOldFilesTasklet() {
        return new DeleteOldFilesTasklet("C:/테스트/", 30);
    }

    @Bean
    public Job deleteOldRecordsJob() {
        return new JobBuilder("deleteOldRecordsJob", jobRepository)
                .start(deleteOldRecordsStep())
                .build();
    }

    @Bean
    public Step deleteOldRecordsStep() {
        return new StepBuilder("deleteOldRecordsStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    int deleted = jdbcTemplate.update("DELETE FROM LOGS WHERE CREATED < CURRENT_TIMESTAMP - INTERVAL '7' DAY");
                    log.info("삭제된 행 개수: {}", deleted);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
}
