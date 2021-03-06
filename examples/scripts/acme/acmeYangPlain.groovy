#!/usr/bin/env groovy

//This is free software licensed under MIT License, see LICENSE file
//(https://bitbucket.org/novakmi/yangbuilder/src/LICENSE)

//If you have Internet connection, use groovy Grab to get dependencies (may take some time for the first time to download jars)
//Run as ordinary groovy script with command 'groovy <scriptName>.groovy' (or as Linux executable script './<scriptName>.groovy')
//Update nodebuilder, yangbuilder version numbers as needed
@Grab(group = 'org.bitbucket.novakmi', module = 'nodebuilder', version = '1.1.1')
@Grab(group = 'org.bitbucket.novakmi', module = 'yangbuilder', version = '1.3.0')

//This script template represents example of usage without any plugin
def builder = new org.bitbucket.novakmi.yangbuilder.YangBuilder() //create new builder, default indent 2
//name of file to generate
moduleName = "acme-module-plain"   // do not use 'def' for script global variable

/* if 'yangroot' is used as root node, its value is not echoed;
   use 'yangroot' e.g. if you need to add comments before 'module' or 'submodule'
   Otherwise use directly builder.module or builder.submodule
*/
builder.yangroot {
    geninfo file: "acmeYang.groovy", time: true,
        cmt: '''\nExample implementation from yang tutorial
                http://www.yang-central.org/twiki/bin/view/Main/YangTutorials'''

    module moduleName, {
        namespace "http://acme.example.com/module"; //semicolon at the end can present (yang style)
        prefix "acme"                               //or semicolon can be missing
        yngbuild ''                                 //yngbuild '' means new line without indentation

        //Groovy/Java keyword like 'import' has to be quoted
        'import' "yang-types", {
            prefix "yang"
        }
        include "acme-system"
        yngbuild ''

        organization 'ACME Inc.'
        contact 'joe@acme.example.com'
        // multiple line description
        description '''The module for entities
                       implementing the ACME products.'''

        yngbuild ''
        revision "2007-06-09", {
            description "Initial revision."
        }
        yngbuild ''

        leaf "host-name", {
            type "string"
            mandatory true
            config true
            description "Hostname for this system"
        }

        "leaf-list" "domain-search", {
            type "string"
            "ordered-by" "user"
            description "List of domain names to search"
        }
    }
}

builder.writeToFile("${builder.getYangName()}.yang")
