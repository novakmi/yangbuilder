/*
Copyright (c) 2011 (Michal Novak) bubbles.way@gmail.com

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

                builder.reset()
                Assert.assertEquals(builder.getText(), '')

                YangBuilderTestCommon._buildTestYang(builder)
                Assert.assertEquals(builder.getText(), YangBuilderTestCommon._getTestYangString())

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

                builder.reset()
                Assert.assertNull(builder.getYangName())

                YangBuilderTestCommon._buildTestYang(builder)
                Assert.assertEquals(builder.getYangName(), YangBuilderTestCommon._TEST_MODULE_NAME)

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

                builder.reset()
                Assert.assertNull(builder.getPrefixName())

                YangBuilderTestCommon._buildTestYang(builder)
                Assert.assertEquals(builder.getPrefixName(), YangBuilderTestCommon._TEST_MODULE_NAME)

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


        //Initialize logging
        private static final Logger logger = LoggerFactory.getLogger(YangBuilderTest.class);
}
