package com.batch.chapter03.flatitemwriter;

import lombok.*;
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
public class DelimiterFlatItemWriterConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job delimiterFlatItemWriterJob(Step delimiterFlatItemWriterStep) {
        return new JobBuilder("delimiterFlatItemWriterJob", jobRepository)
                .start(delimiterFlatItemWriterStep)
                .build();
    }

    @Bean
    public Step delimiterFlatItemWriterStep(FlatFileItemWriter<SystemFailure> delimiterFlatItemWriter) {
        return new StepBuilder("delimiterFlatItemWriterStep", jobRepository)
                .<SystemFailure, SystemFailure>chunk(10, transactionManager)
                .reader(delimiterListItemReader())
                .writer(delimiterFlatItemWriter)
                .build();
    }

    public ListItemReader<SystemFailure> delimiterListItemReader() {
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
    delimiter()
        DelimitedLineAggregator가 필드들을 이어붙일 때 사용할 구분자를 delimiter()로 지정한다.
        기본값이 쉼표(,)지만 명시적으로 지정하는 것을 권장한다.

        FlatFileItemWriterBuilder는 두 가지 FieldExtractor 구성 방식을 제공한다.
        직접 전달 방식: FlatFileItemWriterBuilder의 fieldExtractor() 메서드를 사용해 FieldExtractor 객체를 직접 전달하는 방식이다.
                      보통 커스텀 FieldExtractor를 사용하고자 할 때 이 방법을 선택한다.

        자동 구성 방식:
            도메인 객체의 타입에 따라 BeanWrapperFieldExtractor 또는 RecordFieldExtractor가 자동으로 선택된다.
            FlatFileItemWriterBuilder의 sourceType() 메서드를 사용해 도메인 객체의 타입을 전달하면 자동으로 객체 타입에 맞는 적절한 FieldExtractor가 사용된다.

    sourceType()
        SystemFailure는 일반 자바빈 객체이므로 DelimitedLineAggregator에는 BeanWrapperFieldExtractor가 구성된다.
        
    names()
        문자열로 변환할 객체의 필드 이름을 지정한다.
        여기에 지정된 이름을 활용해 BeanWrapperFieldExtractor가 객체의 getter를 호출한다.

    headerCallback()
        파일에 헤더를 추가할 수 있도록 콜백을 설정한다.
        지정한 FlatFileHeaderCallback은 FlatFileItemWriter가 초기화될 때(open() 시점) 호출되어 도메인 객체가 파일에 쓰여지기 전에 헤더 라인을 작성한다.
        FlatFileItemWriterBuilder는 headerCallback() 외에도 footerCallback() 메서드를 제공한다.
        이 메서드를 설정하면 FlatFileItemWriter가 작업을 마무리할 때(close() 시점) 지정한 내용이 파일의 마지막에 추가된다.
    */
    @Bean
    @StepScope
    public FlatFileItemWriter<SystemFailure> delimiterFlatItemWriter(@Value("#{jobParameters['filePath']}") String filePath) {
        return new FlatFileItemWriterBuilder<SystemFailure>()
                .name("delimiterFlatItemWriter")
                .resource(new FileSystemResource(filePath + "/8.csv"))
                .delimited()
                .delimiter(",")
                .sourceType(SystemFailure.class)
                .names("errorId", "errorDateTime", "severity", "processId", "errorMessage")
                .headerCallback(writer -> writer.write("에러ID,발생시각,심각도,프로세스ID,에러메시지"))
                .build();
    }

    @Getter
    @Setter
    @ToString
    @AllArgsConstructor
    public static class SystemFailure {
        private String errorId;
        private String errorDateTime;
        private String severity;
        private Integer processId;
        private String errorMessage;
    }
}
