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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DelimiterFlatItemReaderConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job delimiterFlatItemReaderJob(Step delimiterFlatItemReaderStep) {
        return new JobBuilder("delimiterFlatItemReaderJob", jobRepository)
                .start(delimiterFlatItemReaderStep)
                .build();
    }

    @Bean
    public Step delimiterFlatItemReaderStep(FlatFileItemReader<SystemFailure> delimiterFlatItemReader) {
        return new StepBuilder("delimiterFlatItemReaderStep", jobRepository)
                .<SystemFailure, SystemFailure>chunk(10, transactionManager)
                .reader(delimiterFlatItemReader)
                .writer(delimiterFlatItemReader())
                .build();
    }

    /*
    name(): ItemReader의 식별자 지정
        FlatFileItemReader를 식별하기 위한 고유 이름을 부여한다.
        이 이름은 ItemReader가 현재 스텝 실행의 진행 상황(읽은 데이터의 위치 등)을 추적할 때 사용된다.

    resource(): 타겟 설정
        읽어 들일 Resource를 지정한다. 여기서는 FileSystemResource를 사용해 파일 시스템의 특정 파일을 지정했다.
        입력 파일의 경로(inputFile)는 잡 파라미터로부터 동적으로 전달받고 있다.

    delimited(): 파일 형식 지정
        FlatFileItemReader에게 읽어들일 파일이 구분자로 분리된 형식임을 알리는 설정으로 가장 핵심이 되는 설정이다.
        delimited()를 호출하면 DefaultLineMapper가 사용할 LineTokenizer 구현체로 DelimitedLineTokenizer가 지정된다.
        DelimitedLineTokenizer는 구분자로 구분된 데이터를 토큰화한다.

    delimiter(): 구분자 지정
        읽어들일 파일이 구분자로 분리된 형식임을 설정했으니 이제 어떤 문자를 구분자로 사용할지 지정할 차례다.
        여기서는 쉼표를 구분자로 지정했다.
        DelimitedLineTokenizer의 기본 구분자가 쉼표(,)이기 때문에 CSV 파일을 처리할 때는 이 메서드를 생략해도 된다.

    names(): 프로퍼티 매핑
        FieldSet의 names 필드에 사용할 객체의 프로퍼티 이름을 전달한다.
        여기서는 SystemFailure 객체의 프로퍼티명을 지정했다.
        이렇게 지정한 이름들은 파일에서 읽어들인 데이터의 각 토큰과 순서대로 1:1 매핑된다.

    targetType(): 매핑 대상 클래스 지정
        기본으로 사용되는 FieldSetMapper 구현체인 BeanWrapperFieldSetMapper에서 FieldSet을 객체로 매핑할 대상 도메인 클래스를 지정한다.
        여기서는 SystemFailure 클래스의 인스턴스로 변환될 것이다.

    linesToSkip(): 헤더 처리
        헤더 skip과 마찬가지로 파일 데이터를 읽을 때 주석 처리도 고려해야 한다.
        FlatFileItemReader는 특정 문자로 시작하는 라인을 주석으로 처리할 수 있다.
        이 설정은 FlatFileItemReaderBuilder의 comments(String... comments) 메서드로 지정할 수 있다.
        FlatFileItemReader는 기본으로 # 문자로 시작하는 라인을 주석으로 인식하기 때문에 별도 설정을 하지 않아도 #로 시작하는 모든 라인은 자동으로 무시된다

    strict(): 파일 검증 강도 설정
        파일과 데이터 검증의 강도를 설정하는 메서드로 기본값은 true이다. 이 경우 파일 누락 시 예외를 발생시켜 배치를 중단한다.
        false면 파일이 존재하지 않아도 경고만 남기고 진행한다. 이 경우 FlatFileItemReader의 read() 메서드에서 null 반환한다.
        또한 이 설정은 LineTokenizer의 검증 강도에도 영향을 미친다.
        LineTokenizer가 토큰화한 tokens의 길이가 names()에 전달된 객체 프로퍼티 이름(names)의 길이와 다를 경우, strict=true면 예외가 발생하고 strict=false면 토큰 수를 자동으로 보정한다.
    */
    @Bean
    @StepScope
    public FlatFileItemReader<SystemFailure> delimiterFlatItemReader(@Value("#{jobParameters['filePath']}") String filePath) {
        return new FlatFileItemReaderBuilder<SystemFailure>()
                .name("delimiterFlatItemReader")
                .resource(new FileSystemResource(filePath))
                .delimited()
                .delimiter(",")
                .names("errorId", "errorDateTime", "severity", "processId", "errorMessage")
                .targetType(SystemFailure.class)
                .linesToSkip(1)
                .build();
    }

    public ItemWriter<SystemFailure> delimiterFlatItemReader() {
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
