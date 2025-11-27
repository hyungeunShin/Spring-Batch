package com.batch.chapter02.listener;

public class Listener {
    /*
    public interface JobExecutionListener {
        default void beforeJob(JobExecution jobExecution) { }
        default void afterJob(JobExecution jobExecution) { }
    }

    JobExecutionListener는 Job 실행의 시작과 종료 시점에 호출되는 리스너 인터페이스이다.

    beforeJob()은 Job 실행 직전에 호출되며, Job 시작 전 필요한 리소스를 준비하는 등의 초기화 작업을 수행할 수 있다.

    afterJob()은 Job 실행 후에 호출되어 Job 실행 결과를 이메일로 전송하거나 리소스를 정리하는 등의 후처리 작업을 수행할 수 있다.
    특히 Job의 성공/실패 여부와 관계없이 무조건 호출되기 때문에 실패 시 알림을 보내거나 더 나아가 실패한 Job의 상태를 변경하는 작업까지 안전하게 수행할 수 있다.
    */

    /*
    public interface StepExecutionListener extends StepListener {
        default void beforeStep(StepExecution stepExecution) {
        }

        @Nullable
        default ExitStatus afterStep(StepExecution stepExecution) {
        return null;
        }
    }

    StepExecutionListener는 Step 실행의 시작과 종료 시점에 호출되는 리스너 인터페이스다.
    Step의 시작 시간, 종료 시간, 처리된 데이터 수를 로그로 기록하는 등의 사용자 정의 작업을 추가할 수 있다.

    beforeStep()은 Step의 시작 시점, 정확히는 StepScope가 활성화된 직후에 호출된다. Step 실행 전 필요한 초기화 작업을 수행할 수 있다.

    afterStep()은 Step 종료 시점에 호출된다.
    JobExecutionListener의 afterJob()과 마찬가지로 Step의 성공/실패 여부와 관계없이 무조건 호출되기 때문에 실패 처리나 리소스 정리 등 안전한 후처리 작업을 수행할 수 있다.
    */

    /*
    public interface ChunkListener extends StepListener {
        default void beforeChunk(ChunkContext context) {
        }

        default void afterChunk(ChunkContext context) {
        }

        default void afterChunkError(ChunkContext context) {
        }
    }

    청크 지향 처리는 청크 단위로 아이템 읽기/쓰기를 반복한다.
    ChunkListener는 이러한 하나의 청크 단위 처리가 시작되기 전, 완료된 후, 그리고 실행 도중 에러가 발생했을 때 호출되는 리스너 인터페이스다.
    각 청크의 처리 현황을 모니터링하거나 로깅하는데 사용할 수 있다.

    afterChunk()는 트랜잭션이 커밋된 후에 호출된다. 반면 청크 처리 도중 예외가 발생하면 호출되는 afterChunkError()는 청크 트랜잭션이 롤백된 이후에 호출된다.

    이름과 달리 ChunkListener는 청크 지향 Step뿐만 아니라 Tasklet 지향 Step에서도 동작한다.
    Tasklet의 execute() 호출 전후, 그리고 execute() 실행 중 오류 발생 시에도 실행되므로 Tasklet 지향 Step에서도 ChunkListener를 활용한 모니터링이 가능하다.
    그럼 Tasklet 지향 Step에서 StepExecutionListener 쓰는 거랑 뭐가 다를까
    차이는 Tasklet.execute()가 반복 실행될 때 드러난다.
    execute()에서 RepeatStatus.CONTINUABLE을 반환하면 execute()가 반복 실행된다.
    이때 ChunkListener는 각 execute() 실행마다 호출되지만 StepExecutionListener는 Step 시작과 끝에만 한 번씩 호출된다.
    즉, 반복 실행 과정을 세밀하게 추적하려면 ChunkListener가 필요하다.
    */

    /*
    public interface ItemReadListener<T> extends StepListener {
        default void beforeRead() { }
        default void afterRead(T item) { }
        default void onReadError(Exception ex) { }
    }

    public interface ItemProcessListener<T, S> extends StepListener {
        default void beforeProcess(T item) { }
        default void afterProcess(T item, @Nullable S result) { }
        default void onProcessError(T item, Exception e) { }
    }

    public interface ItemWriteListener<S> extends StepListener {
        default void beforeWrite(Chunk<? extends S> items) { }
        default void afterWrite(Chunk<? extends S> items) { }
        default void onWriteError(Exception exception, Chunk<? extends S> items) { }
    }

    ItemReadListener.afterRead()는 ItemReader.read() 호출 후에 호출되지만 ItemReader.read() 메서드가 더 이상 읽을 데이터가 없어 null을 반환할 때는 호출되지 않는다.
    ItemProcessListener.afterProcess()는 ItemProcessor.process() 메서드가 null을 반환하더라도 호출된다. 참고로 ItemProcessor에서 null을 반환하는 것은 해당 데이터를 필터링했다는 의미다.
    ItemWriteListener.afterWrite()는 트랜잭션이 커밋되기 전, 그리고 ChunkListener.afterChunk()가 호출되기 전에 호출된다.
    */
}
