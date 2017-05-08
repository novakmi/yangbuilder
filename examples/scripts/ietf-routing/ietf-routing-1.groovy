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
                    enum_ "unreachable", {
                        description '''Discard the packet and notify the sender with an error
                                       message indicating that the destination host is
                                       unreachable.'''
                    }
                    enum_ "prohibit", {
                        description '''Discard the packet and notify the sender with an error
                                       message indicating that the communication is
                                       administratively prohibited.'''
                    }
                    enum_ "receive", description: "The packet will be received by the local system."
                }
                description "Options for special next hops."
            }
        }

        def simple_nexthop_case = { isState = false ->
            case_ "simple-next-hop", {
                description '''This case represents a simple next hop consisting of the
                                next-hop address and/or outgoing interface.
    
                                Modules for address families MUST augment this case with a
                                leaf containing a next-hop address of that address
                                family.'''
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
                            leaf "index", type: "string", {
                                description  '''A user-specified identifier utilized to uniquely
                                                reference the next-hop entry in the next-hop list.
                                                The value of this index has no semantic meaning
                                                 other than for referencing the entry.'''
                            }
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
                description: "Generic parameters of next hops in ${inText}.", {
                choice "next-hop-options", mandatory: true, {
                    description "Options for next hops in ${inText}." + '''

                                 It is expected that further cases will be added through
                                 augments from other modules''' +
                        "${isState ? ", e.g., for recursive\nnext hops" : ""}."
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
            leaf "active", type: "empty", {
                description '''Presence of this leaf indicates that the route is preferred
                               among all routes in the same RIB that have the same
                               destination prefix.'''
            }

            leaf "last-updated", type: "yang:date-and-time", {
                description '''Time stamp of the last modification of the route.  If the
                               route was never modified, it is the time when the route was
                               inserted into the RIB.'''
            }
        }

        cmt "State data", inline: false

        container "routing-state", config: false, description: "State data of the routing subsystem.", {
            uses "router-id", {
                description '''Global router ID.

                               It may be either configured or assigned algorithmically by
                               the implementation.'''
            }
            container "interfaces", description: "Network-layer interfaces used for routing.", {
                leaf_list "interface", type: "if:interface-state-ref", {
                    description '''Each entry is a reference to the name of a configured
                                   network-layer interface.'''
                }
            }

            container "control-plane-protocols", description: "Container for the list of routing protocol instances.", {
                list "control-plane-protocol", key: "type name", {
                    description '''State data of a control-plane protocol instance.

                                   An implementation MUST provide exactly one
                                   system-controlled instance of the 'direct'
                                   pseudo-protocol.  Instances of other control-plane
                                   protocols MAY be created by configuration.'''
                    leaf "type", {
                        type "identityref", base: "control-plane-protocol"
                        description "Type of the control-plane protocol."
                    }
                    leaf "name", type: "string", {
                        description '''The name of the control-plane protocol instance.

                                        For system-controlled instances this name is persistent,
                                        i.e., it SHOULD NOT change across reboots.'''
                    }
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
                    leaf "default-rib", if_feature: "multiple-ribs", type: "boolean", default: true, {
                        description '''This flag has the value of 'true' if and only if the RIB
                                        is the default RIB for the given address family.

                                         By default, control-plane protocols place their routes
                                        in the default RIBs.'''
                    }
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

        identity "ipv${afi}-unicast",  base: "rt:ipv${afi}",
            description: "This identity represents the IPv${afi} unicast address family."

        cmt "State data", inline: false

        leaf_prefix = { nam, descr ->
            leaf nam, type: "inet:ipv${afi}-prefix",
                description: "IPv${afi} ${descr}."
        }

        leaf_address = {nam, descr ->
            leaf nam, type: "inet:ipv${afi}-address",
                description: "IPv${afi} ${descr}."
        }
        
        augment "/rt:routing-state/rt:ribs/rt:rib/rt:routes/rt:route", {
            when "derived-from-or-self(../../rt:address-family, +" +
                "'v${afi}ur:ipv$afi-unicast')", splitOnPlus: true,
                description: "This augment is valid only for IPv${afi} unicast."

            description "This leaf augments an IPv${afi} unicast route."
            leaf_prefix "destination-prefix", "destination prefix"
       }

        augment "/rt:routing-state/rt:ribs/rt:rib/rt:routes/rt:route/+" +
            "rt:next-hop/rt:next-hop-options/rt:simple-next-hop", splitOnPlus: true, {
            when "derived-from-or-self(../../../rt:address-family, +" +
                "'v${afi}ur:ipv${afi}-unicast')", splitOnPlus: true,
                description: "This augment is valid only for IPv${afi} unicast."
            description "Augment 'simple-next-hop' case in IPv${afi} unicast routes."
            leaf_address "next-hop-address", "address of the next hop"
        }

        augment "/rt:routing-state/rt:ribs/rt:rib/rt:routes/rt:route/+" +
            "rt:next-hop/rt:next-hop-options/rt:next-hop-list/+" +
            "rt:next-hop-list/rt:next-hop", splitOnPlus: true, {
            when "derived-from-or-self(../../../../../rt:address-family, +" +
                "'v${afi}ur:ipv${afi}-unicast')", splitOnPlus: true,
                description: "This augment is valid only for IPv${afi} unicast."
            description "This leaf augments the 'next-hop-list' case of IPv${afi} unicast\n" +
                         "routes."
            leaf_address "address", "address of the next-hop"
        }

        augment "/rt:routing-state/rt:ribs/rt:rib/rt:active-route/rt:input", {
            when "derived-from-or-self(../rt:address-family, +" +
                "'v${afi}ur:ipv${afi}-unicast')", splitOnPlus: true,
                description: "This augment is valid only for IPv${afi} unicast RIBs."
            description "This augment adds the input parameter of the 'active-route'\n" +
                         "action."
            leaf_address "destination-address", "destination address"
        }

        augment "/rt:routing-state/rt:ribs/rt:rib/rt:active-route/+" +
            "rt:output/rt:route", splitOnPlus: true, {
            when "derived-from-or-self(../../rt:address-family, +" +
                "'v${afi}ur:ipv${afi}-unicast')", splitOnPlus: true,
                description: "This augment is valid only for IPv${afi} unicast."

            description "This augment adds the destination prefix to the reply of the\n" +
                        "'active-route' action."
            leaf_prefix "destination-prefix", "destination prefix"
        }

        def nextHopAugmentBody = { simple = true ->
            addLevel = ""
            nexthopElem = "simple-next-hop"
            if (!simple) {
                addLevel = "/../.."
                nexthopElem = "next-hop-list"
            }
            when "derived-from-or-self(../../..${addLevel}/rt:address-family, +" +
                "'v${afi}ur:ipv${afi}-unicast')", splitOnPlus: true,
                description: "This augment is valid only for IPv${afi} unicast."
            description "Augment '${nexthopElem}' case in the reply to the\n" +
                        "'active-route' action."
            leaf_address "next-hop-address", "address of the next hop"
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
                    leaf "destination-prefix", mandatory: true, type: "inet:ipv${afi}-prefix",
                        description: "IPv${afi} destination prefix."
                    leaf "description", type: "string",
                        description: "Textual description of the route."
                    container "next-hop", description: "Configuration of next-hop.", {
                        uses "rt:next-hop-content", {
                            augment "next-hop-options/simple-next-hop", {
                                description "Augment 'simple-next-hop' case in IPv${afi} static\n" +
                                             "routes."
                                leaf_address "next-hop-address", "address of the next hop"
                            }
                            augment "next-hop-options/next-hop-list/next-hop-list/+" +
                                "next-hop", splitOnPlus: true, {
                                description "Augment 'next-hop-list' case in IPv${afi} static\n" +
                                    "routes."
                                leaf_address "next-hop-address", "address of the next hop"
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
        belongs_to "ietf-ipv6-unicast-routing", prefix: "v6ur";
        import_ "ietf-inet-types", prefix: "inet"
        import_ "ietf-interfaces", prefix: "if"
        import_ "ietf-ip", prefix: "ip"

        delegate << org_contact

        description '''This YANG module augments the 'ietf-ip' module with
                    configuration and state data of IPv6 router advertisements.''' +
            commmonIetfDesc

        reference "RFC 4861: Neighbor Discovery for IP version 6 (IPv6)."
        delegate << revision

        cmt "State data", inline: false

        augment "/if:interfaces-state/if:interface/ip:ipv6", {
            description '''Augment interface state data with parameters of IPv6 router
                           advertisements.'''

            container "ipv6-router-advertisements",
                description: "Parameters of IPv6 Router Advertisements.", {
                leaf "send-advertisements", type: "boolean", {
                    description '''A flag indicating whether or not the router sends periodic
                                   Router Advertisements and responds to Router
                                   Solicitations.'''
                }
                leaf "max-rtr-adv-interval", {
                    type "uint16", range: "4..1800"
                    units "seconds"
                    description '''The maximum time allowed between sending unsolicited
                                   multicast Router Advertisements from the interface.'''
                }
                leaf "min-rtr-adv-interval", {
                    type "uint16", range: "3..1350"
                    units "seconds"
                    description '''The minimum time allowed between sending unsolicited
                                    multicast Router Advertisements from the interface.'''
                }
                leaf "managed-flag", type: "boolean", {
                    description '''The value that is placed in the 'Managed address
                                    configuration' flag field in the Router Advertisement.'''
                }
                leaf "other-config-flag", type: "boolean", {
                    description '''The value that is placed in the 'Other configuration' flag
                                   field in the Router Advertisement.'''
                }
                leaf "link-mtu", type: "uint32", {
                    description '''The value that is placed in MTU options sent by the
                                   router.  A value of zero indicates that no MTU options are
                                    sent.'''
                }
                leaf "reachable-time", {
                    type "uint32", range: "0..3600000";
                    units "milliseconds"
                    description '''The value that is placed in the Reachable Time field in
                                    the Router Advertisement messages sent by the router.  A
                                    value of zero means unspecified (by this router).'''
                }
                leaf "retrans-timer", type: "uint32", units: "milliseconds", {
                    description '''The value that is placed in the Retrans Timer field in the
                                    Router Advertisement messages sent by the router.  A value
                                    of zero means unspecified (by this router).'''
                }
                leaf "cur-hop-limit", type: "uint8", {
                    description '''The value that is placed in the Cur Hop Limit field in the
                                    Router Advertisement messages sent by the router.  A value
                                    of zero means unspecified (by this router).'''
                }
                leaf "default-lifetime", {
                    type "uint16", range: "0..9000"
                    units "seconds"
                    description '''The value that is placed in the Router Lifetime field of
                                   Router Advertisements sent from the interface, in seconds.
                                   A value of zero indicates that the router is not to be
                                   used as a default router.'''
                }
                container "prefix-list", {
                    description '''A list of prefixes that are placed in Prefix Information
                                   options in Router Advertisement messages sent from the
                                   interface.

                                    By default, these are all prefixes that the router
                                    advertises via routing protocols as being on-link for the
                                    interface from which the advertisement is sent.'''
                    list "prefix", key: "prefix-spec", description: "Advertised prefix entry and its parameters.", {
                        leaf "prefix-spec", type: "inet:ipv6-prefix", description: "IPv6 address prefix."
                        leaf "valid-lifetime", type: "uint32", units: "seconds", {
                            description '''The value that is placed in the Valid Lifetime in the
                                        Prefix Information option.  The designated value of
                                        all 1's (0xffffffff) represents infinity.
                
                                        An implementation SHOULD keep this value constant in
                                        consecutive advertisements except when it is
                                        explicitly changed in configuration.'''
                        }
                        leaf "on-link-flag", type: "boolean", {
                            description '''The value that is placed in the on-link flag ('L-bit')
                                       field in the Prefix Information option.'''
                        }
                        leaf "preferred-lifetime", type: "uint32", units: "seconds", {
                            description '''The value that is placed in the Preferred Lifetime in
                                        the Prefix Information option, in seconds.  The
                                        designated value of all 1's (0xffffffff) represents
                                        infinity.
                
                                            An implementation SHOULD keep this value constant in
                                        consecutive advertisements except when it is
                                        explicitly changed in configuration.'''
                        }
                        leaf "autonomous-flag", type: "boolean", {
                            description '''The value that is placed in the Autonomous Flag field
                                       in the Prefix Information option.'''
                        }
                    }
                }
            }
        }

        cmt " Configuration data", inline: false

        augment "/if:interfaces/if:interface/ip:ipv6", {

            def refRfc4861 = { elem ->
                reference '''RFC 4861: Neighbor Discovery for IP version 6 (IPv6) -
                                ''' + elem + "."
            }

            description '''Augment interface configuration with parameters of IPv6 router
                           advertisements.'''
            container "ipv6-router-advertisements", description: "Configuration of IPv6 Router Advertisements.", {
                leaf "send-advertisements", type: "boolean", default: false, {
                    description '''A flag indicating whether or not the router sends periodic
                                   Router Advertisements and responds to Router
                                   Solicitations.'''
                    refRfc4861 "AdvSendAdvertisements"
                }
                leaf "max-rtr-adv-interval", {
                    type "uint16", range: "4..1800"
                    units "seconds"
                    default_ "600"
                    description '''The maximum time allowed between sending unsolicited
                                    multicast Router Advertisements from the interface.'''
                    refRfc4861 "MaxRtrAdvInterval"
                }
                leaf "min-rtr-adv-interval", {
                    type "uint16", range: "3..1350"
                    units "seconds"
                    must ". <= 0.75 * ../max-rtr-adv-interval", {
                        description '''The value MUST NOT be greater than 75% of
                                        'max-rtr-adv-interval'.'''
                    }
                    description '''The minimum time allowed between sending unsolicited
                                   multicast Router Advertisements from the interface.
                
                                   The default value to be used operationally if this leaf is
                                   not configured is determined as follows:
                
                                   - if max-rtr-adv-interval >= 9 seconds, the default
                                     value is 0.33 * max-rtr-adv-interval;
                
                                   - otherwise, it is 0.75 * max-rtr-adv-interval.'''
                    refRfc4861 "MinRtrAdvInterval"
                }
                leaf "managed-flag", type: "boolean", default: false, {
                    description '''The value to be placed in the 'Managed address
                                    configuration' flag field in the Router Advertisement.'''
                    refRfc4861 "AdvManagedFlag"
                }
                leaf "other-config-flag", type: "boolean", default: false, {
                    description '''The value to be placed in the 'Other configuration' flag
                                    field in the Router Advertisement.'''
                    refRfc4861 "AdvOtherConfigFlag"
                }
                leaf "link-mtu", type: "uint32", default: 0, {
                    description '''The value to be placed in MTU options sent by the router.
                                    A value of zero indicates that no MTU options are sent.'''
                    refRfc4861 "AdvLinkMTU"
                }
                leaf "reachable-time", {
                    type "uint32", range: "0..3600000"
                    units "milliseconds"
                    default_ 0
                    description '''The value to be placed in the Reachable Time field in the
                                   Router Advertisement messages sent by the router.  A value
                                   of zero means unspecified (by this router).'''
                    refRfc4861 "AdvReachableTime"
                }
                leaf "retrans-timer", type: "uint32", units: "milliseconds", default: 0, {
                    description '''The value to be placed in the Retrans Timer field in the
                                    Router Advertisement messages sent by the router.  A value
                                    of zero means unspecified (by this router).'''
                    refRfc4861 "AdvRetransTimer"
                }
                leaf "cur-hop-limit", type: "uint8", {
                    description '''The value to be placed in the Cur Hop Limit field in the
                                    Router Advertisement messages sent by the router.  A value
                                    of zero means unspecified (by this router).
                
                                    If this parameter is not configured, the device SHOULD use
                                    the value specified in IANA Assigned Numbers that was in
                                    effect at the time of implementation.'''
                    reference '''RFC 4861: Neighbor Discovery for IP version 6 (IPv6) -
                                 AdvCurHopLimit.
            
                                 IANA: IP Parameters,
                                 http://www.iana.org/assignments/ip-parameters'''
                }
                leaf "default-lifetime", {
                    type "uint16", range: "0..9000"
                    units "seconds"
                    description '''The value to be placed in the Router Lifetime field of
                                    Router Advertisements sent from the interface, in seconds.
                                    It MUST be either zero or between max-rtr-adv-interval and
                                    9000 seconds.  A value of zero indicates that the router
                                    is not to be used as a default router.  These limits may
                                    be overridden by specific documents that describe how IPv6
                                    operates over different link layers.
                
                                    If this parameter is not configured, the device SHOULD use
                                    a value of 3 * max-rtr-adv-interval.'''
                    refRfc4861 "AdvDefaultLifeTime"
                }
                container "prefix-list", {
                    description '''Configuration of prefixes to be placed in Prefix
                                    Information options in Router Advertisement messages sent
                                    from the interface.
                
                                    Prefixes that are advertised by default but do not have
                                    their entries in the child 'prefix' list are advertised
                                    with the default values of all parameters.
                
                                    The link-local prefix SHOULD NOT be included in the list
                                    of advertised prefixes.'''
                    refRfc4861 "AdvPrefixList"
                    list "prefix", key: "prefix-spec", description: "Configuration of an advertised prefix entry.", {
                        leaf "prefix-spec", type: "inet:ipv6-prefix", description: "IPv6 address prefix."
                        choice "control-adv-prefixes", default: "advertise", {
                            description '''Either the prefix is explicitly removed from the
                                           set of advertised prefixes, or the parameters with
                                           which it is advertised are specified (default case).'''
                            leaf "no-advertise", type: "empty", {
                                description '''The prefix will not be advertised.

                                                This can be used for removing the prefix from the
                                                default set of advertised prefixes.'''
                            }
                            case_ "advertise", {
                                leaf "valid-lifetime", type: "uint32", units: "seconds", default: 2592000, {
                                    description '''The value to be placed in the Valid Lifetime in
                                                    the Prefix Information option.  The designated
                                                    value of all 1's (0xffffffff) represents
                                                    infinity.'''
                                    refRfc4861 "AdvValidLifetime"
                                }
                                leaf "on-link-flag", type: "boolean", default: true, {
                                    description '''The value to be placed in the on-link flag
                                                    ('L-bit') field in the Prefix Information
                                                    option.'''
                                    refRfc4861 "AdvOnLinkFlag"
                                }
                                leaf "preferred-lifetime", type: "uint32", units: "seconds", {
                                    must ". <= ../valid-lifetime", {
                                        description '''This value MUST NOT be greater than
                                                        valid-lifetime.'''
                                    }
                                    default_ 604800
                                    description '''The value to be placed in the Preferred Lifetime
                                                    in the Prefix Information option.  The designated
                                                    value of all 1's (0xffffffff) represents
                                                    infinity.'''
                                    refRfc4861 "AdvPreferredLifetime"
                                }
                                leaf "autonomous-flag", type: "boolean", default: true, {
                                    description '''The value to be placed in the Autonomous Flag
                                                   field in the Prefix Information option.'''
                                    refRfc4861 "AdvAutonomousFlag"
                                }
                            }
                        }
                    }
                }
            }
        }
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
