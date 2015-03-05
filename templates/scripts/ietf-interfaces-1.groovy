#!/usr/bin/env groovy

//This is free software licensed under MIT License, see LICENSE file
//(https://bitbucket.org/novakmi/yangbuilder/src/LICENSE)

import org.bitbucket.novakmi.yangbuilder.CompactYangPlugin

//Grab(group = 'org.bitbucket.novakmi', module = 'nodebuilder', version = '0.9.0')
//@Grab(group = 'org.bitbucket.novakmi', module = 'yangbuilder', version = '1.0.0')

def plugin = new CompactYangPlugin()
def builder = new org.bitbucket.novakmi.yangbuilder.YangBuilder(2, plugin)
plugin.declareCommonAliasesAndQuotes()

moduleName = "ietf-interfaces-1"

builder.module(moduleName) {
        geninfo file: "${moduleName}.groovy", time: true, cmt: "Example implementation of the RFC 7223 in the yangbuilder in the syntax most similar to Yang."

        yang_version 1, nlLevel: true; //nlLevel: true - for this indent level, add \n after each closing }
        namespace "urn:ietf:params:xml:ns:yang:ietf-interfaces";
        prefix "if"; //in yang builder - the value must be written as string (groovy syntax)

        //import is renamed to import_ (as it is groovy keyword)
        import_ "ietf-yang-types", prefix: "yang";
        //CompactYangPlugin child elements can be written as attributes (with ':' after keyword)

        // the string must be started on same line as element (to start on other line we would need to encompass it in brackets)
        organization "IETF NETMOD (NETCONF Data Modeling Language) Working Group";
        //note - semicolon is optional, we keep it only to resemble yang

        contact '''WG Web:   <http://tools.ietf.org/wg/netmod/>
                   WG List:  <mailto:netmod@ietf.org>

                   wG Chair: Thomas Nadeau
                   <mailto:tnadeau@lucidvision.com>

                   WG Chair: Juergen Schoenwaelder
                   <mailto:j.schoenwaelder@jacobs-university.de>

                   Editor:   Martin Bjorklund
                   <mailto:mbj@tail-f.com>''';

        //by default, the description will be "nicely" indented and formatted
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
        ''';

        revision "2014-05-08", description: "Initial revision.", // after comma you can continue with the child elements on the next line
                reference: "RFC 7223: A YANG Data Model for Interface Management";

        //just like in yangs, child elements can be also added to curly brackets block
        typedef "interface-ref", {
                // the value and chidl element block must be encompassed by round brackets (..) { or separated by comma , {
                // value for type (leafdref) must be typed as string
                type "leafref", path: "/if:interfaces/if:interface/if:name";
                description '''
                This type is used by data models that need to reference
                configured interfaces.
                ''';
        }

        typedef "interface-state-ref", {
                type "leafref", path: "/if:interfaces-state/if:interface/if:name";
                description '''
                This type is used by data models that need to reference
                the operationally present interfaces.
                ''';
        }

        cmt("Identities", inline: false); // this will expand to yang /* ... */ comment
        identity "interface-type", description: "Base identity from which specific interface types are derived.";

        cmt("Features", inline: false);
        feature "arbitrary-names", description: '''This feature indicates that the device allows user-controlled
                                                    interfaces to be named arbitrarily.''';

        feature "pre-provisioning", description: '''This feature indicates that the device supports
                                                    pre-provisioning of interface configuration, i.e., it is
                                                    possible to configure an interface whose physical interface
                                                    hardware is not present on the device.''';
        feature "if-mib", description: "This feature indicates that the device implements the IF-MIB.",
                reference: "RFC 2863: The Interfaces Group MIB";


        cmt("Configuration data nodes", inline: false);
        container "interfaces", description: "Interface configuration parameters.", {

                list "interface", key: "name", {  // save line, make key attribute

                        description '''The list of configured interfaces on the device.

                                            The operational state of an interface is available in the
                                            /interfaces-state/interface list.  If the configuration of a
                                            system-controlled interface cannot be used by the system
                                            (e.g., the interface hardware present does not match the
                                            interface type), then the configuration is not applied to
                                            the system-controlled interface shown in the
                                            /interfaces-state/interface list.  If the configuration
                                            of a user-controlled interface cannot be used by the system,
                                            the configured interface is not instantiated in the
                                            /interfaces-state/interface list.''', pnlLevel: true;
                        //pnlLevel: true - add \n before each element on the same level

                        leaf "name", type: "string", {
                                description '''The name of the interface.

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
                                                /interface-state/interface list.''';
                        }

                        leaf "description", type: "string", {
                                description '''A textual description of the interface.

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
                                               datastore.''';
                                reference "RFC 2863: The Interfaces Group MIB - ifAlias";
                        }

                        // shorter child elements added as attributes
                        leaf "type", mandatory: true, reference: "RFC 2863: The Interfaces Group MIB - ifType", {
                                type "identityref", base: "interface-type";

                                description '''"The type of the interface.

                                When an interface entry is created, a server MAY
                                initialize the type leaf with a valid value, e.g., if it
                                is possible to derive the type from the name of the
                                interface.

                                If a client tries to set the type of an interface to a
                                value that can never be used by the system, e.g., if the
                                type is not supported or if the type does not match the
                                name of the interface, the server MUST reject the request.
                                A NETCONF server MUST reply with an rpc-error with the
                                error-tag 'invalid-value' in this case.''';
                        }

                        leaf "enabled", type: "boolean", default: true, {
                                description '''This leaf contains the configured, desired state of the
                                               interface.

                                               Systems that implement the IF-MIB use the value of this
                                               leaf in the 'running' datastore to set
                                               IF-MIB.ifAdminStatus to 'up' or 'down' after an ifEntry
                                               has been initialized, as described in RFC 2863.

                                               Changes in this leaf in the 'running' datastore are
                                               reflected in ifAdminStatus, but if ifAdminStatus is
                                               changed over SNMP, this leaf is not affected.";
                                               reference
                                               "RFC 2863: The Interfaces Group MIB - ifAdminStatus''';
                        }

                        leaf "link-up-down-trap-enable", feature: "if-feature if-mib", {
                                type "enumeration", {
                                        enum_ "enabled", value: 1;  //use 'enum_' as 'enum' is groovy keyword
                                        enum_ "disabled", value: 2;
                                }
                                description '''Controls whether linkUp/linkDown SNMP notifications
                                               should be generated for this interface.

                                               If this node is not configured, the value 'enabled' is
                                               operationally used by the server for interfaces that do
                                               not operate on top of any other interface (i.e., there are
                                               no 'lower-layer-if' entries), and 'disabled' otherwise.''';
                                reference '''RFC 2863: The Interfaces Group MIB -
                                             ifLinkUpDownTrapEnable''';
                        }
                }
        }

        cmt("Operational state data nodes", inline: false)

        container "interfaces-state", config: false, description: "Data nodes for the operational state of interfaces.", {
                list "interface", key: "name", {
                        description '''The list of interfaces on the device.

                                        System-controlled interfaces created by the system are
                                        always present in this list, whether they are configured or
                                        not.''';

                        leaf "name", type: "string", reference: "RFC 2863: The Interfaces Group MIB - ifName", {
                                description '''The name of the interface.

                                A server implementation MAY map this leaf to the ifName
                                MIB object.  Such an implementation needs to use some
                                mechanism to handle the differences in size and characters
                                allowed between this leaf and ifName.  The definition of
                                such a mechanism is outside the scope of this document.''';
                        }
                        leaf "type", mandatory: true, description: "The type of the interface.", reference: "RFC 2863: The Interfaces Group MIB - ifType", {
                                type "identityref", base: "interface-type";
                        }
                        leaf "admin-status", "if-feature": "if-mib", mandatory: true, reference: "RFC 2863: The Interfaces Group MIB - ifAdminStatus", {

                                type "enumeration", {
                                        enum_ "up", value: 1, description: "Ready to pass packets.";
                                        enum_ "down", value: 2, description: "Not ready to pass packets and not in some test mode.";
                                        enum_ "testing", value: 3, description: "In some test mode.";
                                }
                                description '''The desired state of the interface.

                                                This leaf has the same read semantics as ifAdminStatus.''';
                        }
                }
        }
}

println builder.getText()
builder.writeToFile("${builder.getYangName()}.yang")
