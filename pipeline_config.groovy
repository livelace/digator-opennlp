def APP_NAME = "digator-opennlp"
def APP_REPO = "https://github.com/livelace/digator-opennlp.git"
def APP_VERSION = "${env.VERSION}-${env.OPENNLP_VERSION}"


libraries {
    dependency_check
    dependency_track {
        input = "target/dependency-track.xml"
        project = "${APP_NAME}"
        version = "${APP_VERSION}"
    }
    git {
        repo_url = "${APP_REPO}"
        repo_branch = env.VERSION
    }
    harbor_replicate {
        policy = "${APP_NAME}"
    }
    k8s_build {
        image = "harbor-core.k8s-2.livelace.ru/dev/jvm:latest"

        volume = """
            build1-opennlp-storage-shared, data/opennlp, ro
        """
    }
    kaniko {
        destination = "data/${APP_NAME}:${APP_VERSION}"
        options = "--build-arg OPENNLP_VERSION=${env.OPENNLP_VERSION}"
    }
    maven {
        options = "-Dquarkus.package.type=uber-jar -Dopennlp.version=${env.OPENNLP_VERSION}"
    }
    mattermost
    nexus {
        source = "target/digator-opennlp-1.0-SNAPSHOT-runner.jar"
        destination = "dists-internal/${APP_NAME}/${APP_NAME}-${APP_VERSION}.jar"
    }
    sonarqube
    utils
}

