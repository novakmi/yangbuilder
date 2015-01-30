//This is free software licensed under MIT License, see LICENSE file
//(https://bitbucket.org/novakmi/yangbuilder/src/LICENSE)

package org.bitbucket.novakmi.test.yangbuilder

import groovy.util.logging.Slf4j
import org.bitbucket.novakmi.nodebuilder.BuilderException
import org.testng.Assert
import org.testng.annotations.Test
import org.bitbucket.novakmi.yangbuilder.YangBuilder
import org.bitbucket.novakmi.yangbuilder.CompactYangPlugin

@Slf4j //Initialize logging
class CompactYangPluginTest {

        @Test(groups = ["basic"])
        public void compactTypeTest() {
                log.trace("==> compactTypeTest")
                def builder = new YangBuilder(4, [new CompactYangPlugin()]) // new instance

                builder.module(YangBuilderTestCommon._TEST_MODULE_NAME) {
                        namespace "http://novakmi.bitbucket.org/test"; // semicolon at the end can be preset (yang style)
                        prefix YangBuilderTestCommon._TEST_MODULE_NAME // or semicolon can be missing (more groovy like style)
                        yngbuild('') //yngbuild echoes value, yngbuild('') means new line

                        typedef('my-string', type: 'string', description: 'compact typedef')

                        container('socket') {
                                leaf('ip', type: 'string') //compact way to type leafs with simple types
                                leaf('port', type: 'uint16')
                        }
                        'leaf-list'('codes', type: 'uint32')
                        list('values', type: 'type without key is ignored') {
                                key 'value'
                                leaf('value', type: 'string')
                        }
                }

                Assert.assertEquals(builder.getText(),
                        '''module test {
    namespace "http://novakmi.bitbucket.org/test";
    prefix test;

    typedef my-string {
        description "compact typedef";
        type string;
    }
    container socket {
        leaf ip {
            type string;
        }
        leaf port {
            type uint16;
        }
    }
    leaf-list codes {
        type uint32;
    }
    list values {
        key value;
        leaf value {
            type string;
        }
    }
}
''')
                YangBuilderTestCommon.assertYangFile(builder, YangBuilderTestCommon._TEST_MODULE_NAME)

                log.trace("<== compactTypeTest")
        }

        @Test(groups = ["basic"])
        public void compactDescriptionTest() {
                log.trace("==> compactDescriptionTest")
                def builder = new YangBuilder(4, [new CompactYangPlugin()]) // new instance

                builder.module(YangBuilderTestCommon._TEST_MODULE_NAME) {
                        namespace "http://novakmi.bitbucket.org/test"; // semicolon at the end can be preset (yang style)
                        prefix YangBuilderTestCommon._TEST_MODULE_NAME // or semicolon can be missing (more groovy like style)
                        yngbuild('') //yngbuild echoes value, yngbuild('') means new line

                        revision('2012-06-29', description: "initial revision")

                        yngbuild('') //yngbuild echoes value, yngbuild('') means new line
                        typedef('my-string1', type: 'string', description: 'compact typedef')
                        typedef('my-string2', type: 'string', description: 'compact_typedef')
                        container('socket', description: 'socket ip address and port') {
                                leaf('ip', type: 'string', description: 'ip address ')
                                leaf('port', type: 'uint16', description: 'port vlaue')
                        }
                        'leaf-list'('codes', type: 'uint32', description: 'list of codes')
                        list('values', description: 'values', type: 'type without key is ignored') {
                                key 'value'
                                leaf('value', type: 'string')
                        }
                        choice('porta-portb') {
                                leaf('porta', type: 'uint16')
                                leaf('portb', type: 'uint16', description: 'port value')
                        }
                        grouping("Value", nl: true) {
                                container("val", description: "val container")
                        }
                        uses("Value") {
                                refine("val", description: "value container")
                        }
                }

                Assert.assertEquals(builder.getText(),
                        '''module test {
    namespace "http://novakmi.bitbucket.org/test";
    prefix test;

    revision 2012-06-29 {
        description "initial revision";
    }

    typedef my-string1 {
        description "compact typedef";
        type string;
    }
    typedef my-string2 {
        description compact_typedef;
        type string;
    }
    container socket {
        description "socket ip address and port";
        leaf ip {
            description "ip address ";
            type string;
        }
        leaf port {
            description "port vlaue";
            type uint16;
        }
    }
    leaf-list codes {
        description "list of codes";
        type uint32;
    }
    list values {
        description values;
        key value;
        leaf value {
            type string;
        }
    }
    choice porta-portb {
        leaf porta {
            type uint16;
        }
        leaf portb {
            description "port value";
            type uint16;
        }
    }
    grouping Value {
        container val {
            description "val container";
        }
    }

    uses Value {
        refine val {
            description "value container";
        }
    }
}
''')
                YangBuilderTestCommon.assertYangFile(builder, YangBuilderTestCommon._TEST_MODULE_NAME)

                log.trace("<== compactDescriptionTest")
        }

