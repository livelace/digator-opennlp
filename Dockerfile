FROM            harbor-core.k8s-2.livelace.ru/dev/jvm:latest

ARG             OPENNLP_VERSION

ENV             SOURCE_PATH="/data/opennlp/model/news/ru/${OPENNLP_VERSION}/latest"
ENV             DESTINATION_PATH="/models"

USER            root

RUN             mkdir -p "${DESTINATION_PATH}/news/ru" && \
                cp "${SOURCE_PATH}/combined.txt.bin" "${DESTINATION_PATH}/news/ru/combined.bin" && \
                cp "${SOURCE_PATH}/date.txt.bin" "${DESTINATION_PATH}/news/ru/date.bin" && \
                cp "${SOURCE_PATH}/event.txt.bin" "${DESTINATION_PATH}/news/ru/event.bin" && \
                cp "${SOURCE_PATH}/facility.txt.bin" "${DESTINATION_PATH}/news/ru/fac.bin" && \
                cp "${SOURCE_PATH}/geopolitical.txt.bin" "${DESTINATION_PATH}/news/ru/gpe.bin" && \
                cp "${SOURCE_PATH}/location.txt.bin" "${DESTINATION_PATH}/news/ru/loc.bin" && \
                cp "${SOURCE_PATH}/money.txt.bin" "${DESTINATION_PATH}/news/ru/money.bin" && \
                cp "${SOURCE_PATH}/organization.txt.bin" "${DESTINATION_PATH}/news/ru/org.bin" && \
                cp "${SOURCE_PATH}/person.txt.bin" "${DESTINATION_PATH}/news/ru/person.bin" && \
                cp "${SOURCE_PATH}/sentence.txt.bin" "${DESTINATION_PATH}/news/ru/sentence.bin" && \
                cp "${SOURCE_PATH}/time.txt.bin" "${DESTINATION_PATH}/news/ru/time.bin" && \
                cp "${SOURCE_PATH}/statistics.txt" "${DESTINATION_PATH}/news/ru/statistics.txt"

COPY            "target/digator-opennlp-1.0-SNAPSHOT-runner.jar" "/digator-opennlp.jar"

USER            user

CMD             ["java", "-jar", "/digator-opennlp.jar"]
