#!/usr/bin/env groovy

//This is free software licensed under MIT License, see LICENSE file
//(https://bitbucket.org/bubbles.way/yangbuilder/src/LICENSE)

//If you have Internet connection, use groovy Grab to get dependencies (may take some time for the first time to download jars)
//Run as ordinary groovy script with command 'groovy <scriptName>.groovy' (or as Linux executable script './<scriptName>.groovy')
//Update nodebuilder, yangbuilder version numbers as needed
@GrabResolver(name = 'bubbleswayrepo', root = 'https://github.com/bubblesway/bubbleswayrepo/raw/master/releases', m2compatible = true)
@Grab(group = 'org.bitbucket.novakmi', module = 'nodebuilder', version = '0.5.0')
@Grab(group = 'org.bitbucket.novakmi', module = 'yangbuilder', version = '0.2.0')

// This script template represents example of usage without any plugin
def builder = new org.bitbucket.novakmi.yangbuilder.YangBuilder(4) // create new builder, indent 4 (default is 2)
builder.declareAlias('import_', 'import')

//name of file to generate
moduleName = "example1-module"   // do not use 'def' for script global variable

def makeAddressPort(builder, kind = null) { //this is example how function can be used by the builder, parameters can be used
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

builder.module(moduleName) {
        def makeGrouping = { // this is example how closure can be called be used by the builder
                grouping('addressPort') {
                        makeAddressPort(builder)
                }
        }

        yngbuild("/* This yang file was generated with groovy YangBuilder on ${new Date().toString()}", indent: true)
        yngbuild('   see http://bitbucket.org/bubbles.way/yangbuilder */', indent: true)
        yngbuild('') // new line

        namespace "http://bitbucket.org/bubbles.way/yangbuilder"
        prefix "example1"
        yngbuild('')

        import_('ietf-inet-types') { // 'import' alias is declared, it can be used instead of 'import' - workaround for clashing keyword
                prefix 'inet'
        }
        yngbuild('')

        makeGrouping() // this behaves in the same way as if content of the closure is written here

        yngbuild('')

        yngbuild("/* neighbor */", indent: true)
        container('neighbor') {
                uses 'addressPort'  // yang grouping reuse
        }
        yngbuild('')

        ['bgp', 'ospf', 'isis', 'rip'].each {k -> // create 3 containers in loop, not possible in yang
                yngbuild("/* ${k} neighbor */", indent: true)
                container("${k}-neighbor") {
                        makeAddressPort(builder, k) // as if content of function is written here, yangbuilder reuse (not possible in yang)
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

builder.writeToFile("${builder.getYangName()}.yang")
//new File("${moduleName}.yang").write(builder.getText()) // another way how to write to file