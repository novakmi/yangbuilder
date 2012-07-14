#!/usr/bin/env groovy

//This is free software licensed under MIT License, see LICENSE file
//(https://bitbucket.org/bubbles.way/yangbuilder/src/LICENSE)

//If you have Internet connection, use groovy Grab to get dependencies (may take some time for the first time to download jars)
//Run as ordinary groovy script with command 'groovy <scriptName>.groovy' (or as Linux executable script './<scriptName>.groovy')
//Update nodebuilder, yangbuilder version numbers as needed
//adoc-begin - documentation purpose comment
@GrabResolver(name = 'bubbleswayrepo',
root = 'https://github.com/bubblesway/bubbleswayrepo/raw/master/releases', m2compatible = true)
@Grab(group = 'org.bitbucket.novakmi', module = 'nodebuilder', version = '0.6.0')
@Grab(group = 'org.bitbucket.novakmi', module = 'yangbuilder', version = '0.4.0')

// This script template represents example of usage without any plugin
def builder = new org.bitbucket.novakmi.yangbuilder.YangBuilder(4) // create new builder, indent 4 (default is 2)
builder.declareAlias('import_', 'import') //optional alias declaration                                             //<1>

//name of file to generate
moduleName = "example1-module"   // do not use 'def' for script global variable

def makeAddressPort(builder, kind = null) { //function with parameters  can be used by the builder                //<2>
        // in function all nodes have to be prefixed with 'builder.', except for child nodes
        builder.yngbuild("// IPv4 or IPv6 address", indent: true)
        builder.leaf("${kind ? kind + '-' : ''}address") { //output depends on parameters, not possible in yang
                type('inet:ip-address')
        }
        builder.yngbuild("// IP port", indent: true)
        builder.leaf("${kind ? kind + '-' : ''}port") {
                type('uint16')
        }
}

builder.yangroot {                                                                                                   //<3>
        yngbuild('//adocy-begin - documentation purpose comment')//#$# for asciidoc
        yngbuild('//                                                                           *<*1*>*') //#$# for asciidoc
        module(moduleName) {
                def makeGrouping = { // this is example how closure can be used by the builder                       //<4>
                        yngbuild('//                                                           *<*2*>*') //#$# for asciidoc
                        grouping('addressPort') {
                                makeAddressPort(builder)
                        }
                }

                yngbuild("/* This yang file was generated with groovy YangBuilder on ${new Date().toString()}",
                        indent: true)
                yngbuild('   see http://bitbucket.org/bubbles.way/yangbuilder */', indent: true)                    //<5>
                yngbuild('') // new line

                namespace "http://bitbucket.org/bubbles.way/yangbuilder"
                prefix "example1"
                yngbuild('')

                // 'import' alias is declared, it can be used instead of 'import' - workaround for clashing keyword  //<6>
                import_('ietf-inet-types') {
                        prefix 'inet'
                }
                yngbuild('')

                makeGrouping() // this behaves in the same way as if content of the closure is written here         //<7>

                yngbuild('')

                yngbuild("/* neighbor */", indent: true)
                container('neighbor') {
                        uses 'addressPort'  // yang grouping reuse
                }
                yngbuild('')

                ['bgp', 'ospf', 'isis', 'rip'].each {k -> // create 3 containers in loop, not possible in yang       //<8>
                        yngbuild("/* ${k} neighbor */", indent: true)
                        container("${k}-neighbor") {
                                // as if content of the function is written here, reuse (not possible in yang)       //<9>
                                makeAddressPort(builder, k)
                        }
                        yngbuild('')
                }

                list('neighbors') { // key leaf can be attribute of list statement
                        description('List of neighbors')
                        key('neighbor')
                        leaf('neighbor') {
                                description "neighbor IP4 or IPv6 address"
                                type 'inet:ip-address'
                        }
                        leaf('name') {
                                description "neighbor name"
                                type 'string'
                        }
                }
        }
        yngbuild('//adocy-callout - documentation purpose comment') //#$# for asciidoc
        yngbuild('*<*1*>* indent 4') //#$# for asciidoc
        yngbuild('*<*2*>* call to +makeGrouping+') //#$# for asciidoc
        yngbuild('//adocy-end - documentation purpose comment') //#$# for asciidoc
}

builder.writeToFile("${builder.getYangName()}.yang")
/* adoc-callout - documentation purpose comment
<1> declaration of alias keywords (e.g. for keywords clashing with groovy keywords)
<2> function
<3> +yngroot+ is not needed, +builder.module(moduleName)+ can be directly used (it is here for documentation)
<4> closure
<5> closure called
<6> alias is used
<7> indentation
<8> iteration
<9> function called
   adoc-end - documentation purpose comment*/

