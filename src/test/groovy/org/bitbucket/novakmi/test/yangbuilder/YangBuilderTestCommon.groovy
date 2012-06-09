//This is free software licensed under MIT License, see LICENSE file
//(https://bitbucket.org/bubbles.way/yangbuilder/src/LICENSE)

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
                        yngbuild('') //yngbuild echoes value, yngbuild('') means new line
                        'belongs-to'() {
                                prefix _TEST_SUBMODULE_NAME // or semicolon can be missing (more groovy like style)
                        }
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
