package com.batch.chapter02.tasklet;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

@Slf4j
public class TestTasklet implements Tasklet {
    /*
    RepeatStatus가 필요한 이유: 짧은 트랜잭션을 활용한 안전한 배치 처리
        Spring Batch는 Tasklet의 execute() 호출 마다 새로운 트랜잭션을 시작하고 execute()의 실행이 끝나 RepeatStatus가 반환되면 해당 트랜잭션을 커밋한다.
        execute() 메서드 내부에 반복문을 직접 구현했다고 가정해보자. 이 경우 모든 반복 작업이 하나의 트랜잭션 안에서 실행된다.
        만약 실행 도중 예외가 발생하면, 데이터베이스 결과가 execute()호출 전으로 롤백되어 버린다.

        예를 들어, 오래된 주문 데이터를 정리하는 배치 작업을 생각해보자.
        한 번에 만 건씩 데이터를 삭제하는데, 총 100만 건의 데이터를 처리해야 한다고 하자.
        execute() 내부에서 while문을 사용한다면: 80만 건째 처리 중 예외가 발생했을 때, 이미 처리한 79만 건의 데이터도 모두 롤백되어 하나도 정리되지 않은 상태로 돌아간다
        RepeatStatus.CONTINUABLE로 반복한다면: 매 만 건 처리마다 트랜잭션이 커밋되므로, 예외가 발생하더라도 79만 건의 데이터는 이미 안전하게 정리된 상태로 남는다
    */

    private final int a = 10;
    private int b = 1;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        log.info("MyTasklet-{}/{}", b++, a);

        if(b > a) {
            log.info("MyTasklet Finished");
            return RepeatStatus.FINISHED;
        }

        return RepeatStatus.CONTINUABLE;
    }
}
