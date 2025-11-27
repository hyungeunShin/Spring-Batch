package com.batch.chapter02.context;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
public class ExecutionContextBatchConfig {
    /*
    ExecutionContext
        Spring Batch는 JobExecution과 StepExecution을 사용해 시작 시간, 종료 시간, 실행 상태 같은 메타데이터를 관리한다.
        하지만 이런 기본적인 실행 정보만으로는 시스템을 완벽하게 제어하기 부족할 때가 있다.
        비즈니스 로직 처리 중에 발생하는 커스텀 데이터를 관리할 방법이 필요한데 이때 사용하는 것이 바로 ExecutionContext라는 데이터 컨테이너다.

        ExecutionContext를 활용하면 커스텀 컬렉션의 마지막 처리 인덱스나 집계 중간 결과물 같은 데이터를 저장할 수 있다.
        이는 Job이 중단된 후 재시작할 때 특히 유용하다.
        Spring Batch가 재시작 시 ExecutionContext의 데이터를 자동으로 복원하므로, 중단된 지점부터 처리를 이어갈 수 있기 때문이다.

    jobExecutionContext와 stepExecutionContext는 각각 다른 범위를 가진다.
    Job의 ExecutionContext는 Job에 속한 모든 컴포넌트에서 @Value("#{jobExecutionContext['key']}")로 접근할 수 있지만 Step의 ExecutionContext는 오직 해당 Step에 속한 컴포넌트에서만 접근할 수 있다.

    - Step의 ExecutionContext에 저장된 데이터는 @Value("#{jobExecutionContext['key']}")로 접근할 수 없다. 즉, Step 수준의 데이터를 Job 수준에서 가져올 수 없다.
    - 한 Step의 ExecutionContext는 다른 Step에서 접근할 수 없다. 예를 들어 StepA의 ExecutionContext에 저장된 데이터를 StepB에서 @Value("#{stepExecutionContext['key']}")로 가져올 수 없다.
    */

    @Bean
    public Job exampleJob(JobRepository jobRepository, Step stepA, Step stepB) {
        return new JobBuilder("contextJob", jobRepository)
                .start(stepA)
                .next(stepB)
                .build();
    }

    @Bean
    public Step stepA(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("stepA", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    chunkContext.getStepContext()
                            .getStepExecution()
                            .getExecutionContext()
                            .put("a", 123);

                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step stepB(JobRepository jobRepository, PlatformTransactionManager transactionManager, Tasklet taskletB) {
        return new StepBuilder("stepB", jobRepository)
                .tasklet(taskletB, transactionManager)
                .build();
    }

    @Bean
    @StepScope
    public Tasklet taskletB(@Value("#{stepExecutionContext['a']}") Integer a) {
        return (contribution, chunkContext) -> {
            log.info("a: {}", a);
            return RepeatStatus.FINISHED;
        };
    }
}