        @Test(groups = ["basic"])
        public void newLineTypeTest() {
                log.trace("==> newLineTypeTest")
                def builder = new YangBuilder(4, [new CompactYangPlugin()]) // new instance

                builder.module(YangBuilderTestCommon._TEST_MODULE_NAME) {
                        namespace "http://novakmi.bitbucket.org/test"; // semicolon at the end can be preset (yang style), no new line
                        prefix(YangBuilderTestCommon._TEST_MODULE_NAME, nl: true) // nl:true, nl:1, nl:<non false val> - make new line after node is printed

                        container('socket', nl: 1) { // nl:1 is same as nl:true
                                leaf('ip', type: 'string') //compact way to type leafs with simple types
                                leaf('port', type: 'uint16')
                        }

                        'leaf-list'('codes', type: 'uint32', nl: false) //  nl:0 , nl: false or missing => no new line
                        list('values', type: 'type without key is ignored', pnl: true) { //pnl:true - new line before node is processed
                                key 'value'
                                leaf('value', type: 'string')
                        }
                }

                Assert.assertEquals(builder.getText(),
                        '''module test {
    namespace "http://novakmi.bitbucket.org/test";
    prefix test;

    container socket {
        leaf ip {
            type string;
        }
        leaf port {
            type uint16;
        }
    }

    leaf-list codes {
        type uint32;
    }

    list values {
        key value;
        leaf value {
            type string;
        }
    }
}
''')
                YangBuilderTestCommon.assertYangFile(builder, YangBuilderTestCommon._TEST_MODULE_NAME)

                log.trace("<== newLineTypeTest")
        }

        @Test(groups = ["basic"])
        public void compactImportPrefixNamespacePnlTest() {
                log.trace("==> compactImportPrefixNamespacePnlTest")
                def builder = new YangBuilder(4, [new CompactYangPlugin()]) // new instance

                builder.module(YangBuilderTestCommon._TEST_MODULE_NAME, pnl_namespace: "http://novakmi.bitbucket.org/test", prefix_nl: YangBuilderTestCommon._TEST_MODULE_NAME) {
                        'import'('ietf-inet-types', prefix: 'inet', nl: 1)

                        leaf('port', pnl_type_nl: 'uint16', description: 'port value')
                }

                Assert.assertEquals(builder.getText(),
                        '''module test {

    namespace "http://novakmi.bitbucket.org/test";
    prefix test;

    import ietf-inet-types {
        prefix inet;
    }

    leaf port {
        description "port value";

        type uint16;

    }
}
''')
                YangBuilderTestCommon.assertYangFile(builder, YangBuilderTestCommon._TEST_MODULE_NAME)

                log.trace("<== compactImportPrefixNamespacePnlTest")
        }

