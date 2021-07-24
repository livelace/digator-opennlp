jte {
    pipeline_template = 'k8s_build.groovy'
}

libraries {
    git {
        repo_url = 'git@git.livelace.ru:livelace/digator-opennlp.git'
    }
    maven {
        options = '-Dquarkus.package.type=uber-jar -Dopennlp.version=1.8.4'
    }
    mattermost
    nexus {
        source = 'target/digator-opennlp-1.0-SNAPSHOT-runner.jar'
        destination = 'dists-internal/digator-opennlp/digator-opennlp-${VERSION}-1.8.4.jar'
    }
    version
}

keywords {
    build_image = 'harbor-core.k8s-2.livelace.ru/dev/jvm:latest'
}
