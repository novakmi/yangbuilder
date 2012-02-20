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

        static def _TEST_MODULE_NAME = 'test'
        static def _TEST_SUBMODULE_NAME = "${_TEST_MODULE_NAME}_submodule"
        static def WRITE_TO_FILE = true
        static USE_PYANG = true // when true, pyang (http://code.google.com/p/pyang/) has to be in PATH
        //write yang to file
        static def assertYangFile(YangBuilder builder, fileName) {
                logger.trace("==> assertYangFile")
                if (WRITE_TO_FILE) {
                        logger.trace("writing to file {}", builder.getText())
                        builder.writeToFile("./${fileName}.yang")
                        if (USE_PYANG) {
                                Process process = "pyang -f tree ./${fileName}.yang".execute()
                                process.waitFor()
                                logger.trace("process.exitValue() {} process.text {}", process.exitValue(), process.text)
                                logger.trace("process.err.text {}", process.err.text)
                                Assert.assertEquals(process.exitValue(), 0)
                        }
                }
                //Assert.assertNotNull(null) //for debugging
                logger.trace("<== assertYangFile")
        }

        static def _buildTestYang(builder) {
                logger.trace("==> _buildTestYang")
                builder.module(_TEST_MODULE_NAME) {
                        namespace "http://novakmi.bitbucket.org/test"; // semicolon at the end can be preset (yang style)
                        prefix _TEST_MODULE_NAME // or semicolon can be missing (more groovy like style)
                        yngbuild('') //yngbuild echoes value, yngbuild('') means new line

                        organization 'bubbles'
                        contact 'bubbles.way@gmail.com'

                        container('socket') {
                                leaf('ip') {
                                        type 'string'
                                }
                                leaf('port') {
                                        type 'uint16'
                                }
                        }
                }
                logger.trace("<== _buildTestYang")
        }

        static def _buildTestSubmoduleYang(builder) {
                logger.trace("==> _buildTestSubmoduleYang")

                builder.submodule(_TEST_SUBMODULE_NAME) {
                        prefix _TEST_SUBMODULE_NAME // or semicolon can be missing (more groovy like style)
                        yngbuild('') //yngbuild echoes value, yngbuild('') means new line

                        organization 'bubbles'
                        contact 'bubbles.way@gmail.com'

                        container('socket') {
                                leaf('ip') {
                                        type 'string'
                                }
                                leaf('port') {
                                        type 'uint16'
                                }
                        }
                }

                logger.trace("<== _buildTestSubmoduleYang")
        }

        String _getTestYangString() {
                def retVal = "module ${_TEST_MODULE_NAME} " +
                    '''{
    namespace "http://novakmi.bitbucket.org/test";
    prefix '''+ _TEST_MODULE_NAME + ''';

    organization bubbles;
    contact bubbles.way@gmail.com;
    container socket {
        leaf ip {
            type string;
        }
        leaf port {
            type uint16;
        }
    }
}
'''
                return retVal
        }

// test based on example from Instant YANG tutorial, section modules
        @Test(groups = ["basic"])
        public void yangTest() {
                logger.trace("==> yangTest")
                def builder = new YangBuilder(4) // new instance/use indent 4
                _buildTestYang(builder)
                Assert.assertEquals(builder.getText(), _getTestYangString())
                assertYangFile(builder, _TEST_MODULE_NAME)
                logger.trace("<== yangTest")
        }

        @Test(groups = ["basic"])
        public void yangResetTest() {
                logger.trace("==> yangResetTest")

                def builder = new YangBuilder(4) // new instance/use indent 4
                _buildTestYang(builder)
                Assert.assertEquals(builder.getText(), _getTestYangString())

                builder.reset()
                Assert.assertEquals(builder.getText(), '')

                logger.trace("<== yangResetTest")
        }

        @Test(groups = ["basic"])
        public void yangResetAfterYangrootTest() {
                logger.trace("==> yangResetAfterYangrootTest")

                def builder = new YangBuilder(4) // new instance/use indent 4
                builder.yangroot {
                        _buildTestYang(builder)
                }
                Assert.assertEquals(builder.getText(), _getTestYangString())

                builder.reset()
                Assert.assertEquals(builder.getText(), '')

                _buildTestYang(builder)
                Assert.assertEquals(builder.getText(), _getTestYangString())

                logger.trace("<== yangResetAfterYangrootTest")
        }

        @Test(groups = ["basic"])
        public void yangNameTest() {
                logger.trace("==> yangNameTest")

                // module
                def builder = new YangBuilder(4) // new instance/use indent 4
                Assert.assertNull(builder.getYangName())
                builder.yangroot {
                        _buildTestYang(builder)
                }
                Assert.assertEquals(builder.getYangName(), _TEST_MODULE_NAME)

                builder.reset()
                Assert.assertNull(builder.getYangName())

                _buildTestYang(builder)
                Assert.assertEquals(builder.getYangName(), _TEST_MODULE_NAME)

                builder.reset()
                Assert.assertNull(builder.getYangName())

                // submodule
                builder = new YangBuilder(4) // new instance/use indent 4
                Assert.assertNull(builder.getYangName())
                builder.yangroot {
                        _buildTestSubmoduleYang(builder)
                }
                Assert.assertEquals(builder.getYangName(), _TEST_SUBMODULE_NAME)

                builder.reset()
                Assert.assertNull(builder.getYangName())

                _buildTestSubmoduleYang(builder)
                Assert.assertEquals(builder.getYangName(), _TEST_SUBMODULE_NAME)

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
                        _buildTestYang(builder)
                }
                Assert.assertEquals(builder.getPrefixName(), _TEST_MODULE_NAME)

                builder.reset()
                Assert.assertNull(builder.getPrefixName())

                _buildTestYang(builder)
                Assert.assertEquals(builder.getPrefixName(), _TEST_MODULE_NAME)

                builder.reset()
                Assert.assertNull(builder.getPrefixName())

                // submodule
                builder = new YangBuilder(4) // new instance/use indent 4
                Assert.assertNull(builder.getPrefixName())
                builder.yangroot {
                        _buildTestSubmoduleYang(builder)
                }
                Assert.assertEquals(builder.getPrefixName(), _TEST_SUBMODULE_NAME)

                builder.reset()
                Assert.assertNull(builder.getPrefixName())

                _buildTestSubmoduleYang(builder)
                Assert.assertEquals(builder.getPrefixName(), _TEST_SUBMODULE_NAME)

                builder.reset()
                Assert.assertNull(builder.getPrefixName())

                logger.trace("<== prefixNameTest")
        }


//Initialize logging
        private static final Logger logger = LoggerFactory.getLogger(YangBuilderTest.class);
}
