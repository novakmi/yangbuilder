/*
Copyright (c) 2012 Michal Novak (bubbles.way@gmail.com)

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/

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
                        list('values', type: 'type without key is ignored') {
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
        public void compactImportPrefixTest() {
                logger.trace("==> compactImportPrefixTest")
                def builder = new YangBuilder(4, [new CompactYangPlugin()]) // new instance

                builder.module(YangBuilderTestCommon._TEST_MODULE_NAME) {
                        namespace "http://novakmi.bitbucket.org/test"; // semicolon at the end can be preset (yang style)
                        prefix(YangBuilderTestCommon._TEST_MODULE_NAME, nl:1) // or semicolon can be missing (more groovy like style)
                        'import'('ietf-inet-types', prefix: 'inet', nl: 1)
                        leaf('port', type: 'uint16', description: 'port vlaue')
                }

                Assert.assertEquals(builder.getText(),
                    '''module test {
    namespace "http://novakmi.bitbucket.org/test";
    prefix test;

    import ietf-inet-types {
        prefix inet;
    }

    leaf port {
        description "port vlaue";
        type uint16;
    }
}
''')
                YangBuilderTestCommon.assertYangFile(builder, YangBuilderTestCommon._TEST_MODULE_NAME)

                logger.trace("<== compactImportPrefixTest")
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
