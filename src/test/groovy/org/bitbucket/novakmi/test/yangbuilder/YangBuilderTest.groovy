//This is free software licensed under MIT License, see LICENSE file
//(https://bitbucket.org/novakmi/yangbuilder/src/LICENSE)

package org.bitbucket.novakmi.test.yangbuilder

import groovy.util.logging.Slf4j
import org.bitbucket.novakmi.yangbuilder.YangBuilder
import org.testng.Assert
import org.testng.annotations.Test

@Slf4j
//Initialize logging
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
                        namespace "http://novakmi.bitbucket.org/test";
                        // semicolon at the end can be preset (yang style)
                        prefix YangBuilderTestCommon._TEST_MODULE_NAME
                        // or semicolon can be missing (more groovy like style)
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
                                        description '''This is loooooooong and even looooonger and ...........
                                                       multiline dexription that should be nicely formatted according
                                                       to new line and indented to next level. The lines should be trimmed as well'''
                                }
                                must("name = 'test'") {
                                        "error-message"('Name is not test!')
                                        description '''This is loooooooong and even looooonger and ...........
                                                       multiline dexription that should be nicely formatted according
                                                       to new line and not indented to parent level (indent is kept to parent).
                                                       The lines should be trimmed''', indent: false
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
            description
                "This is loooooooong and even looooonger and ...........
                 multiline dexription that should be nicely formatted according
                 to new line and indented to next level. The lines should be trimmed as well";
        }
        must "name = 'test'" {
            error-message "Name is not test!";
            description
"This is loooooooong and even looooonger and ...........
 multiline dexription that should be nicely formatted according
 to new line and not indented to parent level (indent is kept to parent).
 The lines should be trimmed";
        }
    }
}
''')
                YangBuilderTestCommon.assertYangFile(builder, YangBuilderTestCommon._TEST_MODULE_NAME)

                builder.reset()
                builder.module(YangBuilderTestCommon._TEST_MODULE_NAME) {
                        namespace "http://novakmi.bitbucket.org/test";
                        // semicolon at the end can be preset (yang style)
                        prefix YangBuilderTestCommon._TEST_MODULE_NAME
                        // or semicolon can be missing (more groovy like style)
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
                        namespace "http://novakmi.bitbucket.org/test";
                        // semicolon at the end can be preset (yang style)
                        prefix YangBuilderTestCommon._TEST_MODULE_NAME
                        // or semicolon can be missing (more groovy like style)
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
                        namespace "http://novakmi.bitbucket.org/test";
                        // semicolon at the end can be preset (yang style)
                        prefix YangBuilderTestCommon._TEST_MODULE_NAME
                        // or semicolon can be missing (more groovy like style)
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
                        namespace "http://novakmi.bitbucket.org/test";
                        // semicolon at the end can be preset (yang style)
                        prefix YangBuilderTestCommon._TEST_MODULE_NAME
                        // or semicolon can be missing (more groovy like style)
                        yngbuild('') //yngbuild echoes value, yngbuild('') means new line

                        organization 'novakmi'
                        contact('it.novakmi@gmail.com', quotes: '"')
                        description(
                                '''"test quotes
    in multiline
