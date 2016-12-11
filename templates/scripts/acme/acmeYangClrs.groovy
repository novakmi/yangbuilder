#!/usr/bin/env groovy

//This is free software licensed under MIT License, see LICENSE file
//(https://bitbucket.org/novakmi/yangbuilder/src/LICENSE)

//If you have Internet connection, use groovy Grab to get dependencies (may take some time for the first time to download jars)
//Run as ordinary groovy script with command 'groovy <scriptName>.groovy' (or as Linux executable script './<scriptName>.groovy')
//Update nodebuilder, yangbuilder version numbers as needed

@Grab(group = 'org.bitbucket.novakmi', module = 'nodebuilder', version = '1.0.0')
@Grab(group = 'org.bitbucket.novakmi', module = 'yangbuilder', version = '1.1.0')

//name of file to generate
moduleName = "acme-module-closure"   // do not use 'def' for script global variable

// define closures (not depending on builder object)
def makeModuleHeader = {
    header _ygn:true, { //any node with attribute '_ygn' is  ignored
        namespace "http://acme.example.com/module"; //semicolon at the end can present (yang style)
        prefix "acme" //or semicolon can be missing (more groovy like style)
        yngbuild ''  //yngbuild '' means new line without indentation

        'import' "yang-types", {
            //Groovy/Java keywords has to be quoted (or alias e.g. "import_" can be declared)
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
    }
}

def makeModule = { name ->
    module name, {
        delegate << makeModuleHeader

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

/* if 'yangroot' is used as root node, its value is not echoed;
   use 'yangroot' e.g. if you need to add comments before 'module' or 'submodule'
   Otherwise use directly builder.module or builder.submodule
*/
def root = {
    yangroot {
        geninfo file: "acmeYang.groovy", time: true,
                cmt: "Example implementation from yang tutorial http://www.yang-central.org/twiki/bin/view/Main/YangTutorials"
        // one can continue with continue building yang from separate closure
        delegate << makeModule.curry(moduleName) //use curry to pass params to the closure
    }
}

//This script template represents example of usage without any plugin with closure reuse
def builder = new org.bitbucket.novakmi.yangbuilder.YangBuilder() //create new builder, default indent 2
builder << root // build closure
builder.writeToFile("${builder.getYangName()}.yang")

