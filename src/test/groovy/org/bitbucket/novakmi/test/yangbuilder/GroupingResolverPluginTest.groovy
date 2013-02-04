//This is free software licensed under MIT License, see LICENSE file
//(https://bitbucket.org/novakmi/yangbuilder/src/LICENSE)

package org.bitbucket.novakmi.test.yangbuilder

import groovy.util.logging.Slf4j
import org.bitbucket.novakmi.yangbuilder.GroupingResolverPlugin
import org.bitbucket.novakmi.yangbuilder.YangBuilder
import org.testng.Assert
import org.testng.annotations.Test

@Slf4j //Initialize logging
class GroupingResolverPluginTest {

        @Test(groups = ["basic"])
        public void simpleGroupingTest() {
                log.trace("==> simpleGroupingTest")
                def groupingResolver = new GroupingResolverPlugin()
                def builder = new YangBuilder(4, [groupingResolver]) // new instance

                builder.module(YangBuilderTestCommon._TEST_MODULE_NAME) {
                        namespace "http://novakmi.bitbucket.org/test"; // semicolon at the end can be preset (yang style)
                        prefix YangBuilderTestCommon._TEST_MODULE_NAME // or semicolon can be missing (more groovy like style)
                        yngbuild('') //yngbuild echoes value, yngbuild('') means new line

                        grouping('grouping-test') {
                                container('container-a') {
                                        leaf('leaf-a') {
                                                type('uint32')
                                        }
                                }
                                leaf('leaf-b') {
                                        type('uint32')
                                }
                        }

                        container('container-main') {
                                uses('grouping-test')
                        }
                }

                Assert.assertEquals(builder.getText(),
                        '''module test {
    namespace "http://novakmi.bitbucket.org/test";
    prefix test;

    grouping grouping-test {
        container container-a {
            leaf leaf-a {
                type uint32;
            }
        }
        leaf leaf-b {
            type uint32;
        }
    }
    container container-main {
        uses grouping-test;
    }
}
''')

                def newNode = groupingResolver.resolveGroupings(builder.getRootNode())
                Assert.assertEquals(newNode.children.size(), 4)
                Assert.assertEquals(newNode.children[3].name, 'container')
                Assert.assertEquals(newNode.children[3].value, 'container-main')

                Assert.assertEquals(newNode.children[3].children.size(), 2)
                Assert.assertEquals(newNode.children[3].children[0].name, 'container')
                Assert.assertEquals(newNode.children[3].children[0].value, 'container-a')
                Assert.assertEquals(newNode.children[3].children[0].children.size(), 1)
                Assert.assertEquals(newNode.children[3].children[0].children[0].name, 'leaf')
                Assert.assertEquals(newNode.children[3].children[0].children[0].value, 'leaf-a')
                Assert.assertEquals(newNode.children[3].children[0].children[0].children.size(), 1)
                Assert.assertEquals(newNode.children[3].children[0].children[0].children[0].name, 'type')
                Assert.assertEquals(newNode.children[3].children[0].children[0].children[0].value, 'uint32')
                Assert.assertEquals(newNode.children[3].children[1].name, 'leaf')
                Assert.assertEquals(newNode.children[3].children[1].value, 'leaf-b')

                YangBuilderTestCommon.assertYangFile(builder, YangBuilderTestCommon._TEST_MODULE_NAME)

                // resolve without grouping
                Assert.assertEquals(builder.getNodeText(newNode),
                        '''module test {
    namespace "http://novakmi.bitbucket.org/test";
    prefix test;

    container container-main {
        container container-a {
            leaf leaf-a {
                type uint32;
            }
        }
        leaf leaf-b {
            type uint32;
        }
    }
}
''')


                //resolve only subnode
                Assert.assertEquals(builder.getNodeText(newNode.children[3]),
                        '''container container-main {
    container container-a {
        leaf leaf-a {
            type uint32;
        }
    }
    leaf leaf-b {
        type uint32;
    }
}
''')

                builder.reset()

                builder.module(YangBuilderTestCommon._TEST_MODULE_NAME) {
                        namespace "http://novakmi.bitbucket.org/test"; // semicolon at the end can be preset (yang style)
                        prefix YangBuilderTestCommon._TEST_MODULE_NAME // or semicolon can be missing (more groovy like style)
                        yngbuild('') //yngbuild echoes value, yngbuild('') means new line

                        grouping('grouping-test1') {
                                container('container-a1') {
                                        leaf('leaf-a') {
                                                type('uint32')
                                        }
                                }
                                leaf('leaf-b1') {
                                        type('uint32')
                                }
                        }

                        grouping('grouping-test2') {
                                container('container-a') {
                                        leaf('leaf-a') {
                                                type('uint32')
                                        }
                                }
                                uses('grouping-test1')
                                leaf('leaf-b') {
                                        type('uint32')
                                }
                        }

                        container('container-main') {
                                uses('grouping-test2')
                        }
                }

                Assert.assertEquals(builder.getText(),
                        '''module test {
    namespace "http://novakmi.bitbucket.org/test";
    prefix test;

    grouping grouping-test1 {
        container container-a1 {
            leaf leaf-a {
                type uint32;
            }
        }
        leaf leaf-b1 {
            type uint32;
        }
    }
    grouping grouping-test2 {
        container container-a {
            leaf leaf-a {
                type uint32;
            }
        }
        uses grouping-test1;
        leaf leaf-b {
            type uint32;
        }
    }
    container container-main {
        uses grouping-test2;
    }
}
''')


                newNode = groupingResolver.resolveGroupings(builder.getRootNode())
                Assert.assertEquals(newNode.children.size(), 4)
                Assert.assertEquals(newNode.children[3].name, 'container')
                Assert.assertEquals(newNode.children[3].value, 'container-main')

                Assert.assertEquals(newNode.children[3].children.size(), 4)
                Assert.assertEquals(newNode.children[3].children[0].name, 'container')
                Assert.assertEquals(newNode.children[3].children[0].value, 'container-a')
                Assert.assertEquals(newNode.children[3].children[1].name, 'container')
                Assert.assertEquals(newNode.children[3].children[1].value, 'container-a1')
                Assert.assertEquals(newNode.children[3].children[2].name, 'leaf')
                Assert.assertEquals(newNode.children[3].children[2].value, 'leaf-b1')
                Assert.assertEquals(newNode.children[3].children[3].name, 'leaf')
                Assert.assertEquals(newNode.children[3].children[3].value, 'leaf-b')


                log.trace("<== simpleGroupingTest")
        }
}
