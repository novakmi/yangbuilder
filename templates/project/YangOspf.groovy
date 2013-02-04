//This is free software licensed under MIT License, see LICENSE file
//(https://bitbucket.org/novakmi/yangbuilder/src/LICENSE)

// This class is related to ospf-module.yang file
class YangOspf {

        static def name = "ospf-module" // groovy makes automatically getName(), setName()

        static def buildYang(builder) {
                def protocol = 'ospf'
                builder.module(getName()) {
                        YangCommon.buildHeader(builder, protocol)

                        yngbuild("/* ${protocol} neighbor */", indent: true)
                        container("${protocol}-neighbor") {
                                YangCommon.buildAddressPort(builder, protocol) // as if content of function is written here, yangbuilder reuse (not possible in yang)
                        }
                }
        }
}
