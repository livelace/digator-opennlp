properties([
    parameters([
        [
            $class              : 'CascadeChoiceParameter',
            choiceType          : 'PT_SINGLE_SELECT',
            description         : null,
            filterLength        : 1,
            filterable          : false,
            name                : 'VERSION',
            randomName          : 'choice-parameter-245877844563629',
            referencedParameters: '',
            script              : [$class: 'ScriptlerScript', parameters: [], scriptlerScriptId: 'digator_opennlp_version.groovy']
        ],
        [
            $class              : 'CascadeChoiceParameter',
            choiceType          : 'PT_SINGLE_SELECT',
            description         : null,
            filterLength        : 1,
            filterable          : false,
            name                : 'OPENNLP_VERSION',
            randomName          : 'choice-parameter-417264169992283',
            referencedParameters: '',
            script              : [$class: 'ScriptlerScript', parameters: [], scriptlerScriptId: 'opennlp_version.groovy']
        ],
        choice(choices: ['v1', 'v2'], name: 'MODEL_GENERATION'),
    ])])

utils_check_first_run()

//k8s_build()