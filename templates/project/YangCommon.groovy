//This is free software licensed under MIT License, see LICENSE file
//(https://bitbucket.org/bubbles.way/yangbuilder/src/LICENSE)

// This class contains reusable parts, it is not related to specific yang file
class YangCommon {

        static def buildHeader(builder, prefixName = null) {
                builder.yngbuild("/* This yang file was generated with groovy YangBuilder on ${new Date().toString()}", indent: true)
                builder.yngbuild('   see http://bitbucket.org/bubbles.way/yangbuilder */', indent: true)
                builder.yngbuild('') // new line

                if (prefixName) { // do not generate prefix for and namespace for submodules
                        builder.namespace "http://bitbucket.org/bubbles.way/yangbuilder"
                        builder.prefix prefixName
                        builder.yngbuild('')
                }

                builder.'import'('ietf-inet-types') {
                        prefix 'inet'
                }
                builder.yngbuild('')
        }

        static def buildAddressPort(builder, kind = null) { //this is example how function can be used by the builder, parameters can be used
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
}
