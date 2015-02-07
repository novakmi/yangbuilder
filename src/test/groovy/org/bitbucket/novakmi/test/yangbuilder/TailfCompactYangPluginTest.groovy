//This is free software licensed under MIT License, see LICENSE file
//(https://bitbucket.org/novakmi/yangbuilder/src/LICENSE)

package org.bitbucket.novakmi.test.yangbuilder

import groovy.util.logging.Slf4j
import org.bitbucket.novakmi.yangbuilder.CompactYangPlugin
import org.bitbucket.novakmi.yangbuilder.TailfCompactYangPlugin
import org.bitbucket.novakmi.yangbuilder.YangBuilder
import org.testng.Assert
import org.testng.annotations.Test

@Slf4j
//Initialize logging
class TailfCompactYangPluginTest {

        @Test(groups = ["basic"])
        public void tailfCompactInfoDescrTest() {
                log.trace("==> tailfCompactInfoDescrTest")
                def plugin = new TailfCompactYangPlugin()
                def builder = new YangBuilder(4, [plugin])
                plugin.declareCommonAliasesAndQuotes()

                def yangText = '''module test {
    namespace "http://novakmi.bitbucket.org/test";
    prefix test;
    typedef my-string {
        tailf:info "My info";
        description "compact typedef";
        type string;
    }
    leaf my {
        tailf:info "My description and info";
        description "My description and info";
    }
    leaf my2 {
        tailf:info "My2 description and info";
    }
}
'''

                builder.module(YangBuilderTestCommon._TEST_MODULE_NAME) {
                        namespace "http://novakmi.bitbucket.org/test";
                        prefix YangBuilderTestCommon._TEST_MODULE_NAME

                        typedef("my-string",  "tailf:info": "My info") {
                                description("compact typedef")
                                type("string")
                        }
                        leaf("my", "tailf:info-description": "My description and info")
                        leaf("my2", "tailf:info-description": "My2 description and info", description: "My description")
                        // descr. not added as no CompactPluginUsed
                }

                Assert.assertEquals(builder.getText(), yangText)

                builder.reset()
                builder.module(YangBuilderTestCommon._TEST_MODULE_NAME) {
                        namespace "http://novakmi.bitbucket.org/test";
                        prefix YangBuilderTestCommon._TEST_MODULE_NAME

                        typedef("my-string", tailf_info: "My info") {
                                description("compact typedef")
                                type("string")
                        }
                        leaf("my", tailf_info_description: "My description and info")
                        leaf("my2", tailf_info_description: "My2 description and info", description: "My description")
                        // descr. not added as no CompactPluginUsed
                }
                Assert.assertEquals(builder.getText(), yangText)

                log.trace("<== tailfCompactInfoDescrTest")
        }

        @Test(groups = ["basic"])
        public void compactAndTailfCompactInfoDescrTest() {
                log.trace("==> tailfCompactInfoDescrTest")
                def pluginCompact = new CompactYangPlugin()
                def pluginTailf = new TailfCompactYangPlugin()
                def builder = new YangBuilder(4, [pluginTailf, pluginCompact]) // keep this order so Compact goes first
                pluginCompact.declareCommonAliasesAndQuotes()
                pluginTailf.declareCommonAliasesAndQuotes()

                def yangText = '''module test {
    namespace "http://novakmi.bitbucket.org/test";
    prefix test;
    typedef my-string {
        description "compact typedef";
        type string;
        tailf:info "My info";
    }
    leaf my {
        tailf:info "My description and info";
        description "My description and info";
    }
    leaf my2 {
        description "My description";
        tailf:info "My2 description and info";
    }
}
'''

                builder.module(YangBuilderTestCommon._TEST_MODULE_NAME) {
                        namespace "http://novakmi.bitbucket.org/test";
                        prefix YangBuilderTestCommon._TEST_MODULE_NAME

                        typedef("my-string", tailf_info: "My info", description: "compact typedef", type: "string")
                        leaf("my", tailf_info_description: "My description and info")
                        leaf("my2", tailf_info_description: "My2 description and info", description: "My description")
                        // descr. not added as no CompactPluginUsed
                }

                Assert.assertEquals(builder.getText(), yangText)

                log.trace("<== compactAndTailfCompactInfoDescrTest")
        }

}
