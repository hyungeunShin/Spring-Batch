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
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class FixedLengthFlatItemReaderConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job fixedLengthFlatItemReaderJob(Step fixedLengthFlatItemReaderStep) {
        return new JobBuilder("fixedLengthFlatItemReaderJob", jobRepository)
                .start(fixedLengthFlatItemReaderStep)
                .build();
    }

    @Bean
    public Step fixedLengthFlatItemReaderStep(FlatFileItemReader<SystemFailure> fixedLengthFlatItemReader) {
        return new StepBuilder("fixedLengthFlatItemReaderStep", jobRepository)
                .<SystemFailure, SystemFailure>chunk(10, transactionManager)
                .reader(fixedLengthFlatItemReader)
                .writer(fixedLengthFlatItemWriter())
                .build();
    }

    /*
    fixedLength()
        FlatFileItemReader에게 읽어들일 파일이 고정 길이 형식임을 알리는 설정이다.
        이 메서드를 호출하면 DefaultLineMapper가 사용할 LineTokenizer 구현체로 FixedLengthTokenizer가 지정된다.
        FixedLengthTokenizer는 각 필드의 길이가 고정된 데이터를 토큰화하여 읽어들이는 역할을 한다.

    columns()
        고정 길이 파일을 읽기 위해 각 필드의 정확한 위치를 FixedLengthTokenizer에 전달한다.
        Range배열은 각 필드의 시작과 끝 위치를 정의한다.

        공백으로 길이를 맞췄는데 공백 처리는 걱정할 필요 없다.
        FieldSet의 기본 구현체인 DefaultFieldSet이 이미 내부적으로 trim()을 수행하기 때문이다.

    strict()
        strict 설정은 앞서 설명한 파일 존재 여부와 토큰 개수 불일치에 대한 처리 외에도 고정 길이 파일에서 추가적인 의미를 갖는다.
        strict 모드에서 FixedLengthTokenizer는 파일에서 읽은 라인의 길이를 엄격하게 검증한다.
        파일에서 읽은 라인의 길이가 Range에 지정된 최대 길이(마지막 필드의 끝 위치)와 다를 경우 예외를 발생시킨다.
        이는 고정 길이 파일의 데이터 정확성을 보장하는 핵심 메커니즘이다.
    */
    @Bean
    @StepScope
    public FlatFileItemReader<SystemFailure> fixedLengthFlatItemReader(@Value("#{jobParameters['filePath']}") String filePath) {
        return new FlatFileItemReaderBuilder<SystemFailure>()
                .name("fixedLengthSystemFailureItemReader")
                .resource(new FileSystemResource(filePath))
                .fixedLength()
                .columns(new Range[] {
                        new Range(1, 8),    //errorId: ERR001 + 공백 2칸
                        new Range(9, 29),   //errorDateTime: 날짜시간 + 공백 2칸
                        new Range(30, 39),  //severity: CRITICAL/FATAL + 패딩
                        new Range(40, 45),  //processId: 1234 + 공백 2칸
                        new Range(46, 66)   //errorMessage: 메시지 + \n
                })
                .names("errorId", "errorDateTime", "severity", "processId", "errorMessage")
                .targetType(SystemFailure.class)
                .customEditors(Map.of(LocalDateTime.class, dateTimeEditor()))
                .build();
    }

    private PropertyEditor dateTimeEditor() {
        return new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                setValue(LocalDateTime.parse(text, formatter));
            }
        };
    }

    public ItemWriter<SystemFailure> fixedLengthFlatItemWriter() {
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
