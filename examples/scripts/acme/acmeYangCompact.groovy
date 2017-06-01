#!/usr/bin/env groovy
import org.bitbucket.novakmi.yangbuilder.CompactYangPlugin

//This is free software licensed under MIT License, see LICENSE file
//(https://bitbucket.org/novakmi/yangbuilder/src/LICENSE)

//If you have Internet connection, use groovy Grab to get dependencies
//(may take some time for the first time to download jars)
//Run as ordinary groovy script with command 'groovy <scriptName>.groovy'
//(or as Linux executable script './<scriptName>.groovy')
//Update nodebuilder, yangbuilder version numbers as needed

@Grab(group = 'org.bitbucket.novakmi', module = 'nodebuilder', version = '1.1.0')
@Grab(group = 'org.bitbucket.novakmi', module = 'yangbuilder', version = '1.3.0')

//This script template represents example of usage with the CompactYangPlugin
def plugin = new CompactYangPlugin()
def builder = new org.bitbucket.novakmi.yangbuilder.YangBuilder(2, plugin)  //register ...
plugin.declareCommonAliasesAndQuotes()   // and configure compact plugin

//name of file to generate
moduleName = "acme-module-compact"   // do not use 'def' for script global variable

/* if 'yangroot' is used as root node, its value is not echoed;
   use 'yangroot' e.g. if you need to add comments before 'module' or 'submodule'
   Otherwise use directly builder.module or builder.submodule
*/
builder.yangroot {
    geninfo file: "acmeYang.groovy", time: true,
        cmt: '''\nExample implementation from yang tutorial
                http://www.yang-central.org/twiki/bin/view/Main/YangTutorials'''

    builder.module moduleName, {
        namespace "http://acme.example.com/module", nlLevel: true
        // nlLevel: true - insert \n after each element on this indent level
        prefix "acme" //or semicolon can be missing (more groovy like style)

        import_ "yang-types", prefix: "yang"
        include "acme-system"

        organization 'ACME Inc.'
        contact 'joe@acme.example.com'
        // multiple line description
        description '''The module for entities
                       implementing the ACME products.'''

        revision "2007-06-09", description: "Initial revision."
        leaf "host-name", type: "string", mandatory: true, config: true, description: "Hostname for this system"
        leaf_list "domain-search", type: "string", ordered_by: "user", description: "List of domain names to search"
    }
}

builder.writeToFile("${builder.getYangName()}.yang")
