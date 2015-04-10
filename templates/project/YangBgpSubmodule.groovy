//This is free software licensed under MIT License, see LICENSE file
//(https://bitbucket.org/novakmi/yangbuilder/src/LICENSE)

// This class is related to bgp-submodule.yang file
class YangBgpSubmodule {

        static def yangName = "bgp-submodule" // groovy makes automatically getYangName(), setYangName()

        static def buildYang(builder) {
                builder.yangroot { //yangroot is used only because comment is above 'submodule'
                        geninfo file: "${this.name}.groovy"
                        yngbuild("/* Example of submodule */")
                        submodule(getName()) {
                                'belongs-to'(YangBgp.getName()) {
                                        prefix "bgp"
                                }
                                yngbuild('')

                                YangCommon.buildHeader(builder)

                                yngbuild("/* bgp neighbor */", indent: true)
                                grouping("BgpNextHop") {
                                        leaf("next-hop-address") { //output depends on parameters, not possible in yang
                                                type('inet:ip-address')
                                        }
                                }
                        }
                }
        }
}
