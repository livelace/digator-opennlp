libraries {
    git {
        repo_url = "https://github.com/livelace/digator-opennlp.git"
    }
    k8s {
        build_image = "harbor-core.k8s-2.livelace.ru/dev/jvm:latest"
    }
    kaniko {
        destination = "data/digator-opennlp:master-${env.OPENNLP_VERSION}"
        options = "--build-arg OPENNLP_VERSION=${env.OPENNLP_VERSION}"
    }
    maven {
        options = "-Dquarkus.package.type=uber-jar -Dopennlp.version=${env.OPENNLP_VERSION}"
    }
    mattermost
    nexus {
        source = "target/digator-opennlp-1.0-SNAPSHOT-runner.jar"
        destination = "dists-internal/digator-opennlp/digator-opennlp-master-${env.OPENNLP_VERSION}.jar"
    }
}

