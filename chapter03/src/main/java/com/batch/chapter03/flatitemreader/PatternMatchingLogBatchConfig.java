package com.batch.chapter03.flatitemreader;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
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
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.mapping.PatternMatchingCompositeLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class PatternMatchingLogBatchConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job patternMatchingLogJob(Step patternMatchingLogStep) {
        return new JobBuilder("patternMatchingLogJob", jobRepository)
                .start(patternMatchingLogStep)
                .build();
    }

    @Bean
    public Step patternMatchingLogStep(FlatFileItemReader<SystemLog> patternMatchingLogItemReader, ItemWriter<SystemLog> patternMatchingLogItemWriter) {
        return new StepBuilder("patternMatchingLogStep", jobRepository)
                .<SystemLog, SystemLog>chunk(10, transactionManager)
                .reader(patternMatchingLogItemReader)
                .writer(patternMatchingLogItemWriter)
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<SystemLog> patternMatchingLogItemReader(@Value("#{jobParameters['filePath']}") String filePath) {
        return new FlatFileItemReaderBuilder<SystemLog>()
                .name("logItemReader")
                .resource(new FileSystemResource(filePath))
                .lineMapper(patternMatchingCompositeLineMapper())
                .build();
    }

    public PatternMatchingCompositeLineMapper<SystemLog> patternMatchingCompositeLineMapper() {
        //PatternMatchingCompositeLineMapper는 각 파일 라인의 유형에 맞는 LineTokenizer와 이를 적절한 객체로 매핑할 FieldSetMapper 구현체를 필요로 한다.
        PatternMatchingCompositeLineMapper<SystemLog> lineMapper = new PatternMatchingCompositeLineMapper<>();

        Map<String, LineTokenizer> tokenizers = new HashMap<>();
        tokenizers.put("ERROR*", errorLineTokenizer());
        tokenizers.put("ABORT*", abortLineTokenizer());
        tokenizers.put("COLLECT*", collectLineTokenizer());
        lineMapper.setTokenizers(tokenizers);

        Map<String, FieldSetMapper<SystemLog>> mappers = new HashMap<>();
        mappers.put("ERROR*", new ErrorFieldSetMapper());
        mappers.put("ABORT*", new AbortFieldSetMapper());
        mappers.put("COLLECT*", new CollectFieldSetMapper());
        lineMapper.setFieldSetMappers(mappers);

        return lineMapper;
    }

    public DelimitedLineTokenizer errorLineTokenizer() {
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer(",");
        tokenizer.setNames("type", "application", "errorType", "timestamp", "message", "resourceUsage", "logPath");
        return tokenizer;
    }

    public DelimitedLineTokenizer abortLineTokenizer() {
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer(",");
        tokenizer.setNames("type", "application", "errorType", "timestamp", "message", "exitCode", "processPath", "status");
        return tokenizer;
    }

    public DelimitedLineTokenizer collectLineTokenizer() {
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer(",");
        tokenizer.setNames("type", "dumpType", "processId", "timestamp", "dumpPath");
        return tokenizer;
    }

    @Bean
    public ItemWriter<SystemLog> patternMatchingLogItemWriter() {
        return chunk -> chunk.forEach(item -> log.info("{}", item));
    }

    @Getter
    @Setter
    @ToString
    @SuperBuilder
    public static abstract class SystemLog {
        private String type;
        private String timestamp;
    }

    @Getter
    @Setter
    @ToString(callSuper = true)
    @SuperBuilder
    public static class ErrorLog extends SystemLog {
        private String application;
        private String errorType;
        private String message;
        private String resourceUsage;
        private String logPath;
    }

    @Getter
    @Setter
    @ToString(callSuper = true)
    @SuperBuilder
    public static class AbortLog extends SystemLog {
        private String application;
        private String errorType;
        private String message;
        private String exitCode;
        private String processPath;
        private String status;
    }

    @Getter
    @Setter
    @ToString(callSuper = true)
    @SuperBuilder
    public static class CollectLog extends SystemLog {
        private String dumpType;
        private String processId;
        private String dumpPath;
    }

    public static class ErrorFieldSetMapper implements FieldSetMapper<SystemLog> {
        @Override
        public SystemLog mapFieldSet(FieldSet fieldSet) {
            return ErrorLog.builder()
                    .type(fieldSet.readString("type"))
                    .timestamp(fieldSet.readString("timestamp"))
                    .application(fieldSet.readString("application"))
                    .errorType(fieldSet.readString("errorType"))
                    .message(fieldSet.readString("message"))
                    .resourceUsage(fieldSet.readString("resourceUsage"))
                    .logPath(fieldSet.readString("logPath"))
                    .build();
        }
    }

    public static class AbortFieldSetMapper implements FieldSetMapper<SystemLog> {
        @Override
        public SystemLog mapFieldSet(FieldSet fieldSet) {
            return AbortLog.builder()
                    .type(fieldSet.readString("type"))
                    .timestamp(fieldSet.readString("timestamp"))
                    .application(fieldSet.readString("application"))
                    .errorType(fieldSet.readString("errorType"))
                    .message(fieldSet.readString("message"))
                    .exitCode(fieldSet.readString("exitCode"))
                    .processPath(fieldSet.readString("processPath"))
                    .status(fieldSet.readString("status"))
                    .build();
        }
    }

    public static class CollectFieldSetMapper implements FieldSetMapper<SystemLog> {
        @Override
        public SystemLog mapFieldSet(FieldSet fieldSet) {
            return CollectLog.builder()
                    .type(fieldSet.readString("type"))
                    .timestamp(fieldSet.readString("timestamp"))
                    .dumpType(fieldSet.readString("dumpType"))
                    .processId(fieldSet.readString("processId"))
                    .dumpPath(fieldSet.readString("dumpPath"))
                    .build();
        }
    }
}
