#!/usr/bin/env groovy

import org.bitbucket.novakmi.yangbuilder.GroupingResolverPlugin

//This is free software licensed under MIT License, see LICENSE file
//(https://bitbucket.org/bubbles.way/yangbuilder/src/LICENSE)

//adoc-begin - documentation purpose comment
@GrabResolver(name = 'novakmirepo', root = 'https://github.com/novakmi/novakmirepo/raw/master/releases', m2compatible = true)
@Grab(group = 'org.bitbucket.novakmi', module = 'nodebuilder', version = '0.7.0')
@Grab(group = 'org.bitbucket.novakmi', module = 'yangbuilder', version = '0.5.0')

def resolver = new GroupingResolverPlugin()                                                           //<1>
def builder = new org.bitbucket.novakmi.yangbuilder.YangBuilder(2, resolver)                          //<2>

moduleName = "resolver-module"

builder.module(moduleName) {                                                                          //<3>
        namespace "http://novakmi.bitbucket.org/test"; // semicolon at the end can be preset (yang style)
        prefix moduleName // or semicolon can be missing (more groovy like style)
        yngbuild('') //yngbuild echoes value, yngbuild('') means new line

        grouping('grouping-test1') {
                container('container-a1') {
                        leaf('leaf-a') {
                                type('uint32')
                        }
                }
                leaf('leaf-b1') {
                        type('uint32')
                }
        }

        grouping('grouping-test2') {
                container('container-a') {
                        leaf('leaf-a') {
                                type('uint32')
                        }
                }
                uses('grouping-test1')
                leaf('leaf-b') {
                        type('uint32')
                }
        }

        container('container-main') {
                uses('grouping-test2')
        }
}

builder.writeToFile("${builder.getYangName()}.yang")                                               //<4>
def resolverNode = resolver.resolveGroupings(builder.getRootNode())                                //<5>
new File("${builder.getYangName()}-resolved.yang").write(builder.getNodeText(resolverNode))        //<6>



/* adoc-callout - documentation purpose comment
<1> create grouping resolver
<2> pass resolver as plugin to builder
<3> build yang file
<4> write yang file with groupings
<5> create new model with resolved groupings
<6> write yang file with resolved groupings
   adoc-end - documentation purpose comment*/