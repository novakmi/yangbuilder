#!/usr/bin/env groovy

//This is free software licensed under MIT License, see LICENSE file
//(https://bitbucket.org/novakmi/yangbuilder/src/LICENSE)

import org.bitbucket.novakmi.yangbuilder.CompactYangPlugin

//Grab(group = 'org.bitbucket.novakmi', module = 'nodebuilder', version = '0.9.0')
//@Grab(group = 'org.bitbucket.novakmi', module = 'yangbuilder', version = '1.0.0')

def plugin = new CompactYangPlugin()
def builder = new org.bitbucket.novakmi.yangbuilder.YangBuilder(2, plugin)
plugin.declareCommonAliasesAndQuotes()

moduleName = "ietf-interfaces"

builder.module(moduleName) {
        geninfo file: "${moduleName}.groovy", time: true, cmt: "Example implementation of the RFC 7223 in the yangbuilder."

        yang_version 1, nlLevel: true
        namespace "urn:ietf:params:xml:ns:yang:ietf-interfaces"
        prefix "if"

        import_ "ietf-yang-types", prefix: "yang"

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

        revision "2014-05-08", description: "Initial revision.",
                reference: "RFC 7223: A YANG Data Model for Interface Management"

        typedef "interface-ref", type: [val: "leafref", path: "/if:interfaces/if:interface/if:name"],
                description: '''
                This type is used by data models that need to reference
                configured interfaces.
                '''

        typedef "interface-state-ref", type: [val: "leafref", path: "/if:interfaces-state/if:interface/if:name"],
                description: '''
                This type is used by data models that need to reference
                the operationally present interfaces.
                '''

        identity "interface-type", description: "Base identity from which specific interface types are derived."

        [
                [val: "arbitrary-names", descr: "This feature indicates that the device allows user-controlled interfaces to be named arbitrarily."],
                [val: "pre-provisioning", descr: "This feature indicates that the device allows user-controlled interfaces to be named arbitrarily."],
                [val: "if-mib", ref: "RFC 2863: The Interfaces Group MIB", descr: "This feature indicates that the device allows user-controlled interfaces to be named arbitrarily."],
        ].each { f ->
                def map = [description: f.descr]
                if (f.ref) map['reference'] = f.ref
                map['nl'] = 1
                feature f.val, map
        }
        container "interfaces", description: "Interface configuration parameters.", {
                list "interface", key: "name", {
                        description      '''The list of configured interfaces on the device.

                                            The operational state of an interface is available in the
                                            /interfaces-state/interface list.  If the configuration of a
                                            system-controlled interface cannot be used by the system
                                            (e.g., the interface hardware present does not match the
                                            interface type), then the configuration is not applied to
                                            the system-controlled interface shown in the
                                            /interfaces-state/interface list.  If the configuration
                                            of a user-controlled interface cannot be used by the system,
                                            the configured interface is not instantiated in the
                                            /interfaces-state/interface list.'''

                        leaf "name", type: "string", reference: "RFC 2863: The Interfaces Group MIB - ifName", {
                                description     '''The name of the interface.

                                                   A server implementation MAY map this leaf to the ifName
                                                   MIB object.  Such an implementation needs to use some
                                                   mechanism to handle the differences in size and characters
                                                   allowed between this leaf and ifName.  The definition of
                                                   such a mechanism is outside the scope of this document.'''
                        }
                }
        }


}

println builder.getText()
builder.writeToFile("${builder.getYangName()}.yang")
