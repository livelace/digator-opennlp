properties([
        parameters([
                [
                        $class: 'CascadeChoiceParameter',
                        choiceType: 'PT_SINGLE_SELECT',
                        description: null,
                        filterLength: 1,
                        filterable: false,
                        name: 'OPENNLP_VERSION',
                        randomName: 'choice-parameter-417264169992283',
                        referencedParameters: '',
                        script: [$class: 'ScriptlerScript', parameters: [], scriptlerScriptId: 'opennlp_version.groovy']
                ]
        ])])

utils_check_first_run()

def volume = [
        ["build1-opennlp-storage-shared", "data/opennlp", "ro"]
]

k8s_build([], volume)