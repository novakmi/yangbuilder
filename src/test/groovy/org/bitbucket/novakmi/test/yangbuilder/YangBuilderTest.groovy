//This is free software licensed under MIT License, see LICENSE file
//(https://bitbucket.org/novakmi/yangbuilder/src/LICENSE)

package org.bitbucket.novakmi.test.yangbuilder

import groovy.util.logging.Slf4j
import org.testng.annotations.Test
import org.testng.Assert
import org.bitbucket.novakmi.yangbuilder.YangBuilder

@Slf4j //Initialize logging
class YangBuilderTest {

// test based on example from Instant YANG tutorial, section modules
        @Test(groups = ["basic"])
        public void yangTest() {
                log.trace("==> yangTest")
                def builder = new YangBuilder(4) // new instance/use indent 4
                YangBuilderTestCommon._buildTestYang(builder)
                Assert.assertEquals(builder.getText(), YangBuilderTestCommon._getTestYangString())
                YangBuilderTestCommon.assertYangFile(builder, YangBuilderTestCommon._TEST_MODULE_NAME)
                log.trace("<== yangTest")
        }

        @Test(groups = ["basic"])
        public void yangResetTest() {
                log.trace("==> yangResetTest")

                def builder = new YangBuilder(4) // new instance/use indent 4
                YangBuilderTestCommon._buildTestYang(builder)
                Assert.assertEquals(builder.getText(), YangBuilderTestCommon._getTestYangString())
                YangBuilderTestCommon.assertYangFile(builder, YangBuilderTestCommon._TEST_MODULE_NAME)

                builder.reset()
                Assert.assertEquals(builder.getText(), '')

                log.trace("<== yangResetTest")
        }

        @Test(groups = ["basic"])
        public void yangResetAfterYangrootTest() {
                log.trace("==> yangResetAfterYangrootTest")

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

                log.trace("<== yangResetAfterYangrootTest")
        }

        @Test(groups = ["basic"])
        public void yangNameTest() {
                log.trace("==> yangNameTest")

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

                log.trace("<== yangNameTest")
        }

        @Test(groups = ["basic"])
        public void prefixNameTest() {
                log.trace("==> prefixNameTest")

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

                log.trace("<== prefixNameTest")
        }

        @Test(groups = ["basic"])
        public void quoteTest() {
                log.trace("==> quoteTest")
                def builder = new YangBuilder(4) // new instance/use indent 4
                builder.module(YangBuilderTestCommon._TEST_MODULE_NAME) {
                        namespace "http://novakmi.bitbucket.org/test"; // semicolon at the end can be preset (yang style)
                        prefix YangBuilderTestCommon._TEST_MODULE_NAME // or semicolon can be missing (more groovy like style)
                        yngbuild('') //yngbuild echoes value, yngbuild('') means new line

                        organization 'novakmi organization'
                        contact 'it.novakmi@gmail.com e-mail'
                        description 'test quotes'

                        container('socket') {
                                presence 'yes socket'
                                leaf('ipnum') {
                                        type('string') {
                                                pattern('[0-9a-fA-F]*')
                                        }
                                }
                                list('ports') {
                                        key 'port val'
                                        leaf('port') {
                                                type 'uint16'
                                                reference "Reference to port description"
                                        }
                                        leaf('val') {
                                                type 'uint16'
                                        }
                                }
                                leaf("name") {
                                        type("string")
                                }
                                must("name = 'test'") {
                                        "error-message"('Name is not test!')
                                }
                        }
                }
                Assert.assertEquals(builder.getText(), '''module test {
    namespace "http://novakmi.bitbucket.org/test";
    prefix test;

    organization "novakmi organization";
    contact "it.novakmi@gmail.com e-mail";
    description "test quotes";
    container socket {
        presence "yes socket";
        leaf ipnum {
            type string {
                pattern [0-9a-fA-F]*;
            }
        }
        list ports {
            key "port val";
            leaf port {
                type uint16;
                reference "Reference to port description";
            }
            leaf val {
                type uint16;
            }
        }
        leaf name {
            type string;
        }
        must "name = 'test'" {
            error-message "Name is not test!";
        }
    }
}
''')
                YangBuilderTestCommon.assertYangFile(builder, YangBuilderTestCommon._TEST_MODULE_NAME)

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
                YangBuilderTestCommon.assertYangFile(builder, YangBuilderTestCommon._TEST_MODULE_NAME)

                builder.reset()
                builder.module(YangBuilderTestCommon._TEST_MODULE_NAME) {
                        namespace "http://novakmi.bitbucket.org/test"; // semicolon at the end can be preset (yang style)
                        prefix YangBuilderTestCommon._TEST_MODULE_NAME // or semicolon can be missing (more groovy like style)
                        yngbuild('') //yngbuild echoes value, yngbuild('') means new line

                        organization 'novakmi'
                        contact 'it.novakmi@gmail.com'
                        description('test quotes', noAutoQuotes: true) // force no quotes = invalid yang
                }
                Assert.assertEquals(builder.getText(), '''module test {
    namespace "http://novakmi.bitbucket.org/test";
    prefix test;

    organization novakmi;
    contact it.novakmi@gmail.com;
    description test quotes;
}
''')
                //YangBuilderTestCommon.assertYangFile(builder, YangBuilderTestCommon._TEST_MODULE_NAME) - yang not valid

                builder.reset()
                builder.module(YangBuilderTestCommon._TEST_MODULE_NAME) {
                        namespace "http://novakmi.bitbucket.org/test"; // semicolon at the end can be preset (yang style)
                        prefix YangBuilderTestCommon._TEST_MODULE_NAME // or semicolon can be missing (more groovy like style)
                        yngbuild('') //yngbuild echoes value, yngbuild('') means new line

                        organization 'novakmi'
                        contact('it.novakmi@gmail.com', quotes: '"')
                        description('test quotes') // force no quotes = invalid yang
                }
                Assert.assertEquals(builder.getText(), '''module test {
    namespace "http://novakmi.bitbucket.org/test";
    prefix test;

    organization novakmi;
    contact "it.novakmi@gmail.com";
    description "test quotes";
}
''')
                YangBuilderTestCommon.assertYangFile(builder, YangBuilderTestCommon._TEST_MODULE_NAME)

                builder.reset()
                builder.module(YangBuilderTestCommon._TEST_MODULE_NAME) {
                        namespace "http://novakmi.bitbucket.org/test"; // semicolon at the end can be preset (yang style)
                        prefix YangBuilderTestCommon._TEST_MODULE_NAME // or semicolon can be missing (more groovy like style)
                        yngbuild('') //yngbuild echoes value, yngbuild('') means new line

                        organization 'novakmi'
                        contact('it.novakmi@gmail.com', quotes: '"')
                        description(
                                '''test quotes
in multiline
description''',
                                multiline: true, indent: true) // multiline and indent
                }
                Assert.assertEquals(builder.getText(), '''module test {
    namespace "http://novakmi.bitbucket.org/test";
    prefix test;

    organization novakmi;
    contact "it.novakmi@gmail.com";
    description
        "test quotes
         in multiline
         description";
}
''')
                YangBuilderTestCommon.assertYangFile(builder, YangBuilderTestCommon._TEST_MODULE_NAME)

                log.trace("<== quoteTest")
        }

