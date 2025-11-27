package com.batch.chapter02.scope;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JobScopeAndStepScope {
    /*
    Job과 Step의 Scope
        JobScope와 StepScope가 선언된 빈은 애플리케이션 구동 시점에는 우선 프록시 객체로만 존재한다.
        그 후 Job이나 Step이 실행된 후에 프록시 객체에 접근을 시도하면 그 때 실제 빈이 생성된다.
        각각의 스코프 빈은 Job과 Step의 실행(Execution) 시점에 생성되어 종료할 때 함께 소멸된다.
        런타임에 결정되는 JobParameters를 실행 시점에 정확하게 주입받을 수 있고, 동시에 여러 Job이 실행되더라도 각각 독립적인 빈을 사용하게 되어 동시성 문제도 해결할 수 있다.
        또한 Job이나 Step의 실행이 끝나면 해당 빈도 함께 제거되므로, 불필요하게 메모리를 점유하지 않아 리소스 관리 측면에서도 효율적이다.

    @JobScope
        @JobScope는 Job이 실행될 때 실제 빈이 생성되고, Job이 종료될 때 함께 제거되는 스코프다. 즉, JobExecution과 생명주기를 같이 한다.

        지연된 빈 생성: @JobScope가 적용된 빈은 애플리케이션 구동 시점에는 프록시 객체만 생성된다.

        Job 파라미터와의 연동: 지연된 빈 생성이 가능하다 보니 어플리케이션 실행 중에 전달되는 JobParameters를 Job 실행 시점에 생성되는 빈에 주입해줄 수 있다.

        예를 들어 REST API로 Job 시작 요청을 받으면 Job을 실행하는 애플리케이션이 있다.
        동시에 여러 요청이 같은 Job 정의를 서로 다른 파라미터로 실행한다고 가정해본다.
        각각의 요청에 따라 시작된 Job 실행은 서로 다른 JobExecution을 갖게 될 것이다.
        그러나 @JobScope가 없다면 각각의 Job을 실행하는 여러 스레드가 동일한 빈 인스턴스를 공유하게 된다.
        이로 인해 스레드들이 동일한 Tasklet 인스턴스의 상태를 동시에 접근하면서 동시성 이슈가 발생할 수 있다.
        @JobScope를 사용하면 JobExecution마다 별도의 빈이 생성되어 동시성 문제를 방지할 수 있다.

    @StepScope
        이 Tasklet 빈이 Step의 생명주기와 함께 한다는 것을 의미한다.
        즉, 각각의 Step 실행마다 새로운 빈이 생성되고, Step이 종료될 때 함께 제거된다.
        만약 동시에 여러 Step이 실행되면서 이 Tasklet을 사용한다고 해도 @StepScope가 있기 때문에 각 Step 실행마다 독립적인 Tasklet 인스턴스가 생성된다.
        따라서 어떠한 동시성 이슈도 발생하지 않는다.

    JobScope와 StepScope 사용 시 주의사항
        1. 프록시 대상의 타입이 클래스인 경우 반드시 상속 가능한 클래스여야 한다.
            이 스코프들은 CGLIB를 사용해 클래스 기반의 프록시를 생성한다. 당연히 프록시를 생성하려면 대상 클래스가 상속 가능해야 한다.
        2. Step 빈에는 @JobScope와 @StepScope를 사용하지 말라.
            Step에 @StepScope를 달아보면 오류가 난다.
            Spring Batch는 Step 실행 전 메타데이터 관리를 위해 Step 빈에 접근해야 한다.
            문제는 이 시점에는 아직 Step이 실행되지 않아 @StepScope가 활성화되지 않았다는 것이다.
            결국 스코프가 활성화되지 않은 상태에서 프록시에 접근하려 하니 시스템이 폭발하는 것이다.

            Spring Batch 5.2 이전의 공식 문서는 Step 빈에 @StepScope를 사용하지 말라고 경고했다.
            이로 인해 많은 개발자들 사이에 Step에는 @JobScope만 선언 가능하다는 오해가 퍼졌다.
            그러나 Step 빈에 @JobScope를 선언하는 것 또한 권장되지 않는다.
            Spring Batch 5.2부터는 공식 문서에서 "A `Step` bean should not be step-scoped or job-scoped."라고 안내하기 시작했다.

            그렇다면 Step에서 JobParameters는 어떻게 사용해야 할까?
            해답은 Tasklet에 @JobScope 또는 @StepScope를 선언해 파라미터를 받도록 하면 된다.
            ScopeTasklet.class를 참고
    */
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    //--spring.batch.job.name=scopeJob
    @Bean
    public Job scopeJob(Step scopeStep) {
        return new JobBuilder("scopeJob", jobRepository)
                .start(scopeStep)
                .build();
    }

    @Bean
    //@JobScope
    //@StepScope
    public Step scopeStep() {
        return new StepBuilder("scopeStep", jobRepository)
                .tasklet((contribution, chunkContext) -> RepeatStatus.FINISHED, transactionManager)
                .build();
    }

    //빈 주입 방식
    //--spring.batch.job.name=beanInjection
    @Bean
    public Job beanInjection(Step testStep) {
        return new JobBuilder("beanInjection", jobRepository)
                .start(testStep)
                .build();
    }

    /*
    메서드 직접 호출 방식 - Job 파라미터 자리에 null 전달
    가장 깔끔한 방법은 위처럼 빈 주입 방식으로 Step 빈 인스턴스를 참조하는 것이다.
    그러나 직접 메서드를 호출해야 할 경우도 있을 수 있다.
    그럴 땐 이 방법을 사용하면 된다.
    이 방식에서는 잡 파라미터 자리에 null을 전달한다.
    컴파일 시점에는 해당 메서드에 전달할 잡 파라미터 값을 알 수도 없고 전달할 방법도 없기 때문이다.
    우선 null을 전달하여 당장의 코드 레벨에서의 참조를 만족시키면 실제 값은 Job이 실행될 때 입력받은 JobParameters의 값으로 주입될 것이다.
    이것이 바로 Spring Batch의 지연 바인딩(late binding) 특성이다.
    */
    //--spring.batch.job.name=callMethod
    @Bean
    public Job callMethod() {
        return new JobBuilder("callMethod", jobRepository)
                .start(testStep(null))
                .build();
    }

    @Bean
    @JobScope
    public Step testStep(@Value("#{jobParameters['a']}") Long a) {
        return new StepBuilder("testStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("a: {}", a);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
}
