#!/usr/bin/env groovy

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
@Grab(group = 'org.bitbucket.novakmi', module = 'nodebuilder', version = '0.3.0')
@Grab(group = 'org.bitbucket.novakmi', module = 'yangbuilder', version = '0.1.0')

// This script template represents example of usage without any plugin
def builder = new org.bitbucket.novakmi.yangbuilder.YangBuilder(4) // create new builder, indent 4 (default is 2)

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

        'import'('ietf-inet-types') {
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
}

builder.writeToFile("${moduleName}.yang")
//new File("${moduleName}.yang").write(builder.getBuiltText()) // another way how to write to file