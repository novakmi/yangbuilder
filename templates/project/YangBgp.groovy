//This is free software licensed under MIT License, see LICENSE file
//(https://bitbucket.org/novakmi/yangbuilder/src/LICENSE)

// This class is related to bgp-module.yang file
class YangBgp {

        static def yangName = "bgp-module" // groovy makes automatically getYangName(), setYangName()

        static def buildYang(builder) {
                builder.module(getName()) {
                        geninfo file: "${this.name}.groovy"
                        YangCommon.buildHeader(builder, 'bgp')

                        include YangBgpSubmodule.getName()
                        yngbuild('')

                        yngbuild("/* bgp neighbor */", indent: true)
                        container("bgp-neighbor") {
                                YangCommon.buildAddressPort(builder, 'bgp') // as if content of function is written here, yangbuilder reuse (not possible in yang)
                        }
                        yngbuild('')
                        container('next-hop') {
                                uses 'BgpNextHop'
                        }
                }
        }
}
