package com.batch.chapter02.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
public class PromotionListenerBatchConfig {
    /*
    AdvancedListenerBatchConfig에서 Job 수준 ExecutionContext에 저장된 데이터는 Job 내 모든 Step에서 접근할 수 있었다.
    하지만 Step 수준 ExecutionContext에 저장된 데이터는 해당 Step에서만 접근 가능하므로 다른 Step과 공유는 불가능하다.
    그래서 한 Step의 ExecutionContext에 존재하는 데이터를 다음 Step에게 전달하려면 Step의 ExecutionContext의 값을 가져와 이를 Job 수준 ExecutionContext에 직접 설정해주어야 한다.

    StepExecution stepExecution = contribution.getStepExecution();
    ExecutionContext stepExecutionContext = stepExecution.getExecutionContext();
    Integer a = (Integer) stepExecutionContext.get("a");

    JobExecution jobExecution = stepExecution.getJobExecution();
    jobExecution.getExecutionContext().put("a", a);

    Spring Batch는 이런 반복적인 불편한 과정을 알아서 처리해줄 수 있도록 ExecutionContextPromotionListener라는 StepExecutionListener 구현체를 제공한다.
    ExecutionContextPromotionListener를 사용하면 위와 같이 번거로운 데이터 설정 작업 없이도 손쉽게 스텝 간 데이터 공유가 가능하다.

    ExecutionContextPromotionListener
        ExecutionContextPromotionListener는 Step 수준 ExecutionContext의 데이터를 Job 수준 ExecutionContext로 등록시켜주는 StepExecutionListener의 구현체다.
        Spring Batch에서는 Step 수준의 ExecutionContext 데이터를 Job 수준의 ExecutionContext로 옮기는 과정을 승격(Promote)이라 부른다.
        그래서 이 리스너의 이름도 ExecutionContextPromotion + Listener인 것이다.
        이 리스너는 StepExecutionListener의 afterStep() 메서드를 오버라이드하여 승격 작업을 수행한다.

    각 Step은 가능한 한 독립적으로 설계하여 재사용성과 유지보수성을 높이는 것이 좋다.
    불가피한 경우가 아니라면 Step 간 데이터 의존성은 최소화하는 것이 좋다.
    Step 간 데이터 공유가 늘어날수록 복잡도가 증가한다.
    */

    @Bean
    public Job promotionListenerJob(JobRepository jobRepository, Step promotionListenerStep1, Step promotionListenerStep2) {
        return new JobBuilder("promotionListenerJob", jobRepository)
                .start(promotionListenerStep1)
                .next(promotionListenerStep2)
                .build();
    }

    @Bean
    public Step promotionListenerStep1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("promotionListenerStep1", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    ExecutionContext stepContext = contribution.getStepExecution().getExecutionContext();
                    stepContext.put("result", "aaaa");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .listener(executionContextPromotionListener())
                .build();
    }

    @Bean
    public Step promotionListenerStep2(JobRepository jobRepository, PlatformTransactionManager transactionManager, Tasklet promotionListenerTasklet) {
        return new StepBuilder("promotionListenerStep2", jobRepository)
                .tasklet(promotionListenerTasklet, transactionManager)
                .build();
    }

    @Bean
    @StepScope
    public Tasklet promotionListenerTasklet(@Value("#{jobExecutionContext['result']}") String result) {
        return (contribution, chunkContext) -> {
            log.info("promotionListenerTasklet.result: {}", result);
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public ExecutionContextPromotionListener executionContextPromotionListener() {
        ExecutionContextPromotionListener listener = new ExecutionContextPromotionListener();
        //Step 수준의 ExecutionContext에서 Job 수준으로 승격할 데이터의 키 값을 지정
        listener.setKeys(new String[] {"result"});
        return listener;
    }
}
