//This is free software licensed under MIT License, see LICENSE file
//(https://bitbucket.org/novakmi/yangbuilder/src/LICENSE)

// This class is related to ospf-module.yang file
class YangOspf {

        static def yangName = "ospf-module" // groovy makes automatically getYangName(), setYangName()

        def static ospf = { protocol ->
                module getYangName(), {
                        geninfo file: "${this.name}.groovy"
                        delegate << YangCommon.buildHeader.curry(protocol)
                        yngbuild "/* ${protocol} neighbor */", indent: true
                        container "${protocol}-neighbor", {
                                // as if content of closure/function is written here, yangbuilder reuse (not possible in yang)
                                delegate << YangCommon.buildAddressPort.curry(protocol)
                        }
                }
        }

        static def buildYang(builder) {
                builder << ospf.curry("ospf")
        }
}