        @Test(groups = ["basic"])
        public void commentTest() {
                log.trace("==> commentTest")
                def builder = new YangBuilder(4) // new instance/use indent 4
                builder.module(YangBuilderTestCommon._TEST_MODULE_NAME) {
                        namespace "http://novakmi.bitbucket.org/test"; // semicolon at the end can be preset (yang style)
                        prefix YangBuilderTestCommon._TEST_MODULE_NAME // or semicolon can be missing (more groovy like style)
                        yngbuild('') //yngbuild echoes value, yngbuild('') means new line

                        cmt('This is comment', indent: false, inline: false)
                        cmt('This is inline comment', indent: false)
                        yngbuild('') //yngbuild echoes value, yngbuild('') means new line

                        cmt('This is indented comment', inline: false)
                        cmt('This is inline indented comment')
                        yngbuild('') //yngbuild echoes value, yngbuild('') means new line

                        cmt('''This is
multiline comment.''', indent: false, inline: false)
                        cmt('''This is inline
multiline comment''', indent: false)
                        yngbuild('') //yngbuild echoes value, yngbuild('') means new line

                        cmt('''This is indented
multiline comment.''', inline: false)
                        cmt('''This is indented inline
multiline comment''')
                        yngbuild('') //yngbuild echoes value, yngbuild('') means new line


                        organization 'novakmi'
                        contact 'it.novakmi@gmail.com'
                        description 'test quotes'

                        container('socket', cmt: "Inline comment for socket container") {
                                cmt('This is indented comment in container', inline: false)
                                yngbuild('') //yngbuild echoes value, yngbuild('') means new line
                                cmt('This is inline indented comment in container')
                                yngbuild('') //yngbuild echoes value, yngbuild('') means new line
                                presence 'yes'
                                leaf('ipnum') {
                                        type('string') {
                                                pattern('[0-9a-fA-F]*', cmt: "Inline comment for pattern")
                                        }
                                }
                                list('ports') {
                                        cmt('''This is indented
multiline comment in list.''', inline: false)
                                        yngbuild('') //yngbuild echoes value, yngbuild('') means new line
                                        cmt('''This is indented inline
multiline comment in list''')
                                        yngbuild('') //yngbuild echoes value, yngbuild('') means new line

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

/*
This is comment
*/
// This is inline comment

    /*
    This is indented comment
    */
    // This is inline indented comment

/*
This is
multiline comment.
*/
// This is inline
// multiline comment

    /*
    This is indented
    multiline comment.
    */
    // This is indented inline
    // multiline comment

    organization novakmi;
    contact it.novakmi@gmail.com;
    description "test quotes";
    container socket { //Inline comment for socket container
        /*
        This is indented comment in container
        */

        // This is inline indented comment in container

        presence yes;
        leaf ipnum {
            type string {
                pattern [0-9a-fA-F]*; //Inline comment for pattern
            }
        }
        list ports {
            /*
            This is indented
            multiline comment in list.
            */

            // This is indented inline
            // multiline comment in list

            key port;
            leaf port {
                type uint16;
            }
        }
    }
}
''')
                YangBuilderTestCommon.assertYangFile(builder, YangBuilderTestCommon._TEST_MODULE_NAME)
                log.trace("<== commentTest")
        }

        @Test(groups = ["basic"])
        public void commonAliasesTest() {
                log.trace("==> commonAliasesTest")
                def builder = new YangBuilder(4) // new instance/use indent 4
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
                builder.declareCommonAliases()
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
                        leaf("port") {
                                type("int32")
                                default_ 22
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
    leaf port {
        type int32;
        default 22;
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
}
