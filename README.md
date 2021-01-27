# digator-opennlp

***digator-opennlp*** ("dig" + "gator") is an API service for different OpenNLP tasks. 

### Features:

* Named Entity extraction (OpenNLP and Label Studio output formats).
* Sentence detector.

### Quick start:

```shell
# start daemon.
user@localhost ~ $ docker run --rm -p 8080:8080 -ti docker.io/livelace/digator-opennlp:master-1.8.4
__  ____  __  _____   ___  __ ____  ______ 
 --/ __ \/ / / / _ | / _ \/ //_/ / / / __/ 
 -/ /_/ / /_/ / __ |/ , _/ ,< / /_/ /\ \   
--\___\_\____/_/ |_/_/|_/_/|_|\____/___/   
2021-01-27 19:43:04,071 INFO  [io.quarkus] (main) digator-opennlp 1.0-SNAPSHOT on JVM (powered by Quarkus 1.10.5.Final) started in 1.313s. Listening on: http://0.0.0.0:8080
2021-01-27 19:43:04,122 INFO  [io.quarkus] (main) Profile prod activated. 
2021-01-27 19:43:04,122 INFO  [io.quarkus] (main) Installed features: [cdi, resteasy, resteasy-jsonb, smallrye-health, smallrye-openapi, swagger-ui]

# named entity extraction (OpenNLP format).
curl -X POST "http://127.0.0.1:8080/ner/news/ru/combined" \
  -H  "accept: application/json" \
  -H  "Content-Type: application/json" \
  -d "{\"text\":\"Руководитель московского департамента торговли и услуг Алексей Немерюк в среду, 27 января, отметил, что, несмотря на снятие ограничений на работу развлекательных заведений города в ночное время перчаточно-масочный режим в них никто не отменял.\"}"
        
# named entity extraction (Label Studio format).
curl -X POST "http://127.0.0.1:8080/ner/news/ru/combined?format=label-studio" \
  -H  "accept: application/json" \
  -H  "Content-Type: application/json" \
  -d "{\"text\":\"Руководитель московского департамента торговли и услуг Алексей Немерюк в среду, 27 января, отметил, что, несмотря на снятие ограничений на работу развлекательных заведений города в ночное время перчаточно-масочный режим в них никто не отменял.\"}" | jq  
```

Navigate to [http://127.0.0.1:8080/swagger-ui](http://127.0.0.1:8080/swagger-ui) for API UI experience.

