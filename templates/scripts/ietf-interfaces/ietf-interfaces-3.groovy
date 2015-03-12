#!/usr/bin/env groovy

//This is free software licensed under MIT License, see LICENSE file
//(https://bitbucket.org/novakmi/yangbuilder/src/LICENSE)

import org.bitbucket.novakmi.yangbuilder.CompactYangPlugin

//Grab(group = 'org.bitbucket.novakmi', module = 'nodebuilder', version = '0.9.0')
//@Grab(group = 'org.bitbucket.novakmi', module = 'yangbuilder', version = '1.0.0')

def plugin = new CompactYangPlugin()
def builder = new org.bitbucket.novakmi.yangbuilder.YangBuilder(2, plugin)
plugin.declareCommonAliasesAndQuotes()

moduleName = "ietf-interfaces-3"

def enume(el) { [val: "enumeration", elems: el.collect { ["enum", it] }] }


def makeConfiguration(builder) {
        builder.cmt "Configuration data nodes", inline: false  // note: in function each top element must be witten with "builder....."
        builder.container "interfaces", description: "Interface configuration parameters.", {

                // pnl_ attribute - add \n before attribute
                list "interface", key: "name", pnl_description:
                        '''The list of configured interfaces on the device.

                                        The operational state of an interface is available in the
                                        /interfaces-state/interface list.  If the configuration of a
                                        system-controlled interface cannot be used by the system
                                        (e.g., the interface hardware present does not match the
                                        interface type), then the configuration is not applied to
                                        the system-controlled interface shown in the
                                        /interfaces-state/interface list.  If the configuration
                                        of a user-controlled interface cannot be used by the system,
                                        the configured interface is not instantiated in the
                                        /interfaces-state/interface list.''', {

                        leaf "name", type: "string", pnlLevel: true, description:
                                '''The name of the interface.

                                    A device MAY restrict the allowed values for this leaf,
                                    possibly depending on the type of the interface.
                                    For system-controlled interfaces, this leaf is the
                                    device-specific name of the interface.  The 'config false'
                                    list /interfaces-state/interface contains the currently
                                    existing interfaces on the device.

                                    If a client tries to create configuration for a
                                    system-controlled interface that is not present in the
                                    /interfaces-state/interface list, the server MAY reject
                                    the request if the implementation does not support
                                    pre-provisioning of interfaces or if the name refers to
                                    an interface that can never exist in the system.  A
                                    NETCONF server MUST reply with an rpc-error with the
                                    error-tag 'invalid-value' in this case.

                                    If the device supports pre-provisioning of interface
                                    configuration, the 'pre-provisioning' feature is
                                    advertised.

                                    If the device allows arbitrarily named user-controlled
                                    interfaces, the 'arbitrary-names' feature is advertised.

                                     When a configured user-controlled interface is created by
                                     the system, it is instantiated with the same name in the
                                     /interface-state/interface list.'''

                        leaf "description", type: "string", reference: "RFC 2863: The Interfaces Group MIB - ifAlias",
                                description: '''A textual description of the interface.

                                               A server implementation MAY map this leaf to the ifAlias
                                               MIB object.  Such an implementation needs to use some
                                               mechanism to handle the differences in size and characters
                                               allowed between this leaf and ifAlias.  The definition of
                                               such a mechanism is outside the scope of this document.

                                               Since ifAlias is defined to be stored in non-volatile
                                               storage, the MIB implementation MUST map ifAlias to the
                                               value of 'description' in the persistently stored
                                               datastore.

                                               Specifically, if the device supports ':startup', when
                                               ifAlias is read the device MUST return the value of
                                               'description' in the 'startup' datastore, and when it is
                                               written, it MUST be written to the 'running' and 'startup\'
                                               datastores.  Note that it is up to the implementation to
                                               decide whether to modify this single leaf in 'startup' or
                                               perform an implicit copy-config from 'running' to
                                               'startup'.

                                               If the device does not support ':startup', ifAlias MUST
                                               be mapped to the 'description' leaf in the 'running'
                                               datastore.'''

                        // shorter child elements added as attributes
                        leaf "type", mandatory: true, reference: "RFC 2863: The Interfaces Group MIB - ifType",
                                type: [val: "identityref", base: "interface-type"],
                                description: '''The type of the interface.

                                When an interface entry is created, a server MAY
                                initialize the type leaf with a valid value, e.g., if it
                                is possible to derive the type from the name of the
                                interface.

                                If a client tries to set the type of an interface to a
                                value that can never be used by the system, e.g., if the
                                type is not supported or if the type does not match the
                                name of the interface, the server MUST reject the request.
                                A NETCONF server MUST reply with an rpc-error with the
                                error-tag 'invalid-value' in this case.'''

                        leaf "enabled", type: "boolean", default: true, reference: "RFC 2863: The Interfaces Group MIB - ifAdminStatus",
                                description: '''This leaf contains the configured, desired state of the
                                               interface.

                                               Systems that implement the IF-MIB use the value of this
                                               leaf in the 'running' datastore to set
                                               IF-MIB.ifAdminStatus to 'up' or 'down' after an ifEntry
                                               has been initialized, as described in RFC 2863.

                                               Changes in this leaf in the 'running' datastore are
                                               reflected in ifAdminStatus, but if ifAdminStatus is
                                               changed over SNMP, this leaf is not affected.'''

                        leaf "link-up-down-trap-enable", if_feature: "if-mib",
                                type: enume([[val: "enabled", value: 1], [val: "disabled", value: 2]]),
                                description: '''Controls whether linkUp/linkDown SNMP notifications
                                               should be generated for this interface.

                                               If this node is not configured, the value 'enabled' is
                                               operationally used by the server for interfaces that do
                                               not operate on top of any other interface (i.e., there are
                                               no 'lower-layer-if' entries), and 'disabled' otherwise.''',
                                reference: '''RFC 2863: The Interfaces Group MIB -
                                             ifLinkUpDownTrapEnable'''
                }
        }
}

