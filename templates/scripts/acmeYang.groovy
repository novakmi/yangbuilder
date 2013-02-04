#!/usr/bin/env groovy

//This is free software licensed under MIT License, see LICENSE file
//(https://bitbucket.org/novakmi/yangbuilder/src/LICENSE)

//If you have Internet connection, use groovy Grab to get dependencies (may take some time for the first time to download jars)
//Run as ordinary groovy script with command 'groovy <scriptName>.groovy' (or as Linux executable script './<scriptName>.groovy')
//Update nodebuilder, yangbuilder version numbers as needed
//adoc-begin - documentation purpose comment
@GrabResolver(name = 'novakmirepo',
root = 'https://github.com/novakmi/novakmirepo/raw/master/releases', m2compatible = true)        //<2>

@Grab(group = 'org.bitbucket.novakmi', module = 'nodebuilder', version = '0.7.0')
@Grab(group = 'org.bitbucket.novakmi', module = 'yangbuilder', version = '0.5.0')

//This script template represents example of usage without any plugin
def builder = new org.bitbucket.novakmi.yangbuilder.YangBuilder() //create new builder, default indent 2

//name of file to generate
moduleName = "acme-module"   // do not use 'def' for script global variable

def makeModule(builder) {
        builder.module(moduleName) {                                                                   //<3>
                //yngbuild echoes its value, indent:true forces indentation of echoed line
                yngbuild('// based on example from Instant YANG tutorial, section modules', indent: true)
                namespace "http://acme.example.com/module"; //semicolon at the end can present (yang style)
                prefix "acme" //or semicolon can be missing (more groovy like style)
                yngbuild('')  //yngbuild('') means new line without indentation

                //Groovy/Java keywords has to be quoted; if node has sub nodes, value has to be in brackets
                'import'("yang-types") {
                        prefix "yang"
                }
                include "acme-system" // if node does not have sub nodes, brackets are optional
                yngbuild('')

                organization 'ACME Inc.'
                contact 'joe@acme.example.com'
                description('''The module for entities
implementing the ACME products.''', multiline: true) // multiple line description
                yngbuild('')

                revision('2007-06-09') {
                        description "Initial revision."
                }
                yngbuild('')
        }
}

/* if 'yangroot' is used as root node, its value is not echoed;
   use 'yangroot' e.g. if you need to add comments before 'module' or 'submodule'
   Otherwise use directly builder.module or builder.submodule
*/
builder.yangroot {                                                                                     //<4>
        yngbuild('//adocy-begin - documentation purpose comment') //#$# for asciidoc
        yngbuild("/* This yang file was generated with groovy YangBuilder on ${new Date().toString()}")
        yngbuild('   see http://bitbucket.org/novakmi/yangbuilder */')
        yngbuild('//                                                                           *<*1*>*') //#$# for asciidoc
        // one can continue with  module(moduleName) ... or build continue building yang in
        // separate function (another option is to define closure after builder.yangroot { ...)
        makeModule(builder)
        yngbuild('/* adocy-callout - documentation purpose comment') //#$# for asciidoc
        yngbuild('*<*1*>* default indent is 2 unless overwritten') //#$# for asciidoc
        yngbuild('   adocy-end - documentation purpose comment */') //#$# for asciidoc
}

builder.writeToFile("${builder.getYangName()}.yang")                                                  //<5>
/* adoc-callout - documentation purpose comment
<2> With Internet connection, use groovy <<Groovy>> +Grab+ to get dependencies automatically (may take some time for the first time to download jars)
Without Internet connection you need to specify <<nodebuilder>> and <<yangbuilder>> jars on the classpath.
E.g. +groovy -cp ./nodebuilder-0.7.0.jar:./yangbuilder-0.5.0.jar <scriptname>.groovy>+. In this way one can also use different version of the jar files than
<3>  function that makes main <<yang>> module
<4> script entry point, normally we would start with +builder.module(moduleName)+, but since we want to add some comments before module, we start
with +yangroot+ ant then continue with +ynngbuild+ (echoing commands)
<5> with function +getYangName+ builder returns name of the +module+ or the +submodule+ node, which we can use to get <<yang>> file name.
   adoc-end - documentation purpose comment*/

