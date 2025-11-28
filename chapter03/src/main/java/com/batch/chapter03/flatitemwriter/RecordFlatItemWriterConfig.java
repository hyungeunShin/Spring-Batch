package com.batch.chapter03.flatitemwriter;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.RecordFieldExtractor;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class RecordFlatItemWriterConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job recordFlatItemWriterJob(Step recordFlatItemWriterStep) {
        return new JobBuilder("recordFlatItemWriterJob", jobRepository)
                .start(recordFlatItemWriterStep)
                .build();
    }

    @Bean
    public Step recordFlatItemWriterStep(FlatFileItemWriter<SystemFailure> recordFlatItemWriter) {
        return new StepBuilder("recordFlatItemWriterStep", jobRepository)
                .<SystemFailure, SystemFailure>chunk(10, transactionManager)
                .reader(recordListItemReader())
                .writer(recordFlatItemWriter)
                .build();
    }

    public ListItemReader<SystemFailure> recordListItemReader() {
        List<SystemFailure> systemFailures = List.of(
                new SystemFailure(
                        "ERR001",
                        "2025-01-01 08:01:02",
                        "CRITICAL",
                        1234,
                        "SYSTEM_CRASH"),
                new SystemFailure(
                        "ERR002",
                        "2025-01-02 09:02:03",
                        "FATAL",
                        1235,
                        "MEMORY_OVERFLOW"),
                new SystemFailure(
                        "ERR003",
                        "2025-01-03 10:03:04",
                        "FATAL",
                        1235,
                        "MEMORY_OVERFLOW")
        );

        return new ListItemReader<>(systemFailures);
    }

    @Bean
    @StepScope
    public FlatFileItemWriter<SystemFailure> recordFlatItemWriter(@Value("#{jobParameters['filePath']}") String filePath) {
        return new FlatFileItemWriterBuilder<SystemFailure>()
                .name("recordFlatItemWriter")
                .resource(new FileSystemResource(filePath + "/9.csv"))
                .delimited()
                .delimiter(",")
                //.sourceType(SystemFailure.class)
                //.names("errorId", "errorDateTime", "severity", "processId", "errorMessage")
                //.headerCallback(writer -> writer.write("에러ID,발생시각,심각도,프로세스ID,에러메시지"))
                //커스텀 FieldExtractor
                .fieldExtractor(fieldExtractor())
                .headerCallback(writer -> writer.write("에러ID,발생시각,에러메시지"))
                .build();
    }

    public RecordFieldExtractor<SystemFailure> fieldExtractor() {
        RecordFieldExtractor<SystemFailure> fieldExtractor = new RecordFieldExtractor<>(SystemFailure.class);
        fieldExtractor.setNames("errorId", "errorDateTime", "errorMessage");
        return fieldExtractor;
    }

    public record SystemFailure(String errorId, String errorDateTime, String severity, Integer processId, String errorMessage) {

    }
}