def makeOperational(builder) {
        builder.cmt("Operational state data nodes", inline: false)
        builder.container "interfaces-state", config: false, description: "Data nodes for the operational state of interfaces.", {
                list "interface", key: "name", description: '''The list of interfaces on the device.

                                                                System-controlled interfaces created by the system are
                                                                always present in this list, whether they are configured or
                                                                not.''', {

                        leaf "name", type: "string", reference: "RFC 2863: The Interfaces Group MIB - ifName",
                                description: '''The name of the interface.

                                A server implementation MAY map this leaf to the ifName
                                MIB object.  Such an implementation needs to use some
                                mechanism to handle the differences in size and characters
                                allowed between this leaf and ifName.  The definition of
                                such a mechanism is outside the scope of this document.'''

                        leaf "type", mandatory: true, description: "The type of the interface.", reference: "RFC 2863: The Interfaces Group MIB - ifType",
                                type: [val: "identityref", base: "interface-type"]

                        leaf "admin-status", if_feature: "if-mib", mandatory: true, reference: "RFC 2863: The Interfaces Group MIB - ifAdminStatus",
                                type: enume([[val: "up", value: 1, description: "Ready to pass packets."],
                                             [val: "down", value: 2, description: "Not ready to pass packets and not in some test mode."],
                                             [val: "testing", value: 3, description: "In some test mode."]]),
                                description: '''The desired state of the interface.

                                                    This leaf has the same read semantics as ifAdminStatus.'''

                        leaf "oper-status", mandatory: true, reference: "RFC 2863: The Interfaces Group MIB - ifOperStatus",
                                type: enume([[val: "up", value: 1, description: "Ready to pass packets."],
                                             [val: "down", value: 2, description: "The interface does not pass any packets."],
                                             [val: "testing", value: 3, description: '''In some test mode.  No operational packets can
                                                                                   be passed.'''],
                                             [val: "unknown", value: 4, description: "Status cannot be determined for some reason."],
                                             [val: "dormant", value: 5, description: "Waiting for some external event."],
                                             [val: "not-present", value: 6, description: "Some component (typically hardware) is missing."],
                                             [val: "lower-layer-down", value: 7, description: "Down due to state of lower-layer interface(s)."]]),
                                description: '''The current operational state of the interface.

                                                This leaf has the same semantics as ifOperStatus.'''

                        leaf "last-change", type: "yang:date-and-time", reference: "RFC 2863: The Interfaces Group MIB - ifLastChange",
                                description: '''The time the interface entered its current operational
                                                state.  If the current state was entered prior to the
                                                last re-initialization of the local network management
                                                subsystem, then this node is not present.'''

                        leaf "if-index", if_feature: "if-mib", mandatory: true, reference: "RFC 2863: The Interfaces Group MIB - ifIndex",
                                type: [val: "int32", range: "1..2147483647"],
                                description: '''The ifIndex value for the ifEntry represented by this
                                               interface.'''

                        leaf "phys-address", type: "yang:phys-address", reference: "RFC 2863: The Interfaces Group MIB - ifPhysAddress",
                                description: '''The interface's address at its protocol sub-layer.  For
                                               example, for an 802.x interface, this object normally
                                               contains a Media Access Control (MAC) address.  The
                                               interface's media-specific modules must define the bit
                                               and byte ordering and the format of the value of this
                                               object.  For interfaces that do not have such an address
                                               (e.g., a serial line), this node is not present.'''

                        leaf_list "higher-layer-if", type: "interface-state-ref", reference: "RFC 2863: The Interfaces Group MIB - ifStackTable",
                                description: '''A list of references to interfaces layered on top of this
                                               interface.'''

                        leaf_list "lower-layer-if", type: "interface-state-ref", reference: "RFC 2863: The Interfaces Group MIB - ifStackTable",
                                description: '''A list of references to interfaces layered underneath this
                                               interface.'''

                        leaf "speed", type: "yang:gauge64", units: "bits/second", reference: "RFC 2863: The Interfaces Group MIB - ifSpeed, ifHighSpeed",
                                description: '''An estimate of the interface's current bandwidth in bits
                                               per second.  For interfaces that do not vary in
                                               bandwidth or for those where no accurate estimation can
                                               be made, this node should contain the nominal bandwidth.
                                               For interfaces that have no concept of bandwidth, this
                                               node is not present.'''

                        container "statistics", description: "A collection of interface-related statistics objects.", {

                                leaf "discontinuity-time", type: "yang:date-and-time", mandatory: true,
                                        description: '''The time on the most recent occasion at which any one or
                                                        more of this interface's counters suffered a
                                                        discontinuity.  If no such discontinuities have occurred
                                                        since the last re-initialization of the local management
                                                        subsystem, then this node contains the time the local
                                                        management subsystem re-initialized itself.'''

                                def stat_leaf = { name, yang_type, ref, descr ->
                                        leaf name, type: "yang:${yang_type}", reference: "RFC 2863: The Interfaces Group MIB - ${ref}",
                                                description: descr + '''

                                                        Discontinuities in the value of this counter can occur
                                                        at re-initialization of the management system, and at
                                                        other times as indicated by the value of
                                                        'discontinuity-time'.'''
                                }

                                stat_leaf "in-octets", "counter64", "ifHCInOctets",
                                        '''The total number of octets received on the interface,
                                           including framing characters.'''

                                stat_leaf "in-unicast-pkts", "counter64", "ifHCInUcastPkts",
                                        '''The number of packets, delivered by this sub-layer to a
                                             higher (sub-)layer, that were not addressed to a
                                             multicast or broadcast address at this sub-layer.'''

                                stat_leaf "in-broadcast-pkts", "counter64", "ifHCInBroadcastPkts",
                                        '''The number of packets, delivered by this sub-layer to a
                                            higher (sub-)layer, that were addressed to a broadcast
                                            address at this sub-layer.'''

                                stat_leaf "in-multicast-pkts", "counter64", "ifHCInMulticastPkts",
                                        '''The number of packets, delivered by this sub-layer to a
                                           higher (sub-)layer, that were addressed to a multicast
                                           address at this sub-layer.  For a MAC-layer protocol,
                                           this includes both Group and Functional addresses.'''

                                stat_leaf "in-discards", "counter32", "ifInDiscards",
                                        '''The number of inbound packets that were chosen to be
                                           discarded even though no errors had been detected to
                                           prevent their being deliverable to a higher-layer
                                           protocol.  One possible reason for discarding such a
                                           packet could be to free up buffer space.'''

                                stat_leaf "in-errors", "counter32", "ifInErrors",
                                        '''For packet-oriented interfaces, the number of inbound
                                           packets that contained errors preventing them from being
                                           deliverable to a higher-layer protocol.  For character-
                                           oriented or fixed-length interfaces, the number of
                                           inbound transmission units that contained errors
                                           preventing them from being deliverable to a higher-layer
                                           protocol.'''

                                stat_leaf "in-unknown-protos", "counter32", "ifInUnknownProtos",
                                        '''For packet-oriented interfaces, the number of packets
                                           received via the interface that were discarded because
                                           of an unknown or unsupported protocol.  For
                                           character-oriented or fixed-length interfaces that
                                           support protocol multiplexing, the number of
                                           transmission units received via the interface that were
                                           discarded because of an unknown or unsupported protocol.
                                           For any interface that does not support protocol
                                           multiplexing, this counter is not present.'''

                                stat_leaf "out-octets", "counter64", "ifHCOutOctets",
                                        '''The total number of octets transmitted out of the
                                           interface, including framing characters.'''

                                stat_leaf "out-unicast-pkts", "counter64", "ifHCOutUcastPkts",
                                        '''The total number of packets that higher-level protocols
                                           requested be transmitted, and that were not addressed
                                           to a multicast or broadcast address at this sub-layer,
                                           including those that were discarded or not sent.'''

                                stat_leaf "out-broadcast-pkts", "counter64", "ifHCOutBroadcastPkts",
                                        '''The total number of packets that higher-level protocols
                                            requested be transmitted, and that were addressed to a
                                            broadcast address at this sub-layer, including those
                                            that were discarded or not sent.'''

                                stat_leaf "out-multicast-pkts", "counter64", "ifHCOutMulticastPkts",
                                        '''The total number of packets that higher-level protocols
                                           requested be transmitted, and that were addressed to a
                                           multicast address at this sub-layer, including those
                                           that were discarded or not sent.  For a MAC-layer
                                           protocol, this includes both Group and Functional
                                           addresses.'''

                                stat_leaf "out-discards", "counter32", "ifOutDiscards",
                                        '''The number of outbound packets that were chosen to be
                                           discarded even though no errors had been detected to
                                           prevent their being transmitted.  One possible reason
                                           for discarding such a packet could be to free up buffer
                                           space.'''

                                stat_leaf "out-errors", "counter32", "ifOutErrors",
                                        '''For packet-oriented interfaces, the number of outbound
                                           packets that could not be transmitted because of errors.
                                           For character-oriented or fixed-length interfaces, the
                                           number of outbound transmission units that could not be
                                           transmitted because of errors.'''
                        }
                }
        }
}


