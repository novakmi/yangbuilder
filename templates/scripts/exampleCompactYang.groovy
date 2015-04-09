#!/usr/bin/env groovy

//This is free software licensed under MIT License, see LICENSE file
//(https://bitbucket.org/novakmi/yangbuilder/src/LICENSE)

import org.bitbucket.novakmi.yangbuilder.CompactYangPlugin

//If you have Internet connection, use groovy Grab to get dependencies (may take some time for the first time to download jars)
//Run as ordinary groovy script with command 'groovy <scriptName>.groovy' (or as Linux executable script './<scriptName>.groovy')
//Update nodebuilder, yangbuilder version numbers as needed
//adoc-begin - documentation purpose comment
@Grab(group = 'org.bitbucket.novakmi', module = 'nodebuilder', version = '0.9.0')
@Grab(group = 'org.bitbucket.novakmi', module = 'yangbuilder', version = '1.0.0')

// This script template represents example of usage with Compact yang plugin
// (syntax is more different from yang, but more compact)
// create new builder, pass plugin or list of plugins as constructor parameter,
// default indent of 2 has to be specified if we pass plugin (or list of plugins) in constructor
// (other option is to use builder.registerPlugin(new CompactYangPlugin()))
def builder = new org.bitbucket.novakmi.yangbuilder.YangBuilder(2, new CompactYangPlugin())                        //<1>

//name of file to generate
moduleName = "example-compact-module"   // do not use 'def' for script global variable

//this is example how function can be used by the builder, parameters can be used
def makeAddressPort(builder, kind = null) {
        // in function all nodes have to be prefixed with 'builder.', except for child nodes
        builder.yngbuild("// IPv4 or IPv6 address", indent: true)
        //output depends on parameters, not possible in yang
        builder.leaf("${kind ? kind + '-' : ''}address", type: 'inet:ip-address', description: "IPv4 or IPv6 address")
        builder.yngbuild("// IP port", indent: true)
        //with compact plugin 'type' can be declared as param. of leaf. 'description' as param of element          //<2>
        builder.leaf("${kind ? kind + '-' : ''}port", type: 'uint16', description: "IP port")
}

//module's prefix and namespace as attributes '_nl' means new line
builder.module(moduleName, pnl_namespace:'http://bitbucket.org/novakmi/yangbuilder', prefix_nl: 'example') {      //<3>
        def makeGrouping = { // this is example how closure can be called be used by the builder
                grouping('addressPort') {
                        makeAddressPort(builder)
                }
        }

        yngbuild("/* This yang file was generated with groovy YangBuilder on ${new Date().toString()}",
                indent: true)
        // 'nl:true adds new line (you can also use nl:1, etc.)                                                    //<4>
        yngbuild('   see http://bitbucket.org/novakmi/yangbuilder */', indent: true, nl: true)

        //with compact yang 'prefix' can be added as attribute of import,
        // nl:1 means add new line after ending '}' of import
        'import'('ietf-inet-types', prefix: 'inet', nl: 1)                                                         //<5>

        makeGrouping() // this behaves in the same way as if content of the closure is written here
        yngbuild('')

        yngbuild("/* neighbor */", indent: true)
        container('neighbor', pnl: 1, nl: 1) { // pnl - prefix new line - new line before node processing          //<6>
                uses 'addressPort'  // yang grouping reuse
        }

        ['bgp', 'ospf', 'isis', 'rip'].each {k -> // create 4 containers in loop, not possible in yang
                yngbuild("/* ${k} neighbor */", indent: true)
                container("${k}-neighbor", description: "${k}-neighbor container", nl: 1) {
                        // as if content of function is written here, yangbuilder reuse (not possible in yang)
                        makeAddressPort(builder, k)
                }
        }

        // key leaf can be attribute of list statement
        list('neighbors', key: 'neighbor', description: 'List of neighbors') {                                    //<7>
                leaf('neighbor', type: 'inet:ip-address', description: 'neighbor IP4 or IPv6 address')
                leaf('name', type: 'string', description: 'neighbor name')
        }
}

builder.writeToFile("${builder.getYangName()}.yang")
/* adoc-callout - documentation purpose comment
<1> The plugin is registered with the +builder+ in constructor or with method +registerPlugin+. When registered
    in constructor, it is passed as second attribute after +indent level+ value. It is possible to pass list of
    plugins to register several plugins at once.
<2> +type+ and +description+ can be added as +leaf+ attribute
<3>  +namespace+ and +prefix+ can be added as +module+ attribute.
     +pnl_namespace+ means +\n+ before a +namespace+ element,
     +prefix_nl+ means +\n+ after a +prefix+ element
<4> if +nl+ attribute evaluates to +true+, it  means +\n+ after element is added (no need for +yngbuild('')+)
<5> +nl+ can be shortened/replaced  with +prefix_nl+, e.g. +'import'('ietf-inet-types', prefix_nl: 'inet')+
<6> +nl+ attribute evaluating to +true+ means +\n+ before element (no need for +yngbuild('')+)
<7> +key+ and +description+ can be added as +list+ attributes
   adoc-end - documentation purpose comment*/