#!/usr/bin/env groovy

import org.bitbucket.novakmi.yangbuilder.CompactYangPlugin

/*
Copyright (c) 2012 Michal Novak (bubbles.way@gmail.com)
http://bitbucket.org/bubbles.way/yangbuilder

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/

//If you have Internet connection, use groovy Grab to get dependencies (may take some time for the first time to download jars)
//Run as ordinary groovy script with command 'groovy <scriptName>.groovy' (or as Linux executable script './<scriptName>.groovy')
//Update nodebuilder, yangbuilder version numbers as needed
@GrabResolver(name = 'bubbleswayrepo', root = 'https://github.com/bubblesway/bubbleswayrepo/raw/master/releases', m2compatible = true)
@Grab(group = 'org.bitbucket.novakmi', module = 'nodebuilder', version = '0.4.0')
@Grab(group = 'org.bitbucket.novakmi', module = 'yangbuilder', version = '0.2.0')

// This script template represents example of usage with Compact yang plugin (syntax is more different from yang, but more compact)
// create new builder, pass plugin or list of plugins as constructor parameter,
// default indent of 2 has to be specified if we pass plugin (or list of plugins) in constructor (other option is to use builder.registerPlugin(new CompactYangPlugin()))
def builder = new org.bitbucket.novakmi.yangbuilder.YangBuilder(2, new CompactYangPlugin())

//name of file to generate
moduleName = "example1-compact-module"   // do not use 'def' for script global variable

def makeAddressPort(builder, kind = null) { //this is example how function can be used by the builder, parameters can be used
        // in function all nodes have to be prefixed with 'builder.', except for child nodes
        builder.yngbuild("// IPv4 or IPv6 address", indent: true)
        builder.leaf("${kind ? kind + '-' : ''}address", type: 'inet:ip-address', description: "IPv4 or IPv6 address") //output depends on parameters, not possible in yang
        builder.yngbuild("// IP port", indent: true)
        builder.leaf("${kind ? kind + '-' : ''}port", type: 'uint16', description: "IP port")  //with compact plugin 'type' can be declared as param. of leaf. 'description' as param of element
}

builder.module(moduleName) {
        def makeGrouping = { // this is example how closure can be called be used by the builder
                grouping('addressPort') {
                        makeAddressPort(builder)
                }
        }

        yngbuild("/* This yang file was generated with groovy YangBuilder on ${new Date().toString()}", indent: true)
        yngbuild('   see http://bitbucket.org/bubbles.way/yangbuilder */', indent: true, nl: true)  // 'nl:true adds new line (you can also use nl:1, etc.)

        namespace "http://bitbucket.org/bubbles.way/yangbuilder"
        prefix("example1", nl: 1) // add new line

        'import'('ietf-inet-types', prefix: 'inet', nl: 1) //with compact yang 'prefix' can be added as attribute of import, add new line after ending '}' of import

        makeGrouping() // this behaves in the same way as if content of the closure is written here
        yngbuild('')

        yngbuild("/* neighbor */", indent: true)
        container('neighbor', nl: 1) {
                uses 'addressPort'  // yang grouping reuse
        }

        ['bgp', 'ospf', 'isis', 'rip'].each {k -> // create 3 containers in loop, not possible in yang
                yngbuild("/* ${k} neighbor */", indent: true)
                container("${k}-neighbor", nl: 1) {
                        makeAddressPort(builder, k) // as if content of function is written here, yangbuilder reuse (not possible in yang)
                }
        }

        list('neighbors', key: 'neighbor', description: 'List of neighbors') { // key leaf can be attribute of list statement
                leaf('neighbor', type: 'inet:ip-address', description: 'neighbor IP4 or IPv6 address')
                leaf('name', type: 'string', description: 'neighbor name')
        }
}

builder.writeToFile("${moduleName}.yang")
//new File("${moduleName}.yang").write(builder.getText()) // another way how to write to file