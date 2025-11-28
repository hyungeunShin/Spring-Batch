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
public class RecordFlatItemReaderConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job recordFlatItemReaderJob(Step recordFlatItemReaderStep) {
        return new JobBuilder("recordFlatItemReaderJob", jobRepository)
                .start(recordFlatItemReaderStep)
                .build();
    }

    @Bean
    public Step recordFlatItemReaderStep(FlatFileItemReader<System> recordFlatItemReader) {
        return new StepBuilder("recordFlatItemReaderStep", jobRepository)
                .<System, System>chunk(10, transactionManager)
                .reader(recordFlatItemReader)
                .writer(recordFlatItemWriter())
                .build();
    }

    /*
    targetType() 메서드에 record 를 전달하면 Spring Batch는 내부적으로 BeanWrapperFieldSetMapper 대신 RecordFieldSetMapper를 사용한다.
    BeanWrapperFieldSetMapper가 객체의 setter 메서드를 사용해 데이터를 바인딩하는 것과 달리, RecordFieldSetMapper는 record의 canonical constructor(모든 필드를 매개변수로 받는 생성자)를 사용해 데이터를 매핑한다.
    */
    @Bean
    @StepScope
    public FlatFileItemReader<System> recordFlatItemReader(@Value("#{jobParameters['filePath']}") String filePath) {
        return new FlatFileItemReaderBuilder<System>()
                .name("recordItemReader")
                .resource(new FileSystemResource(filePath))
                .delimited()
                .names("command", "cpu", "status")
                .targetType(System.class)
                .linesToSkip(1)
                .build();
    }

    public ItemWriter<System> recordFlatItemWriter() {
        return chunk -> chunk.forEach(item -> log.info("{}", item));
    }

    public record System(String command, int cpu, String status) {

    }
}