        @Test(groups = ["basic"])
        public void compactDefaultTest() {
                log.trace("==> compactDefaultTest")
                def builder = new YangBuilder(4, [new CompactYangPlugin()]) // new instance

                builder.module(YangBuilderTestCommon._TEST_MODULE_NAME, pnl_namespace: "http://novakmi.bitbucket.org/test", prefix_nl: YangBuilderTestCommon._TEST_MODULE_NAME) {
                        'import'('ietf-inet-types', prefix: 'inet', nl: 1)
                        typedef("Integer", type: 'int32', default: -1, nl: true);
                        grouping("Port", nl: true) {
                                leaf('port', type: 'uint16', description: 'port value', default: 0)
                        }
                        uses("Port", nl: true) {
                                refine("port", default: 22)
                        }

                        leaf("port2", type: "int32", default: 22, nl: true)

                        deviation("/port2") {
                                deviate("replace", default: 2222)
                        }
                }

                Assert.assertEquals(builder.getText(),
                        '''module test {

    namespace "http://novakmi.bitbucket.org/test";
    prefix test;

    import ietf-inet-types {
        prefix inet;
    }

    typedef Integer {
        type int32;
        default -1;
    }

    grouping Port {
        leaf port {
            description "port value";
            type uint16;
            default 0;
        }
    }

    uses Port {
        refine port {
            default 22;
        }
    }

    leaf port2 {
        type int32;
        default 22;
    }

    deviation /port2 {
        deviate replace {
            default 2222;
        }
    }
}
''')
                YangBuilderTestCommon.assertYangFile(builder, YangBuilderTestCommon._TEST_MODULE_NAME)

                log.trace("<== compactDefaultTest")
        }

        @Test(groups = ["basic"])
        public void compactConfigTest() {
                log.trace("==> compactConfigTest")
                def builder = new YangBuilder(4, [new CompactYangPlugin()]) // new instance

                builder.module(YangBuilderTestCommon._TEST_MODULE_NAME, pnl_namespace: "http://novakmi.bitbucket.org/test", prefix_nl: YangBuilderTestCommon._TEST_MODULE_NAME) {
                        'import'('ietf-inet-types', prefix: 'inet', nl: 1)
                        grouping("Port", nl: true) {
                                leaf('port', type: "int32", config: false)
                        }
                        uses("Port", nl: true) {
                                refine("port", config: true)
                        }
                        choice('porta-portb', config: false) {
                                leaf('porta', type: 'uint16')
                                leaf('portb', type: 'uint16', description: 'port value')
                        }
                        'leaf-list'('codes', type: 'uint32', config: false, nl: false)
                        list("numbers", key: "val", type: "int32", config: true, nl: true) {
                                leaf("val", type: "int32")
                        }

                        deviation("/numbers") {
                                deviate("replace", config: false)
                        }
                }

                Assert.assertEquals(builder.getText(),
                        '''module test {

    namespace "http://novakmi.bitbucket.org/test";
    prefix test;

    import ietf-inet-types {
        prefix inet;
    }

    grouping Port {
        leaf port {
            config false;
            type int32;
        }
    }

    uses Port {
        refine port {
            config true;
        }
    }

    choice porta-portb {
        config false;
        leaf porta {
            type uint16;
        }
        leaf portb {
            description "port value";
            type uint16;
        }
    }
    leaf-list codes {
        config false;
        type uint32;
    }
    list numbers {
        config true;
        key val;
        leaf val {
            type int32;
        }
    }

    deviation /numbers {
        deviate replace {
            config false;
        }
    }
}
''')
                YangBuilderTestCommon.assertYangFile(builder, YangBuilderTestCommon._TEST_MODULE_NAME)

                log.trace("<== compactConfigTest")
        }


