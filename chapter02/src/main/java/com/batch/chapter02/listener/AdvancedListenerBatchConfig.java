package com.batch.chapter02.listener;

import com.batch.chapter02.listener.impl.AdvancedJobExecutionListenerImpl;
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

import java.util.Map;
import java.util.Random;

@Slf4j
@Configuration
public class AdvancedListenerBatchConfig {
    /*
    왜 JobParameters가 아닌 ExecutionContext를 사용할까?
        한 번 생성된 JobParameters는 변경할 수 없기 때문이다.
        Spring Batch의 핵심 철학 중 하나는 배치 작업의 재현 가능성(Repeatability)과 일관성(Consistency)을 보장하는 것이다.
        이를 위해 JobParameters는 불변(immutable)하게 설계되었다.
        따라서 Job 실행 중에 동적으로 생성되거나 변경되어야 하는 데이터는 ExecutionContext를 통해 관리하는 것이 좋다.

        재현 가능성: 동일한 JobParameters로 실행한 Job은 항상 동일한 결과를 생성해야 한다. 실행 중간에 JobParameters가 변경되면 이를 보장할 수 없다.
        추적 가능성: 배치 작업의 실행 기록(JobInstance, JobExecution)과 JobParameters는 메타데이터 저장소에 저장된다. JobParameters가 변경 가능하다면 기록과 실제 작업의 불일치가 발생할 수 있다.
    */

    //Job 수준 ExecutionContext에 저장된 데이터는 해당 Job이 실행하는 모든 Step에서 접근이 가능
    @Bean
    public Job advancedListenerJob(JobRepository jobRepository, Step advancedListenerStep1, Step advancedListenerStep2) {
        return new JobBuilder("advancedListenerJob", jobRepository)
                .listener(new AdvancedJobExecutionListenerImpl())
                .start(advancedListenerStep1)
                .next(advancedListenerStep2)
                .build();
    }

    @Bean
    public Step advancedListenerStep1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("advancedListenerStep1", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    Map<String, Object> resultMap = (Map<String, Object>) chunkContext.getStepContext().getJobExecutionContext().get("resultMap");
                    log.info("advancedListenerStep1.resultMap: {}", resultMap);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step advancedListenerStep2(JobRepository jobRepository, PlatformTransactionManager transactionManager, Tasklet advancedListenerTasklet) {
        return new StepBuilder("advancedListenerStep2", jobRepository)
                .tasklet(advancedListenerTasklet, transactionManager)
                .build();
    }

    @Bean
    @StepScope
    public Tasklet advancedListenerTasklet(@Value("#{jobExecutionContext['resultMap']}") Map<String, Object> resultMap) {
        return (contribution, chunkContext) -> {
            log.info("advancedListenerTasklet.resultMap: {}", resultMap);

            //resultString을 Job 수준 ExecutionContext에 저장
            if(new Random().nextBoolean()) {
                log.info("advancedListenerTasklet -> True");
                contribution.getStepExecution().getJobExecution().getExecutionContext().put("resultString", "A");
            } else {
                log.info("advancedListenerTasklet -> False");
                contribution.getStepExecution().getJobExecution().getExecutionContext().put("resultString", "B");
            }

            return RepeatStatus.FINISHED;
        };
    }
}
