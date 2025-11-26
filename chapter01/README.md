## CommandLineJobRunner

```shell
./gradlew run --args="com.batch.chapter01.command.CommandLineConfig job"
```

---

## JobLauncherApplicationRunner

```shell
./gradlew bootRun -PbootProfile=enabled --args='--spring.profiles.active=boot --spring.batch.job.name=job'
```