        @Test(groups = ["basic"])
        public void compactMinMaxElementsTest() {
                log.trace("==> compactMinMaxElementsTest")
                def builder = new YangBuilder(4, [new CompactYangPlugin()]) // new instance

                builder.module(YangBuilderTestCommon._TEST_MODULE_NAME, pnl_namespace: "http://novakmi.bitbucket.org/test", prefix_nl: YangBuilderTestCommon._TEST_MODULE_NAME) {
                        'import'('ietf-inet-types', prefix: 'inet', nl: 1)
                        typedef("Integer", type: 'int32', default: -1, nl: true);

                        grouping("Ports", nl: true) {
                                "leaf-list"('ports', type: 'uint16', 'min-elements': 4, 'max-elements': 5)
                        }

                        list("port-group", key: "name", 'min-elements': 10, 'max-elements': 15, nl: true) {
                                leaf("name", type: "string")
                                "leaf-list"("ports", type: "int32",  'min-elements': 1, 'max-elements': 3)
                        }

                        uses("Ports", nl: true) {
                                refine("ports", 'min-elements': 7, 'max-elements': 8)
                        }

                        deviation("/port-group") {
                                deviate("replace", "min-elements": 2, "max-elements": 11)
                        }
                }

                Assert.assertEquals(builder.getText(),
                        '''module test {

    namespace "http://novakmi.bitbucket.org/test";
    prefix test;

    import ietf-inet-types {
        prefix inet;
    }

    typedef Integer {
        type int32;
        default -1;
    }

    grouping Ports {
        leaf-list ports {
            min-elements 4;
            max-elements 5;
            type uint16;
        }
    }

    list port-group {
        key name;
        min-elements 10;
        max-elements 15;
        leaf name {
            type string;
        }
        leaf-list ports {
            min-elements 1;
            max-elements 3;
            type int32;
        }
    }

    uses Ports {
        refine ports {
            min-elements 7;
            max-elements 8;
        }
    }

    deviation /port-group {
        deviate replace {
            min-elements 2;
            max-elements 11;
        }
    }
}
''')
                YangBuilderTestCommon.assertYangFile(builder, YangBuilderTestCommon._TEST_MODULE_NAME)

                log.trace("<== compactMinMaxElementsTest")
        }

        @Test(groups = ["basic"])
        public void compactImportPrefixBelongsToTest() {
                log.trace("==> compactImportPrefixBelongsToTest")
                def builder = new YangBuilder(4, [new CompactYangPlugin()]) // new instance

                builder.submodule(YangBuilderTestCommon._TEST_SUBMODULE_NAME) {

                        'belongs-to'(YangBuilderTestCommon._TEST_SUBMODULE_NAME, prefix: 'test_submodule_prefix', nl: 1)

                        'import'('ietf-inet-types', prefix: 'inet', nl: 1)


                        leaf('port', type: 'uint16', description: 'port value')
                }

                Assert.assertEquals(builder.getText(),
                        '''submodule test_submodule {
    belongs-to test_submodule {
        prefix test_submodule_prefix;
    }

    import ietf-inet-types {
        prefix inet;
    }

    leaf port {
        description "port value";
        type uint16;
    }
}
''')
                YangBuilderTestCommon.assertYangFile(builder, YangBuilderTestCommon._TEST_MODULE_NAME)

                log.trace("<== compactImportPrefixBelongsToTest")
        }

        @Test(groups = ["basic"])
        public void compactListKeyTest() {
                log.trace("==> compactListKeyTest")
                def builder = new YangBuilder(4, [new CompactYangPlugin()]) // new instance

                builder.module(YangBuilderTestCommon._TEST_MODULE_NAME) {
                        namespace "http://novakmi.bitbucket.org/test"; // semicolon at the end can be preset (yang style)
                        prefix(YangBuilderTestCommon._TEST_MODULE_NAME, nl: 1) // or semicolon can be missing (more groovy like style)

                        list('values', key: 'value', description: 'values', type: 'type without key is ignored') {
                                leaf('value', type: 'string')
                        }
                }

                Assert.assertEquals(builder.getText(),
                        '''module test {
    namespace "http://novakmi.bitbucket.org/test";
    prefix test;

    list values {
        description values;
        key value;
        leaf value {
            type string;
        }
    }
}
''')
                YangBuilderTestCommon.assertYangFile(builder, YangBuilderTestCommon._TEST_MODULE_NAME)

                log.trace("<== compactListKeyTest")
        }

