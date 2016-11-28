#!/usr/bin/env groovy

//This is free software licensed under MIT License, see LICENSE file
//(https://bitbucket.org/novakmi/yangbuilder/src/LICENSE)

import org.bitbucket.novakmi.yangbuilder.CompactYangPlugin

@Grab(group = 'org.bitbucket.novakmi', module = 'nodebuilder', version = '1.0.0')
@Grab(group = 'org.bitbucket.novakmi', module = 'yangbuilder', version = '1.1.0')

plugin = new CompactYangPlugin()
builder = new org.bitbucket.novakmi.yangbuilder.YangBuilder(2, plugin)
plugin.declareCommonAliasesAndQuotes()

moduleName = "ietf-isis-1"


builder.module(moduleName) {
    geninfo file: "${moduleName}.groovy", time: true,
        cmt: '''Example implementation of the Internet-Draft for the ISIS routing protocol in the yangbuilder
                with the syntax most similar to the Yang.
                See https://github.com/igp-yang/isis-yang/blob/master/ietf-isis.yang/ for original file'''
    namespace "urn:ietf:params:xml:ns:yang:ietf-isis", nlLevel: true;

    prefix "isis";

    import_ "ietf-routing", {
        prefix "rt";
    }

    import_ "ietf-inet-types", {
        prefix "inet";
    }

    import_ "ietf-yang-types", {
        prefix "yang";
    }

    organization "IETF ISIS Working Group";

    contact'''WG List:	&lt;mailto:isis-wg@ietf.org&gt;

        Editor:		Stephane Litkowski
        &lt;mailto:stephane.litkowski@orange.com&gt;

        Derek Yeung
        &lt;mailto:myeung@cisco.com&gt;
        Acee Lindem
        &lt;mailto:acee@cisco.com&gt;
        Jeffrey Zhang
        &lt;mailto:zzhang@juniper.net&gt;
        Ladislav Lhotka
        &lt;mailto:llhotka@nic.cz&gt;
        Yi Yang
        &lt;mailto:yiya@cisco.com&gt;
        Dean Bogdanovic
        &lt;mailto:deanb@juniper.net&gt;
        Kiran Agrahara Sreenivasa
        &lt;mailto:kkoushik@brocade.com&gt;
        Yingzhen Qu
        &lt;mailto:yiqu@cisco.com&gt;''';

    description '''The YANG module defines a generic configuration model for
                        ISIS common across all of the vendor implementations.''';

    revision "2014-12-15", {
        description '''
                    * Adding IPFRR
                    * Adding igp-ldp sync
                    * Adding segment routing
                    * Adding instance reference to operational states.
                    ''';
        reference '""';
    }
    revision "2014-10-24", {
        description \
        '''
            * Change hello-padding to container
            * Change bfd to container
            * Make BFD a feature
            * Creates mpls-te container and put router-id
            inside
            * Remove GR helper disable and timers
            ''';
        reference "draft-ietf-isis-yang-isis-cfg-01";
    }
    revision "2014-10-21", {
        description \
        '''
            * Interface metric move from af container to interface
            container
            * Hello-padding on interface moved to hello-padding-disable
            with empty type
            * three-way-handshake removed
            * route preference changed to a choice
            * csnp-authentication/psnp-authentication merged
            to authentication container
            * lsp-gen-interval-exp-delay removed
            * Added overload-max-metric feature
            * overload-max-metric is in a separate container
            ''';
        reference '""';
    }

    revision "2014-10-07", {
        description \
        '''
            * Removed spf parameters (should be part of
                vendor specific extensions.
                * Removed hello parameters at global level.
                * Interface configuration uses a string rather
                than a reference. This permits to map to some
                vendor specific configuration.
                ''';
        reference "draft-ietf-isis-yang-isis-00";
    }
    revision "2014-09-26", {
        description \
        '''
            * Add BFD support
            * remove max-elements to max-area-addresses
            ''';
        reference '""';
    }
    revision "2014-09-11", {
        description \
        '''
            * Add level parameter to ispf and spf delay
            * Add LSP generation as a feature
            * Make lsp-refresh a feature
            * Change parameter container to list
            ''';
        reference '""';
    }
    revision "2014-09-05", {
        description " Rewrite of the global hierarchy.";
        reference '""';
    }
    revision "2014-08-06", {
        description \
        '''
            * isis-state renamed to isis.
            * Add GR support
            * Add meshgroup support
            * Add CLNS support
            * Add 64bits tags
            * Add notifications to be aligned with MIB4444
            * Add packet-counters, interface-counters, system-counters
            states
            * Add 3-way handshake support
            * Rename isis-adjacency-updown to adjacency-change
            * Add notification for LSP reception
            * Use feature for reference BW
            * Add lsp-retransmit-interval on interfaces
            * Rename lsp-interval to lsp-pacing-interval
            * Add ispf support as feature
            * Add spf delay support as feature (2step & exp backoff)
            * Add maximum-area-addresses
            * Add default-metric
            ''';
        reference "RFC XXXX: YANG Data Model for ISIS Protocol";
    }
    revision "2014-06-25", {
        description \
            '''
            * isis-cfg renamed to isis.
            * Add precisions on authentication-keys in description
            ''';
        reference "draft-litkowski-isis-yang-isis-01";
    }

    revision "2014-06-20", {
        description '''
            * isis-op renamed to isis-state.
                * Multiple instances under ISIS are removed.
            * interface-cfg grouping removed and content
            is directly included in container isis.
                * TLVxx renamed with human-readable name in isis-database.
                TLV reference are putted in description.
                * Reference to core routing module were fixed.
                * Namespace fixed.
            * Add simple-iso-address type.
                * area-id and system-id in ISIS container are merged to
            nsap-address.
                * Add isis-system-id type.
                * Add isis-lsp-id type.
                * Add remaining-lifetime leaf in isis-database.
                * Add TLV2 (is-neighbor) in isis-database.
                * Renamed some container name for consistency
            reason ('isis-' prefixed).
                * Add new identities isis-cfg and isis-state.
                * Add descriptions.
            * Add notification isis-adjacency-updown.
                * Add RPC clear-isis-adjacency and clear-isis-database.
                ''';
        reference "draft-litkowski-isis-yang-isis-00";
    }

    revision "2014-06-11", {
        description "Initial revision.";
        reference "draft-litkowski-netmod-isis-cfg-00";
    }
    identity "isis", {
        base "rt:routing-protocol";
        description "Identity for the ISIS routing protocol.";
    }

    identity "isis-adjacency-change", {
        description '''Identity for the ISIS routing protocol
            adjacency state.''';
    }

    identity "clear-isis-database", {
        description '''Identity for the ISIS routing protocol
            database reset action.''';
    }

    identity "clear-isis-adjacency", {
        description '''Identity for the ISIS routing protocol
            adjacency reset action.''';
    }

    feature "igp-ldp-sync", {
        description "Support of RFC5443.";
    }
    feature "lfa", {
        description "Support of Loop Free Alternates.";
    }
    feature "remote-lfa", {
        description "Support of remote Loop Free Alternates.";
    }
    feature "segment-routing", {
        description "Support of Segment Routing.";
    }
    feature "bfd", {
        description "Support of BFD.";
    }
    feature "overload-max-metric", {
        description \
        '''Support of overload by setting
            all links to max metric.''';
    }
    feature "prefix-tag", {
        description \
        "Add 32bit tag to prefixes";
    }
    feature "prefix-tag64", {
        description \
        "Add 64bit tag to prefixes";
    }
    feature "reference-bandwidth", {
        description \
        "Use a reference bandwidth to compute metric.";
    }
    feature "ipv4-router-id", {
        description \
        "Support of IPv4 router ID configuration under ISIS.";
    }

    feature "ipv6-router-id", {
        description \
        "Support of IPv6 router ID configuration under ISIS.";
    }

    feature "multi-topology", {
        description \
        "Multitopology routing support.";
    }
    feature "nlpid-control", {
        description \
        '''This feature controls the advertisement
            of support NLPID within ISIS configuration.''';
    }
    feature "graceful-restart", {
        description \
        "Graceful restart support as per RFC5306.";
    }

    feature "lsp-refresh", {
        description \
        "Configuration of LSP refresh interval.";
    }

    feature "maximum-area-addresses", {
        description \
        "Support of maximum-area-addresses config.";
    }

    typedef "instance-state-ref", {
        type "leafref", {
            path "/rt:routing-state/rt:routing-instance/rt:routing-protocols/rt:routing-protocol/rt:name";
        }
        description \
        '''This type is used for leaves that reference state data of
            an ISIS protocol instance.''';
    }

    typedef "admin-state", {
       type "enumeration", {
            enum_ "up", {
                description \
                "Up state";
            }
            enum_ "down", {
                description \
                "Down state";
            }
        }
        description \
        "Administrative state of a component.";
    }
    typedef "oper-state", {
       type "enumeration", {
            enum_ "up", {
                description \
                "Up state";
            }
            enum_ "down", {
                description \
                "Down state";
            }
        }
        description \
        "Operational state of a component.";
    }
    typedef "circuit-id", {
        type "uint8";
        description \
        '''This type defines the circuit ID
            associated with an interface.''';
    }

    typedef "extended-circuit-id", {
        type "uint32";
        description \
        '''This type defines the extended circuit ID
            associated with an interface.''';
    }

    typedef "interface-type", {
       type "enumeration", {
            enum_ "broadcast", {
                description '''Broadcast interface type.
                    Would result in DIS election.''';
            }
            enum_ "point-to-point", {
                description \
                "Point to point interface type.";
            }
        }
        description \
        '''This type defines the type of adjacency
            to be established on the interface.
            This is affecting the type of hello
            message that would be used.''';

    }
    typedef "authentication-type", {
       type "enumeration", {
            enum_ "none", {
                description "No authentication used.";
            }
            enum_ "plaintext", {
                description "Plain text password used.";
            }
            enum_ "message-digest", {
                description "MD5 digest used.";
            }
        }
        description \
        "This type defines available authentication types.";
    }

    typedef "level", {
       type "enumeration", {
            enum_ "level-1", {
                description \
                "This enum describes L1 only capability.";
            }
            enum_ "level-2", {
                description \
                "This enum describes L2 only capability.";
            }
            enum_ "level-all", {
                description \
                "This enum describes both levels capability.";
            }
        }
        default_ "level-all";
        description \
        "This type defines ISIS level of an object.";

    }

    typedef "level-number", {
        type "uint8", {
            range "1..2";
        }
        description \
        "This type defines a current ISIS level.";
    }

    typedef "lsp-id", {
        type "string", {
            pattern "[0-9A-Fa-f],{4}\\.[0-9A-Fa-f],{4}\\.[0-9A-Fa-f],{4}\\.[0-9][0-9]-[0-9][0-9]";
        }
        description \
        '''This type defines ISIS LSP ID using pattern,
            system id looks like : 0143.0438.AeF0.02-01''';
    }


    typedef "area-address", {
        type "string", {
            pattern "[0-9A-Fa-f],{2}\\.([0-9A-Fa-f],{4}\\.),{0,3}";
        }
        description \
        "This type defines the area address.";
    }

    typedef "snpa", {
        type "string", {
            length "0..20";
        }
        description \
        "This type defines Subnetwork Point of Attachement format.";

    }

    typedef "system-id", {
        type "string", {
            pattern "[0-9A-Fa-f],{4}\\.[0-9A-Fa-f],{4}\\.[0-9A-Fa-f],{4}\\.00";
        }
        description \
        '''This type defines ISIS system id using pattern,
            system id looks like : 0143.0438.AeF0.00''';
    }

    typedef "wide-metric", {
        type "uint32", {
            range "0..16777215";
        }
        description \
        '''This type defines wide style format
            of ISIS metric.''';
    }

    typedef "std-metric", {
        type "uint8", {
            range "0..63";
        }
        description \
        '''This type defines old style format
            of ISIS metric.''';
    }

    typedef "mesh-group-state", {
       type "enumeration", {
            enum_ "meshInactive", {
                description \
                "Interface is not part of a mesh group.";
            }
            enum_ "meshSet", {
                description \
                "Interface is part of a mesh group.";
            }
            enum_ "meshBlocked", {
                description \
                "LSPs must not be flooded over that interface.";
            }
        }
        description \
        "This type describes meshgroup state of an interface";
    }

    grouping "notification-instance-hdr", {
        description \
        '''This group describes common instance specific
            data for notifications.''';
        leaf "instance-name", {
            type "string";
            description \
            "Describes the name of the ISIS instance.";
        }
        leaf "instance-level", {
            type "level";
            description \
            "Describes the ISIS level of the instance.";
        }
    }

    grouping "notification-interface-hdr", {
        description \
        '''This group describes common interface specific
            data for notifications.''';
        leaf "interface-name", {
            type "string";
            description \
            "Describes the name of the ISIS interface.";
        }
        leaf "interface-level", {
            type "level";
            description \
            "Describes the ISIS level of the interface.";
        }
        leaf "extended-circuit-id", {
            type "extended-circuit-id";
            description \
            "Describes the extended circuit-id of the interface.";
        }
    }

    grouping "route-content", {
        description \
        "This group add isis-specific route properties.";
        leaf "metric", {
            type "uint32";
            description \
            "This leaf describes ISIS metric of a route.";
        }
        leaf_list "tag", {
            type "uint64";
            description \
            '''This leaf describes list of tags associated
                with the route. The leaf describes both
                32bits and 64bits tags.''';
        }
        leaf "route-type", {
           type "enumeration", {
                enum_ "l2-up-internal", {
                    description '''Level 2 internal route
                        and not leaked to a lower level''';
                }
                enum_ "l1-up-internal", {
                    description '''Level 1 internal route
                        and not leaked to a lower level''';
                }
                enum_ "l2-up-external", {
                    description '''Level 2 external route
                        and not leaked to a lower level''';
                }
                enum_ "l1-up-external", {
                    description '''Level 1 external route
                        and not leaked to a lower level''';
                }
                enum_ "l2-down-internal", {
                    description '''Level 2 internal route
                        and leaked to a lower level''';
                }
                enum_ "l1-down-internal", {
                    description '''Level 1 internal route
                        and leaked to a lower level''';
                }
                enum_ "l2-down-external", {
                    description '''Level 2 external route
                        and leaked to a lower level''';
                }
                enum_ "l1-down-external", {
                    description '''Level 1 external route
                        and leaked to a lower level''';
                }
            }
            description \
            "This leaf describes the type of ISIS route.";
        }
    }

    augment "/rt:routing-state/rt:ribs/rt:rib/rt:routes/rt:route", {
        when "rt:source-protocol = 'isis:isis'", {
            description "ISIS-specific route attributes.";
        }
        uses "route-content";
        description \
        '''This augments route object in RIB with ISIS-specific
            attributes.''';
    }

    augment "/rt:active-route/rt:output/rt:route", {
        uses "route-content";
        description "ISIS-specific route attributes.";
    }


    grouping "prefix-ipv4-std", {
        description \
        '''This group defines attributes of an
            IPv4 standard prefix.''';
        leaf "up-down", {
            type "boolean";
            description \
            "This leaf expresses the value of up/down bit.";
        }
        leaf "i-e", {
            type "boolean";
            description \
            "This leaf expresses the value of I/E bit.";
        }
        leaf "ip-prefix", {
            type "inet:ipv4-address";
            description \
            "This leaf describes the IPv4 prefix";
        }
        leaf "prefix-len", {
            type "uint8";
            description \
            "This leaf describes the IPv4 prefix len in bits";
        }
        leaf "default-metric", {
            type "std-metric";
            description \
            "This leaf describes the ISIS default metric value";
        }
        container "delay-metric", {
            leaf "metric", {
                type "std-metric";
                description \
                "This leaf describes the ISIS delay metric value";
            }
            leaf "supported", {
                type "boolean";
                default_ "false";
                description \
                "This leaf describes if the metric is supported.";
            }

            description \
            "This container defines the ISIS delay metric.";
        }
        container "expense-metric", {
            leaf "metric", {
                type "std-metric";
                description \
                "This leaf describes the ISIS expense metric value";
            }
            leaf "supported", {
                type "boolean";
                default_ "false";
                description \
                "This leaf describes if the metric is supported.";
            }
            description \
            "This container defines the ISIS expense metric.";
        }
        container "error-metric", {
            leaf "metric", {
                type "std-metric";
                description \
                "This leaf describes the ISIS error metric value";
            }
            leaf "supported", {
                type "boolean";
                default_ "false";
                description \
                "This leaf describes if the metric is supported.";
            }

            description \
            "This container defines the ISIS error metric.";
        }
    }

    grouping "prefix-ipv4-extended", {
        description \
        '''This group defines attributes of an
            IPv4 extended prefix.''';
        leaf "up-down", {
            type "boolean";
            description \
            "This leaf expresses the value of up/down bit.";
        }
        leaf "ip-prefix", {
            type "inet:ipv4-address";
            description \
            "This leaf describes the IPv4 prefix";
        }
        leaf "prefix-len", {
            type "uint8";
            description \
            "This leaf describes the IPv4 prefix len in bits";
        }

        leaf "metric", {
            type "wide-metric";
            description \
            "This leaf describes the ISIS metric value";
        }
        leaf_list "tag", {
            type "uint32";
            description \
            '''This leaf describes a list of tags associated with
                the prefix.''';
        }
        leaf_list "tag64", {
            type "uint64";
            description \
            '''This leaf describes a list of 64-bit tags associated with
                the prefix.''';
        }
    }

    grouping "prefix-ipv6-extended", {
        description \
        '''This group defines attributes of an
            IPv6 prefix.''';
        leaf "up-down", {
            type "boolean";
            description \
            "This leaf expresses the value of up/down bit.";
        }
        leaf "ip-prefix", {
            type "inet:ipv6-address";
            description \
            "This leaf describes the IPv6 prefix";
        }
        leaf "prefix-len", {
            type "uint8";
            description \
            "This leaf describes the IPv4 prefix len in bits";
        }

        leaf "metric", {
            type "wide-metric";
            description \
            "This leaf describes the ISIS metric value";
        }
        leaf_list "tag", {
            type "uint32";
            description \
            '''This leaf describes a list of tags associated with
                the prefix.''';
        }
        leaf_list "tag64", {
            type "uint64";
            description \
            '''This leaf describes a list of 64-bit tags associated with
                the prefix.''';
        }
    }

    grouping "neighbor-extended", {
        description \
        '''This group defines attributes of an
            ISIS extended neighbor.''';
        leaf "neighbor-id", {
            type "system-id";
            description \
            "This leaf describes the system-id of the neighbor.";
        }
        leaf "metric", {
            type "wide-metric";
            description \
            "This leaf describes the ISIS metric value";
        }
    }

    grouping "neighbor", {
        description \
        '''This group defines attributes of an
            ISIS standard neighbor.''';
        leaf "neighbor-id", {
            type "system-id";
            description \
            "This leaf describes the system-id of the neighbor.";
        }
        leaf "i-e", {
            type "boolean";
            description \
            "This leaf expresses the value of I/E bit.";
        }
        leaf "default-metric", {
            type "std-metric";
            description \
            "This leaf describes the ISIS default metric value";
        }
        container "delay-metric", {
            leaf "metric", {
                type "std-metric";
                description \
                "This leaf describes the ISIS delay metric value";
            }
            leaf "supported", {
                type "boolean";
                default_ "false";
                description \
                "This leaf describes if the metric is supported.";
            }
            description \
            "This container defines the ISIS delay metric.";
        }
        container "expense-metric", {
            leaf "metric", {
                type "std-metric";
                description \
                "This leaf describes the ISIS delay expense value";
            }
            leaf "supported", {
                type "boolean";
                default_ "false";
                description \
                "This leaf describes if the metric is supported.";
            }
            description \
            "This container defines the ISIS expense metric.";
        }
        container "error-metric", {
            leaf "metric", {
                type "std-metric";
                description \
                "This leaf describes the ISIS error metric value";
            }
            leaf "supported", {
                type "boolean";
                default_ "false";
                description \
                "This leaf describes if the metric is supported.";
            }
            description \
            "This container defines the ISIS error metric.";
        }
    }

    grouping "database", {
        description \
        '''This group defines attributes of an
            ISIS database (Link State DB).''';
        leaf "lsp-id", {
            type "lsp-id";
            description \
            "This leaf describes the LSP ID of the LSP.";
        }
        leaf "checksum", {
            type "uint16";
            description \
            "This leaf describes the checksum of the LSP.";
        }
        leaf "remaining-lifetime", {
            type "uint16";
            units "seconds";
            description \
            '''This leaf describes the remaining lifetime
                in seconds before the LSP expiration.''';
        }
        leaf "sequence", {
            type "uint32";
            description \
            "This leaf describes the sequence number of the LSP.";
        }
        leaf "attributes", {
            type "bits", {
                bit "PARTITIONNED", {
                    description \
                    '''If set, the originator supports partition
                        repair.''';
                }
                bit "ATTACHED-ERROR", {
                    description \
                    '''If set, the originator is attached to
                        another area using the referred metric.''';
                }
                bit "ATTACHED-EXPENSE", {
                    description \
                    '''If set, the originator is attached to
                        another area using the referred metric.''';
                }
                bit "ATTACHED-DELAY", {
                    description \
                    '''If set, the originator is attached to
                        another area using the referred metric.''';
                }
                bit "ATTACHED-DEFAULT", {
                    description \
                    '''If set, the originator is attached to
                        another area using the referred metric.''';
                }
                bit "OVERLOAD", {
                    description \
                    '''If set, the originator is overloaded,
                        and must be avoided in path calculation.''';
                }
            }
            description \
            "This leaf describes attributes of the LSP.";
        }

        container "is-neighbor", {
             list "neighbor", {
                key "neighbor-id";
                uses "neighbor";
                description \
                "List of neighbors.";
            }
            description \
            '''This leaf describes list of ISIS neighbors.
                ISIS reference is TLV 2.''';
        }

        container "authentication", {
            leaf "authentication-type", {
                type "authentication-type";
                description \
                '''This leaf describes the authentication type
                    to be used.''';
            }
            leaf "authentication-key", {
                type "string";
                description \
                '''This leaf describes the authentication key
                    to be used. For security reason, the
                    authentication key MUST NOT be presented
                    in plaintext format. Authors recommends
                    to use MD5 hash to present the authentication-key.''';
            }
            description ''' This container describes authentication
                information of the node. ISIS reference is TLV 10.''';
        }

        container "extended-is-neighbor", {
             list "neighbor", {
                key "neighbor-id";
                uses "neighbor-extended";
                description \
                "List of neighbors.";
            }
            description \
            '''This container describes list of ISIS extended
                neighbors.
                    ISIS reference is TLV 22.''';
        }

        container "ipv4-internal-reachability", {
             list "prefixes", {
                key "ip-prefix";
                uses "prefix-ipv4-std";
                description \
                "List of prefixes.";
            }
            description \
            '''This container describes list of IPv4 internal
                reachability information.
                    ISIS reference is TLV 128.''';
        }

        leaf_list "protocol-supported", {
            type "uint8";
            description \
            '''This leaf describes the list of
                supported protocols.
                    ISIS reference is TLV 129.''';
        }

        container "ipv4-external-reachability", {
             list "prefixes", {
                key "ip-prefix";
                uses "prefix-ipv4-std";
                description \
                "List of prefixes.";
            }
            description \
            '''This container describes list of IPv4 external
                reachability information.
                    ISIS reference is TLV 130.''';
        }

        leaf_list "ipv4-addresses", {
            type "inet:ipv4-address";
            description \
            '''This leaf describes the IPv4 addresses of the node.
                ISIS reference is TLV 132.''';
        }

        leaf "ipv4-te-routerid", {

            type "inet:ipv4-address";
            description \
            '''This leaf describes the IPv4 Traffic Engineering
                router ID of the node.
                ISIS reference is TLV 134.''';
        }

        container "extended-ipv4-reachability", {

             list "prefixes", {
                key "ip-prefix";
                uses "prefix-ipv4-extended";
                description \
                "List of prefixes.";
            }
            description \
            '''This container describes list of IPv4 extended
                reachability information.
                    ISIS reference is TLV 135.''';
        }

        leaf "dynamic-hostname", {
            type "string";

            description \
            '''This leaf describes the name of the node.
                ISIS reference is TLV 137.''';
        }

        leaf "ipv6-te-routerid", {
            type "inet:ipv6-address";
            description \
            '''This leaf describes the IPv6 Traffic Engineering
                router ID of the node.
                ISIS reference is TLV 140.''';
        }


        container "mt-is-neighbor", {
             list "neighbor", {
                key "neighbor-id";
                leaf "MT-ID", {
                    type "uint16", {
                        range "0..4095";
                    }
                    description \
                    '''This leaf defines the identifier
                        of a topology.''';
                }
                uses "neighbor-extended";
                description \
                "List of neighbors.";
            }
            description \
            '''This container describes list of ISIS multi-topology
                neighbors.
                    ISIS reference is TLV 223.''';
        }

        container "mt-entries", {
             list "topology", {
                key "MT-ID";

                leaf "MT-ID", {
                    type "uint16", {
                        range "0..4095";
                    }
                    description \
                    '''This leaf defines the identifier
                        of a topology.''';
                }

                leaf "attributes", {
                    type "bits", {
                        bit "OVERLOAD", {
                            description \
                            '''If set, the originator is overloaded,
                                and must be avoided in path
                                calculation.''';
                        }
                        bit "ATTACHED", {
                            description \
                            '''If set, the originator is attached to
                                another area using the referred metric.''';
                        }
                    }
                    description \
                    '''This leaf describes attributes of the LSP
                        for the associated topology.''';
                }
                description \
                "List of topologies supported.";
            }
            description \
            '''This container describes the topology supported.
                ISIS reference is TLV 229.''';
        }

        leaf_list "ipv6-addresses", {
            type "inet:ipv6-address";
            description \
            '''This leaf describes the IPv6 interface
                addresses of the node.
                    ISIS reference is TLV 232.''';
        }




        container "mt-extended-ipv4-reachability", {
             list "prefixes", {
                key "ip-prefix";
                leaf "MT-ID", {
                    type "uint16", {
                        range "0..4095";
                    }
                    description \
                    '''This leaf defines the identifier
                        of a topology.''';
                }
                uses "prefix-ipv4-extended";
                description \
                "List of prefixes.";

            }
            description \
            '''This container describes list of IPv4
                reachability information in multi-topology
                environment.
                    ISIS reference is TLV 235.''';
        }

        container "mt-ipv6-reachability", {
             list "prefixes", {
                key "ip-prefix";
                leaf "MT-ID", {
                    type "uint16", {
                        range "0..4095";
                    }
                    description \
                    '''This leaf defines the identifier
                        of a topology.''';
                }
                uses "prefix-ipv6-extended";
                description \
                "List of prefixes.";
            }
            description \
            '''This container describes list of IPv6
                reachability information in multi-topology
                environment.
                    ISIS reference is TLV 237.''';
        }

        container "ipv6-reachability", {
             list "prefixes", {
                key "ip-prefix";
                uses "prefix-ipv6-extended";
                description \
                "List of prefixes.";
            }
            description \
            '''This container describes list of IPv6
                reachability information.
                    ISIS reference is TLV 236.''';
        }

        container "router-capabilities", {
            leaf "binary", {
                type "binary";
                description \
                '''This leaf describes the capability of the node.
                    Format is binary according to the protocol encoding.''';
            }
            description \
            '''This container describes the capabilities of the node.
                This container may be extended with detailed
                information.
                    ISIS reference is TLV 242.''';
        }
    }





    augment "/rt:routing/rt:routing-instance/rt:routing-protocols/" + "rt:routing-protocol", {
        when "rt:type = 'isis:isis'", {
            description \
            '''This augment is only valid when routing protocol
                instance type is isis.''';
        }
        description \
        '''This augments a routing protocol instance with ISIS
            specific parameters.''';
        container "isis", {

             list "instance", {
                must "count(area-address) > 0", {
                    error_message '''At least one area-address
                        must be configured.''';
                    description \
                    "Enforce configuration of at least one area.";
                }

                key "routing-instance";

                leaf "routing-instance", {
                    type "rt:routing-instance-ref";
                    description \
                    '''Reference routing instance.
                        For protocol centric model, which is
                        supported in
                        default-instance only, this could reference
                        any VRF routing-instance.
                        For VRF centric model, must reference the
                        enclosing routing-instance.''';
                }

                leaf "level-type", {
                    type "level";
                    default_ "level-all";
                    description \
                    '''This leaf describes the type of ISIS node.
                        A node can be level-1-only, level-2-only
                        or level-1-2.
                            ''';
                }

                leaf "system-id", {
                    type "system-id";
                    description \
                    "This leaf defines the system-id of the node.";
                }


                leaf "maximum-area-addresses", {
                    if_feature "maximum-area-addresses";
                    type "uint8";
                    default_ 3;
                    description \
                    "Defines the maximum areas supported.";
                }

                leaf_list "area-address", {
                    type "area-address";
                    description \
                    '''List of areas supported by the
                        protocol instance.''';
                }

                container "mpls-te", {
                    leaf "ipv4-router-id", {
                        if_feature "ipv4-router-id";
                        type "inet:ipv4-address";
                        description \
                        '''Router ID value that would be used in
                            TLV 134.''';
                    }
                    leaf "ipv6-router-id", {
                        if_feature "ipv6-router-id";
                        type "inet:ipv6-address";
                        description \
                        '''Router ID value that would be used in
                            TLV 140.''';
                    }
                    description \
                    "This container handles mpls te config.";
                }
                leaf "reference-bandwidth", {
                    if_feature "reference-bandwidth";
                    type "uint32";
                    units "bps";
                    description \
                    '''This leaf defines the bandwidth for calculating
                        metric.''';
                }

                leaf "lsp-mtu", {
                    type "uint16";
                    units "bytes";
                    default_ 1492;
                    description \
                    '''This leaf describes the maximum size of a
                        LSP PDU in bytes.''';
                }
                leaf "lsp-lifetime", {
                    type "uint16";
                    units "seconds";
                    description \
                    '''This leaf describes the lifetime of the router
                        LSP in seconds.''';
                }
                leaf "lsp-refresh", {
                    if_feature "lsp-refresh";
                    type "uint16";
                    units "seconds";
                    description \
                    '''This leaf describes the refresh interval of the
                        router LSP in seconds.''';
                }

                container "graceful-restart", {
                    if_feature "graceful-restart";
                    leaf "enabled", {
                        type "boolean";
                        description \
                        "Control enabling the feature.";
                    }
                    description \
                    "This container activates graceful restart.";
                }

                container "fast-reroute", {
                    description \
                    '''This container needs to be
                        augmented with global parameters
                        for IPFRR.''';
                }
                container "segment-routing", {
                    if_feature "segment-routing";
                    leaf "transport-type", {
                       type "enumeration", {
                            enum_ "mpls";
                        }
                        description "Dataplane to be used.";
                    }
                     list "srgb", {
                        key "lower-bound upper-bound";
                        leaf "lower-bound", {
                            type "uint32";
                            description \
                            "Lower value in the block.";
                        }
                        leaf "upper-bound", {
                            type "uint32";
                            description \
                            "Upper value in the block.";
                        }
                        description \
                        '''List of global blocks to be
                            advertised.''';
                    }
                    description \
                    "segment routing global config.";
                }
                 list "authentication", {
                    key "level";

                    leaf "key", {
                        type "string";
                        description \
                        '''This leaf describes the
                            authentication key.''';
                    }
                    leaf "type", {
                        type "authentication-type";
                        description \
                        '''This leaf describes the authentication
                            type to be used.''';
                    }
                    leaf "level", {
                        type "level";
                        description \
                        "Level applicability.";
                    }
                    description \
                    '''Container for ISIS authentication.
                        It covers both LSPs and SNPs.''';
                }



                list "metric-type", {
                    key "level";

                    leaf "value", {
                       type "enumeration", {
                            enum_ "wide-only", {
                                description \
                                '''Advertise new metric style only
                                    (RFC5305)''';
                            }
                            enum_ "old-only", {
                                description \
                                '''Advertise old metric style only
                                    (RFC1195)''';
                            }
                            enum_ "both", {
                                description '''Advertise both metric
                                    styles''';
                            }
                        }
                        description \
                        '''This leaf describes the type of metric
                            to be generated.
                            Wide-only means only new metric style
                            is generated,
                                old-only means that only old style metric
                            is generated,
                                and both means that both are advertised.
                                This leaf is only affecting IPv4 metrics.''';
                    }
                    leaf "level", {
                        type "level";
                        description \
                        "Level applicability.";
                    }
                    description \
                    "Metric style container.";
                }
                 list "preference", {
                    key "level";

                    choice "granularity", {
                        case_ "detail", {
                            leaf "internal", {
                                type "uint8";
                                description \
                                '''This leaf defines the protocol
                                    preference for internal routes.''';
                            }
                            leaf "external", {
                                type "uint8";
                                description \
                                '''This leaf defines the protocol
                                    preference for external routes.''';
                            }
                        }
                        case_ "coarse", {
                            leaf "default", {
                                type "uint8";
                                description \
                                '''This leaf defines the protocol
                                    preference for all ISIS routes.''';
                            }
                        }
                        description \
                        "Choice for implementation of route preference.";
                    }

                    leaf "level", {
                        type "level";
                        description \
                        "Level applicability.";
                    }
                    description \
                    "This leaf defines the protocol preference.";
                }

                list "default-metric", {
                    key "level";

                    leaf "value", {
                        type "wide-metric";
                        description \
                        "Value of the metric";
                    }
                    leaf "level", {
                        type "level";
                        description \
                        "Level applicability of the metric.";
                    }
                    description \
                    "Defines the metric to be used by default.";
                }
                list "af", {
                    if_feature "nlpid-control";
                    key "af";
                    leaf "af", {
                        type "string";
                        description \
                        "Address-family";
                    }

                    leaf "enabled", {
                        type "boolean";
                        description \
                        '''Describes the activation state of the
                            AF.''';
                    }
                    description \
                    '''This list permits activation
                        of new address families.''';

                }


                 list "topologies", {
                    if_feature "multi-topology";

                    key "name";
                    leaf "enabled", {
                        type "boolean";
                        description \
                        '''Describes the activation state of the
                            AF.''';
                    }
                    leaf "name", {
                        type "rt:rib-ref";
                        description "RIB";
                    }
                    list "default-metric", {
                        key "level";

                        leaf "value", {
                            type "wide-metric";
                            description \
                            "Value of the metric";
                        }
                        leaf "level", {
                            type "level";
                            description \
                            "Level applicability of the metric.";
                        }
                        description \
                        "Defines the metric to be used by default.";
                    }
                    description \
                    "List of topologies";
                }


                 list "overload", {
                    key "level";

                    leaf "status", {
                        type "boolean";
                        description \
                        "This leaf defines the overload status.";
                    }

                    leaf "timeout", {
                        type "uint16";
                        units "seconds";
                        description \
                        '''This leaf defines the timeout in seconds
                            of the overload condition.''';
                    }
                    leaf "level", {
                        type "level";
                        description \
                        "Level applicability of the metric.";
                    }
                    description \
                    '''This leaf describes if the router is
                        set to overload state.''';
                }

                list "overload-max-metric", {
                    if_feature "overload-max-metric";
                    key "level";

                    leaf "status", {
                        type "boolean";
                        description \
                        "This leaf defines the overload status.";
                    }

                    leaf "timeout", {
                        type "uint16";
                        units "seconds";
                        description \
                        '''This leaf defines the timeout in seconds
                            of the overload condition.''';
                    }
                    leaf "level", {
                        type "level";
                        description \
                        "Level applicability of the metric.";
                    }
                    description \
                    '''This leaf describes if the router is
                        set to overload state.''';
                }

                container "interfaces", {
                    list "interface", {
                        key "name";
                        leaf "name", {
                            type "string";

                            description \
                            '''Reference to the interface within
                                the routing-instance.''';
                        }
                        leaf "level-type", {
                            type "level";
                            default_ "level-all";
                            description \
                            '''This leaf defines the associated ISIS
                                level of the interface.''';
                        }
                        leaf "lsp-pacing-interval", {
                            type "uint16";
                            units "milliseconds";
                            description \
                            '''This leaf defines the interval between
                                LSP transmissions in milli-seconds''';
                        }
                        leaf "lsp-retransmit-interval", {
                            type "uint16";
                            units "seconds";
                            description \
                            '''This leaf defines the interval between
                                retransmission of LSP''';
                        }
                        leaf "passive", {
                            type "boolean";
                            default_ "false";
                            description \
                            '''This leaf defines if interface is in
                                passive mode (ISIS not running,
                                    but network is advertised).''';
                        }
                        leaf "csnp-interval", {
                            type "uint16";
                            units "seconds";
                            description \
                            '''This leaf defines the interval of CSNP
                                messages.''';
                        }

                        container "hello-padding", {
                            leaf "enabled", {
                                type "boolean";
                                default_ "true";
                                description \
                                '''Status of Hello-padding activation.
                                    By default, the implementation shall
                                    pad HELLOs.''';
                            }

                            description \
                            '''This container handles ISIS hello padding
                                configuration.''';
                        }

                        leaf "mesh-group-enabled", {
                            type "mesh-group-state";
                            description \
                            '''Describes the mesh group state of
                                the interface.''';
                        }


                        leaf "mesh-group", {
                            when "../mesh-group-enabled = meshSet", {
                                description \
                                '''Only valid when mesh-group-enabled
                                    equals meshSet''';
                            }
                            type "uint8";
                            description \
                            '''Describes the mesh group ID of
                                the interface.''';
                        }

                        leaf "interface-type", {
                            type "interface-type";
                            description \
                            '''This leaf defines the type of adjacency
                                to be established on the interface.
                                This is affecting the type of hello
                                message that would be used.''';
                        }

                        leaf "enabled", {
                            type "boolean";
                            default_ "true";
                            description \
                            '''This leaf describes the administrative
                                status of the ISIS interface.''';

                        }

                        leaf_list "tag", {
                            if_feature "prefix-tag";

                            type "uint32";
                            description \
                            '''This leaf defines list of tags associated
                                with the interface.''';
                        }

                        leaf_list "tag64", {
                            if_feature "prefix-tag64";

                            type "uint64";
                            description \
                            '''This leaf defines list of 64bits tags
                                associated with the interface.''';
                        }

                        list "hello-authentication", {
                            key "level";

                            leaf "type", {
                                type "authentication-type";

                                description \
                                '''This leaf describes the authentication
                                    type to be used in hello messages.''';
                            }
                            leaf "key", {
                                type "string";
                                description \
                                '''This leaf describes the
                                    authentication key
                                    to be used in hello messages.
                                        For security reason, the
                                    authentication key MUST
                                    NOT be presented
                                    in plaintext format upon a
                                    get-config reply.
                                        Authors recommends
                                    to use MD5 hash to present the
                                    authentication-key''';
                            }
                            leaf "level", {
                                type "level";
                                description \
                                "Level applicability.";
                            }
                            description \
                            '''This leaf describes the authentication type
                                to be used in hello messages.''';
                        }

                        list "hello-interval", {
                            key "level";

                            leaf "value", {
                                type "uint16";
                                units "seconds";
                                description \
                                '''This leaf defines the interval of
                                    hello messages.''';
                            }
                            leaf "level", {
                                type "level";
                                description \
                                "Level applicability.";
                            }
                            description \
                            '''This leaf defines the interval of
                                hello messages.''';
                        }
                        list "hello-multiplier", {
                            key "level";

                            leaf "value", {
                                type "uint16";
                                description \
                                '''This leaf defines the number of
                                    hello failed to be received before
                                    declaring the adjacency down.''';
                            }
                            leaf "level", {
                                type "level";
                                description \
                                "Level applicability.";
                            }
                            description \
                            '''This leaf defines the number of
                                hello failed to be received before
                                declaring the adjacency down.''';
                        }


                         list "priority", {
                            must 'interface-type = "broadcast"';
                            key "level";
                            leaf "value", {
                                type "uint8", {
                                    range "0..127";
                                }

                                description \
                                '''This leaf describes the priority of
                                    the interface
                                    for DIS election.''';
                            }
                            leaf "level", {
                                type "level";
                                description \
                                "Level applicability.";
                            }
                            description \
                            '''This leaf describes the priority of
                                the interface
                                for DIS election.''';
                        }
                         list "metric", {
                            key "level";

                            leaf "value", {
                                type "wide-metric";
                                description \
                                "Metric value.";
                            }
                            leaf "level", {
                                type "level";
                                description \
                                "Level applicability.";
                            }
                            description \
                            "Container for interface metric";
                        }
                        list "af", {
                            key "af";

                            leaf "af", {
                                type "string";
                                description \
                                "Address-family";
                            }
                            container "segment-routing", {
                                if_feature "segment-routing";
                                list "prefix-sid", {
                                    key "index";
                                    leaf "index", {
                                        type "uint32";
                                        description \
                                        '''Index associated with
                                            prefix.''';

                                    }
                                    leaf "node-flag", {
                                        type "boolean";
                                        default_ true;
                                        description \
                                        '''Set prefix as a node
                                            representative prefix.''';
                                    }
                                    leaf "explicit-null", {
                                        type "boolean";
                                        description \
                                        '''Force explicit NULL
                                            forwarding for this SID.''';
                                    }
                                    leaf "php", {
                                        type "boolean";
                                        description \
                                        '''Activates PHP for this
                                            SID.''';
                                    }
                                    description \
                                    '''List of prefix-SID associated with
                                        the interface.''';
                                }
                                description \
                                "Segment routing interface configuration.";
                            }
                            container "bfd", {
                                if_feature "bfd";
                                leaf "enabled", {
                                    type "boolean";
                                    default_ false;
                                    description \
                                    "This leaf enables BFD.";
                                }

                                description \
                                '''The container describes
                                    BFD config.''';
                            }

                            description \
                            "List of AFs.";
                        }

                         list "topologies", {
                            key "name";

                            leaf "name", {
                                type "rt:rib-ref";
                                description \
                                "Name of RIB.";
                            }
                             list "metric", {
                                key "level";

                                leaf "value", {
                                    type "wide-metric";
                                    description \
                                    "Metric value.";
                                }
                                leaf "level", {
                                    type "level";
                                    description \
                                    "Level applicability.";
                                }
                                description \
                                "Container for interface metric";
                            }
                            description \
                            "List of topologies.";
                        }
                        container "fast-reroute", {
                            container "lfa", {
                                if_feature "lfa";
                                leaf "candidate-disabled", {
                                    type "boolean";
                                    description \
                                    "Prevent the interface to be used as backup.";
                                }
                                leaf "enabled", {
                                    type "boolean";
                                    description \
                                    "Activates LFA.";
                                }
                                container "remote-lfa", {
                                    if_feature "remote-lfa";
                                    leaf "enabled", {
                                        type "boolean";
                                        description \
                                        "Activates rLFA.";
                                    }
                                    description \
                                    "remote LFA configuration.";
                                }
                                description \
                                "LFA configuration.";
                            }
                            description \
                            "Fast-reroute configuration.";
                        }
                        description \
                        "List of ISIS interfaces.";
                    }
                    container "igp-ldp-sync", {
                        if_feature "igp-ldp-sync";
                        leaf "holdtime", {
                            type "uint16";
                            description \
                            "Time to wait in sec for LDP session setup.";
                        }
                        description \
                        "IGP-LDP sync configuration.";
                    }

                    description \
                    '''This container defines ISIS interface specific
                        configuration objects.''';
                }
                description \
                "List of ISIS instances.";
            }
            description \
            '''This container defines ISIS specific configuration
                objects.''';
        }
    }

    augment "/rt:routing-state/rt:routing-instance/" + "rt:routing-protocols/rt:routing-protocol", {
        when "rt:type = 'isis:isis'", {
            description \
            '''This augment is only valid when routing protocol
                instance type is isis.''';
        }
        description \
        '''This augments routing protocol instance states with ISIS
            specific parameters.''';
        container "isis", {
            config false;
             list "instance", {

                key "routing-instance";

                leaf "routing-instance", {
                    type "rt:routing-instance-ref";
                    description \
                    '''Reference routing instance.
                        For protocol centric model, which is
                        supported in
                        default-instance only, this could reference
                        any VRF routing-instance.
                            For VRF centric model, must reference the
                        enclosing routing-instance.''';
                }
                container "system-counters", {
                     list "level", {
                        key "level";

                        leaf "level", {
                            type "level-number";
                            description \
                            "This leaf describes the ISIS level.";
                        }
                        leaf "corrupted-lsps", {
                            type "uint32";
                            description \
                            '''Number of corrupted in-memory LSPs detected.
                                LSPs received from the wire with a bad
                                checksum are silently dropped and not counted.
                                LSPs received from the wire with parse errors
                                are counted by lsp-errors.''';
                        }
                        leaf "authentication-type-fails", {
                            type "uint32";
                            description \
                            "Number of authentication type mismatches.";
                        }
                        leaf "authentication-fails", {
                            type "uint32";
                            description \
                            "Number of authentication key failures.";
                        }
                        leaf "database-overload", {
                            type "uint32";
                            description \
                            '''Number of times the database has become
                                overloaded.''';
                        }
                        leaf "own-lsp-purge", {
                            type "uint32";
                            description \
                            '''Number of times a zero-aged copy of the
                                system's own LSP is received from some
                                other node.''';
                        }
                        leaf "manual-address-drop-from-area", {
                            type "uint32";
                            description \
                            '''Number of times a manual address
                                has been dropped from the area.''';
                        }
                        leaf "max-sequence", {
                            type "uint32";
                            description \
                            '''Number of times the system has attempted
                                to exceed the maximum sequence number.''';
                        }
                        leaf "sequence-number-skipped", {
                            type "uint32";
                            description \
                            '''Number of times a sequence number skip has
                                occured.''';
                        }
                        leaf "id-len-mismatch", {
                            type "uint32";
                            description \
                            '''Number of times a PDU is received with
                                a different value for ID field length
                                from that of the receiving system.''';
                        }
                        leaf "partition-changes", {
                            type "uint32";
                            description \
                            "Number of partition changes detected.";
                        }
                        leaf "lsp-errors", {
                            type "uint32";
                            description \
                            '''Number of LSPs with errors we have
                                received.''';
                        }
                        leaf "spf-runs", {
                            type "uint32";
                            description \
                            "Number of times we ran SPF at this level.";
                        }
                        description \
                        "List of supported levels.";
                    }
                    description \
                    '''The container defines a list of counters
                        for the IS.''';
                }
                container "interface-counters", {
                    list "interface", {
                        key "interface";

                        leaf "interface", {
                            type "string";
                            description \
                            '''This leaf describes the name
                                of the interface.''';
                        }

                        leaf "adjacency-changes", {
                            type "uint32";
                            description \
                            '''The number of times an adjacency state
                                change has occured on this interface.''';
                        }
                        leaf "adjacency-number", {
                            type "uint32";
                            description \
                            '''The number of adjacencies on this
                                interface.''';
                        }
                        leaf "init-fails", {
                            type "uint32";
                            description \
                            '''The number of times initialization of
                                this interface has failed. This counts
                                events such as PPP NCP failures.
                                    Failures to form an adjacency are counted
                                by adjacency-rejects.''';
                        }
                        leaf "adjacency-rejects", {
                            type "uint32";
                            description \
                            '''The number of times an adjacency has been
                                rejected on this interface.''';
                        }
                        leaf "id-len-mismatch", {
                            type "uint32";
                            description \
                            '''The number of times an IS-IS PDU with an ID
                                field length different from that for this
                                system has been received on this interface.''';
                        }
                        leaf "max-area-addresses-mismatch", {
                            type "uint32";
                            description \
                            '''The number of times an IS-IS PDU with
                                according max area address field
                                differs from that for
                                this system has been received on this
                                interface.''';
                        }
                        leaf "authentication-type-fails", {
                            type "uint32";
                            description \
                            "Number of authentication type mismatches.";
                        }
                        leaf "authentication-fails", {
                            type "uint32";
                            description \
                            "Number of authentication key failures.";
                        }
                        leaf "lan-dis-changes", {
                            type "uint32";
                            description \
                            '''The number of times the DIS has changed
                                on this interface at this level.
                                    If the interface type is point to point,
                                    the count is zero.''';
                        }
                        description \
                        "List of interfaces.";
                    }
                    description \
                    '''The container defines a list of counters
                        for interfaces.''';
                }
                container "packet-counters", {
                     list "level", {
                        key "level";

                        leaf "level", {
                            type "level-number";
                            description \
                            "This leaf describes the ISIS level.";
                        }

                        container "iih", {
                            leaf "in", {
                                type "uint32";
                                description \
                                "Received PDUs.";
                            }
                            leaf "out", {
                                type "uint32";
                                description \
                                "Sent PDUs.";
                            }
                            description \
                            "The number of IIH PDUs received/sent.";
                        }
                        container "ish", {
                            leaf "in", {
                                type "uint32";
                                description \
                                "Received PDUs.";
                            }
                            leaf "out", {
                                type "uint32";
                                description \
                                "Sent PDUs.";
                            }
                            description \
                            "The number of ISH PDUs received/sent.";
                        }
                        container "esh", {
                            leaf "in", {
                                type "uint32";
                                description \
                                "Received PDUs.";
                            }
                            leaf "out", {
                                type "uint32";
                                description \
                                "Sent PDUs.";
                            }
                            description \
                            "The number of ESH PDUs received/sent.";
                        }
                        container "lsp", {
                            leaf "in", {
                                type "uint32";
                                description \
                                "Received PDUs.";
                            }
                            leaf "out", {
                                type "uint32";
                                description \
                                "Sent PDUs.";
                            }
                            description \
                            "The number of LSP PDUs received/sent.";
                        }
                        container "psnp", {
                            leaf "in", {
                                type "uint32";
                                description \
                                "Received PDUs.";
                            }
                            leaf "out", {
                                type "uint32";
                                description \
                                "Sent PDUs.";
                            }
                            description \
                            "The number of PSNP PDUs received/sent.";
                        }
                        container "csnp", {
                            leaf "in", {
                                type "uint32";
                                description \
                                "Received PDUs.";
                            }
                            leaf "out", {
                                type "uint32";
                                description \
                                "Sent PDUs.";
                            }
                            description \
                            "The number of CSNP PDUs received/sent.";
                        }
                        container "unknown", {
                            leaf "in", {
                                type "uint32";
                                description \
                                "Received PDUs.";
                            }
                            leaf "out", {
                                type "uint32";
                                description \
                                "Sent PDUs.";
                            }
                            description \
                            "The number of unknown PDUs received/sent.";
                        }
                        description \
                        "List of supported levels.";
                    }
                    description \
                    "The container defines a list of PDU counters.";
                }
                container "interfaces", {
                     list "interfaces", {
                        key "interface";

                        leaf "interface", {
                            type "string";
                            description \
                            '''This leaf describes the name
                                of the interface.''';
                        }
                        leaf "circuit-id", {
                            type "circuit-id";
                            description \
                            '''This leaf describes the circuit-id
                                associated with the interface.''';
                        }
                        leaf "admin-state", {
                            type "admin-state";
                            description \
                            '''This leaf describes the admin state
                                of the interface.''';
                        }
                        leaf "oper-state", {
                            type "oper-state";
                            description \
                            '''This leaf describes the operational state
                                of the interface.''';
                        }
                        leaf "interface-type", {
                            type "interface-type";
                            description \
                            "Type of interface to be used.";
                        }
                        leaf "level", {
                            type "level";
                            description \
                            "Level associated with the interface.";
                        }
                        leaf "passive", {
                            type "empty";
                            description \
                            '''The interface is included in LSP, but
                                does not run ISIS protocol.''';
                        }
                        leaf "three-way-handshake", {
                            type "empty";
                            description \
                            "The interface uses 3-way handshake.";
                        }
                        description \
                        "List of interfaces.";
                    }
                    description \
                    '''The container defines operational parameters
                        of interfaces.''';
                }
                container "adjacencies", {
                     list "adjacency", {
                        leaf "interface", {
                            type "string";
                            description \
                            '''This leaf describes the name
                                of the interface.''';
                        }
                        leaf "level", {
                            type "level";
                            description \
                            '''This leaf describes the associated
                                ISIS level of the interface.
                                ''';
                        }
                        leaf "neighbor-sysid", {
                            type "system-id";
                            description \
                            "The system-id of the neighbor";

                        }
                        leaf "neighbor-extended-circuit-id", {
                            type "extended-circuit-id";
                            description \
                            "Circuit ID of the neighbor";
                        }
                        leaf "neighbor-snpa", {
                            type "snpa";
                            description \
                            "SNPA of the neighbor";
                        }
                        leaf "neighbor-level", {
                            type "level";
                            description \
                            "The type of the neighboring system.";
                        }
                        leaf "hold-timer", {
                            type "uint16";
                            description \
                            '''The holding time in seconds for this
                                adjacency. This value is based on
                                received hello PDUs and the elapsed
                                time since receipt.''';
                        }
                        leaf "neighbor-priority", {
                            type "uint8", {
                                range "0..127";
                            }
                            description \
                            '''Priority of the neighboring IS for becoming
                                the DIS.''';
                        }
                        leaf "lastuptime", {
                            type "yang:timestamp";
                            description \
                            '''When the adjacency most recently entered
                                state 'up', measured in hundredths of a
                                second since the last reinitialization of
                                the network management subsystem.
                                    The value is 0 if the adjacency has never
                                been in state 'up'.''';

                        }
                        leaf "state", {
                           type "enumeration", {
                                enum_ "Up", {
                                    description \
                                    '''This state describes that
                                        adjacency is established.''';
                                }
                                enum_ "Down", {
                                    description \
                                    '''This state describes that
                                        adjacency is NOT established.''';
                                }
                                enum_ "Init", {
                                    description \
                                    '''This state describes that
                                        adjacency is establishing.''';
                                }
                            }
                            description \
                            '''This leaf describes the state of the
                                interface.''';
                        }
                        description \
                        "List of operational adjacencies.";
                    }
                    description \
                    '''This container lists the adjacencies of
                        the local node.''';
                }
                container "spf-log", {
                     list "event", {
                        key "id";

                        leaf "id", {
                            type "uint32";
                            description \
                            '''This leaf defines the event identifier.
                                This is a purely internal value.''';
                        }
                        leaf "spf-type", {
                           type "enumeration", {
                                enum_ "full", {
                                    description \
                                    "Computation done is a Full SPF.";
                                }
                                enum_ "incremental", {
                                    description \
                                    '''Computation done is an
                                        incremental SPF.''';
                                }
                                enum_ "route-only", {
                                    description \
                                    '''Computation done is a
                                        reachability computation
                                        only.''';
                                }
                            }
                            description \
                            '''This leaf describes the type of computation
                                used.''';
                        }
                        leaf "level", {
                            type "level-number";
                            description \
                            '''This leaf describes the level affected by the
                                the computation.''';
                        }
                        leaf "spf-delay", {
                            type "uint32";
                            units "milliseconds";
                            description \
                            '''This leaf describes the SPF delay that
                                was used for this event.''';
                        }
                        leaf "schedule-timestamp", {
                            type "yang:timestamp";
                            description \
                            '''This leaf describes the timestamp
                                when the computation was scheduled.''';
                        }
                        leaf "start-timestamp", {
                            type "yang:timestamp";
                            description \
                            '''This leaf describes the timestamp
                                when the computation was started.''';
                        }
                        leaf "end-timestamp", {
                            type "yang:timestamp";
                            description \
                            '''This leaf describes the timestamp
                                when the computation was ended.''';
                        }
                        list "trigger-lsp", {
                            key "lsp";
                            leaf "lsp", {
                                type "lsp-id";
                                description \
                                '''This leaf describes the LSPID
                                    of the LSP.''';
                            }
                            leaf "sequence", {
                                type "uint32";
                                description \
                                '''This leaf describes the sequence
                                    number of the LSP.''';
                            }
                            description \
                            '''This leaf describes list of LSPs
                                that triggered the computation.''';
                        }
                        description \
                        "List of computation events.";
                    }

                    description \
                    "This container lists the SPF computation events.";
                }
                container "lsp-log", {
                     list "event", {
                        key "id";

                        leaf "id", {
                            type "uint32";
                            description \
                            '''This leaf defines the event identifier.
                                This is a purely internal value.''';
                        }
                        leaf "level", {
                            type "level-number";
                            description \
                            '''This leaf describes the level affected by the
                                the computation.''';
                        }
                        container "lsp", {
                            leaf "lsp", {

                                type "lsp-id";
                                description \
                                '''This leaf describes the LSPID
                                    of the LSP.''';
                            }
                            leaf "sequence", {
                                type "uint32";
                                description \
                                '''This leaf describes the sequence
                                    number of the LSP.''';
                            }
                            description \
                            '''This container describes the received LSP
                                , in case of local LSP update the local
                                LSP ID is referenced.''';
                        }

                        leaf "received-timestamp", {
                            type "yang:timestamp";

                            description \
                            '''This leaf describes the timestamp
                                when the LSP was received. In case of
                                local LSP update, the timestamp refers
                                to the local LSP update time.''';
                        }

                        description \
                        "List of LSP events.";
                    }

                    description \
                    '''This container lists the LSP reception events.
                        Local LSP modification are also contained in the
                        list.''';
                }
                container "database", {
                    list "level-db", {
                        key "level";

                        leaf "level", {
                            type "level-number";
                            description \
                            "Current level number";
                        }
                         list "lsp", {
                            key "lsp-id";

                            uses "database";
                            description \
                            "List of LSPs in LSDB.";
                        }

                        description \
                        '''This container describes the list of LSPs
                            in the level x database.''';
                    }

                    description \
                    '''This container describes ISIS Link State
                        databases.''';
                }
                container "hostnames", {

                     list "hostname", {
                        key "system-id";
                        leaf "system-id", {
                            type "system-id";
                            description \
                            '''This leaf describes the system-id
                                associated with the hostname.''';
                        }
                        leaf "hostname", {

                            type "string";
                            description \
                            '''This leaf describes the hostname
                                associated with the system ID.''';
                        }
                        description \
                        "List of system-id/hostname associations";
                    }

                    description \
                    '''This container describes the list
                        of binding between system-id and
                        hostnames.''';
                }

                description \
                "List of ISIS instances.";
            }
            description \
            "This container defines various ISIS states objects.";
        }
    }

    cmt("RPC methods", inline: false)
    rpc "clear-adjacency", {
        description '''This RPC request clears a particular
            set of ISIS adjacencies. If the operation
            fails for ISIS internal reason, then
            error-tag and error-app-tag should be set
            to a meaningful value.''';

        input "", {
            leaf "routing-instance-name", {
                type "rt:routing-instance-state-ref";
                mandatory "true";
                description \
                '''Name of the routing instance whose ISIS
                    information is being queried.

                        If the routing instance with name equal to the
                    value of this parameter doesn't exist, then this
                    operation SHALL fail with error-tag 'data-missing'
                    and error-app-tag 'routing-instance-not-found'.''';


            }
            leaf "routing-protocol-instance-name", {
                type "instance-state-ref";
                mandatory "true";
                description \
                '''Name of the ISIS protocol instance whose ISIS
                    information is being queried.

                        If the ISIS instance with name equal to the
                    value of this parameter doesn't exist, then this
                    operation SHALL fail with error-tag 'data-missing'
                    and error-app-tag
                    'routing-protocol-instance-not-found'.''';
            }
            leaf "level", {
                type "level";
                description \
                '''ISIS level of the adjacency to be cleared.
                    If ISIS level is level-1-2, both level 1 and level 2
                    adjacencies would be cleared.

                        If the value provided is different from the one
                    authorized in the enum type, then this
                    operation SHALL fail with error-tag 'data-missing'
                    and error-app-tag
                    'bad-isis-level'.
                        ''';
            }
            leaf "interface", {
                type "string";
                description \
                '''Name of the ISIS interface.

                    If the ISIS interface with name equal to the
                    value of this parameter doesn't exist, then this
                    operation SHALL fail with error-tag 'data-missing'
                    and error-app-tag
                    'isis-interface-not-found'.''';
            }
        }
    }

    rpc "clear-database", {
        description \
        '''This RPC request clears a particular
            ISIS database. If the operation
            fails for ISIS internal reason, then
            error-tag and error-app-tag should be set
            to a meaningful value.''';
        input "", {
            leaf "routing-instance-name", {
                type "rt:routing-instance-state-ref";
                mandatory "true";
                description \
                '''Name of the routing instance whose ISIS
                    information is being queried.

                        If the routing instance with name equal to the
                    value of this parameter doesn't exist, then this
                    operation SHALL fail with error-tag 'data-missing'
                    and error-app-tag 'routing-instance-not-found'.''';


            }
            leaf "routing-protocol-instance-name", {
                type "instance-state-ref";
                mandatory "true";
                description \
                '''Name of the ISIS protocol instance whose ISIS
                    information is being queried.

                        If the ISIS instance with name equal to the
                    value of this parameter doesn't exist, then this
                    operation SHALL fail with error-tag 'data-missing'
                    and error-app-tag
                    'routing-protocol-instance-not-found'.''';
            }
            leaf "level", {
                type "level";
                description \
                '''ISIS level of the adjacency to be cleared.
                    If ISIS level is level-1-2, both level 1 and level 2
                    adjacencies would be cleared.

                        If the value provided is different from the one
                    authorized in the enum type, then this
                    operation SHALL fail with error-tag 'data-missing'
                    and error-app-tag
                    'bad-isis-level'.
                        ''';
            }
        }

    }

    cmt("Notifications", inline: false)
    notification "database-overload", {
        uses "notification-instance-hdr";

        leaf "overload", {
           type "enumeration", {
                enum_ "off", {
                    description \
                    "The system has left overload condition.";
                }
                enum_ "on", {
                    description \
                    "The system is in overload condition.";
                }

            }
            description \
            "Describes the new overload state of the instance.";
        }
        description \
        '''This notification is sent when an ISIS instance
            overload condition changes.''';
    }

    notification "lsp-too-large", {
        uses "notification-instance-hdr";
        uses "notification-interface-hdr";

        leaf "pdu-size", {
            type "uint32";
            description \
            "Size of the PDU";
        }
        leaf "lsp-id", {
            type "lsp-id";
            description \
            "LSP ID.";
        }
        description \
        '''This notification is sent when we attempt
            to propagate an LSP that is larger than the
            dataLinkBlockSize for the circuit.
                The notification generation must be throttled
            with at least a 5 second gap.
            ''';
    }

    notification "corrupted-lsp-detected", {
        uses "notification-instance-hdr";
        leaf "lsp-id", {
            type "lsp-id";
            description \
            "LSP ID.";
        }
        description \
        '''This notification is sent when we find
            that an LSP that was stored in memory has
            become corrupted.
                ''';
    }

    notification "attempt-to-exceed-max-sequence", {
        uses "notification-instance-hdr";
        leaf "lsp-id", {
            type "lsp-id";
            description \
            "LSP ID.";
        }
        description \
        '''This notification is sent when the system
            wraps the 32-bit sequence counter of an LSP.
            ''';
    }

    notification "id-len-mismatch", {
        uses "notification-instance-hdr";
        uses "notification-interface-hdr";

        leaf "pdu-field-len", {
            type "uint8";
            description \
            "Size of the ID length in the received PDU";
        }
        leaf "raw-pdu", {
            type "binary";
            description \
            "Received raw PDU.";
        }
        description \
        '''This notification is sent when we receive a PDU
            with a different value for the System ID length.
                The notification generation must be throttled
            with at least a 5 second gap.
            ''';
    }

    notification "max-area-addresses-mismatch", {
        uses "notification-instance-hdr";
        uses "notification-interface-hdr";

        leaf "max-area-addresses", {
            type "uint8";
            description \
            "Received number of supported areas";
        }
        leaf "raw-pdu", {
            type "binary";
            description \
            "Received raw PDU.";
        }
        description \
        '''This notification is sent when we receive a PDU
            with a different value for the Maximum Area Addresses.
                The notification generation must be throttled
            with at least a 5 second gap.
            ''';
    }

    notification "own-lsp-purge", {
        uses "notification-instance-hdr";
        uses "notification-interface-hdr";
        leaf "lsp-id", {
            type "lsp-id";
            description \
            "LSP ID.";
        }
        description \
        '''This notification is sent when the system
            receives a PDU with its own system ID and zero age.
            ''';
    }

    notification "sequence-number-skipped", {
        uses "notification-instance-hdr";
        uses "notification-interface-hdr";
        leaf "lsp-id", {
            type "lsp-id";
            description \
            "LSP ID.";
        }
        description \
        '''This notification is sent when the system
            receives a PDU with its own system ID and
            different contents. The system has to reissue
            the LSP with a higher sequence number.
            ''';
    }

    notification "authentication-type-failure", {
        uses "notification-instance-hdr";
        uses "notification-interface-hdr";
        leaf "raw-pdu", {
            type "binary";
            description \
            "Received raw PDU.";
        }
        description \
        '''This notification is sent when the system
            receives a PDU with the wrong authentication type
            field.
                The notification generation must be throttled with
            at least a 5 second gap.
                ''';
    }

    notification "authentication-failure", {
        uses "notification-instance-hdr";
        uses "notification-interface-hdr";
        leaf "raw-pdu", {
            type "binary";
            description \
            "Received raw PDU.";
        }
        description \
        '''This notification is sent when the system
            receives a PDU with the wrong authentication
            information.
                The notification generation must be throttled with
            at least a 5 second gap.
                ''';
    }

    notification "version-skew", {
        uses "notification-instance-hdr";
        uses "notification-interface-hdr";
        leaf "protocol-version", {
            type "uint8";
            description \
            "Protocol version received in the PDU.";
        }
        leaf "raw-pdu", {
            type "binary";
            description \
            "Received raw PDU.";
        }
        description \
        '''This notification is sent when the system
            receives a PDU with a different protocol version
            number.
                The notification generation must be throttled with at least
            a 5 second gap.
                ''';
    }

    notification "area-mismatch", {
        uses "notification-instance-hdr";
        uses "notification-interface-hdr";
        leaf "raw-pdu", {
            type "binary";
            description \
            "Received raw PDU.";
        }
        description \
        '''This notification is sent when the system
            receives a Hello PDU from an IS that does
            not share any area address.
            The notification generation must be throttled with at least
            a 5 second gap.
                ''';
    }

    notification "rejected-adjacency", {
        uses "notification-instance-hdr";
        uses "notification-interface-hdr";
        leaf "raw-pdu", {
            type "binary";
            description \
            "Received raw PDU.";
        }
        leaf "reason", {
            type "string";
            description \
            '''The system may provide a reason to reject the
                adjacency. If the reason is not available,
                    the system use an empty string.''';
        }
        description \
        '''This notification is sent when the system
            receives a Hello PDU from an IS but does not
            establish an adjacency for some reason.
                The notification generation must be throttled with at least
            a 5 second gap.
                ''';
    }


    notification "protocols-supported-mismatch", {
        uses "notification-instance-hdr";
        uses "notification-interface-hdr";
        leaf "raw-pdu", {
            type "binary";
            description \
            "Received raw PDU.";
        }
        leaf_list "protocols", {
            type "uint8";
            description \
            '''The list of protocols supported by the
                remote system.''';
        }
        description \
        '''This notification is sent when the system
            receives a non pseudonode LSP that has no matching
            protocol supported.
                The notification generation must be throttled with at least
            a 5 second gap.
                ''';
    }

    notification "lsp-error-detected", {
        uses "notification-instance-hdr";
        uses "notification-interface-hdr";
        leaf "lsp-id", {
            type "lsp-id";
            description \
            "LSP ID.";
        }
        leaf "raw-pdu", {
            type "binary";
            description \
            "Received raw PDU.";
        }
        leaf "error-offset", {
            type "uint32";
            description \
            '''If the problem is a malformed TLV,
                the error-offset points to the start of the TLV.
                If the problem is with the LSP header,
                    the error-offset points to the suspicious byte''';
        }
        leaf "tlv-type", {
            type "uint8";
            description \
            '''if the problem is a malformed TLV, the tlv-type is set
                to the type value of the suspicious TLV.
                    Otherwise this leaf is not present.''';
        }
        description \
        '''This notification is sent when the system
            receives a  LSP with a parse error.
            The notification generation must be throttled with at least
            a 5 second gap.
                ''';
    }

    notification "adjacency-change", {
        uses "notification-instance-hdr";
        uses "notification-interface-hdr";
        leaf "neighbor", {
            type "string";
            description \
            '''Describes the name of the neighbor. If the
                name of the neighbor is not available, the
                field would be empty.''';
        }
        leaf "neighbor-system-id", {
            type "system-id";
            description \
            "Describes the system-id of the neighbor.";
        }
        leaf "level", {
            type "level";
            description \
            "Describes the ISIS level of the adjacency.";
        }
        leaf "state", {
           type "enumeration", {
                enum_ "Up", {
                    description \
                    '''This state describes that
                        adjacency is established.''';
                }
                enum_ "Down", {
                    description \
                    '''This state describes that
                        adjacency is no more established.''';
                }
            }
            description \
            '''This leaf describes the new state of the
                ISIS adjacency.''';
        }
        leaf "reason", {
            type "string";
            description \
            '''If the adjacency is going to DOWN,
                this leaf provides a reason for the adjacency
                going down. The reason is provided as a text.
                    If the adjacency is going to UP, no reason is
                provided.''';
        }
        description \
        '''This notification is sent when an ISIS adjacency
            moves to Up state or to Down state.''';
    }

    notification "lsp-received", {
        uses "notification-instance-hdr";
        uses "notification-interface-hdr";

        leaf "lsp-id", {
            type "lsp-id";
            description \
            "LSP ID.";
        }
        leaf "sequence", {
            type "uint32";
            description \
            "Sequence number of the received LSP.";
        }
        leaf "received-timestamp", {
            type "yang:timestamp";

            description \
            '''This leaf describes the timestamp
                when the LSP was received. ''';
        }
        leaf "neighbor-system-id", {
            type "system-id";
            description \
            '''Describes the system-id of the neighbor
                that sent the LSP.''';
        }
        description \
        '''This notification is sent when a LSP
            is received.
                The notification generation must be throttled with at least
            a 5 second gap. ''';
    }

    notification "lsp-generation", {
        uses "notification-instance-hdr";

        leaf "lsp-id", {
            type "lsp-id";
            description \
            "LSP ID.";
        }
        leaf "sequence", {
            type "uint32";
            description \
            "Sequence number of the received LSP.";
        }
        leaf "send-timestamp", {
            type "yang:timestamp";

            description \
            '''This leaf describes the timestamp
                when our LSP was regenerated. ''';
        }
        description \
        '''This notification is sent when a LSP
            is regenerated.
                The notification generation must be throttled with at least
            a 5 second gap.''';
    }

}

println builder.getText()
builder.writeToFile("${builder.getYangName()}.yang")
