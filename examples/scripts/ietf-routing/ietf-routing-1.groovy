#!/usr/bin/env groovy

//This is free software licensed under MIT License, see LICENSE file
//(https://bitbucket.org/novakmi/yangbuilder/src/LICENSE)

import org.bitbucket.novakmi.yangbuilder.CompactYangPlugin

//@Grab(group = 'org.bitbucket.novakmi', module = 'nodebuilder', version = '1.0.0')
//@Grab(group = 'org.bitbucket.novakmi', module = 'yangbuilder', version = '1.2.0')

def plugin = new CompactYangPlugin()
def builder = new org.bitbucket.novakmi.yangbuilder.YangBuilder(2, plugin, [autoNl: true])
plugin.declareCommonAliasesAndQuotes()

scriptName = "ietf-routing"
gVer = 1
moduleName = scriptName

def ietf_routing_header = { name ->
    geninfo file: "$scriptName-${gVer}.groovy", time: true,
        cmt: '''Example implementation of the RFC 8022 in the yangbuilder
               in the syntax most similar to the Yang.
               See https://tools.ietf.org/html/rfc8022'''
    yang_version "1.1", nlLevel: true
}

def org_contact = { name ->
    organization "IETF NETMOD (NETCONF Data Modeling Language) Working Group"

    contact '''WG Web:   <https://datatracker.ietf.org/wg/netmod/>
               WG List:  <mailto:netmod@ietf.org>

               WG Chair: Lou Berger
                         <mailto:lberger@labn.net>

               WG Chair: Kent Watsen
                         <mailto:kwatsen@juniper.net>
                              
               Editor:   Ladislav Lhotka
                         <mailto:lhotka@nic.cz>                              

               Editor:   Acee Lindem
                         <mailto:acee@cisco.com>'''
}

def ietf_routing_yang = {
    def name = scriptName
    module "$name-$gVer", {
        delegate << ietf_routing_header.curry(name)
    }
}

def ietf_ipvx_unicast_routing = { afi ->
    def name = "ietf-ipv${afi}-unicast-routing"
    module "$name-$gVer", {
        delegate << ietf_routing_header.curry(name)
        namespace "urn:ietf:params:xml:ns:yang:ietf-ipv$afi-unicast-routing"
        prefix "v${afi}ur"

        import_ "ietf-routing", prefix: "rt"

        import_ "ietf-inet-types", prefix: "inet"

        if (afi == 6) {
            "include" "ietf-ipv6-router-advertisements",
                "revision-date": "2016-11-04"
        }

        delegate << org_contact

        description '''
         This YANG module augments the 'ietf-routing' module with basic\n''' +
            "configuration and state data for IPv${afi} unicast routing." + '''

         Copyright (c) 2016 IETF Trust and the persons identified as
         authors of the code.  All rights reserved.
    
         Redistribution and use in source and binary forms, with or
         without modification, is permitted pursuant to, and subject to
         the license terms contained in, the Simplified BSD License set
         forth in Section 4.c of the IETF Trust's Legal Provisions
         Relating to IETF Documents
         (http://trustee.ietf.org/license-info).
    
         The key words 'MUST', 'MUST NOT', 'REQUIRED', 'SHALL', 'SHALL
         NOT', 'SHOULD', 'SHOULD NOT', 'RECOMMENDED', 'MAY', and
         'OPTIONAL' in the module text are to be interpreted as described
         in RFC 2119.
    
         This version of this YANG module is part of RFC 8022;
         see the RFC itself for full legal notices.'''

        revision "2016-11-04", {
            description "Initial revision."
            reference "RFC 8022: A YANG Data Model for Routing Management"
        }

        cmt "Identities", inline: false

        identity "ipv$afi-unicast", {
            base "rt:ipv${afi}"
            description "This identity represents the IPv${afi} unicast address family."
        }

        cmt "State data", inline: false

        augment "/rt:routing-state/rt:ribs/rt:rib/rt:routes/rt:route", {
            when "derived-from-or-self(../../rt:address-family, +" +
                "'v${afi}ur:ipv$afi-unicast')", splitOnPlus: true, {
                description "This augment is valid only for IPv${afi} unicast."
            }
            description "This leaf augments an IPv${afi} unicast route."
            leaf "destination-prefix", {
                type "inet:ipv${afi}-prefix"
                description "IPv${afi} destination prefix."
            }
        }

        augment "/rt:routing-state/rt:ribs/rt:rib/rt:routes/rt:route/+" +
            "rt:next-hop/rt:next-hop-options/rt:simple-next-hop", splitOnPlus: true, {
            when "derived-from-or-self(../../../rt:address-family, +" +
                "'v${afi}ur:ipv${afi}-unicast')", splitOnPlus: true, {
                description "This augment is valid only for IPv${afi} unicast."
            }
            description "Augment 'simple-next-hop' case in IPv${afi} unicast routes."
            leaf "next-hop-address", {
                type "inet:ipv${afi}-address"
                description "IPv${afi} address of the next hop."
            }
        }

        augment "/rt:routing-state/rt:ribs/rt:rib/rt:routes/rt:route/+" +
            "rt:next-hop/rt:next-hop-options/rt:next-hop-list/+" +
            "rt:next-hop-list/rt:next-hop", splitOnPlus: true,{
            when "derived-from-or-self(../../../../../rt:address-family, +" +
                "'v${afi}ur:ipv${afi}-unicast')", splitOnPlus: true,{
                description "This augment is valid only for IPv${afi} unicast."
            }

            description "This leaf augments the 'next-hop-list' case of IPv${afi} unicast\n" +
                "routes."

            leaf "address", {
                type "inet:ipv${afi}-address"
                description "IPv${afi} address of the next-hop."
            }
        }
    }
}

def ietf_ipv6_router_advertisements = {
    def name = "ietf-ipv6-router-advertisements"
    submodule "$name-$gVer", {
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