        @Test(groups = ["basic"])
        public void compactMandatoryTest() {
                log.trace("==> compactMandatoryTest")
                def builder = new YangBuilder(4, [new CompactYangPlugin()]) // new instance

                builder.module(YangBuilderTestCommon._TEST_MODULE_NAME, pnl_namespace: "http://novakmi.bitbucket.org/test", prefix_nl: YangBuilderTestCommon._TEST_MODULE_NAME) {
                        'import'('ietf-inet-types', prefix: 'inet', nl: 1)

                        leaf('port1', type: 'uint16', description: 'port value', mandatory: true, nl: true)
                        leaf('port2', type: 'uint16', description: 'port value', mandatory: false, nl: true)
                        leaf('port3', type: 'uint16', description: 'port value', nl: true)

                        grouping("Ports", nl: true) {
                                "leaf-list"('ports', type: 'uint16', config: false)
                        }

                        container('port-c', mandatory: true, nl: true) {  // mandatory has no effect under container
                                leaf('port4', type: 'uint16', description: 'port value', mandatory: true)
                        }

                        choice('porta-portb', mandatory: true, nl: true) {
                                leaf('porta', type: 'uint16', description: 'port value')
                                leaf('portb', type: 'uint16', description: 'port value')
                        }

                        uses("Ports") {
                                refine("ports", config: true)
                        }
                }

                Assert.assertEquals(builder.getText(),
                        '''module test {

    namespace "http://novakmi.bitbucket.org/test";
    prefix test;

    import ietf-inet-types {
        prefix inet;
    }

    leaf port1 {
        description "port value";
        mandatory true;
        type uint16;
    }

    leaf port2 {
        description "port value";
        mandatory false;
        type uint16;
    }

    leaf port3 {
        description "port value";
        type uint16;
    }

    grouping Ports {
        leaf-list ports {
            config false;
            type uint16;
        }
    }

    container port-c {
        leaf port4 {
            description "port value";
            mandatory true;
            type uint16;
        }
    }

    choice porta-portb {
        mandatory true;
        leaf porta {
            description "port value";
            type uint16;
        }
        leaf portb {
            description "port value";
            type uint16;
        }
    }

    uses Ports {
        refine ports {
            config true;
        }
    }
}
''')
                YangBuilderTestCommon.assertYangFile(builder, YangBuilderTestCommon._TEST_MODULE_NAME)

                builder.reset()
                builder.module(YangBuilderTestCommon._TEST_MODULE_NAME, pnl_namespace: "http://novakmi.bitbucket.org/test", prefix_nl: YangBuilderTestCommon._TEST_MODULE_NAME) {
                        'import'('ietf-inet-types', prefix: 'inet', nl: 1)
                        leaf('port1', type: 'uint16', description: 'port value', mandatory: 'true', nl: true) // mandatory cannot be string
                }
                try {
                        builder.getText()
                        Assert.fail()
                } catch (BuilderException expected) {
                        // do nothing
                }

                log.trace("<== compactMandatoryTest")
        }

