//This is free software licensed under MIT License, see LICENSE file
//(https://bitbucket.org/novakmi/yangbuilder/src/LICENSE)

// This class is related to bgp-submodule.yang file
class YangBgpSubmodule {

        static def yangName = "bgp-submodule" // groovy makes automatically getYangName(), setYangName()

        static def makeGrouping(builder) {
                builder.yngbuild "/* bgp neighbor */", indent: true
                builder.grouping "BgpNextHop", {
                        leaf "next-hop-address", { //output depends on parameters, not possible in yang
                                type 'inet:ip-address'
                        }
                }
        }

        static def buildYang(builder) {
                builder.yangroot { //yangroot is used only because comment is above 'submodule'
                        geninfo file: "${this.name}.groovy"
                        yngbuild "/* Example of submodule */"
                        submodule getYangName(), {
                                'belongs-to' YangBgpSubmodule.getYangName(), {
                                        prefix "bgp"
                                }
                                yngbuild ''
                                delegate << YangCommon.buildHeader
                                makeGrouping(delegate)
                                // as if content of closure is written here, yangbuilder reuse (not possible in yang); `delegate` is same object as `builder`
                        }
                }
        }
}
