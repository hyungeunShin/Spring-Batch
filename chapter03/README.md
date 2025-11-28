## SystemFailureBatchConfig 

```text
에러ID,발생시각,심각도,프로세스ID,에러메시지
ERR001,2025-01-01 08:01:02,CRITICAL,1234,SYSTEM_CRASH
ERR002,2025-01-02 09:02:03,FATAL,1235,MEMORY_OVERFLOW
# 주석
ERR003,2025-01-03 10:03:04,FATAL,1235,MEMORY_OVERFLOW
```

```shell
./gradlew bootRun --args='--spring.batch.job.name=delimiterSystemFailureJob filePath=C:/테스트/1.csv'
```

---

## FixedLengthSystemFailureBatchConfig

```text
ERR001  2025-01-19 10:15:23  CRITICAL  1234  SYSTEM  CRASH DETECT 
ERR002  2025-01-19 10:15:25  FATAL     1235  MEMORY  OVERFLOW FAIL
```

```shell
./gradlew bootRun --args='--spring.batch.job.name=fixedLengthSystemFailureJob filePath=C:/테스트/2.txt'
```

---

## LogAnalysisBatchConfig

```text
[WARNING][Thread-156][CPU: 78%] Thread pool saturation detected - 45/50 threads in use...
[ERROR][Thread-157][CPU: 92%] Thread deadlock detected between Thread-157 and Thread-159
[FATAL][Thread-159][CPU: 95%] Thread dump initiated - system unresponsive for 30s
```

```shell
./gradlew bootRun --args='--spring.batch.job.name=regexLogJob filePath=C:/테스트/3.txt'
```

---

## PatternMatchingLogBatchConfig

```text
ERROR,mysql-prod,OOM,2025-01-24T09:30:00,heap space killing spree,85%,/var/log/mysql
ABORT,spring-batch,MemoryLeak,2025-01-24T10:15:30,forced termination,-1,/usr/apps/batch,TERMINATED
COLLECT,heap-dump,PID-9012,2025-01-24T11:00:15,/tmp/heapdump
ERROR,redis-cache,SocketTimeout,2025-01-24T13:45:00,connection timeout,92%,/var/log/redis
ABORT,zombie-process,Deadlock,2025-01-24T13:46:20,kill -9 executed,-1,/proc/dead,TERMINATED
```

```shell
./gradlew bootRun --args='--spring.batch.job.name=patternMatchingLogJob filePath=C:/테스트/4.log'
```

---

## RecordBatchConfig

```text
command,cpu,status
destroy,99,memory overflow
explode,100,cpu meltdown
collapse,95,disk burnout
```

```shell
./gradlew bootRun --args='--spring.batch.job.name=recordJob filePath=C:/테스트/5.txt'
```

---

## MultiResourceBatchConfig

```text
에러ID,발생시각,심각도,프로세스ID,에러메시지
ERR001,2025-01-19 10:15:23,CRITICAL,1234,SYSTEM_CRASH
ERR002,2025-01-19 10:15:25,FATAL,1235,MEMORY_OVERFLOW
ERR003,2025-01-19 10:16:10,CRITICAL,1236,DATABASE_CORRUPTION
```

```text
에러ID,발생시각,심각도,프로세스ID,에러메시지
ERR101,2025-01-19 10:20:30,WARN,2001,HIGH_CPU_USAGE
ERR102,2025-01-19 10:21:15,INFO,2002,CACHE_MISS
ERR103,2025-01-19 10:22:45,WARN,2003,SLOW_QUERY_DETECTED
```

```shell
./gradlew bootRun --args='--spring.batch.job.name=multiResourceJob filePath=C:/테스트/multi'
```