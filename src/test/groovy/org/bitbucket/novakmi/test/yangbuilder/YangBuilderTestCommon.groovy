//This is free software licensed under MIT License, see LICENSE file
//(https://bitbucket.org/novakmi/yangbuilder/src/LICENSE)

package org.bitbucket.novakmi.test.yangbuilder

import groovy.util.logging.Slf4j
import org.bitbucket.novakmi.yangbuilder.YangBuilder
import org.testng.Assert

@Slf4j //Initialize logging
class YangBuilderTestCommon {
        static def _TEST_MODULE_NAME = 'test'
        static def _TEST_SUBMODULE_NAME = "${_TEST_MODULE_NAME}_submodule"
        static def WRITE_TO_FILE = true
        // when true, pyang (http://code.google.com/p/pyang/) has to be in PATH
        //write yang to file
        static USE_PYANG = true

        public static def assertYangFile(YangBuilder builder, fileName) {
                log.trace("==> assertYangFile")
                if (WRITE_TO_FILE) {
                        log.trace("writing to file {}", builder.getText())
                        builder.writeToFile("./${fileName}.yang")
                        if (USE_PYANG) {
                                def sout = new StringBuffer()
                                def serr = new StringBuffer()
                                Process process = "pyang -f tree ./${fileName}.yang".execute()
                                process.consumeProcessOutput(sout, serr)
                                process.waitFor()
                                log.trace("process.exitValue() {} sout {}", process.exitValue(), sout)
                                log.trace("serr {}", serr)
                                Assert.assertEquals(process.exitValue(), 0)
                        }
                }
                //Assert.assertNotNull(null) //for debugging
                log.trace("<== assertYangFile")
        }

        static def _buildTestYang(builder) {
                log.trace("==> _buildTestYang")
                builder.module(_TEST_MODULE_NAME) {
                        namespace "http://novakmi.bitbucket.org/test"; // semicolon at the end can be preset (yang style)
                        prefix _TEST_MODULE_NAME // or semicolon can be missing (more groovy like style)
                        yngbuild('') //yngbuild echoes value, yngbuild('') means new line

                        organization 'novakmi'
                        contact 'it.novakmi@gmail.com'

                        container('socket') {
                                leaf('ip') {
                                        type 'string'
                                }
                                leaf('port') {
                                        type 'uint16'
                                }
                        }
                }
                log.trace("<== _buildTestYang")
        }

        static def _buildTestSubmoduleYang(builder) {
                log.trace("==> _buildTestSubmoduleYang")

                builder.submodule(_TEST_SUBMODULE_NAME) {
                        yngbuild('') //yngbuild echoes value, yngbuild('') means new line
                        'belongs-to'() {
                                prefix _TEST_SUBMODULE_NAME // or semicolon can be missing (more groovy like style)
                        }
                        yngbuild('') //yngbuild echoes value, yngbuild('') means new line

                        organization 'novakmi'
                        contact 'it.novakmi@gmail.com'

                        container('socket') {
                                leaf('ip') {
                                        type 'string'
                                }
                                leaf('port') {
                                        type 'uint16'
                                }
                        }
                }

                log.trace("<== _buildTestSubmoduleYang")
        }

        static String _getTestYangString() {
                def retVal = "module ${YangBuilderTestCommon._TEST_MODULE_NAME} " +
                    '''{
    namespace "http://novakmi.bitbucket.org/test";
    prefix '''+ YangBuilderTestCommon._TEST_MODULE_NAME + ''';

    organization novakmi;
    contact it.novakmi@gmail.com;
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
}
