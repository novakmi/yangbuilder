//This is free software licensed under MIT License, see LICENSE file
//(https://bitbucket.org/bubbles.way/yangbuilder/src/LICENSE)

package org.bitbucket.novakmi.test.yangbuilder

import org.testng.annotations.Test
import org.testng.Assert
import org.bitbucket.novakmi.yangbuilder.YangBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory


class YangBuilderTest {

// test based on example from Instant YANG tutorial, section modules
        @Test(groups = ["basic"])
        public void yangTest() {
                logger.trace("==> yangTest")
                def builder = new YangBuilder(4) // new instance/use indent 4
                YangBuilderTestCommon._buildTestYang(builder)
                Assert.assertEquals(builder.getText(), YangBuilderTestCommon._getTestYangString())
                YangBuilderTestCommon.assertYangFile(builder, YangBuilderTestCommon._TEST_MODULE_NAME)
                logger.trace("<== yangTest")
        }

        @Test(groups = ["basic"])
        public void yangResetTest() {
                logger.trace("==> yangResetTest")

                def builder = new YangBuilder(4) // new instance/use indent 4
                YangBuilderTestCommon._buildTestYang(builder)
                Assert.assertEquals(builder.getText(), YangBuilderTestCommon._getTestYangString())
                YangBuilderTestCommon.assertYangFile(builder, YangBuilderTestCommon._TEST_MODULE_NAME)

                builder.reset()
                Assert.assertEquals(builder.getText(), '')

                logger.trace("<== yangResetTest")
        }

        @Test(groups = ["basic"])
        public void yangResetAfterYangrootTest() {
                logger.trace("==> yangResetAfterYangrootTest")

                def builder = new YangBuilder(4) // new instance/use indent 4
                builder.yangroot {
                        YangBuilderTestCommon._buildTestYang(builder)
                }
                Assert.assertEquals(builder.getText(), YangBuilderTestCommon._getTestYangString())
                YangBuilderTestCommon.assertYangFile(builder, YangBuilderTestCommon._TEST_MODULE_NAME)

                builder.reset()
                Assert.assertEquals(builder.getText(), '')

                YangBuilderTestCommon._buildTestYang(builder)
                Assert.assertEquals(builder.getText(), YangBuilderTestCommon._getTestYangString())
                YangBuilderTestCommon.assertYangFile(builder, YangBuilderTestCommon._TEST_MODULE_NAME)

                logger.trace("<== yangResetAfterYangrootTest")
        }

        @Test(groups = ["basic"])
        public void yangNameTest() {
                logger.trace("==> yangNameTest")

                // module
                def builder = new YangBuilder(4) // new instance/use indent 4
                Assert.assertNull(builder.getYangName())
                builder.yangroot {
                        YangBuilderTestCommon._buildTestYang(builder)
                }
                Assert.assertEquals(builder.getYangName(), YangBuilderTestCommon._TEST_MODULE_NAME)
                YangBuilderTestCommon.assertYangFile(builder, YangBuilderTestCommon._TEST_MODULE_NAME)

                builder.reset()
                Assert.assertNull(builder.getYangName())

                YangBuilderTestCommon._buildTestYang(builder)
                Assert.assertEquals(builder.getYangName(), YangBuilderTestCommon._TEST_MODULE_NAME)
                YangBuilderTestCommon.assertYangFile(builder, YangBuilderTestCommon._TEST_MODULE_NAME)

                builder.reset()
                Assert.assertNull(builder.getYangName())

                // submodule
                builder = new YangBuilder(4) // new instance/use indent 4
                Assert.assertNull(builder.getYangName())
                builder.yangroot {
                        YangBuilderTestCommon._buildTestSubmoduleYang(builder)
                }
                Assert.assertEquals(builder.getYangName(), YangBuilderTestCommon._TEST_SUBMODULE_NAME)

                builder.reset()
                Assert.assertNull(builder.getYangName())

                YangBuilderTestCommon._buildTestSubmoduleYang(builder)
                Assert.assertEquals(builder.getYangName(), YangBuilderTestCommon._TEST_SUBMODULE_NAME)

                builder.reset()
                Assert.assertNull(builder.getYangName())

                logger.trace("<== yangNameTest")
        }

        @Test(groups = ["basic"])
        public void prefixNameTest() {
                logger.trace("==> prefixNameTest")

                // module
                def builder = new YangBuilder(4) // new instance/use indent 4
                Assert.assertNull(builder.getPrefixName())
                builder.yangroot {
                        YangBuilderTestCommon._buildTestYang(builder)
                }
                Assert.assertEquals(builder.getPrefixName(), YangBuilderTestCommon._TEST_MODULE_NAME)
                YangBuilderTestCommon.assertYangFile(builder, YangBuilderTestCommon._TEST_MODULE_NAME)

                builder.reset()
                Assert.assertNull(builder.getPrefixName())

                YangBuilderTestCommon._buildTestYang(builder)
                Assert.assertEquals(builder.getPrefixName(), YangBuilderTestCommon._TEST_MODULE_NAME)
                YangBuilderTestCommon.assertYangFile(builder, YangBuilderTestCommon._TEST_MODULE_NAME)

                builder.reset()
                Assert.assertNull(builder.getPrefixName())

                // submodule
                builder = new YangBuilder(4) // new instance/use indent 4
                Assert.assertNull(builder.getPrefixName())
                builder.yangroot {
                        YangBuilderTestCommon._buildTestSubmoduleYang(builder)
                }
                Assert.assertEquals(builder.getPrefixName(), YangBuilderTestCommon._TEST_SUBMODULE_NAME)

                builder.reset()
                Assert.assertNull(builder.getPrefixName())

                YangBuilderTestCommon._buildTestSubmoduleYang(builder)
                Assert.assertEquals(builder.getPrefixName(), YangBuilderTestCommon._TEST_SUBMODULE_NAME)

                builder.reset()
                Assert.assertNull(builder.getPrefixName())

                logger.trace("<== prefixNameTest")
        }

