package com.batch.chapter03.flatitemreader;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.MultiResourceItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MultiResourceFlatItemReaderConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job multiResourceFlatItemReaderJob(Step multiResourceFlatItemReaderStep) {
        return new JobBuilder("multiResourceFlatItemReaderJob", jobRepository)
                .start(multiResourceFlatItemReaderStep)
                .build();
    }

    @Bean
    public Step multiResourceFlatItemReaderStep(MultiResourceItemReader<SystemFailure> multiResourceFlatItemReader) {
        return new StepBuilder("multiResourceFlatItemReaderStep", jobRepository)
                .<SystemFailure, SystemFailure>chunk(10, transactionManager)
                .reader(multiResourceFlatItemReader)
                .writer(multiResourceFlatItemWriter())
                .build();
    }

    @Bean
    @StepScope
    public MultiResourceItemReader<SystemFailure> multiResourceFlatItemReader(@Value("#{jobParameters['filePath']}") String filePath) {
        log.info("{}", filePath + "/6.csv");
        log.info("{}", filePath + "/7.csv");
        return new MultiResourceItemReaderBuilder<SystemFailure>()
                .name("multiResourceItemReader")
                .resources(new Resource[] {
                        new FileSystemResource(filePath + "/6.csv"),
                        new FileSystemResource(filePath + "/7.csv")
                })
                .delegate(delegateItemReader())
                .build();
    }

    public FlatFileItemReader<SystemFailure> delegateItemReader() {
        return new FlatFileItemReaderBuilder<SystemFailure>()
                .name("delegateItemReader")
                .delimited()
                .delimiter(",")
                .names("errorId", "errorDateTime", "severity", "processId", "errorMessage")
                .targetType(SystemFailure.class)
                .linesToSkip(1)
                .build();
    }

    public ItemWriter<SystemFailure> multiResourceFlatItemWriter() {
        return chunk -> chunk.forEach(item -> log.info("{}", item));
    }

    @Getter
    @Setter
    @ToString
    public static class SystemFailure {
        private String errorId;
        private String errorDateTime;
        private String severity;
        private Integer processId;
        private String errorMessage;
    }
}
