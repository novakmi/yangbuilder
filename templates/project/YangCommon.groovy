//This is free software licensed under MIT License, see LICENSE file
//(https://bitbucket.org/novakmi/yangbuilder/src/LICENSE)

// This class contains reusable parts, it is not related to specific yang file
class YangCommon {

        static def buildHeader = { prefixName = null ->
                yngbuild("/* This yang file was generated with groovy YangBuilder on ${new Date().toString()}", indent: true)
                yngbuild('   see http://bitbucket.org/novakmi/yangbuilder */', indent: true)
                yngbuild('') // new line

                if (prefixName) { // do not generate prefix for and namespace for submodules
                        namespace "http://bitbucket.org/novakmi/yangbuilder"
                        prefix prefixName
                        yngbuild('')
                }

                'import'('ietf-inet-types') {
                        prefix 'inet'
                }
                yngbuild('')
        }

        static def buildAddressPort = { kind = null -> //this is example how closure can be used, parameter has tu be curried
                yngbuild("// IPv4 or IPv6 address", indent: true)
                leaf("${kind ? kind + '-' : ''}address") { //output depends on parameters, not possible in yang
                        type('inet:ip-address')
                }
                yngbuild("// IP port", indent: true)
                leaf("${kind ? kind + '-' : ''}port") {
                        type('uint16')
                }
        }
}
