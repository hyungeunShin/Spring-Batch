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
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class CustomFormatFlatItemWriterConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job customFormatFlatItemWriterJob(Step customFormatFlatItemWriterStep) {
        return new JobBuilder("customFormatFlatItemWriterJob", jobRepository)
                .start(customFormatFlatItemWriterStep)
                .build();
    }

    @Bean
    public Step customFormatFlatItemWriterStep(FlatFileItemWriter<SystemFailure> customFormatFlatItemWriter) {
        return new StepBuilder("customFormatFlatItemWriterStep", jobRepository)
                .<SystemFailure, SystemFailure>chunk(10, transactionManager)
                .reader(customFormatListItemReader())
                .writer(customFormatFlatItemWriter)
                .build();
    }

    public ListItemReader<SystemFailure> customFormatListItemReader() {
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

    /*
    formatted()
        FlatFileItemWriter가 사용할 LineAggregator 구현체로 FormatterLineAggregator를 지정한다.
        FormatterLineAggregator는 객체의 각 필드를 지정된 포맷 문자열에 맞춰 하나의 문자열로 변환한다.

    format()
        FormatterLineAggregator가 사용할 포맷 문자열을 지정한다.
        String.format()에서 사용하는 것과 동일한 포맷 문자열을 사용할 수 있다.

    shouldDeleteIfExists(): 기존 파일의 처리 여부(기본값은 true)
    append(): 기존 파일에 데이터 덧붙이기 여부(기본값은 false)
    shouldDeleteIfEmpty(): 빈 결과 파일 처리 여부(기본값은 false)
    */
    @Bean
    @StepScope
    public FlatFileItemWriter<SystemFailure> customFormatFlatItemWriter(@Value("#{jobParameters['filePath']}") String filePath) {
        return new FlatFileItemWriterBuilder<SystemFailure>()
                .name("customFormatFlatItemWriter")
                .resource(new FileSystemResource(filePath + "/10.csv"))
                .append(true)
                .formatted()
                .format("에러ID: %s | 발생시각: %s | 심각도: %s | 프로세스ID: %d | 에러메시지: %s")
                .sourceType(SystemFailure.class)
                .names("errorId", "errorDateTime", "severity", "processId", "errorMessage")
                .headerCallback(writer -> writer.write("===시작==="))
                .footerCallback(writer -> writer.write("===완료===\n"))
                .build();
    }

    public record SystemFailure(String errorId, String errorDateTime, String severity, Integer processId, String errorMessage) {

    }
}
