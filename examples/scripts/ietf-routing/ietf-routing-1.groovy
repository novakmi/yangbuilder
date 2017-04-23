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

def commmonIetfDesc = '''

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

def revision = {
    revision "2016-11-04", {
        description "Initial revision."
        reference "RFC 8022: A YANG Data Model for Routing Management"
    }
}

def ietf_routing_yang = {
    def name = scriptName
    module "$name-$gVer", {
        delegate << ietf_routing_header.curry(name)
        namespace "urn:ietf:params:xml:ns:yang:ietf-routing"
        prefix "rt"

        import_ "ietf-yang-types", prefix: "yang"
        import_ "ietf-interfaces", prefix: "if"

        delegate << org_contact

        description '''This YANG module defines essential components for the management
                    of a routing subsystem.''' + commmonIetfDesc

        delegate << revision

        cmt "Features", inline: false

        def featureIndicates = "This feature indicates that the server supports "
        def serverNotAdv = "Servers that do not advertise this feature "

        feature "multiple-ribs", {
            description featureIndicates + '''user-defined
               RIBs.

               ''' + serverNotAdv + '''SHOULD provide
               exactly one system-controlled RIB per supported address family
               and make it also the default RIB.  This RIB then appears as an
               entry of the list /routing-state/ribs/rib.'''
        }

        feature "router-id", {
            description featureIndicates + '''configuration
              of an explicit 32-bit router ID that is used by some routing
              protocols.

              ''' + serverNotAdv + '''set a router ID
              algorithmically, usually to one of the configured IPv4
              addresses.  However, this algorithm is implementation
              specific.'''
        }

        def identity = { idName, baseName, descr ->
            identity idName, {
                if (baseName) {
                    base baseName
                }
                description descr
            }
        }

        cmt "Identities", inline: false

        identity "address-family", null,
            '''Base identity from which identities describing address
               families are derived.'''

        [4, 6].each { afi ->
            identity "ipv${afi}", "address-family",
                "This identity represents IPv${afi} address family."
        }

        identity "control-plane-protocol", null,
            '''Base identity from which control-plane protocol identities are
               derived.'''

        identity "routing-protocol", "control-plane-protocol",
            '''Identity from which Layer 3 routing protocol identities are
               derived.'''

        identity "direct", "routing-protocol",
            '''Routing pseudo-protocol that provides routes to directly
               connected networks.'''

        identity "static", "routing-protocol", "Static routing pseudo-protocol."

        cmt "Type Definitions", inline: false

        typedef "route-preference", type: "uint32",
            description: "This type is used for route preferences."

        cmt "Groupings", inline: false

        grouping "address-family", {
            description '''This grouping provides a leaf identifying an address
                            family.'''
            leaf "address-family", {
                type "identityref", base: "address-family"
                mandatory true
                description "Address family."
            }
        }

        grouping "router-id", description: "This grouping provides router ID.", {
            leaf "router-id", type: "yang:dotted-quad", {
                description '''A 32-bit number in the form of a dotted quad that is used by
                                some routing protocols identifying a router.'''
                reference '''RFC 2328: OSPF Version 2.'''
            }
        }

        grouping "special-next-hop", {
            description '''This grouping provides a leaf with an enumeration of special
                           next hops.'''
            leaf "special-next-hop", {
                type "enumeration", {
                    enum_ "blackhole", description: "Silently discard the packet."
                    enum_ "unreachable",
                        description: '''Discard the packet and notify the sender with an error
                                       message indicating that the destination host is
                                       unreachable.'''
                    enum_ "prohibit",
                        description: '''Discard the packet and notify the sender with an error
                                       message indicating that the communication is
                                       administratively prohibited.'''
                    enum_ "receive", description: "The packet will be received by the local system."
                }
                description "Options for special next hops."
            }
        }

        def simple_nexthop_case = { isState = false ->
            case_ "simple-next-hop",
                description: '''This case represents a simple next hop consisting of the
                                     next-hop address and/or outgoing interface.

                                     Modules for address families MUST augment this case with a
                                     leaf containing a next-hop address of that address
                                     family.''', {
                leaf "outgoing-interface", type: "if:interface-${isState ? "state-" : ""}ref",
                    description: "Name of the outgoing interface."
            }
        }

        def nexthop_list_case = { isState = false ->
            case_ "next-hop-list", {
                container "next-hop-list", description: "Container for multiple next-hops.", {
                    list "next-hop", {
                        if (!isState) {
                            key "index"
                        }
                        description '''An entry of a next-hop list.

                            Modules for address families MUST augment this list
                            with a leaf containing a next-hop address of that
                            address family.'''
                        if (!isState) {
                            leaf "index", type: "string", description:
                                '''A user-specified identifier utilized to uniquely
                                reference the next-hop entry in the next-hop list.
                                The value of this index has no semantic meaning
                                other than for referencing the entry.'''
                        }
                        leaf "outgoing-interface", type: "if:interface-${isState ? "state-" : ""}ref",
                            description: "Name of the outgoing interface."
                    }
                }
            }
        }

        def next_hop_grouping = { isState = false ->
            def inText = isState ? "state data" : "static routes"
            grouping "next-hop-${isState ? "state-" : ""}content",
                description:
                    "Generic parameters of next hops in ${inText}.", {
                choice "next-hop-options", mandatory: true,
                    description: "Options for next hops in ${inText}." + '''

                                 It is expected that further cases will be added through
                                 augments from other modules''' +
                        "${isState ? ", e.g., for recursive\nnext hops" : ""}.", {
                    simple_nexthop_case(isState)
                    case_ "special-next-hop", uses: "special-next-hop"
                    nexthop_list_case(isState)
                }
            }
        }
        next_hop_grouping()
        next_hop_grouping(true)

        grouping "route-metadata", description: "Common route metadata.", {
            leaf "source-protocol", {
                type "identityref", base: "routing-protocol"
                mandatory true
                description '''Type of the routing protocol from which the route
                               originated.'''
            }
            leaf "active", type: "empty",
                description: '''Presence of this leaf indicates that the route is preferred
                                among all routes in the same RIB that have the same
                                destination prefix.'''

            leaf "last-updated", type: "yang:date-and-time",
                description: '''Time stamp of the last modification of the route.  If the
                                route was never modified, it is the time when the route was
                                inserted into the RIB.'''
        }

        cmt "State data", inline: false

        container "routing-state", config: false, description: "State data of the routing subsystem.", {
            uses "router-id", description: '''Global router ID.

                                              It may be either configured or assigned algorithmically by
                                              the implementation.'''
            container "interfaces", description: "Network-layer interfaces used for routing.", {
                leaf_list "interface", type: "if:interface-state-ref",
                    description: '''Each entry is a reference to the name of a configured
                                    network-layer interface.'''
            }

            container "control-plane-protocols", description: "Container for the list of routing protocol instances.", {
                list "control-plane-protocol", key: "type name",
                    description: '''State data of a control-plane protocol instance.

                                    An implementation MUST provide exactly one
                                    system-controlled instance of the 'direct'
                                    pseudo-protocol.  Instances of other control-plane
                                    protocols MAY be created by configuration.''', {
                    leaf "type", {
                        type "identityref", base: "control-plane-protocol"
                        description "Type of the control-plane protocol."
                    }
                    leaf "name", type: "string",
                        description: '''The name of the control-plane protocol instance.

                                        For system-controlled instances this name is persistent,
                                        i.e., it SHOULD NOT change across reboots.'''
                }

            }

            container "ribs", description: "Container for RIBs.", {
                list "rib", key: "name", min_elements: "1", {
                    description '''Each entry represents a RIB identified by the 'name' key.
                                    All routes in a RIB MUST belong to the same address
                                    family.

                                        An implementation SHOULD provide one system-controlled
                                    default RIB for each supported address family.'''
                    leaf "name", type: "string", description: "The name of the RIB."
                    uses "address-family"
                    leaf "default-rib", if_feature: "multiple-ribs", type: "boolean", default_: true,
                        description: '''This flag has the value of 'true' if and only if the RIB
                                        is the default RIB for the given address family.

                                            By default, control-plane protocols place their routes
                                        in the default RIBs.'''

                    container "routes", description: "Current content of the RIB.", {
                        list "route", {
                            description ''' A RIB route entry.  This data node MUST be augmented
                                    with information specific for routes of each address
                                    family.'''
                            leaf "route-preference", type: "route-preference", {
                                description '''This route attribute, also known as administrative
                                        distance, allows for selecting the preferred route
                                        among routes with the same destination prefix.  A
                                        smaller value means a more preferred route.'''
                            }
                            container "next-hop", description: "Route's next-hop attribute.",
                                uses: "next-hop-state-content"
                            uses "route-metadata"
                        }
                    }

                    action "active-route", {
                        description '''Return the active RIB route that is used for the
                                       destination address.

                                        Address-family-specific modules MUST augment input
                                        parameters with a leaf named 'destination-address'.'''
                        output {
                            container "route", {
                                description '''The active RIB route for the specified destination.

                                                If no route exists in the RIB for the destination
                                                address, no output is returned.

                                                Address-family-specific modules MUST augment this
                                                container with appropriate route contents.'''
                                container "next-hop", description: "Route's next-hop attribute.",
                                    uses: "next-hop-state-content"
                                uses "route-metadata"
                            }
                        }
                    }
                }
            }
        }

        cmt " Configuration Data", inline: false

        container "routing", description: "Configuration parameters for the routing subsystem.", {
            uses "router-id", if_feature: "router-id", {
                description '''Configuration of the global router ID.  Routing protocols
                                that use router ID can use this parameter or override it
                                with another value.'''
            }

            container "control-plane-protocols",
                description: "Configuration of control-plane protocol instances.", {
                list "control-plane-protocol", key: "type name", {
                    description '''Each entry contains configuration of a control-plane
                                protocol instance.'''
                    leaf "type", {
                        type "identityref", base: "control-plane-protocol"
                        description '''Type of the control-plane protocol - an identity derived
                                    from the 'control-plane-protocol' base identity.'''
                    }
                    leaf "name", type: "string", {
                        description '''An arbitrary name of the control-plane protocol
                                    instance.'''
                    }
                    leaf "description", type: "string", {
                        description '''Textual description of the control-plane protocol
                                   instance.'''
                    }
                    container "static-routes", {
                        when "derived-from-or-self(../type, 'rt:static')", {
                            description '''This container is only valid for the 'static' routing
                                        protocol.'''
                        }
                        description '''Configuration of the 'static' pseudo-protocol.

                                   Address-family-specific modules augment this node with
                                   their lists of routes.'''
                    }
                }
            }

            container "ribs", description: "Configuration of RIBs.", {
                list "rib", key: "name", {
                    description '''Each entry contains configuration for a RIB identified by
                               the 'name' key.

                               Entries having the same key as a system-controlled entry
                               of the list /routing-state/ribs/rib are used for
                               configuring parameters of that entry.  Other entries
                               define additional user-controlled RIBs.'''
                    leaf "name", type: "string", {
                        description '''The name of the RIB.

                                    For system-controlled entries, the value of this leaf
                                    must be the same as the name of the corresponding entry
                                    in state data.
                
                                        For user-controlled entries, an arbitrary name can be
                                    used.'''
                    }
                    uses "address-family", {
                        description '''Address family of the RIB.

                                   It is mandatory for user-controlled RIBs.  For
                                   system-controlled RIBs it can be omitted; otherwise, it
                                   must match the address family of the corresponding state
                                   entry.'''
                        refine "address-family", mandatory: false
                    }
                    leaf "description", type: "string", description: "Textual description of the RIB."
                }
            }
        }
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
            "configuration and state data for IPv${afi} unicast routing." +
            commmonIetfDesc

        delegate << revision

        cmt "Identities", inline: false

        identity "ipv${afi}-unicast", {
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
            "rt:next-hop-list/rt:next-hop", splitOnPlus: true, {
            when "derived-from-or-self(../../../../../rt:address-family, +" +
                "'v${afi}ur:ipv${afi}-unicast')", splitOnPlus: true, {
                description "This augment is valid only for IPv${afi} unicast."
            }

            description "This leaf augments the 'next-hop-list' case of IPv${afi} unicast\n" +
                "routes."

            leaf "address", {
                type "inet:ipv${afi}-address"
                description "IPv${afi} address of the next-hop."
            }
        }

        augment "/rt:routing-state/rt:ribs/rt:rib/rt:active-route/rt:input", {
            when "derived-from-or-self(../rt:address-family, +" +
                "'v${afi}ur:ipv${afi}-unicast')", splitOnPlus: true, {
                description "This augment is valid only for IPv${afi} unicast RIBs."
            }
            description "This augment adds the input parameter of the 'active-route'\n" +
                "action."
            leaf "destination-address", {
                type "inet:ipv${afi}-address"
                description "IPv${afi} destination address."
            }
        }

        augment "/rt:routing-state/rt:ribs/rt:rib/rt:active-route/+" +
            "rt:output/rt:route", splitOnPlus: true, {
            when "derived-from-or-self(../../rt:address-family, +" +
                "'v${afi}ur:ipv${afi}-unicast')", splitOnPlus: true, {
                description "This augment is valid only for IPv${afi} unicast."
            }
            description "This augment adds the destination prefix to the reply of the\n" +
                "'active-route' action."
            leaf "destination-prefix", {
                type "inet:ipv${afi}-prefix"
                description "IPv${afi} destination prefix."
            }
        }

        def nextHopAugmentBody = { simple = true ->
            addLevel = ""
            nexthopElem = "simple-next-hop"
            if (!simple) {
                addLevel = "/../.."
                nexthopElem = "next-hop-list"
            }
            when "derived-from-or-self(../../..${addLevel}/rt:address-family, +" +
                "'v${afi}ur:ipv${afi}-unicast')", splitOnPlus: true, {
                description "This augment is valid only for IPv${afi} unicast."
            }
            description "Augment '${nexthopElem}' case in the reply to the\n" +
                "'active-route' action."
            leaf "next-hop-address", {
                type "inet:ipv${afi}-address"
                description "IPv${afi} address of the next hop."
            }
        }

        augment "/rt:routing-state/rt:ribs/rt:rib/rt:active-route/+" +
            "rt:output/rt:route/rt:next-hop/rt:next-hop-options/+" +
            "rt:simple-next-hop", splitOnPlus: true, {
            nextHopAugmentBody()
        }

        augment "/rt:routing-state/rt:ribs/rt:rib/rt:active-route/+" +
            "rt:output/rt:route/rt:next-hop/rt:next-hop-options/+" +
            "rt:next-hop-list/rt:next-hop-list/rt:next-hop", splitOnPlus: true, {
            nextHopAugmentBody(false)
        }

        cmt "Configuration data", inline: false

        augment "/rt:routing/rt:control-plane-protocols/+" +
            "rt:control-plane-protocol/rt:static-routes", splitOnPlus: true, {
            description "This augment defines the configuration of the 'static'\n" +
                "pseudo-protocol with data specific to IPv${afi} unicast."
            container "ipv${afi}", {
                description '''Configuration of a 'static' pseudo-protocol instance
                               consists of a list of routes.'''
                list "route", key: "destination-prefix",
                    description: "A list of static routes.", {
                    leaf "destination-prefix", mandatory: true, {
                        type "inet:ipv${afi}-prefix"
                        description "IPv${afi} destination prefix."
                    }
                    leaf "description", type: "string",
                        description: "Textual description of the route."
                    container "next-hop", description: "Configuration of next-hop.", {
                        uses "rt:next-hop-content", {
                            augment "next-hop-options/simple-next-hop", {
                                description "Augment 'simple-next-hop' case in IPv${afi} static\n" +
                                    "routes."
                                leaf "next-hop-address", {
                                    type "inet:ipv${afi}-address"
                                    description "IPv${afi} address of the next hop."
                                }
                            }
                            augment "next-hop-options/next-hop-list/next-hop-list/+" +
                                "next-hop", splitOnPlus: true, {
                                description "Augment 'next-hop-list' case in IPv${afi} static\n" +
                                    "routes."
                                leaf "next-hop-address", {
                                    type "inet:ipv${afi}-address"
                                    description "IPv${afi} address of the next hop."
                                }
                            }
                        }
                    }
                }
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