description"''', noAutoQuotes: true) // multiline and indent
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

                builder.reset()
                builder.module(YangBuilderTestCommon._TEST_MODULE_NAME) {
                        namespace "http://novakmi.bitbucket.org/test";
                        // semicolon at the end can be preset (yang style)
                        prefix YangBuilderTestCommon._TEST_MODULE_NAME
                        // or semicolon can be missing (more groovy like style)
                        yngbuild('') //yngbuild echoes value, yngbuild('') means new line

                        organization 'novakmi'
                        contact('it.novakmi@gmail.com', quotes: '"')
                        description('test') // no quotes
                        leaf("name") {
                                type("string")
                                description "test", doQuote:true //force quotes
                        }
                        leaf("name2") {
                                type("string")
                                description "test", quotes:"\"" //force quotes - another way
                        }
                }
                Assert.assertEquals(builder.getText(), '''module test {
    namespace "http://novakmi.bitbucket.org/test";
    prefix test;

    organization novakmi;
    contact "it.novakmi@gmail.com";
    description test;
    leaf name {
        type string;
        description "test";
    }
    leaf name2 {
        type string;
        description "test";
    }
}
''')

                log.trace("<== quoteTest")
        }

        @Test(groups = ["basic"])
        public void commentTest() {
                log.trace("==> commentTest")
                def builder = new YangBuilder(4) // new instance/use indent 4
                builder.module(YangBuilderTestCommon._TEST_MODULE_NAME) {
                        namespace "http://novakmi.bitbucket.org/test";
                        // semicolon at the end can be preset (yang style)
                        prefix YangBuilderTestCommon._TEST_MODULE_NAME
                        // or semicolon can be missing (more groovy like style)
                        yngbuild('') //yngbuild echoes value, yngbuild('') means new line

                        cmt('\nThis is comment', indent: false, inline: false)
                        cmt('This is inline comment', indent: false)
                        yngbuild('') //yngbuild echoes value, yngbuild('') means new line

                        cmt('\nThis is indented comment', inline: false)
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
                                cmt('\nThis is indented comment in container', inline: false)
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
        public void geninfoTest() {
                log.trace("==> geninfoTest")
                def builder = new YangBuilder(4) // new instance/use indent 4
                builder.module(YangBuilderTestCommon._TEST_MODULE_NAME) {
                        geninfo(file: "test.groovy", time: false)
                        namespace "http://novakmi.bitbucket.org/test";
                        // semicolon at the end can be preset (yang style)
                        prefix YangBuilderTestCommon._TEST_MODULE_NAME
                        // or semicolon can be missing (more groovy like style)

                        leaf('port') {
                                type 'uint16'
                        }
                }
                Assert.assertEquals(builder.getText(), '''module test {
    /*
    ***** DO NOT EDIT THIS FILE! *****
    This 'yang' file was generated with Groovy 'yangbuilder'
    (http://bitbucket.org/novakmi/yangbuilder)
    Original file is test.groovy
    */
    namespace "http://novakmi.bitbucket.org/test";
    prefix test;
    leaf port {
        type uint16;
    }
}
''')
                builder.reset()
                builder.module(YangBuilderTestCommon._TEST_MODULE_NAME) {
                        geninfo(file: "test.groovy", time: false, cmt: "Example implementation.")
                        namespace "http://novakmi.bitbucket.org/test";
                        // semicolon at the end can be preset (yang style)
                        prefix YangBuilderTestCommon._TEST_MODULE_NAME
                        // or semicolon can be missing (more groovy like style)

                        leaf('port') {
                                type 'uint16'
                        }
                }
                Assert.assertEquals(builder.getText(), '''module test {
    /*
    ***** DO NOT EDIT THIS FILE! *****
    This 'yang' file was generated with Groovy 'yangbuilder'
    (http://bitbucket.org/novakmi/yangbuilder)
    Original file is test.groovy
    Example implementation.
    */
    namespace "http://novakmi.bitbucket.org/test";
    prefix test;
    leaf port {
        type uint16;
    }
}
''')

                builder.reset()
                builder.module(YangBuilderTestCommon._TEST_MODULE_NAME) {
                        geninfo(file: "test.groovy", time: false,
                                cmt: '''
                                        Example implementation.

                                        The comment can be on several lines.
                                        Each line will be trimmed and printed.
                                           The lines will be aligned
                                        and indented.''')
                        namespace "http://novakmi.bitbucket.org/test";
                        // semicolon at the end can be preset (yang style)
                        prefix YangBuilderTestCommon._TEST_MODULE_NAME
                        // or semicolon can be missing (more groovy like style)

                        leaf('port') {
                                type 'uint16'
                        }
                }
                Assert.assertEquals(builder.getText(), '''module test {
    /*
    ***** DO NOT EDIT THIS FILE! *****
    This 'yang' file was generated with Groovy 'yangbuilder'
    (http://bitbucket.org/novakmi/yangbuilder)
    Original file is test.groovy

    Example implementation.

    The comment can be on several lines.
    Each line will be trimmed and printed.
    The lines will be aligned
    and indented.
    */
    namespace "http://novakmi.bitbucket.org/test";
    prefix test;
    leaf port {
        type uint16;
    }
}
''')

                log.trace("<== geninfoTest")
        }

        @Test(groups = ["basic"])
        public void ygnTest() {
                log.trace("==> ygnTest")
                def builder = new YangBuilder(4) // new instance/use indent 4
                builder.module(YangBuilderTestCommon._TEST_MODULE_NAME) {
                        namespace "http://novakmi.bitbucket.org/test"
                        prefix YangBuilderTestCommon._TEST_MODULE_NAME
                        'import'("ietf-inet-types") {
                                prefix "inet"
                        }
                        container("my_text") {
                                leaf("text") {
                                        type "string"
                                }
                                my_leaf(_ygn: true)
                        }
                        container("not_in_yang", _ygn: true)
                        container("not_in_yang2", _ygn: true) {
                                container("in_yang", presence: true) { //child nodes not ignored
                                        presence(true)
                                }
                        }
                        leaf("after_ygn") { // check indentation is not influenced by _ygn node
                                type("uint32")
                        }
                }
                Assert.assertEquals(builder.getText(), '''module test {
    namespace "http://novakmi.bitbucket.org/test";
    prefix test;
    import ietf-inet-types {
        prefix inet;
    }
    container my_text {
        leaf text {
            type string;
        }
    }
    container in_yang {
        presence true;
    }
    leaf after_ygn {
        type uint32;
    }
}
''')
                log.trace("<== ygnTest")
        }

        @Test(groups = ["basic"])
        public void closureTest() {
                log.trace("==> closureTest")  // since nodebuilder-1.0.0

                //all closures are instantiated before builder (using `delegate`)
                def header = {
                        namespace "http://novakmi.bitbucket.org/test"
                        prefix YangBuilderTestCommon._TEST_MODULE_NAME
                        'import'("ietf-inet-types") {
                                prefix "inet"
                        }
                }

                def leaf_text = { txt ->
                        leaf(txt) {
                                type "string"
                        }
                }

                def my_text = {
                        container("my_text") {
                                delegate << leaf_text.curry("text") //pass param with curry
                                my_leaf(_ygn: true)
                        }
                }

                def in_yang = { val ->
                        container(val, presence: true) { //child nodes not ignored
                                presence(true)
                        }
                }

                def module = {
                        module(YangBuilderTestCommon._TEST_MODULE_NAME) {
                                delegate << header
                                delegate << my_text
                                container("not_in_yang", _ygn: true)
                                container("not_in_yang2", _ygn: true) {
                                        delegate << in_yang.curry("in_yang")
                                }
                                leaf("after_ygn") { // check indentation is not influenced by _ygn node
                                        type("uint32")
                                }
                        }
                }

                def builder = new YangBuilder(4)
                builder << module // send closure to builder

                Assert.assertEquals(builder.getText(), '''module test {
    namespace "http://novakmi.bitbucket.org/test";
    prefix test;
    import ietf-inet-types {
        prefix inet;
    }
    container my_text {
        leaf text {
            type string;
        }
    }
    container in_yang {
        presence true;
    }
    leaf after_ygn {
        type uint32;
    }
}
''')
                log.trace("<== closureTest")
        }

        @Test(groups = ["basic"])
        public void splitOnPlusTest() {
                log.trace("==> splitOnPlusTest")

                def module = {
                        module(YangBuilderTestCommon._TEST_MODULE_NAME) {
                                augment "/rt:routing-state/rt:routes/rt:route/+" +
                                    "rt:next-hop", doSplitOnPlus: true, {
                                        container("ContA")
                                }
                                // slash before plus "\\+" means no split, just plus
                                augment "/rt:routing-state/rt:routes/rt:route/\\+" +
                                    "rt:next-hop", doSplitOnPlus: true, {
                                        container("ContB") 
                                }
                        }
                }

                def builder = new YangBuilder(4)
                builder << module // send closure to builder

                Assert.assertEquals(builder.getText(), '''module test {
    augment "/rt:routing-state/rt:routes/rt:route/"
          + "rt:next-hop" {
        container ContA;
    }
    augment "/rt:routing-state/rt:routes/rt:route/+rt:next-hop" {
        container ContB;
    }
}
''')
                log.trace("<== splitOnPlusTest")
        }

        @Test(groups = ["basic"])
        public void doQuoteTest() {
                log.trace("==> doQuoteTest")

                def module = {
                        module(YangBuilderTestCommon._TEST_MODULE_NAME) {
                                container("contA") {
                                        description "one"
                                }
                                leaf("val") {
                                        mandatory true
                                        type("uint32")
                                }
                        }
                }

                def builder = new YangBuilder(4)
                builder << module // send closure to builder

                Assert.assertEquals(builder.getText(), '''module test {
    container contA {
        description one;
    }
    leaf val {
        mandatory true;
        type uint32;
    }
}
''')
                 builder.resetText()
                 builder.addConfig([doQuote:true])
                 Assert.assertEquals(builder.getText(), '''module test {
    container contA {
        description "one";
    }
    leaf val {
        mandatory true;
        type uint32;
    }
}
''')

                builder.resetText()
                builder.quoteKeywords += ["mandatory"]
                Assert.assertEquals(builder.getText(), '''module test {
    container contA {
        description "one";
    }
    leaf val {
        mandatory "true";
        type uint32;
    }
}
''')

                log.trace("<== doQuoteTest")
        }

        @Test(groups = ["basic"])
        public void autoNlTest() {
                log.trace("==> autoNlTest")

                def module = {
                        module(YangBuilderTestCommon._TEST_MODULE_NAME) {
                                container("contA") {
                                        description "one"
                                }
                        }
                }

                def builder = new YangBuilder(4)
                builder << module // send closure to builder

                Assert.assertEquals(builder.getText(), '''module test {
    container contA {
        description one;
    }
}
''')
                builder.resetText()
                builder.addConfig([doNl:true])
                Assert.assertEquals(builder.getText(), '''module test {
    container contA {
        description
            one;
    }
}
''')
                log.trace("<== autoNlTest")
        }

}
