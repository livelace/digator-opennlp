libraries {
    dependency_check
    dependency_track {
        input = "target/bom.xml"
        project = "digator-opennlp"
        version = "master-${env.OPENNLP_VERSION}"
    }
    git {
        repo_url = "https://github.com/livelace/digator-opennlp.git"
    }
    harbor_replicate {
        policy = "digator-opennlp"
    }
    harbor_scan {
        artifact = "data/digator-opennlp:master-${env.OPENNLP_VERSION}"
        severity = "medium"
    }
    k8s_build {
        image = "harbor-core.k8s-2.livelace.ru/dev/jvm:latest"

        volume = """
            build1-opennlp-storage-shared, data/opennlp, ro
        """
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
    sonarqube
    utils
}