        @Test(groups = ["basic"])
        public void compactPresenceTest() {
                log.trace("==> compactPresenceTest")
                def builder = new YangBuilder(4, [new CompactYangPlugin()]) // new instance

                builder.module(YangBuilderTestCommon._TEST_MODULE_NAME) {
                        namespace "http://novakmi.bitbucket.org/test"; // semicolon at the end can be preset (yang style)
                        prefix(YangBuilderTestCommon._TEST_MODULE_NAME, nl: 1) // or semicolon can be missing (more groovy like style)

                        grouping("Value", nl: true) {
                                container("val", presence: true)
                        }

                        container('value-c1', presence: true) {
                                leaf('value', type: 'string')
                        }

                        container('value-c2', presence: 'value-c2') {
                                leaf('value', type: 'string')
                        }

                        container('value-c3', presence: "value c3") {
                                leaf('value', type: 'string')
                        }

                        container('value-c4') {
                                leaf('value', type: 'string')
                        }
                        uses("Value") {
                                refine("val", presence: false)
                        }
                }

                Assert.assertEquals(builder.getText(),
                        '''module test {
    namespace "http://novakmi.bitbucket.org/test";
    prefix test;

    grouping Value {
        container val {
            presence true;
        }
    }

    container value-c1 {
        presence true;
        leaf value {
            type string;
        }
    }
    container value-c2 {
        presence value-c2;
        leaf value {
            type string;
        }
    }
    container value-c3 {
        presence "value c3";
        leaf value {
            type string;
        }
    }
    container value-c4 {
        leaf value {
            type string;
        }
    }
    uses Value {
        refine val {
            presence false;
        }
    }
}
''')
                YangBuilderTestCommon.assertYangFile(builder, YangBuilderTestCommon._TEST_MODULE_NAME)

                log.trace("<== compactPresenceTest")
        }

        @Test(groups = ["basic"])
        public void compactTypeEnumerationTest() {
                log.trace("==> compactTypeEnumerationTest")
                def builder = new YangBuilder(4, [new CompactYangPlugin()]) // new instance

                builder.module(YangBuilderTestCommon._TEST_MODULE_NAME) {
                        namespace "http://novakmi.bitbucket.org/test"; // semicolon at the end can be preset (yang style)
                        prefix(YangBuilderTestCommon._TEST_MODULE_NAME, nl: 1) // or semicolon can be missing (more groovy like style)

                        typedef('my-type1') {
                                type('enumeration') {
                                        'enum'('one')
                                        'enum'('two')
                                        'enum'('three')
                                }
                        }
                        typedef('my-type2') {
                                type('enumeration', enums: ['one', 'two', 'three'])
                        }
                        leaf('enum-leaf') {
                                type('enumeration', enums: ['one', 'two', 'three'])
                        }

                }

                Assert.assertEquals(builder.getText(),
                        '''module test {
    namespace "http://novakmi.bitbucket.org/test";
    prefix test;

    typedef my-type1 {
        type enumeration {
            enum one;
            enum two;
            enum three;
        }
    }
    typedef my-type2 {
        type enumeration {
            enum one;
            enum two;
            enum three;
        }
    }
    leaf enum-leaf {
        type enumeration {
            enum one;
            enum two;
            enum three;
        }
    }
}
''')
                YangBuilderTestCommon.assertYangFile(builder, YangBuilderTestCommon._TEST_MODULE_NAME)

                builder.reset()
                builder.module(YangBuilderTestCommon._TEST_MODULE_NAME) {
                        namespace "http://novakmi.bitbucket.org/test";
                        prefix(YangBuilderTestCommon._TEST_MODULE_NAME)

                        typedef('my-type-non-list') {
                                type('enumeration', enums: 'one') // enums must be list
                        }
                }

                try {
                        builder.getText()
                        Assert.fail()
                } catch (BuilderException expected) {
                        // do nothing
                }

                builder.reset()
                builder.module(YangBuilderTestCommon._TEST_MODULE_NAME) {
                        namespace "http://novakmi.bitbucket.org/test";
                        prefix(YangBuilderTestCommon._TEST_MODULE_NAME)

                        typedef('my-type-non-list') {
                                type('enumeration', enums: [1, 2, 3]) // enums must be Strings
                        }
                }

                try {
                        builder.getText()
                        Assert.fail()
                } catch (BuilderException expected) {
                        // do nothing
                }

                log.trace("<== compactTypeEnumerationTest")
        }