        @Test(groups = ["basic"])
        public void quoteTest() {
                logger.trace("==> quoteTest")
                def builder = new YangBuilder(4) // new instance/use indent 4
                builder.module(YangBuilderTestCommon._TEST_MODULE_NAME) {
                        namespace "http://novakmi.bitbucket.org/test"; // semicolon at the end can be preset (yang style)
                        prefix YangBuilderTestCommon._TEST_MODULE_NAME // or semicolon can be missing (more groovy like style)
                        yngbuild('') //yngbuild echoes value, yngbuild('') means new line

                        organization 'bubbles'
                        contact 'bubbles.way@gmail.com'
                        description 'test quotes'

                        container('socket') {
                                presence 'yes'
                                leaf('ip') {
                                        type('string') {
                                                pattern('*.')
                                        }
                                }
                                list('ports') {
                                        key 'port'
                                        leaf('port') {
                                                type 'uint16'
                                        }
                                }
                        }
                }
                Assert.assertEquals(builder.getText(), '''module test {
    namespace "http://novakmi.bitbucket.org/test";
    prefix test;

    organization bubbles;
    contact bubbles.way@gmail.com;
    description "test quotes";
    container socket {
        presence yes;
        leaf ip {
            type string {
                pattern *.;
            }
        }
        list ports {
            key port;
            leaf port {
                type uint16;
            }
        }
    }
}
''')
                builder.reset()
                builder.module(YangBuilderTestCommon._TEST_MODULE_NAME) {
                        namespace "http://novakmi.bitbucket.org/test"; // semicolon at the end can be preset (yang style)
                        prefix YangBuilderTestCommon._TEST_MODULE_NAME // or semicolon can be missing (more groovy like style)
                        yngbuild('') //yngbuild echoes value, yngbuild('') means new line

                        list('ports') {
                                key('port', quotes: '"') // force quotes
                                leaf('port') {
                                        type 'uint16'
                                }
                        }
                }
                Assert.assertEquals(builder.getText(), '''module test {
    namespace "http://novakmi.bitbucket.org/test";
    prefix test;

    list ports {
        key "port";
        leaf port {
            type uint16;
        }
    }
}
''')
                builder.reset()
                builder.module(YangBuilderTestCommon._TEST_MODULE_NAME) {
                        namespace "http://novakmi.bitbucket.org/test"; // semicolon at the end can be preset (yang style)
                        prefix YangBuilderTestCommon._TEST_MODULE_NAME // or semicolon can be missing (more groovy like style)
                        yngbuild('') //yngbuild echoes value, yngbuild('') means new line

                        organization 'bubbles'
                        contact 'bubbles.way@gmail.com'
                        description('test quotes', noAutoQuotes: true) // force no quotes = invalid yang
                }
                Assert.assertEquals(builder.getText(), '''module test {
    namespace "http://novakmi.bitbucket.org/test";
    prefix test;

    organization bubbles;
    contact bubbles.way@gmail.com;
    description test quotes;
}
''')
                builder.reset()
                builder.module(YangBuilderTestCommon._TEST_MODULE_NAME) {
                        namespace "http://novakmi.bitbucket.org/test"; // semicolon at the end can be preset (yang style)
                        prefix YangBuilderTestCommon._TEST_MODULE_NAME // or semicolon can be missing (more groovy like style)
                        yngbuild('') //yngbuild echoes value, yngbuild('') means new line

                        organization 'bubbles'
                        contact('bubbles.way@gmail.com', quotes: '"')
                        description('test quotes') // force no quotes = invalid yang
                }
                Assert.assertEquals(builder.getText(), '''module test {
    namespace "http://novakmi.bitbucket.org/test";
    prefix test;

    organization bubbles;
    contact "bubbles.way@gmail.com";
    description "test quotes";
}
''')

                builder.reset()
                builder.module(YangBuilderTestCommon._TEST_MODULE_NAME) {
                        namespace "http://novakmi.bitbucket.org/test"; // semicolon at the end can be preset (yang style)
                        prefix YangBuilderTestCommon._TEST_MODULE_NAME // or semicolon can be missing (more groovy like style)
                        yngbuild('') //yngbuild echoes value, yngbuild('') means new line

                        organization 'bubbles'
                        contact('bubbles.way@gmail.com', quotes: '"')
                        description(
                                '''test quotes
in multiline
description''',
                                multiline: true) // force no quotes = invalid yang
                }
                Assert.assertEquals(builder.getText(), '''module test {
    namespace "http://novakmi.bitbucket.org/test";
    prefix test;

    organization bubbles;
    contact "bubbles.way@gmail.com";
    description
        "test quotes
         in multiline
         description";
}
''')
                logger.trace("<== quoteTest")
        }

        //Initialize logging
        private static final Logger logger = LoggerFactory.getLogger(YangBuilderTest.class);
}
