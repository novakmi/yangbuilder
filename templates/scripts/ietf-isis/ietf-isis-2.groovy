#!/usr/bin/env groovy

//This is free software licensed under MIT License, see LICENSE file
//(https://bitbucket.org/novakmi/yangbuilder/src/LICENSE)

import org.bitbucket.novakmi.yangbuilder.CompactYangPlugin

//Grab(group = 'org.bitbucket.novakmi', module = 'nodebuilder', version = '0.9.0')
//@Grab(group = 'org.bitbucket.novakmi', module = 'yangbuilder', version = '1.0.0')

plugin = new CompactYangPlugin()
builder = new org.bitbucket.novakmi.yangbuilder.YangBuilder(2, plugin)
plugin.declareCommonAliasesAndQuotes()

moduleName = "ietf-isis-2"


builder.module(moduleName) {
    namespace "urn:ietf:params:xml:ns:yang:ietf-isis", nlLevel: true
    prefix "isis"

    import_ "ietf-routing", prefix: "rt"
    import_ "ietf-inet-types", prefix: "inet"
    import_ "ietf-yang-types", prefix: "yang"

    organization "IETF ISIS Working Group"

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
                    ISIS common across all of the vendor implementations.'''

    def isis_revision = { date, descr, ref = null ->
        revision "$date", {
            def descrText = '"\n'
            descr.split('\n').each { l -> descrText += "* ${l.trim()}\n" }
            descrText += '"'
            description descrText
            if (ref) {
                reference ref
            }
        }
    }

    isis_revision("2014-12-15",'''Adding IPFRR
                                  Adding igp-ldp sync
                                  Adding segment routing
                                  Adding instance reference to operational states.''')

    isis_revision("2014-10-24",'''Change hello-padding to container
                                  Change bfd to container
                                  Make BFD a feature
                                  Creates mpls-te container and put router-id inside
                                  Remove GR helper disable and timers''',  "draft-ietf-isis-yang-isis-cfg-01")

    revision "2014-10-21", {
        description '''
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
                    '''

         reference '""'
    }

    revision "2014-10-07", {
        description '''
                    * Removed spf parameters (should be part of
                      vendor specific extensions.
                    * Removed hello parameters at global level.
                    * Interface configuration uses a string rather
                      than a reference. This permits to map to some
                      vendor specific configuration.
                    '''
        reference "draft-ietf-isis-yang-isis-00"
    }
    revision "2014-09-26", {
        description \
        '''
            * Add BFD support
            * remove max-elements to max-area-addresses
            '''
        reference '""'
    }
    revision "2014-09-11", {
        description \
        '''
            * Add level parameter to ispf and spf delay
            * Add LSP generation as a feature
            * Make lsp-refresh a feature
            * Change parameter container to list
            '''
        reference '""'
    }
    revision "2014-09-05", {
        description " Rewrite of the global hierarchy."
        reference '""'
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
            '''
        reference "RFC XXXX: YANG Data Model for ISIS Protocol"
    }
    revision "2014-06-25", {
        description \
            '''
            * isis-cfg renamed to isis.
            * Add precisions on authentication-keys in description
            '''
        reference "draft-litkowski-isis-yang-isis-01"
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
                '''
        reference "draft-litkowski-isis-yang-isis-00"
    }

    revision "2014-06-11",  description: "Initial revision.",  reference: "draft-litkowski-netmod-isis-cfg-00"

    identity "isis", base: "rt:routing-protocol", description: "Identity for the ISIS routing protocol."

    identity "isis-adjacency-change", description: '''Identity for the ISIS routing protocol
                                                      adjacency state.'''

    identity "clear-isis-database", description: '''Identity for the ISIS routing protocol
                                                    database reset action.'''

    identity "clear-isis-adjacency", description: '''Identity for the ISIS routing protocol
                                                     adjacency reset action.'''

    feature "igp-ldp-sync", description: "Support of RFC5443."
    feature "lfa", description: "Support of Loop Free Alternates."
    feature "remote-lfa", description: "Support of remote Loop Free Alternates."
    feature "segment-routing", description: "Support of Segment Routing."
    feature "bfd", description: "Support of BFD."
    feature "overload-max-metric", description: '''Support of overload by setting
                                                   all links to max metric.'''
    feature "prefix-tag", description: "Add 32bit tag to prefixes"
    feature "prefix-tag64", description: "Add 64bit tag to prefixes"
    feature "reference-bandwidth", description: "Use a reference bandwidth to compute metric."
    feature "ipv4-router-id", description: "Support of IPv4 router ID configuration under ISIS."
    feature "ipv6-router-id", description: "Support of IPv6 router ID configuration under ISIS."
    feature "multi-topology", description: "Multitopology routing support."
    feature "nlpid-control", description: '''This feature controls the advertisement
                                             of support NLPID within ISIS configuration.'''
    feature "graceful-restart", description: "Graceful restart support as per RFC5306."
    feature "lsp-refresh", description:  "Configuration of LSP refresh interval."
    feature "maximum-area-addresses", description: "Support of maximum-area-addresses config."

    typedef "instance-state-ref", {
        type "leafref", path: "/rt:routing-state/rt:routing-instance/rt:routing-protocols/rt:routing-protocol/rt:name"
        description '''This type is used for leaves that reference state data of
                       an ISIS protocol instance.'''
    }

    def enum_descr = {e, descr -> enum_ e, description:  descr}
    def state_typedef = {state ->
        typedef "${state}-state",description: "${state=="admin"?"Administrative":"Operational"} state of a component.", {
            type "enumeration", {
                enum_descr "up", "Up state"
                enum_descr "down", "Down state"
            }
        }
    }

    state_typedef("admin")
    state_typedef("oper")

    typedef "circuit-id",  type: "uint8",  description:  '''This type defines the circuit ID
                                                            associated with an interface.'''

    typedef "extended-circuit-id", type: "uint32",description: '''This type defines the extended circuit ID
                                                                   associated with an interface.'''

    typedef "interface-type", {
       type "enumeration", {
            enum_descr "broadcast", '''Broadcast interface type.
                                       Would result in DIS election.'''
            enum_descr "point-to-point", "Point to point interface type."
        }
        description '''This type defines the type of adjacency
                        to be established on the interface.
                        This is affecting the type of hello
                        message that would be used.'''
    }

    typedef "authentication-type",  description: "This type defines available authentication types.", {
       type "enumeration", {
            enum_descr "none", "No authentication used."
            enum_descr "plaintext", "Plain text password used."
            enum_descr "message-digest", "MD5 digest used."
        }
    }

    typedef "level", default_: "level-all", description: "This type defines ISIS level of an object.", {
        type "enumeration", {
            enum_descr "level-1", "This enum describes L1 only capability."
            enum_descr "level-2", "This enum describes L2 only capability."
            enum_descr "level-all", "This enum describes both levels capability."
        }
    }

    typedef "level-number", description: "This type defines a current ISIS level.", {
        type "uint8",range: "1..2"
    }

    typedef "lsp-id", {
        type "string", pattern: "[0-9A-Fa-f],{4}\\.[0-9A-Fa-f],{4}\\.[0-9A-Fa-f],{4}\\.[0-9][0-9]-[0-9][0-9]"
        description '''This type defines ISIS LSP ID using pattern,
                        system id looks like : 0143.0438.AeF0.02-01'''
    }

    typedef "area-address", description: "This type defines the area address.", {
        type "string", pattern: "[0-9A-Fa-f],{2}\\.([0-9A-Fa-f],{4}\\.),{0,3}"
    }

    typedef "snpa", description: "This type defines Subnetwork Point of Attachement format.", {
        type "string", length: "0..20"
    }

    typedef "system-id", {
        type "string", pattern: "[0-9A-Fa-f],{4}\\.[0-9A-Fa-f],{4}\\.[0-9A-Fa-f],{4}\\.00"
        description '''This type defines ISIS system id using pattern,
                        system id looks like : 0143.0438.AeF0.00'''
    }

    typedef "wide-metric", {
        type "uint32", range: "0..16777215"
        description '''This type defines wide style format
                       of ISIS metric.'''
    }

    typedef "std-metric", {
        type "uint8", range: "0..63"
        description: '''This type defines old style format
                        of ISIS metric.'''
    }

    typedef "mesh-group-state", description: "This type describes meshgroup state of an interface", {
        type "enumeration", {
            enum_descr "meshInactive", "Interface is not part of a mesh group."
            enum_descr "meshSet", "Interface is part of a mesh group."
            enum_descr "meshBlocked", "LSPs must not be flooded over that interface."
        }
    }

    def grouping_if_inst = {kind -> //closure reuse
        grouping "notification-${kind}-hdr", {
            description "This group describes common ${kind} "+ '''specific
                         data for notifications.'''
            leaf "${kind}-name", type: "string", description: "Describes the name of the ISIS ${kind}."
            leaf "${kind}-level", type: "level", description: "Describes the ISIS level of the ${kind}."
            if (kind == "interface") {
                leaf "extended-circuit-id", type: "extended-circuit-id",
                    description: "Describes the extended circuit-id of the interface."
            }
        }
    }
    grouping_if_inst("instance")
    grouping_if_inst("interface")

    grouping "route-content", description: "This group add isis-specific route properties.", {
        leaf "metric", type: "uint32",description: "This leaf describes ISIS metric of a route."
        leaf_list "tag", type: "uint64", description: '''This leaf describes list of tags associated
                                                          with the route. The leaf describes both
                                                          32bits and 64bits tags.'''
        leaf "route-type", description: "This leaf describes the type of ISIS route.", {
           type "enumeration", {
               enum_descr "l2-up-internal", '''Level 2 internal route
                                               and not leaked to a lower level'''
               enum_descr "l1-up-internal", '''Level 1 internal route
                                               and not leaked to a lower level'''
               enum_descr "l2-up-external", '''Level 2 external route
                                               and not leaked to a lower level'''
               enum_descr "l1-up-external", '''Level 1 external route
                                               and not leaked to a lower level'''
               enum_descr "l2-down-internal", '''Level 2 internal route
                                                 and leaked to a lower level'''
               enum_descr "l1-down-internal", '''Level 1 internal route
                                                 and leaked to a lower level'''
               enum_descr "l2-down-external", '''Level 2 external route
                                                 and leaked to a lower level'''
               enum_descr "l1-down-external", '''Level 1 external route
                                                 and leaked to a lower level'''
            }
        }
    }

    augment "/rt:routing-state/rt:ribs/rt:rib/rt:routes/rt:route", {
        when "rt:source-protocol = 'isis:isis'", description: "ISIS-specific route attributes."
        uses "route-content"
        description '''This augments route object in RIB with ISIS-specific
                       attributes.'''
    }

    augment "/rt:active-route/rt:output/rt:route", uses: "route-content", description: "ISIS-specific route attributes."

    def leaf_up_down = {leaf "up-down", type: "boolean", description: "This leaf expresses the value of up/down bit."}
    def ip_prefix_len = {afi = 4 ->
        leaf "ip-prefix", type: "inet:ipv${afi}-address", description: "This leaf describes the IPv${afi} prefix"
        leaf "prefix-len", type: "uint8", description: "This leaf describes the IPv${afi} prefix len in bits"
    }
    def leaf_metric = { kind = "default" -> leaf "${kind == "default" ? "default-" : ""}metric", type: "${kind == "wide" ? kind : "std"}-metric",
        description: "This leaf describes the ISIS ${kind != "wide" ? "${kind} " : ""}metric value" }

    def ie_and_metrics = {
        leaf "i-e", type: "boolean", description: "This leaf expresses the value of I/E bit."
        leaf_metric "default"
        ["delay", "expense", "error"].each { kind ->
            container "${kind}-metric", description: "This container defines the ISIS ${kind} metric.", {
                leaf_metric(kind)
                leaf "supported", type: "boolean", default_: "false",
                    description: "This leaf describes if the metric is supported."
            }
        }
    }

    grouping "prefix-ipv4-std", {
        description '''This group defines attributes of an
                        IPv4 standard prefix.'''
        leaf_up_down()
        ip_prefix_len()
        ie_and_metrics()
    }

    def leaf_tag = { bits = 32 ->
        leaf_list "tag${bits == 64 ? "64" : ""}", type: "uint${bits}", {
            description "This leaf describes a list of ${bits == 64 ? "64-bit" : ""} tags " + '''associated with
                            the prefix.'''
        }
    }
    [4, 6].each { afi ->
        grouping "prefix-ipv${afi}-extended", {
            description '''This group defines attributes of an
                        IPv4 extended prefix.'''
            leaf_up_down()
            ip_prefix_len(afi)
            leaf_metric("wide")
            leaf_tag()
            leaf_tag(64)
        }
    }

    grouping "neighbor-extended", {
        description  '''This group defines attributes of an
                        ISIS extended neighbor.'''
        leaf "neighbor-id", type: "system-id", description: "This leaf describes the system-id of the neighbor."
        leaf_metric("wide")
    }

    grouping "neighbor", {
        description '''This group defines attributes of an
                        ISIS standard neighbor.'''
        leaf "neighbor-id", type: "system-id", description: "This leaf describes the system-id of the neighbor."
        ie_and_metrics()
    }

    grouping "database", {
        def make_bit = {name, descr ->
            bit name,  description: "If set, the originator " + descr
        }
        def make_bit_overload = {
            make_bit "OVERLOAD", '''is overloaded,
                                        and must be avoided in path calculation.'''
        }
        description  '''This group defines attributes of an
                        ISIS database (Link State DB).'''
        leaf "lsp-id", type: "lsp-id", description: "This leaf describes the LSP ID of the LSP."
        leaf "checksum", type: "uint16", description: "This leaf describes the checksum of the LSP."
        leaf "remaining-lifetime", type: "uint16",  units: "seconds", description: '''This leaf describes the remaining lifetime
                                                                                      in seconds before the LSP expiration.'''
        leaf "sequence", type: "uint32", description: "This leaf describes the sequence number of the LSP."
        leaf "attributes",  description: "This leaf describes attributes of the LSP.", {
            type "bits", {
                make_bit "PARTITIONNED", '''supports partition
                                            repair.'''
                ["ATTACHED-ERROR", "ATTACHED-EXPENSE", "ATTACHED-DELAY", "ATTACHED-DEFAULT"].each {name ->
                    make_bit name, '''is attached to
                                      another area using the referred metric.'''
                }
                make_bit_overload()
            }
        }

        def isis_ref = {ref ->"ISIS reference is TLV ${ref}."}
        def leaf_mt = {
            leaf "MT-ID", {
                type "uint16", range: "0..4095"
                description '''This leaf defines the identifier
                               of a topology.'''
            }
        }

        def neighbors_container = {kind = null, ref = 2 ->
            container "${kind?"${kind}-":""}is-neighbor", {
                def uses = kind?"-${kind}":""
                def descr = kind ? "${kind} " : ""
                if (kind == "mt") {
                    uses = "-extended"
                    descr = "multi-topology "
                }
                list "neighbor", key: "neighbor-id", uses: "neighbor${uses}", description: "List of neighbors.", {
                    if (kind == "mt") leaf_mt()
                }
                description "This container describes list of ISIS ${descr}" + '''neighbors.
                             ''' + isis_ref(ref)
            }
        }
        neighbors_container()

        container "authentication", {
            leaf "authentication-type", type: "authentication-type", description:  '''This leaf describes the authentication type
                                                                                       to be used.'''
            leaf "authentication-key", type: "string", description:  '''This leaf describes the authentication key
                                                                        to be used. For security reason, the
                                                                        authentication key MUST NOT be presented
                                                                        in plaintext format. Authors recommends
                                                                        to use MD5 hash to present the authentication-key.'''
            description ''' This container describes authentication
                            information of the node. ''' + isis_ref(10)
        }
        neighbors_container("extended", 22)

        def reachability = {kind, ref ->
            container "${kind == "extended"?"${kind}-":""}ipv4${kind != "extended"?"-${kind}":""}-reachability", {
                list "prefixes", key: "ip-prefix", uses: "prefix-ipv4-${kind == "extended"?"${kind}":"std"}", description: "List of prefixes."
                description "This container describes list of IPv4 ${kind}"+'''
                           reachability information.
                            ''' + isis_ref(ref)
            }
        }

        reachability "internal", 128

        leaf_list "protocol-supported",  type: "uint8", {
            description  '''This leaf describes the list of
                            supported protocols.
                            ''' + isis_ref(129)
        }

        reachability "external", 130
        def make_ip_address = { afi, ref ->
            leaf_list "ipv${afi}-addresses", type: "inet:ipv${afi}-address", {
                description "This leaf describes the IPv${afi} " + '''addresses of the node.
                           ''' + isis_ref(ref)
            }
        }
        make_ip_address 4, 132

        def router_id = { afi, ref  ->
            leaf "ipv${afi}-te-routerid", type: "inet:ipv${afi}-address", {
                description "This leaf describes the IPv${afi} "+ '''Traffic Engineering
                            router ID of the node.
                            ''' + isis_ref(ref)
            }
        }
        router_id(4, 134)
        reachability "extended", 135

        leaf "dynamic-hostname", type: "string", description: '''This leaf describes the name of the node.
                                                                ''' + isis_ref(137)
        router_id(6, 140)
        neighbors_container "mt", 223

        container "mt-entries", {
             list "topology",  key: "MT-ID", description: "List of topologies supported.",{
                leaf_mt()
                leaf "attributes", {
                    type "bits", {
                        make_bit_overload()
                        make_bit "ATTACHED", '''is attached to
                                                another area using the referred metric.'''
                    }
                    description '''This leaf describes attributes of the LSP
                                    for the associated topology.'''
                }
            }
            description '''This container describes the topology supported.
                           ''' + isis_ref(229)
        }

        make_ip_address 6, 232

        def make_prefixes = {afi = 4, mt = true ->
            list "prefixes", key: "ip-prefix", uses: "prefix-ipv${afi}-extended", description: "List of prefixes.", {
                if (mt) leaf_mt()
            }
        }

        container "mt-extended-ipv4-reachability", {
            make_prefixes()
            description '''This container describes list of IPv4
                            reachability information in multi-topology
                            environment.
                            ''' + isis_ref(235)
        }

        container "mt-ipv6-reachability", {
            make_prefixes(6)
            description '''This container describes list of IPv6
                            reachability information in multi-topology
                            environment.
                            ''' + isis_ref(237)
        }

        container "ipv6-reachability", {
            make_prefixes(6, false)
            description '''This container describes list of IPv6
                            reachability information.
                         ''' + isis_ref(236)
        }

        container "router-capabilities", {
            leaf "binary", type: "binary", description: '''This leaf describes the capability of the node.
                                                           Format is binary according to the protocol encoding.'''
            description '''This container describes the capabilities of the node.
                           This container may be extended with detailed
                           information.
                           ''' + isis_ref(242)
        }
    }

    def leaf_boolean = { name, descr, defv = null ->
        leaf name, type: "boolean", description: descr, {
            if (defv != null) {default_ defv}
        }
    }
    def leaf_enabled = {descr, tr = null ->  leaf_boolean("eanbled", descr, tr) }
    def leaf_level = {descr->  leaf "level", type: "level", description: descr}
    def leaf_level_num = {descr->  leaf "level", type: "level-number", description: descr}
    def leaf_level_appl = { kind = null -> leaf_level "Level applicability${kind == "metric" ? " of the metric" : ""}." }
    augment "/rt:routing/rt:routing-instance/rt:routing-protocols/" + "rt:routing-protocol", {
        when "rt:type = 'isis:isis'", description: '''This augment is only valid when routing protocol
                                                       instance type is isis.'''
        description '''This augments a routing protocol instance with ISIS
                        specific parameters.'''

        container "isis", description: '''This container defines ISIS specific configuration
                                          objects.''', {
            list "instance", key: "routing-instance", description: "List of ISIS instances.", {
                must "count(area-address) > 0", description: "Enforce configuration of at least one area.",
                    error_message: '''At least one area-address
                                     must be configured.'''

                leaf "routing-instance", type: "rt:routing-instance-ref",
                    description: '''Reference routing instance.
                                    For protocol centric model, which is
                                    supported in
                                    default-instance only, this could reference
                                    any VRF routing-instance.
                                    For VRF centric model, must reference the
                                    enclosing routing-instance.'''

                leaf "level-type", type: "level", default_: "level-all",
                    description: '''This leaf describes the type of ISIS node.
                                    A node can be level-1-only, level-2-only
                                    or level-1-2.'''

                leaf "system-id", type: "system-id", description: "This leaf defines the system-id of the node."

                leaf "maximum-area-addresses", if_feature: "maximum-area-addresses", type: "uint8", default_: 3,
                    description: "Defines the maximum areas supported."

                leaf_list "area-address", type: "area-address", description: '''List of areas supported by the
                                                                                 protocol instance.'''

                container "mpls-te", description: "This container handles mpls te config.", {
                    [4, 6].each { afi ->
                        leaf "ipv${afi}-router-id", if_feature: "ipv${afi}-router-id", type: "inet:ipv${afi}-address",
                            description: '''Router ID value that would be used in
                                            ''' + "TLV ${afi == 4 ? 134 : 140}."
                    }
                }
                leaf "reference-bandwidth", if_feature: "reference-bandwidth", type: "uint32", units: "bps",
                    description: '''This leaf defines the bandwidth for calculating
                                    metric.'''

                ["mtu", "lifetime", "refresh"].each { type ->
                    def unts = type == "mtu" ? "bytes" : "seconds"
                    def descr = "lifetime of the router\nLSP in seconds."
                    leaf "lsp-${type}", type: "uint16", units: unts, {
                        if (type == "mtu") {
                            descr = "maximum size of a\nLSP PDU in bytes."
                            default_ 1492
                        }
                        if (type == "refresh") {
                            descr = "refresh interval of the\nrouter LSP in seconds."
                            if_feature "lsp-refresh"
                        }
                        description "This leaf describes the " + descr
                    }
                }

                container "graceful-restart", if_feature: "graceful-restart", description: "This container activates graceful restart.", {
                    leaf_enabled "Control enabling the feature."
                }

                container "fast-reroute",
                    description: '''This container needs to be
                                    augmented with global parameters
                                    for IPFRR.'''

                container "segment-routing", if_feature: "segment-routing", description: "segment routing global config.", {
                    leaf "transport-type", description: "Dataplane to be used.", {
                        type "enumeration", enums: ["mpls"] //enums without description can be put into array
                    }
                    list "srgb", key: "lower-bound upper-bound", description: "List of global blocks to be advertised.", {
                        ["lower", "upper"].each { bnd ->
                            leaf "${bnd}-bound", type: "uint32", description: "${bnd.capitalize()} value in the block."
                        }
                    }
                }
                list "authentication", key: "level", description: '''Container for ISIS authentication.
                                                                     It covers both LSPs and SNPs.''', {

                    leaf "key", type: "string", description: '''This leaf describes the
                                                                authentication key.'''
                    leaf "type", type: "authentication-type", description: '''This leaf describes the authentication
                                                                                type to be used.'''
                    leaf_level_appl()
                }


                list "metric-type", key: "level", description: "Metric style list.", {
                    leaf "value", {
                        type "enumeration", {
                            enum_descr "wide-only", ''''Advertise new metric style only
                                                        (RFC5305)'''
                            enum_descr "old-only", '''Advertise old metric style only
                                                      (RFC1195)'''
                            enum_descr "both", '''Advertise both metric
                                                  styles'''
                        }
                        description '''This leaf describes the type of metric
                                       to be generated.
                                       Wide-only means only new metric style
                                       is generated,
                                       old-only means that only old style metric
                                       is generated,
                                       and both means that both are advertised.
                                       This leaf is only affecting IPv4 metrics.'''
                    }
                    leaf_level_appl()
                }
                list "preference", key: "level", description: "This list defines the protocol preference.", {
                    def pref_leaf = { kind ->
                        leaf kind, type: "uint8", description: '''This leaf defines the protocol
                                                                  preference for ''' + "${kind == "default" ? "all ISIS" : kind} routes."
                    }
                    choice "granularity", description: "Choice for implementation of route preference.", {
                        case_ "detail", {
                            pref_leaf("internal")
                            pref_leaf("external")
                        }
                        case_ "coarse", {
                            pref_leaf("default")
                        }
                    }
                    leaf_level_appl()
                }

                def default_metric_list = {
                    list "default-metric", key: "level", description: "Defines the metric to be used by default.", {
                        leaf "value", type: "wide-metric", description: "Value of the metric"
                        leaf_level_appl("metric")
                    }
                }
                default_metric_list()
                def leaf_af = { leaf "af", type: "string", description: "Address-family" }
                def leaf_enabled_af = {
                    leaf_enabled '''Describes the activation state of the
                                   AF.'''
                }
                list "af", if_feature: "nlpid-control", key: "af", {
                    leaf_af()
                    leaf_enabled_af()
                    description '''This list permits activation
                                    of new address families.'''
                }

                list "topologies", if_feature: "multi-topology", key: "name", description: "List of topologies", {
                    leaf_enabled_af()
                    leaf "name", type: "rt:rib-ref", description: "RIB"
                    default_metric_list()
                }

                ["overload", "overload-max-metric"].each { kind ->
                    list kind, key: "level", {
                        if (kind != "overload") {
                            if_feature kind
                        }

                        leaf_boolean "status", "This leaf defines the overload status."
                        leaf "timeout", type: "uint16", units: "seconds",
                            description: '''This leaf defines the timeout in seconds
                                             of the overload condition.'''
                        leaf_level_appl("metric")
                        description '''This leaf describes if the router is
                                        set to overload state.'''
                    }
                }

                container "interfaces", description: '''This container defines ISIS interface specific
                                   configuration objects.''', {
                    list "interface", key: "name", description: "List of ISIS interfaces.", {
                        leaf "name", type: "string", description: '''Reference to the interface within
                                                                        the routing-instance.'''
                        leaf "level-type", type: "level", default_: "level-all",
                            description: '''This leaf defines the associated ISIS
                                            level of the interface.'''
                        leaf_interval = { kind, unts, descr ->
                            leaf "${kind}-interval", type: "uint16", units: unts,
                                description: "This leaf defines the interval " + descr
                        }
                        leaf_interval "lsp-pacing", "miliseconds", '''between
                                                        LSP transmissions in milli-seconds'''

                        leaf_interval "lsp-retransmit", "seconds", '''between
                                                                     retransmission of LSP'''
                        leaf_boolean "passive", ''''This leaf defines if interface is in
                                                     passive mode (ISIS not running,
                                                     but network is advertised).''', false

                        leaf_interval "csnp", "seconds", "of CSNP  messages."


                        container "hello-padding", description: '''This container handles ISIS hello padding
                                                                    configuration.''', {
                            leaf_enabled '''Status of Hello-padding activation.
                                             By default, the implementation shall
                                             pad HELLOs.''', true

                        }

                        leaf "mesh-group-enabled", type: "mesh-group-state",
                            description: '''Describes the mesh group state of
                                            the interface.'''

                        leaf "mesh-group", type: "uint8", description: '''Describes the mesh group ID of
                                                                         the interface.''', {
                            when "../mesh-group-enabled = meshSet",
                                description: '''Only valid when mesh-group-enabled
                                                 equals meshSet'''
                        }

                        leaf "interface-type", type: "interface-type",
                            description: '''This leaf defines the type of adjacency
                                            to be established on the interface.
                                            This is affecting the type of hello
                                            message that would be used.'''

                        leaf_enabled '''This leaf describes the administrative
                                      status of the ISIS interface.''', true

                        [32, 64].each { t ->
                            leaf_list "tag${t == 32 ? "" : 64}", if_feature: "prefix-tag${t == 32 ? "" : 64}", type: "uint${t}",
                                description: "This leaf defines list of ${t == 32 ? "" : "64bits "}" + '''tags associated
                                                with the interface.'''

                        }

                        list "hello-authentication", key: "level", {
                            leaf "type", type: "authentication-type",
                                description: '''This leaf describes the authentication
                                                 type to be used in hello messages.'''
                            leaf "key", type: "string", description: '''This leaf describes the
                                                                          authentication key
                                                                          to be used in hello messages.
                                                                          For security reason, the
                                                                          authentication key MUST
                                                                          NOT be presented
                                                                          in plaintext format upon a
                                                                          get-config reply.
                                                                          Authors recommends
                                                                          to use MD5 hash to present the
                                                                          authentication-key'''
                            leaf_level_appl()
                            description '''This list describes the authentication type
                                            to be used in hello messages.'''
                        }

                        list "hello-interval", key: "level", {
                            leaf "value", type: "uint16", units: "seconds",
                                description: '''This leaf defines the interval of
                                                hello messages.'''
                            leaf_level_appl()
                            descriptionS '''This list defines the interval of
                                            hello messages.'''
                        }
                        list "hello-multiplier", key: "level", {

                            leaf "value", type: "uint16", description: '''This leaf defines the number of
                                                                         hello failed to be received before
                                                                         declaring the adjacency down.'''
                            leaf_level_appl()
                            description ''''This list defines the number of
                                            hello failed to be received before
                                            declaring the adjacency down.'''
                        }
                        list "priority", must: 'interface-type = "broadcast"', key: "level", {
                            leaf "value", {
                                type "uint8", range: "0..127"
                                description ''''This leaf describes the priority of
                                                the interface
                                                for DIS election.'''
                            }
                            leaf_level_appl()
                            description '''This list describes the priority of
                                            the interface
                                            for DIS election.'''
                        }
                        def list_metric = {
                            list "metric", key: "level", description: "Container for interface metric", {
                                leaf "value", type: "wide-metric", description: "Metric value."
                                leaf_level_appl()
                            }
                        }
                        list_metric()
                        list "af", key: "af", description: "List of AFs.", {
                            leaf_af()
                            container "segment-routing", if_feature: "segment-routing",
                                description: "Segment routing interface configuration.", {
                                list "prefix-sid", key: "index", {
                                    leaf "index", type: "uint32", description: '''Index associated with
                                                                                    prefix.'''
                                    leaf_boolean "node-flag", '''Set prefix as a node
                                                                  representative prefix.''', true
                                    leaf_boolean "explicit-null", '''Force explicit NULL
                                                                     forwarding for this SID.'''
                                    leaf_boolean "php", '''Activates PHP for this
                                                           SID.'''
                                    description '''List of prefix-SID associated with
                                                    the interface.'''
                                }
                            }
                            container "bfd", if_feature: "bfd", description: '''The container describes
                                                                                BFD config.''', {
                                leaf_enabled "This leaf enables BFD.", false
                            }
                        }
                        list "topologies", key: "name", description: "List of topologies.", {
                            leaf "name", type: "rt:rib-ref", description: "Name of RIB."
                            list_metric()
                        }
                        container "fast-reroute", description: "Fast-reroute configuration.", {
                            container "lfa", if_feature: "lfa", description: "LFA configuration.", {
                                leaf_boolean "candidate-disabled", "Prevent the interface to be used as backup."
                                leaf_enabled "Activates LFA."
                                container "remote-lfa", if_feature: "remote-lfa", description: "remote LFA configuration.", {
                                    leaf_enabled "Activates rLFA."
                                }
                            }
                        }
                    }
                    container "igp-ldp-sync", if_feature: "igp-ldp-sync", description: "IGP-LDP sync configuration.", {
                        leaf "holdtime", type: "uint16", description: "Time to wait in sec for LDP session setup."
                    }
                }
            }
        }
    }

    augment "/rt:routing-state/rt:routing-instance/rt:routing-protocols/rt:routing-protocol", {
        when "rt:type = 'isis:isis'", description: '''This augment is only valid when routing protocol
                                                       instance type is isis.'''

        description '''This augments routing protocol instance states with ISIS
                        specific parameters.'''
        container "isis", config: false, description: "This container defines various ISIS states objects.", {
            list "instance", key: "routing-instance", description: "List of ISIS instances.", {
                leaf "routing-instance", type: "rt:routing-instance-ref", {
                    description '''Reference routing instance.
                                    For protocol centric model, which is
                                    supported in
                                    default-instance only, this could reference
                                    any VRF routing-instance.
                                    For VRF centric model, must reference the
                                    enclosing routing-instance.'''
                }
                def leaf_uint32 = { name, descr -> leaf name, type: "uint32", description: descr }
                def level_isis = { leaf_level_num "This leaf describes the ISIS level." }
                def leaf_timestamp = {name, descr -> leaf name, type: "yang:timestamp", description: descr }
                container "system-counters", description: '''The container defines a list of counters
                                                             for the IS.''', {
                    list "level", key: "level", description: "List of supported levels.", {
                        level_isis()
                        leaf_uint32 "corrupted-lsps", '''Number of corrupted in-memory LSPs detected.
                                                          LSPs received from the wire with a bad
                                                          checksum are silently dropped and not counted.
                                                          LSPs received from the wire with parse errors
                                                          are counted by lsp-errors.'''
                        leaf_uint32 "authentication-type-fails", "Number of authentication type mismatches."
                        leaf_uint32 "authentication-fails", "Number of authentication key failures."
                        leaf_uint32 "database-overload", '''Number of times the database has become
                                                            overloaded.'''
                        leaf_uint32 "own-lsp-purge", '''Number of times a zero-aged copy of the
                                                        system's own LSP is received from some
                                                        other node.'''
                        leaf_uint32 "manual-address-drop-from-area", '''Number of times a manual address
                                                                        has been dropped from the area.'''
                        leaf_uint32 "max-sequence", '''Number of times the system has attempted
                                                to exceed the maximum sequence number.'''
                        leaf_uint32 "sequence-number-skipped", '''Number of times a sequence number skip has
                                                                    occured.'''
                        leaf_uint32 "id-len-mismatch", '''Number of times a PDU is received with
                                                          a different value for ID field length
                                                          from that of the receiving system.'''
                        leaf_uint32 "partition-changes", "Number of partition changes detected."
                        leaf_uint32 "lsp-errors", '''Number of LSPs with errors we have
                                                      received.'''
                        leaf_uint32 "spf-runs", "Number of times we ran SPF at this level."
                    }
                }
                def leaf_interface = {
                    leaf "interface", type: "string", description: '''This leaf describes the name
                                                                           of the interface.'''
                }
                container "interface-counters", description: '''The container defines a list of counters
                                                                for interfaces.''', {
                    list "interface", key: "interface", description: "List of interfaces.", {
                        leaf_interface()
                        leaf_uint32 "adjacency-changes", '''The number of times an adjacency state
                                                             change has occured on this interface.'''
                        leaf_uint32 "adjacency-number", '''The number of adjacencies on this
                                                            interface.'''
                        leaf_uint32 "init-fails", '''The number of times initialization of
                                                     this interface has failed. This counts
                                                     events such as PPP NCP failures.
                                                     Failures to form an adjacency are counted
                                                     by adjacency-rejects.'''
                        leaf_uint32 "adjacency-rejects", '''The number of times an adjacency has been
                                                           rejected on this interface.'''
                        leaf_uint32 "id-len-mismatch", '''The number of times an IS-IS PDU with an ID
                                                   field length different from that for this
                                                   system has been received on this interface.'''
                        leaf_uint32 "max-area-addresses-mismatch", '''The number of times an IS-IS PDU with
                                                                        according max area address field
                                                                        differs from that for
                                                                        this system has been received on this
                                                                        interface.'''
                        leaf_uint32 "authentication-type-fails", "Number of authentication type mismatches."
                        leaf_uint32 "authentication-fails", "Number of authentication key failures."
                        leaf_uint32 "lan-dis-changes", '''The number of times the DIS has changed
                                                    on this interface at this level.
                                                    If the interface type is point to point,
                                                    the count is zero.'''
                    }
                }
                container "packet-counters", description: "The container defines a list of PDU counters.", {
                    list "level", key: "level", description: "List of supported levels.", {
                        level_isis()
                        ["iih", "ish", "esh", "lsp", "psnp", "csnp", "unknown"].each { c ->
                            container c, {
                                leaf_uint32 "in", "Received PDUs."
                                leaf_uint32 "out", "Sent PDUs."
                                description "The number of ${c != "unknown" ? c.toUpperCase() : c} PDUs received/sent."
                            }
                        }
                    }
                }
                container "interfaces", description: '''The container defines operational parameters
                                                        of interfaces.''', {
                    list "interfaces", key: "interface", description: "List of interfaces.", {
                        leaf_interface()
                        leaf "circuit-id", type: "circuit-id", description: '''This leaf describes the circuit-id
                                                                                associated with the interface.'''
                        leaf "admin-state", type: "admin-state", description: '''This leaf describes the admin state
                                                                                 of the interface.'''
                        leaf "oper-state", type: "oper-state", description: '''This leaf describes the operational state
                                                                               of the interface.'''
                        leaf "interface-type", type: "interface-type", description: "Type of interface to be used."
                        leaf_level "Level associated with the interface."
                        leaf "passive", type: "empty", description: '''The interface is included in LSP, but
                                                                         does not run ISIS protocol.'''
                        leaf "three-way-handshake", type: "empty", description: "The interface uses 3-way handshake."
                    }
                }
                 container "adjacencies", description: '''This container lists the adjacencies of
                                                          the local node.''', {
                     list "adjacency", description: "List of operational adjacencies.", {  //TODO key element??
                        leaf_interface()
                        leaf_level '''This leaf describes the associated
                                      ISIS level of the interface.'''
                        leaf "neighbor-sysid", type:"system-id",  description: "The system-id of the neighbor"
                        leaf "neighbor-extended-circuit-id", type: "extended-circuit-id", description: "Circuit ID of the neighbor"
                        leaf "neighbor-snpa", type: "snpa",description:  "SNPA of the neighbor"
                        leaf "neighbor-level", type: "level", description: "The type of the neighboring system."
                         leaf "hold-timer", type: "uint16", description: '''The holding time in seconds for this
                                                                            adjacency. This value is based on
                                                                            received hello PDUs and the elapsed
                                                                            time since receipt.'''
                        leaf "neighbor-priority", {
                            type "uint8", range: "0..127"
                            description '''Priority of the neighboring IS for becoming
                                           the DIS.'''
                        }
                        leaf_timestamp "lastuptime", '''When the adjacency most recently entered
                                                        state 'up', measured in hundredths of a
                                                        second since the last reinitialization of
                                                        the network management subsystem.
                                                        The value is 0 if the adjacency has never
                                                        been in state 'up'.'''

                        leaf "state",  description: '''This leaf describes the state of the
                                                        interface.''', {
                           type "enumeration", {
                               ["Up", "Down", "Init"].each {e->
                                   enum_descr e, '''This state describes that
                                                    adjacency is ''' + "${e=="Down"?"NOT ":""}establish${e == "Init"?"ing":"ed"}."
                               }
                            }
                        }
                    }
                }
                container "spf-log", description: "This container lists the SPF computation events.", {
                    list "event", key: "id", description: "List of computation events.", {
                        leaf_uint32 "id", '''This leaf defines the event identifier.
                                             This is a purely internal value.'''
                        leaf "spf-type", description: '''This leaf describes the type of computation
                                                         used.''', {
                            type "enumeration", {
                                enum_descr "full", "Computation done is a Full SPF."
                                enum_descr "incremental", '''Computation done is an
                                                       incremental SPF.'''
                                enum_descr "route-only", '''Computation done is a
                                                            reachability computation
                                                            only.'''
                            }
                        }
                        leaf_level_num '''This leaf describes the level affected by the
                                          the computation.'''
                        leaf "spf-delay", type: "uint32", units: "milliseconds",
                            description: '''This leaf describes the SPF delay that
                                             was used for this event.'''
                        leaf_timestamp "schedule-timestamp", '''This leaf describes the timestamp
                                                                 when the computation was scheduled.'''
                        leaf_timestamp "start-timestamp", '''This leaf describes the timestamp
                                                             when the computation was started.'''
                        leaf_timestamp "end-timestamp", '''This leaf describes the timestamp
                                                            when the computation was ended.'''
                        list "trigger-lsp", key: "lsp", description: '''This leaf describes list of LSPs
                                                                        that triggered the computation.''', {
                            leaf "lsp", type: "lsp-id", description: '''This leaf describes the LSPID
                                                                        of the LSP.'''
                            leaf_uint32 "sequence", '''This leaf describes the sequence
                                                        number of the LSP.'''
                        }
                    }
                }
                container "lsp-log", description: '''This container lists the LSP reception events.
                                                     Local LSP modification are also contained in the
                                                     list.''', {
                    list "event", key: "id", description: "List of LSP events.", {
                        leaf_uint32 "id", '''This leaf defines the event identifier.
                                             This is a purely internal value.'''
                        leaf_level_num '''This leaf describes the level affected by the
                                          the computation.'''
                        container "lsp", description: '''This container describes the received LSP
                                                              , in case of local LSP update the local
                                                                LSP ID is referenced.''', {
                            leaf "lsp", type: "lsp-id", description: '''This leaf describes the LSPID
                                                                        of the LSP.'''
                            leaf_uint32 "sequence", '''This leaf describes the sequence
                                                        number of the LSP.'''
                        }
                        leaf_timestamp "received-timestamp", '''This leaf describes the timestamp
                                                                  when the LSP was received. In case of
                                                                  local LSP update, the timestamp refers
                                                                  to the local LSP update time.'''
                    }
                }
                container "database", description: '''This container describes ISIS Link State
                                                      databases.''', {
                    list "level-db", key: "level", {
                        leaf_level_num "Current level number"
                        list "lsp", key: "lsp-id", uses: "database", description: "List of LSPs in LSDB."
                    }
                }
                container "hostnames",  description: '''This container describes the list
                                                        of binding between system-id and
                                                        hostnames.''', {
                     list "hostname",  key: "system-id", description:  "List of system-id/hostname associations", {
                        leaf "system-id", type: "system-id", description:  '''This leaf describes the system-id
                                                                              associated with the hostname.'''
                        leaf "hostname", type: "string", description: '''This leaf describes the hostname
                                                                         associated with the system ID.'''
                    }
                }
            }
        }
    }

    cmt("RPC methods", inline: false)
    rpc "clear-adjacency", description: '''This RPC request clears a particular
                                            set of ISIS adjacencies. If the operation
                                            fails for ISIS internal reason, then
                                            error-tag and error-app-tag should be set
                                            to a meaningful value.''', {
        input "", {
            leaf "routing-instance-name", type: "rt:routing-instance-state-ref", mandatory: "true",
                description: '''Name of the routing instance whose ISIS
                                information is being queried.

                                If the routing instance with name equal to the
                                value of this parameter doesn't exist, then this
                                operation SHALL fail with error-tag 'data-missing'
                                and error-app-tag 'routing-instance-not-found'.'''
            leaf "routing-protocol-instance-name",  type: "instance-state-ref", mandatory: "true",
                description: '''Name of the ISIS protocol instance whose ISIS
                                information is being queried.

                                If the ISIS instance with name equal to the
                                value of this parameter doesn't exist, then this
                                operation SHALL fail with error-tag 'data-missing'
                                and error-app-tag
                                'routing-protocol-instance-not-found'.'''
            leaf_level '''ISIS level of the adjacency to be cleared.
                          If ISIS level is level-1-2, both level 1 and level 2
                          adjacencies would be cleared.

                          If the value provided is different from the one
                          authorized in the enum type, then this
                          operation SHALL fail with error-tag 'data-missing'
                          and error-app-tag
                          'bad-isis-level'.'''
            leaf "interface", type: "string", description: '''Name of the ISIS interface.

                                                              If the ISIS interface with name equal to the
                                                              value of this parameter doesn't exist, then this
                                                              operation SHALL fail with error-tag 'data-missing'
                                                              and error-app-tag
                                                              'isis-interface-not-found'.'''
        }
    }

    rpc "clear-database", description: '''This RPC request clears a particular
                                          ISIS database. If the operation
                                          fails for ISIS internal reason, then
                                          error-tag and error-app-tag should be set
                                          to a meaningful value.''', {
        input "", {
            leaf "routing-instance-name", type: "rt:routing-instance-state-ref", mandatory: "true",
                description: '''Name of the routing instance whose ISIS
                                information is being queried.

                                If the routing instance with name equal to the
                                value of this parameter doesn't exist, then this
                                operation SHALL fail with error-tag 'data-missing'
                                and error-app-tag 'routing-instance-not-found'.'''
            leaf "routing-protocol-instance-name", type: "instance-state-ref", mandatory: "true",
                description: '''Name of the ISIS protocol instance whose ISIS
                                information is being queried.

                                If the ISIS instance with name equal to the
                                value of this parameter doesn't exist, then this
                                operation SHALL fail with error-tag 'data-missing'
                                and error-app-tag
                                'routing-protocol-instance-not-found'.'''
            leaf_level '''ISIS level of the adjacency to be cleared.
                          If ISIS level is level-1-2, both level 1 and level 2
                          adjacencies would be cleared.

                          If the value provided is different from the one
                          authorized in the enum type, then this
                          operation SHALL fail with error-tag 'data-missing'
                          and error-app-tag
                          'bad-isis-level'.'''
        }
    }


    cmt("Notifications", inline: false)
    notification "database-overload", {
        uses "notification-instance-hdr"

        leaf "overload", {
           type "enumeration", {
                enum_ "off", {
                    description \
                    "The system has left overload condition."
                }
                enum_ "on", {
                    description \
                    "The system is in overload condition."
                }

            }
            description \
            "Describes the new overload state of the instance."
        }
        description \
        '''This notification is sent when an ISIS instance
            overload condition changes.'''
    }

    notification "lsp-too-large", {
        uses "notification-instance-hdr"
        uses "notification-interface-hdr"

        leaf "pdu-size", {
            type "uint32"
            description \
            "Size of the PDU"
        }
        leaf "lsp-id", {
            type "lsp-id"
            description \
            "LSP ID."
        }
        description \
        '''This notification is sent when we attempt
            to propagate an LSP that is larger than the
            dataLinkBlockSize for the circuit.
                The notification generation must be throttled
            with at least a 5 second gap.
            '''
    }

    notification "corrupted-lsp-detected", {
        uses "notification-instance-hdr"
        leaf "lsp-id", {
            type "lsp-id"
            description \
            "LSP ID."
        }
        description \
        '''This notification is sent when we find
            that an LSP that was stored in memory has
            become corrupted.
                '''
    }

    notification "attempt-to-exceed-max-sequence", {
        uses "notification-instance-hdr"
        leaf "lsp-id", {
            type "lsp-id"
            description \
            "LSP ID."
        }
        description \
        '''This notification is sent when the system
            wraps the 32-bit sequence counter of an LSP.
            '''
    }

    notification "id-len-mismatch", {
        uses "notification-instance-hdr"
        uses "notification-interface-hdr"

        leaf "pdu-field-len", {
            type "uint8"
            description \
            "Size of the ID length in the received PDU"
        }
        leaf "raw-pdu", {
            type "binary"
            description \
            "Received raw PDU."
        }
        description \
        '''This notification is sent when we receive a PDU
            with a different value for the System ID length.
                The notification generation must be throttled
            with at least a 5 second gap.
            '''
    }

    notification "max-area-addresses-mismatch", {
        uses "notification-instance-hdr"
        uses "notification-interface-hdr"

        leaf "max-area-addresses", {
            type "uint8"
            description \
            "Received number of supported areas"
        }
        leaf "raw-pdu", {
            type "binary"
            description \
            "Received raw PDU."
        }
        description \
        '''This notification is sent when we receive a PDU
            with a different value for the Maximum Area Addresses.
                The notification generation must be throttled
            with at least a 5 second gap.
            '''
    }

    notification "own-lsp-purge", {
        uses "notification-instance-hdr"
        uses "notification-interface-hdr"
        leaf "lsp-id", {
            type "lsp-id"
            description \
            "LSP ID."
        }
        description \
        '''This notification is sent when the system
            receives a PDU with its own system ID and zero age.
            '''
    }

    notification "sequence-number-skipped", {
        uses "notification-instance-hdr"
        uses "notification-interface-hdr"
        leaf "lsp-id", {
            type "lsp-id"
            description \
            "LSP ID."
        }
        description \
        '''This notification is sent when the system
            receives a PDU with its own system ID and
            different contents. The system has to reissue
            the LSP with a higher sequence number.
            '''
    }

    notification "authentication-type-failure", {
        uses "notification-instance-hdr"
        uses "notification-interface-hdr"
        leaf "raw-pdu", {
            type "binary"
            description \
            "Received raw PDU."
        }
        description \
        '''This notification is sent when the system
            receives a PDU with the wrong authentication type
            field.
                The notification generation must be throttled with
            at least a 5 second gap.
                '''
    }

    notification "authentication-failure", {
        uses "notification-instance-hdr"
        uses "notification-interface-hdr"
        leaf "raw-pdu", {
            type "binary"
            description \
            "Received raw PDU."
        }
        description \
        '''This notification is sent when the system
            receives a PDU with the wrong authentication
            information.
                The notification generation must be throttled with
            at least a 5 second gap.
                '''
    }

    notification "version-skew", {
        uses "notification-instance-hdr"
        uses "notification-interface-hdr"
        leaf "protocol-version", {
            type "uint8"
            description \
            "Protocol version received in the PDU."
        }
        leaf "raw-pdu", {
            type "binary"
            description \
            "Received raw PDU."
        }
        description \
        '''This notification is sent when the system
            receives a PDU with a different protocol version
            number.
                The notification generation must be throttled with at least
            a 5 second gap.
                '''
    }

    notification "area-mismatch", {
        uses "notification-instance-hdr"
        uses "notification-interface-hdr"
        leaf "raw-pdu", {
            type "binary"
            description \
            "Received raw PDU."
        }
        description \
        '''This notification is sent when the system
            receives a Hello PDU from an IS that does
            not share any area address.
            The notification generation must be throttled with at least
            a 5 second gap.
                '''
    }

    notification "rejected-adjacency", {
        uses "notification-instance-hdr"
        uses "notification-interface-hdr"
        leaf "raw-pdu", {
            type "binary"
            description \
            "Received raw PDU."
        }
        leaf "reason", {
            type "string"
            description \
            '''The system may provide a reason to reject the
                adjacency. If the reason is not available,
                    the system use an empty string.'''
        }
        description \
        '''This notification is sent when the system
            receives a Hello PDU from an IS but does not
            establish an adjacency for some reason.
                The notification generation must be throttled with at least
            a 5 second gap.
                '''
    }


    notification "protocols-supported-mismatch", {
        uses "notification-instance-hdr"
        uses "notification-interface-hdr"
        leaf "raw-pdu", {
            type "binary"
            description \
            "Received raw PDU."
        }
        leaf_list "protocols", {
            type "uint8"
            description \
            '''The list of protocols supported by the
                remote system.'''
        }
        description \
        '''This notification is sent when the system
            receives a non pseudonode LSP that has no matching
            protocol supported.
                The notification generation must be throttled with at least
            a 5 second gap.
                '''
    }

    notification "lsp-error-detected", {
        uses "notification-instance-hdr"
        uses "notification-interface-hdr"
        leaf "lsp-id", {
            type "lsp-id"
            description \
            "LSP ID."
        }
        leaf "raw-pdu", {
            type "binary"
            description \
            "Received raw PDU."
        }
        leaf "error-offset", {
            type "uint32"
            description \
            '''If the problem is a malformed TLV,
                the error-offset points to the start of the TLV.
                If the problem is with the LSP header,
                    the error-offset points to the suspicious byte'''
        }
        leaf "tlv-type", {
            type "uint8"
            description \
            '''if the problem is a malformed TLV, the tlv-type is set
                to the type value of the suspicious TLV.
                    Otherwise this leaf is not present.'''
        }
        description \
        '''This notification is sent when the system
            receives a  LSP with a parse error.
            The notification generation must be throttled with at least
            a 5 second gap.
                '''
    }

    notification "adjacency-change", {
        uses "notification-instance-hdr"
        uses "notification-interface-hdr"
        leaf "neighbor", {
            type "string"
            description \
            '''Describes the name of the neighbor. If the
                name of the neighbor is not available, the
                field would be empty.'''
        }
        leaf "neighbor-system-id", {
            type "system-id"
            description \
            "Describes the system-id of the neighbor."
        }
        leaf "level", {
            type "level"
            description \
            "Describes the ISIS level of the adjacency."
        }
        leaf "state", {
           type "enumeration", {
                enum_ "Up", {
                    description \
                    '''This state describes that
                        adjacency is established.'''
                }
                enum_ "Down", {
                    description \
                    '''This state describes that
                        adjacency is no more established.'''
                }
            }
            description \
            '''This leaf describes the new state of the
                ISIS adjacency.'''
        }
        leaf "reason", {
            type "string"
            description \
            '''If the adjacency is going to DOWN,
                this leaf provides a reason for the adjacency
                going down. The reason is provided as a text.
                    If the adjacency is going to UP, no reason is
                provided.'''
        }
        description \
        '''This notification is sent when an ISIS adjacency
            moves to Up state or to Down state.'''
    }

    notification "lsp-received", {
        uses "notification-instance-hdr"
        uses "notification-interface-hdr"

        leaf "lsp-id", {
            type "lsp-id"
            description \
            "LSP ID."
        }
        leaf "sequence", {
            type "uint32"
            description \
            "Sequence number of the received LSP."
        }
        leaf "received-timestamp", {
            type "yang:timestamp"

            description \
            '''This leaf describes the timestamp
                when the LSP was received. '''
        }
        leaf "neighbor-system-id", {
            type "system-id"
            description \
            '''Describes the system-id of the neighbor
                that sent the LSP.'''
        }
        description \
        '''This notification is sent when a LSP
            is received.
                The notification generation must be throttled with at least
            a 5 second gap. '''
    }

    notification "lsp-generation", {
        uses "notification-instance-hdr"

        leaf "lsp-id", {
            type "lsp-id"
            description \
            "LSP ID."
        }
        leaf "sequence", {
            type "uint32"
            description \
            "Sequence number of the received LSP."
        }
        leaf "send-timestamp", {
            type "yang:timestamp"

            description \
            '''This leaf describes the timestamp
                when our LSP was regenerated. '''
        }
        description \
        '''This notification is sent when a LSP
            is regenerated.
                The notification generation must be throttled with at least
            a 5 second gap.'''
    }

}

println builder.getText()
builder.writeToFile("${builder.getYangName()}.yang")