        @Test(groups = ["basic"])
        public void compactElemsTest() {
                log.trace("==> compactElemsTest")
                CompactYangPlugin plugin = new CompactYangPlugin()
                def builder = new YangBuilder(4, [plugin]) // new instance

                builder.module(YangBuilderTestCommon._TEST_MODULE_NAME) {
                        namespace "http://novakmi.bitbucket.org/test"; // semicolon at the end can be preset (yang style)
                        prefix(YangBuilderTestCommon._TEST_MODULE_NAME, nl: 1) // or semicolon can be missing (more groovy like style)

                        leaf('my-leaf', type: "string", elems: ["my:annot1", "my:annot2", "description \"My leaf descr.\""])
                }
                Assert.assertEquals(builder.getText(),
                        '''module test {
    namespace "http://novakmi.bitbucket.org/test";
    prefix test;

    leaf my-leaf {
        type string;
        my:annot1;
        my:annot2;
        description "My leaf descr.";
    }
}
''')

                builder.reset()
                plugin.declareMinColAliases(["my:annot1", "my:annot-2"])
                builder.module(YangBuilderTestCommon._TEST_MODULE_NAME) {
                        namespace "http://novakmi.bitbucket.org/test"; // semicolon at the end can be preset (yang style)
                        prefix(YangBuilderTestCommon._TEST_MODULE_NAME, nl: 1) // or semicolon can be missing (more groovy like style)

                        leaf('my-leaf', type: "string", elems: ["my_annot1", "my_annot_2"])
                }
                Assert.assertEquals(builder.getText(),
                        '''module test {
    namespace "http://novakmi.bitbucket.org/test";
    prefix test;

    leaf my-leaf {
        type string;
        my:annot1;
        my:annot-2;
    }
}
''')

                builder.reset()
                builder.module(YangBuilderTestCommon._TEST_MODULE_NAME) {
                        namespace "http://novakmi.bitbucket.org/test";
                        prefix(YangBuilderTestCommon._TEST_MODULE_NAME)
                        leaf('my-leaf', type: "string", elems: "NoListElemes") // ensure elemes is list
                }

                try {
                        builder.getText()
                       Assert.fail()
                } catch (BuilderException expected) {
                        // do nothing
                }

                builder.reset()
                builder.module(YangBuilderTestCommon._TEST_MODULE_NAME) {
                        namespace "http://novakmi.bitbucket.org/test";
                        prefix(YangBuilderTestCommon._TEST_MODULE_NAME)
                        leaf('my-leaf', type: "string", elems: ["stringElem", 1, false])   // ensure only string elems
                }

                try {
                        builder.getText()
                        Assert.fail()
                } catch (BuilderException expected) {
                        // do nothing
                }

                log.trace("<== compactElemsTest")
        }

