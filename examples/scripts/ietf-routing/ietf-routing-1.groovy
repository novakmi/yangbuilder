#!/usr/bin/env groovy

//This is free software licensed under MIT License, see LICENSE file
//(https://bitbucket.org/novakmi/yangbuilder/src/LICENSE)

import org.bitbucket.novakmi.yangbuilder.CompactYangPlugin

@Grab(group = 'org.bitbucket.novakmi', module = 'nodebuilder', version = '1.0.0')
@Grab(group = 'org.bitbucket.novakmi', module = 'yangbuilder', version = '1.2.0')

def plugin = new CompactYangPlugin()
def builder = new org.bitbucket.novakmi.yangbuilder.YangBuilder(2, plugin)
plugin.declareCommonAliasesAndQuotes()

scriptName = "ietf-routing-1"
moduleName = scriptName

def ietf_routing_header = { name ->
    geninfo file: "${name}.groovy", time: true,
        cmt:'''Example implementation of the RFC 8022 in the yangbuilder
               in the syntax most similar to the Yang.
               See https://tools.ietf.org/html/rfc8022'''
    yang_version "1.1", nlLevel: true
}

def ietf_routing_yang = {
    def name = scriptName
    module name, {
        delegate << ietf_routing_header.curry(name)
    }
}

def ietf_ipvx_unicast_routing = { afi->
    def name = "ietf-ipv${afi}-unicast-routing-1"
    module name, {
        delegate << ietf_routing_header.curry(name)
    }
}

def ietf_ipv6_router_advertisements = {
    def name = "ietf-ipv6-router-advertisements-1"
    submodule  name, {
        delegate << ietf_routing_header.curry(name)
    }
}

[
    ietf_routing_yang,
    ietf_ipvx_unicast_routing.curry(4),
    ietf_ipvx_unicast_routing.curry(6),
    ietf_ipv6_router_advertisements,
].each {
    builder.reset()
    builder << it
    builder.writeToFile("${builder.getYangName()}.yang")
}





