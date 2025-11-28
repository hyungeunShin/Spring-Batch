package com.batch.chapter03.flatitemreader;

import lombok.*;
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
import org.springframework.batch.item.file.transform.RegexLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RegexLogBatchConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job regexLogJob(Step regexLogStep) {
        return new JobBuilder("regexLogJob", jobRepository)
                .start(regexLogStep)
                .build();
    }

    @Bean
    public Step regexLogStep(FlatFileItemReader<LogEntry> regexLogItemReader, ItemWriter<LogEntry> regexLogItemWriter) {
        return new StepBuilder("regexLogStep", jobRepository)
                .<LogEntry, LogEntry>chunk(10, transactionManager)
                .reader(regexLogItemReader)
                .writer(regexLogItemWriter)
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<LogEntry> regexLogItemReader(@Value("#{jobParameters['filePath']}") String filePath) {
        /*
        \\[\\\\w+\\]: 대괄호 안의 로그 레벨(예: WARNING, ERROR)을 패턴으로 매칭한다. 이 부분은 분석 대상에서 제외된다.
        \\[Thread-(\\\\d+)\\]: 스레드 번호에 해당하는 두 번째 대괄호 안에서 Thread- 뒤에 나오는 숫자가 첫 번째 그룹으로 캡처된다.
        \\[CPU: \\\\d+%\\]: CPU 사용량을 나타내는 부분이다. 이건 로그 메시지 파싱엔 필요 없으니 건너뛴다.
        (.+): 마지막으로 로그 메시지를 전부 가져오는 부분이다. 이게 두 번째 그룹으로 캡처된다.
        */
        RegexLineTokenizer tokenizer = new RegexLineTokenizer();
        tokenizer.setRegex("\\[\\w+\\]\\[Thread-(\\d+)\\]\\[CPU: \\d+%\\] (.+)");

        /*
        fieldSet.readString(0): FieldSet의 첫 번째 필드(캡처된 Thread 번호)를 읽어서 LogEntry 객체의 threadNum 필드에 매핑
        fieldSet.readString(1): FieldSet의 두 번째 필드(캡처된 로그 메시지)를 읽어서 LogEntry 객체의 message 필드에 매핑

        targetType()을 지정한 경우 fieldSetMapper()에 설정한 내용이 무시된다.
        */
        return new FlatFileItemReaderBuilder<LogEntry>()
                .name("logItemReader")
                .resource(new FileSystemResource(filePath))
                .lineTokenizer(tokenizer)
                .fieldSetMapper(fieldSet -> new LogEntry(fieldSet.readString(0), fieldSet.readString(1)))
                .build();
    }

    @Bean
    public ItemWriter<LogEntry> regexLogItemWriter() {
        return chunk -> chunk.forEach(item -> log.info("THD-{}: {}", item.getThreadNum(), item.getMessage()));
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LogEntry {
        private String threadNum;
        private String message;
    }
}