        @Test(groups = ["basic"])
        public void commonAliasesTest() {
                log.trace("==> commonAliasesTest")
                CompactYangPlugin plugin = new CompactYangPlugin()
                def builder = new YangBuilder(4, [plugin]) // new instance/use indent 4

                builder.module(YangBuilderTestCommon._TEST_MODULE_NAME) {
                        namespace "http://novakmi.bitbucket.org/test";
                        // semicolon at the end can be preset (yang style)
                        prefix YangBuilderTestCommon._TEST_MODULE_NAME
                        // or semicolon can be missing (more groovy like style)
                        yngbuild('') //yngbuild echoes value, yngbuild('') means new line
                        import_("ietf-inet-types") {
                                prefix "inet";
                        }
                        leaf_list("numbers") {
                                type "int32"
                                min_elements(0)
                                max_elements(100)
                                ordered_by("user")
                        }
                }

                Assert.assertNotEquals(builder.getText(), '''module test {
    namespace "http://novakmi.bitbucket.org/test";
    prefix test;

    import ietf-inet-types {
        prefix inet;
    }
    leaf-list numbers {
        type int32;
        min-elements 0;
        max-elements 100;
        ordered-by user;
    }
}
''')

                builder.reset()
                plugin.declareCommonAliases()
                builder.module(YangBuilderTestCommon._TEST_MODULE_NAME) {
                        namespace "http://novakmi.bitbucket.org/test";
                        // semicolon at the end can be preset (yang style)
                        prefix YangBuilderTestCommon._TEST_MODULE_NAME
                        // or semicolon can be missing (more groovy like style)
                        yngbuild('') //yngbuild echoes value, yngbuild('') means new line
                        import_("ietf-inet-types") {
                                prefix "inet";
                        }

                        organization 'novakmi organization'
                        contact 'it.novakmi@gmail.com e-mail'
                        description 'test aliases'

                        feature "USE_NUMBERS"

                        leaf_list("numbers") {
                                if_feature "USE_NUMBERS"
                                type "int32"
                                min_elements(0)
                                max_elements(100)
                                ordered_by("user")
                        }
                        leaf_list("names", type: "string", pnl_min_elements: "10", pnl_max_elements_nl: 20)
                        list("ports", key: "port", min_elements: 10, max_elements: 20) {
                                leaf("port") {
                                        type("int32")
                                        default_ 22
                                }
                        }
                        typedef('my-type1') {
                                type('enumeration') {
                                        enum_('one')
                                        enum_('two')
                                }
                                default_ "one"
                        }
                }
                Assert.assertEquals(builder.getText(), '''module test {
    namespace "http://novakmi.bitbucket.org/test";
    prefix test;

    import ietf-inet-types {
        prefix inet;
    }
    organization "novakmi organization";
    contact "it.novakmi@gmail.com e-mail";
    description "test aliases";
    feature USE_NUMBERS;
    leaf-list numbers {
        if-feature USE_NUMBERS;
        type int32;
        min-elements 0;
        max-elements 100;
        ordered-by user;
    }
    leaf-list names {

        min-elements 10;

        max-elements 20;

        type string;
    }
    list ports {
        key port;
        min-elements 10;
        max-elements 20;
        leaf port {
            type int32;
            default 22;
        }
    }
    typedef my-type1 {
        type enumeration {
            enum one;
            enum two;
        }
        default one;
    }
}
''')
                YangBuilderTestCommon.assertYangFile(builder, YangBuilderTestCommon._TEST_MODULE_NAME)
                log.trace("<== commonAliasesTest")
        }

        @Test(groups = ["basic"])
        public void minColAliasesTest() {
                log.trace("==> minColAliasesTest")
                CompactYangPlugin plugin = new CompactYangPlugin()
                def builder = new YangBuilder(4, [plugin]) // new instance/use indent 4
                plugin.declareMinColAliases(["my:test1", "my:test-2"])
                builder.module(YangBuilderTestCommon._TEST_MODULE_NAME) {
                        namespace "http://novakmi.bitbucket.org/test";
                        // semicolon at the end can be preset (yang style)
                        prefix YangBuilderTestCommon._TEST_MODULE_NAME
                        // or semicolon can be missing (more groovy like style)
                        yngbuild('') //yngbuild echoes value, yngbuild('') means new line
                        'import'("ietf-inet-types") {
                                prefix "inet";
                        }
                        leaf("number") {
                                my_test1()
                                my_test_2()
                                type "int32"

                        }
                }

                Assert.assertEquals(builder.getText(), '''module test {
    namespace "http://novakmi.bitbucket.org/test";
    prefix test;

    import ietf-inet-types {
        prefix inet;
    }
    leaf number {
        my:test1;
        my:test-2;
        type int32;
    }
}
''')
                log.trace("<== minColAliasesTest")
        }
}
