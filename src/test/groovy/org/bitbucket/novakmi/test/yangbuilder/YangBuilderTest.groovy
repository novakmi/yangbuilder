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

        //write yang to file
        static def assertYangFile(builder, fileName) {
                logger.trace("==> assertYangFile")
                logger.trace(builder.getBuiltText())
                File f = new File("./${fileName}.yang").write(builder.getBuiltText())
                logger.trace("<== assertYangFile")
        }

        // test based on example from Instant YANG tutorial, section modules
        @Test(groups = ["basic"])
        public void yangTest() {
                logger.trace("==> yangTest")
                def builder = new YangBuilder(4) // new instance/use indent 4

                builder.module('acme-module') {

                        namespace "http://acme.example.com/module"; // semicolon at the end can be preset (more yang style)
                        prefix "acme" // or semicolon can be missing (more groovy style)
                        yngbuild('') //new line

                        'import'("yang-types") {
                                prefix "yang"
                        }
                        include "acme-system"
                        yngbuild('')

                        organization 'ACME Inc.'
                        contact 'joe@acme.example.com'
                        description "The module for entities implementing the ACME products"
                        yngbuild('')

                        revision('2007-06-09') {
                                description "Initial revision."
                        }

                }

                Assert.assertEquals(builder.getBuiltText(),
                    '''module acme-module {
    namespace "http://acme.example.com/module";
    prefix acme;

    import yang-types {
        prefix yang;
    }
    include acme-system;

    organization "ACME Inc.";
    contact joe@acme.example.com;
    description "The module for entities implementing the ACME products";

    revision 2007-06-09 {
        description "Initial revision.";
    }
}
''')
                //assertYangFile(builder, 'acme-module')
                logger.trace("<== yangTest")
        }
        //Initialize logging
        private static final Logger logger = LoggerFactory.getLogger(YangBuilderTest.class);
}
