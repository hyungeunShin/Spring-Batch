## JobParameters 기본 표기법

```
parameterName=parameterValue,parameterType,identificationFlag
```
- parameterName: 배치 Job에서 파라미터를 찾을 때 사용할 key 값이다. 이 이름으로 Job 내에서 파라미터에 접근할 수 있다.
- parameterValue: 파라미터의 실제 값
- parameterType: 파라미터의 타입(java.lang.String, java.lang.Integer와 같은 fully qualified name), 타입을 명시하지 않을 경우 String 타입으로 설정
- identificationFlag: Spring Batch에게 해당 파라미터가 JobInstance 식별에 사용될 파라미터인지 여부를 전달하는 값으로 true이면 식별에 사용, 플래그를 명시하지 않을 경우 true로 설정

---

## TestTaskletBatchConfig

```shell
./gradlew bootRun --args='--spring.batch.job.name=testTaskletJob'
```

---

## TestChunkBatchConfig

```shell
./gradlew bootRun --args='--spring.batch.job.name=testChunkJob'
```

---

## DeleteOldFilesBatchConfig

```shell
./gradlew bootRun --args='--spring.batch.job.name=deleteOldFilesJob'
```

```shell
./gradlew bootRun --args='--spring.batch.job.name=deleteOldRecordsJob'
```

---

## SimpleJobParameterBatchConfig

```shell
./gradlew bootRun --args='--spring.batch.job.name=simpleJobParameterJob a=홍길동,java.lang.String b=5,java.lang.Integer'
```

---

## DateJobParameterBatchConfig

```shell
./gradlew bootRun --args='--spring.batch.job.name=dateJobParameterJob executionDate=2025-01-01,java.time.LocalDate startTime=2025-01-01T23:59:59,java.time.LocalDateTime'
```

| 타입                      | 형식                  |
|-------------------------|---------------------|
| java.util.Date          | ISO_INSTANT         |
| java.time.LocalDate     | ISO_LOCAL_DATE      |
| java.time.LocalDateTime | ISO_LOCAL_DATE_TIME |
| java.time.LocalTime     | ISO_LOCAL_TIME      |

---

## EnumJobParameterBatchConfig

```shell
./gradlew bootRun --args='--spring.batch.job.name=enumJobParameterJob testEnum=B,com.batch.chapter02.jobparameter.EnumJobParameterBatchConfig$TestEnum'
```
- Java에서 내부 클래스의 full name을 표현할 때는 $ 기호로 외부 클래스와 내부 클래스를 구분

---

## PojoJobParameterBatchConfig

```shell
./gradlew bootRun --args='--spring.batch.job.name=pojoJobParameterJob name=홍길동,java.lang.String id=hong age=20,java.lang.Integer,false'
```
- 잡 파라미터의 기본 타입이 String이라 id의 파라미터 표기에서는 타입 선언을 생략
- age는 identifying=false로 설정되어 Job 인스턴스 식별에는 사용되지 않음

---

## JsonJobParameterBatchConfig

- JSON 기반 파라미터 표기법을 사용하려면 JsonJobParametersConverter가 필요
- JsonJobParametersConverter는 지금까지 사용한 DefaultJobParametersConverter를 계승한 클래스로 내부적으로 ObjectMapper를 사용해 JSON 형태의 파라미터 표기를 해석

**※ 인텔리제이 Edit Configurations**

- Settings > Build, Execution, Deployment > Gradle > Build and run using, Run tests using을 IntelliJ IDEA로 변경

```
//Program arguments
--spring.profiles.active=json --spring.batch.job.name=jsonJobParameterJob a={\"value\":\"aaa,bbb\",\"type\":\"java.lang.String\"}
```

**※ 인텔리제이 터미널(Command Prompt)**

- ./gradlew clean bootJar

```
cd build/libs
java -jar chapter02-0.0.1-SNAPSHOT.jar --spring.profiles.active=json --spring.batch.job.name=jsonJobParameterJob a={\"value\":\"aaa,bbb\",\"type\":\"java.lang.String\"}
```

**※ 인텔리제이 터미널(Windows PowerShell)**

- ./gradlew clean bootJar

```
cd build/libs
java -jar chapter02-0.0.1-SNAPSHOT.jar --spring.profiles.active=json --spring.batch.job.name=jsonJobParameterJob a='{\"value\":\"aaa,bbb\",\"type\":\"java.lang.String\"}'
```

---

## JobParameterValidatorBatchConfig

```shell
./gradlew bootRun --args='--spring.batch.job.name=jobParameterValidatorJob1 a=5,java.lang.Long'
```

```shell
./gradlew bootRun --args='--spring.batch.job.name=jobParameterValidatorJob1'
```

```shell
./gradlew bootRun --args='--spring.batch.job.name=jobParameterValidatorJob2 a=10,java.lang.Long b=5,java.lang.Long'
```

```shell
./gradlew bootRun --args='--spring.batch.job.name=jobParameterValidatorJob2 a=10,java.lang.Long'
```

---

## ExecutionContextBatchConfig

```shell
./gradlew bootRun --args='--spring.batch.job.name=contextJob'
```

---

## TestListenerBatchConfig

```shell
./gradlew bootRun --args='--spring.batch.job.name=testListenerJob'
```

---

## AdvancedListenerBatchConfig

```shell
./gradlew bootRun --args='--spring.batch.job.name=advancedListenerJob'
```

---

## PromotionListenerBatchConfig

```shell
./gradlew bootRun --args='--spring.batch.job.name=promotionListenerJob'
```

---

## JobParameterAndListenerBatchConfig

```shell
./gradlew bootRun --args='--spring.batch.job.name=jobParameterAndListenerJob result=aaa,java.lang.String'
```

---

## ExceptionListenerBatchConfig

```shell
./gradlew bootRun --args='--spring.batch.job.name=exceptionListenerJob'
```