FROM            harbor-core.k8s-2.livelace.ru/dev/jvm:latest

ARG             OPENNLP_VERSION

ENV             SOURCE_PATH="data/opennlp/model/news/ru/${OPENNLP_VERSION}/latest"
ENV             DESTINATION_PATH="/models/news/ru"

USER            root

RUN             mkdir -p "${DESTINATION_PATH}"

COPY            "${SOURCE_PATH}/combined.txt.bin" "${DESTINATION_PATH}/combined.bin"
COPY            "${SOURCE_PATH}/date.txt.bin" "${DESTINATION_PATH}/date.bin"
COPY            "${SOURCE_PATH}/event.txt.bin" "${DESTINATION_PATH}/event.bin"
COPY            "${SOURCE_PATH}/facility.txt.bin" "${DESTINATION_PATH}/fac.bin"
COPY            "${SOURCE_PATH}/geopolitical.txt.bin" "${DESTINATION_PATH}/gpe.bin"
COPY            "${SOURCE_PATH}/location.txt.bin" "${DESTINATION_PATH}/loc.bin"
COPY            "${SOURCE_PATH}/money.txt.bin" "${DESTINATION_PATH}/money.bin"
COPY            "${SOURCE_PATH}/organization.txt.bin" "${DESTINATION_PATH}/org.bin"
COPY            "${SOURCE_PATH}/person.txt.bin" "${DESTINATION_PATH}/person.bin"
COPY            "${SOURCE_PATH}/sentence.txt.bin" "${DESTINATION_PATH}/sentence.bin"
COPY            "${SOURCE_PATH}/time.txt.bin" "${DESTINATION_PATH}/time.bin"
COPY            "${SOURCE_PATH}/statistics.txt" "${DESTINATION_PATH}/statistics.txt"

COPY            "source/target/digator-opennlp-1.0-SNAPSHOT-runner.jar" "/digator-opennlp.jar"

USER            user

CMD             ["java", "-jar", "/digator-opennlp.jar"]