builder.module(moduleName) {
        geninfo file: "${moduleName}.groovy", time: true,
                cmt: '''
                        Example implementation of the RFC 7223 in the yangbuilder in the syntax similar to the Yang, with minimal curly brackets.
                        See https://tools.ietf.org/html/rfc7223'''

        //pnl:true - add one \n before this element, nlLevel: true - for this indent level, add \n after each closing }
        yang_version 1, pnl: true, nlLevel: true // semicolons are not needed

        namespace "urn:ietf:params:xml:ns:yang:ietf-interfaces"
        prefix "if"

        import_ "ietf-yang-types", prefix: "yang"

        def header = {
                organization "IETF NETMOD (NETCONF Data Modeling Language) Working Group"

                contact '''WG Web:   <http://tools.ietf.org/wg/netmod/>
                   WG List:  <mailto:netmod@ietf.org>

                   wG Chair: Thomas Nadeau
                             <mailto:tnadeau@lucidvision.com>

                   WG Chair: Juergen Schoenwaelder
                             <mailto:j.schoenwaelder@jacobs-university.de>

                   Editor:   Martin Bjorklund
                             <mailto:mbj@tail-f.com>'''

                description '''
        This module contains a collection of YANG definitions for
        managing network interfaces.

        Copyright (c) 2014 IETF Trust and the persons identified as
        authors of the code.  All rights reserved.

                Redistribution and use in source and binary forms, with or
        without modification, is permitted pursuant to, and subject
        to the license terms contained in, the Simplified BSD License
        set forth in Section 4.c of the IETF Trust's Legal Provisions
        Relating to IETF Documents
        (http://trustee.ietf.org/license-info).

        This version of this YANG module is part of RFC 7223; see
        the RFC itself for full legal notices.
        '''

                revision "2014-05-08", description: "Initial revision.", reference: "RFC 7223: A YANG Data Model for Interface Management"

                // use closure for quick reuse
                def lfref_typedef = { v, p, d = "configured" ->
                        typedef v, type: [val: "leafref", path: p], description:
                                '''This type is used by data models that need to reference
                ''' + "${d} interfaces."
                }
                lfref_typedef("interface-ref", "/if:interfaces/if:interface/if:name") // call with brackets
                lfref_typedef "interface-state-ref", "/if:interfaces-state/if:interface/if:name", "the operationally present"
                //or without brackets

                cmt("Identities", inline: false)
                identity "interface-type", description: "Base identity from which specific interface types are derived."

                cmt("Features", inline: false)

                def dev_feature = { name, descr, ref = null ->
                        feature name, description: "This feature indicates that the device ${descr}", {
                                if (ref) {
                                        reference ref
                                }
                        }
                }

                dev_feature "arbitrary-names", '''allows user-controlled
                                          interfaces to be named arbitrarily.'''

                dev_feature "pre-provisioning", '''supports
                                      pre-provisioning of interface configuration, i.e., it is
                                      possible to configure an interface whose physical interface
                                      hardware is not present on the device.'''

                dev_feature "if-mib", "implements the IF-MIB.", "RFC 2863: The Interfaces Group MIB"
        }

        header() //block of code can be placed into closure
        this.makeConfiguration(builder) // or even seprate function (Note: this. not needed)
        this.makeOperational(builder)
}

println builder.getText()
builder.writeToFile("${builder.getYangName()}.yang")
