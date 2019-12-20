# Dev
```shell script
npm run watch
sbt run
```

# Prod
```shell script
npm run prod
sbt clean compile stage
./target/universal/stage/bin/scala-web-project -Dapp.home=./target/universal/stage -Dhttp.port=8081 -Dpidfile.path=./server1.pid
```
