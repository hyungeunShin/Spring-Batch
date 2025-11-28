package com.batch.chapter02.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
public class ExceptionListenerBatchConfig {
    /*
    JobExecutionListener의 beforeJob()과 StepExecutionListener의 beforeStep()에서 예외가 발생하면 Job과 Step이 실패한 것으로 판단된다.
    하지만 모든 예외가 Step을 중단시켜야 할 만큼 치명적인 것은 아니다. 이런 경우는 직접 예외를 잡아서 무시하고 진행하는 것이 현명하다.

    반면, JobExecutionListener.afterJob()과 StepExecutionListener.afterStep()에서 발생한 예외는 무시된다.
    즉, 예외가 발생해도 Job과 Step의 실행 결과에 영향을 미치진 않는다.
    */

    @Bean
    public Job exceptionListenerJob(JobRepository jobRepository, Step exceptionListenerStep1, Step exceptionListenerStep2) {
        return new JobBuilder("exceptionListenerJob", jobRepository)
                .listener(exceptionJobListener())
                .start(exceptionListenerStep1)
                .next(exceptionListenerStep2)
                .build();
    }

    @Bean
    public Step exceptionListenerStep1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("exceptionListenerStep1", jobRepository)
                .tasklet((contribution, chunkContext) -> RepeatStatus.FINISHED, transactionManager)
                .listener(exceptionStepListener())
                .build();
    }

    @Bean
    public Step exceptionListenerStep2(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("exceptionListenerStep2", jobRepository)
                .tasklet((contribution, chunkContext) -> RepeatStatus.FINISHED, transactionManager)
                .listener(exceptionStepListener())
                .build();
    }

    @Bean
    public JobExecutionListener exceptionJobListener() {
        return new JobExecutionListener() {
            @Override
            public void beforeJob(JobExecution jobExecution) {
                log.info("exceptionJobListener.beforeJob");
                //throw new NullPointerException("exceptionJobListener.beforeJob exception");
            }

            @Override
            public void afterJob(JobExecution jobExecution) {
                throw new NullPointerException("exceptionJobListener.afterJob");
            }
        };
    }

    @Bean
    public StepExecutionListener exceptionStepListener() {
        return new StepExecutionListener() {
            @Override
            public void beforeStep(StepExecution stepExecution) {
                log.info("exceptionStepListener.beforeStep");
                //throw new NullPointerException("exceptionStepListener.beforeStep exception");
            }

            @Override
            public ExitStatus afterStep(StepExecution stepExecution) {
                throw new NullPointerException("exceptionStepListener.afterStep");
            }
        };
    }
}
