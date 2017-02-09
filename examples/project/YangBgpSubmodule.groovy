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

        def static yang = {
                yangroot { //yangroot is used only because comment is above 'submodule'
                        geninfo file: "${this.name}.groovy"
                        yngbuild "/* Example of submodule */"
                        submodule yangName, {
                                'belongs-to' YangBgp.yangName, {
                                        prefix "bgp"
                                }
                                yngbuild ''
                                delegate << YangCommon.buildHeader
                                makeGrouping(delegate) //delegate is builder (example of reuse through function)
                        }
                }
        }
}
