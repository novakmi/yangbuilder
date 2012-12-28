//This is free software licensed under MIT License, see LICENSE file
//(https://bitbucket.org/bubbles.way/yangbuilder/src/LICENSE)

package org.bitbucket.novakmi.test.yangbuilder

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testng.Assert
import org.testng.annotations.Test
import org.bitbucket.novakmi.yangbuilder.YangBuilder
import org.bitbucket.novakmi.yangbuilder.CompactYangPlugin

class CompactYangPluginTest {

        @Test(groups = ["basic"])
        public void compactTypeTest() {
                logger.trace("==> compactTypeTest")
                def builder = new YangBuilder(4, [new CompactYangPlugin()]) // new instance

                builder.module(YangBuilderTestCommon._TEST_MODULE_NAME) {
                        namespace "http://novakmi.bitbucket.org/test"; // semicolon at the end can be preset (yang style)
                        prefix YangBuilderTestCommon._TEST_MODULE_NAME // or semicolon can be missing (more groovy like style)
                        yngbuild('') //yngbuild echoes value, yngbuild('') means new line

                        typedef('my-string', type:'string', description: 'compact typedef')

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

                logger.trace("<== compactTypeTest")
        }

        @Test(groups = ["basic"])
        public void compactDescriptionTest() {
                logger.trace("==> compactDescriptionTest")
                def builder = new YangBuilder(4, [new CompactYangPlugin()]) // new instance

                builder.module(YangBuilderTestCommon._TEST_MODULE_NAME) {
                        namespace "http://novakmi.bitbucket.org/test"; // semicolon at the end can be preset (yang style)
                        prefix YangBuilderTestCommon._TEST_MODULE_NAME // or semicolon can be missing (more groovy like style)
                        yngbuild('') //yngbuild echoes value, yngbuild('') means new line

                        revision('2012-06-29', description: "initial revision")

                        yngbuild('') //yngbuild echoes value, yngbuild('') means new line
                        typedef('my-string1', type:'string', description: 'compact typedef')
                        typedef('my-string2', type:'string', description: 'compact_typedef')
                        container('socket', description: 'socket ip address and port') {
                                leaf('ip', type: 'string', description: 'ip address ')
                                leaf('port', type: 'uint16', description: 'port vlaue')
                        }
                        'leaf-list'('codes', type: 'uint32', description: 'list of codes')
                        list('values', description: 'values', type: 'type without key is ignored') {
                                key 'value'
                                leaf('value', type: 'string')
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
}
''')
                YangBuilderTestCommon.assertYangFile(builder, YangBuilderTestCommon._TEST_MODULE_NAME)

                logger.trace("<== compactDescriptionTest")
        }

        @Test(groups = ["basic"])
        public void newLineTypeTest() {
                logger.trace("==> newLineTypeTest")
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

                logger.trace("<== newLineTypeTest")
        }

        @Test(groups = ["basic"])
        public void compactImportPrefixNamespacePnlTest() {
                logger.trace("==> compactImportPrefixNamespacePnlTest")
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

                logger.trace("<== compactImportPrefixNamespacePnlTest")
        }


        @Test(groups = ["basic"])
        public void compactImportPrefixBelongsToTest() {
                logger.trace("==> compactImportPrefixBelongsToTest")
                def builder = new YangBuilder(4, [new CompactYangPlugin()]) // new instance

                builder.submodule(YangBuilderTestCommon._TEST_SUBMODULE_NAME) {

                        'belongs-to'(YangBuilderTestCommon._TEST_SUBMODULE_NAME, prefix: 'test_submodule_prefix', nl:1 )

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

                logger.trace("<== compactImportPrefixBelongsToTest")
        }

        @Test(groups = ["basic"])
        public void compactListKeyTest() {
                logger.trace("==> compactListKeyTest")
                def builder = new YangBuilder(4, [new CompactYangPlugin()]) // new instance

                builder.module(YangBuilderTestCommon._TEST_MODULE_NAME) {
                        namespace "http://novakmi.bitbucket.org/test"; // semicolon at the end can be preset (yang style)
                        prefix(YangBuilderTestCommon._TEST_MODULE_NAME, nl:1) // or semicolon can be missing (more groovy like style)

                        list('values', key:'value', description: 'values', type: 'type without key is ignored') {
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

                logger.trace("<== compactListKeyTest")
        }

        //Initialize logging
        private static final Logger logger = LoggerFactory.getLogger(CompactYangPluginTest.class);
}
