package com.batch.chapter03.flatitemreader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RecordBatchConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job recordJob(Step recordStep) {
        return new JobBuilder("recordJob", jobRepository)
                .start(recordStep)
                .build();
    }

    @Bean
    public Step recordStep(FlatFileItemReader<System> recordItemReader, ItemWriter<System> recordItemWriter) {
        return new StepBuilder("recordStep", jobRepository)
                .<System, System>chunk(10, transactionManager)
                .reader(recordItemReader)
                .writer(recordItemWriter)
                .build();
    }

    /*
    targetType() 메서드에 record 를 전달하면 Spring Batch는 내부적으로 BeanWrapperFieldSetMapper 대신 RecordFieldSetMapper를 사용한다.
    BeanWrapperFieldSetMapper가 객체의 setter 메서드를 사용해 데이터를 바인딩하는 것과 달리, RecordFieldSetMapper는 record의 canonical constructor(모든 필드를 매개변수로 받는 생성자)를 사용해 데이터를 매핑한다.
    */
    @Bean
    @StepScope
    public FlatFileItemReader<System> recordItemReader(@Value("#{jobParameters['filePath']}") String filePath) {
        return new FlatFileItemReaderBuilder<System>()
                .name("recordItemReader")
                .resource(new FileSystemResource(filePath))
                .delimited()
                .names("command", "cpu", "status")
                .targetType(System.class)
                .linesToSkip(1)
                .build();
    }

    @Bean
    public ItemWriter<System> recordItemWriter() {
        return chunk -> chunk.forEach(item -> log.info("{}", item));
    }

    public record System(String command, int cpu, String status) {

    }
}
