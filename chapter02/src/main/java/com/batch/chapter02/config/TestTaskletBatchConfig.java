package com.batch.chapter02.config;

import com.batch.chapter02.tasklet.TestTasklet;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class TestTaskletBatchConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job testTaskletJob() {
        return new JobBuilder("testTaskletJob", jobRepository)
                .start(testTaskletStep())
                .build();
    }

    /*
    일반적으로 Tasklet에서 DB 트랜잭션 관리가 필요한 경우가 많지는 않다.
    모든 Tasklet이 데이터베이스 작업을 포함하는 것은 아니기 때문이다.
    예를 들어 파일을 정리하거나 외부 API를 호출하거나 단순한 알림을 보내는 작업이라면 DB 트랜잭션을 고려할 필요가 없다.

    이런 경우에는 실제 DB 커넥션을 관리하는 일반적인 PlatformTransactionManager 구현체를 사용하는 대신 ResourcelessTransactionManager 옵션을 고려한다.
    */
    @Bean
    public Step testTaskletStep() {
        return new StepBuilder("testTaskletStep", jobRepository)
                .tasklet(testTasklet(), transactionManager)
                .build();
    }

    @Bean
    public Tasklet testTasklet() {
        return new TestTasklet();
    }
}
