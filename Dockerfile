FROM            harbor-core.k8s-2.livelace.ru/dev/jvm:latest

ARG             OPENNLP_VERSION

ENV             SOURCE_PATH="data/opennlp/model/news/ru/${OPENNLP_VERSION}/latest"
ENV             DESTINATION_PATH="/models/news/ru"

USER            root

RUN             mkdir -p "${DESTINATION_PATH}"

COPY            "${SOURCE_PATH}/all.bin"   "${DESTINATION_PATH}/all.bin"
COPY            "${SOURCE_PATH}/date.bin"  "${DESTINATION_PATH}/date.bin"
COPY            "${SOURCE_PATH}/event.bin" "${DESTINATION_PATH}/event.bin"
COPY            "${SOURCE_PATH}/fac.bin"   "${DESTINATION_PATH}/fac.bin"
COPY            "${SOURCE_PATH}/gpe.bin"   "${DESTINATION_PATH}/gpe.bin"
COPY            "${SOURCE_PATH}/loc.bin"   "${DESTINATION_PATH}/loc.bin"
COPY            "${SOURCE_PATH}/money.bin" "${DESTINATION_PATH}/money.bin"
COPY            "${SOURCE_PATH}/org.bin"   "${DESTINATION_PATH}/org.bin"
COPY            "${SOURCE_PATH}/per.bin"   "${DESTINATION_PATH}/per.bin"
COPY            "${SOURCE_PATH}/time.bin"  "${DESTINATION_PATH}/time.bin"

#COPY            "${SOURCE_PATH}/sentence.bin" "${DESTINATION_PATH}/sentence.bin"

COPY            "work/target/digator-opennlp-1.0-SNAPSHOT-runner.jar" "/digator-opennlp.jar"

USER            user

CMD             ["java", "-jar", "/digator-opennlp.jar"]
