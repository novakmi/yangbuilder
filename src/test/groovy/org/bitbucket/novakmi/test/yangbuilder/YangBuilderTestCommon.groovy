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

import org.bitbucket.novakmi.yangbuilder.YangBuilder
import org.testng.Assert
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class YangBuilderTestCommon {
        static def _TEST_MODULE_NAME = 'test'
        static def _TEST_SUBMODULE_NAME = "${_TEST_MODULE_NAME}_submodule"
        static def WRITE_TO_FILE = true
        // when true, pyang (http://code.google.com/p/pyang/) has to be in PATH
        //write yang to file
        static USE_PYANG = true

        public static def assertYangFile(YangBuilder builder, fileName) {
                org.bitbucket.novakmi.test.yangbuilder.YangBuilderTest.logger.trace("==> assertYangFile")
                if (WRITE_TO_FILE) {
                        org.bitbucket.novakmi.test.yangbuilder.YangBuilderTest.logger.trace("writing to file {}", builder.getText())
                        builder.writeToFile("./${fileName}.yang")
                        if (USE_PYANG) {
                                Process process = "pyang -f tree ./${fileName}.yang".execute()
                                process.waitFor()
                                org.bitbucket.novakmi.test.yangbuilder.YangBuilderTest.logger.trace("process.exitValue() {} process.text {}", process.exitValue(), process.text)
                                org.bitbucket.novakmi.test.yangbuilder.YangBuilderTest.logger.trace("process.err.text {}", process.err.text)
                                Assert.assertEquals(process.exitValue(), 0)
                        }
                }
                //Assert.assertNotNull(null) //for debugging
                org.bitbucket.novakmi.test.yangbuilder.YangBuilderTest.logger.trace("<== assertYangFile")
        }

        static def _buildTestYang(builder) {
                org.bitbucket.novakmi.test.yangbuilder.YangBuilderTest.logger.trace("==> _buildTestYang")
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
                org.bitbucket.novakmi.test.yangbuilder.YangBuilderTest.logger.trace("<== _buildTestYang")
        }

        static def _buildTestSubmoduleYang(builder) {
                org.bitbucket.novakmi.test.yangbuilder.YangBuilderTest.logger.trace("==> _buildTestSubmoduleYang")

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

                org.bitbucket.novakmi.test.yangbuilder.YangBuilderTest.logger.trace("<== _buildTestSubmoduleYang")
        }

        static String _getTestYangString() {
                def retVal = "module ${YangBuilderTestCommon._TEST_MODULE_NAME} " +
                    '''{
    namespace "http://novakmi.bitbucket.org/test";
    prefix '''+ YangBuilderTestCommon._TEST_MODULE_NAME + ''';

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


        //Initialize logging
        private static final Logger logger = LoggerFactory.getLogger(YangBuilderTestCommon.class);
}
