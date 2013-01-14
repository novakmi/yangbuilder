//This is free software licensed under MIT License, see LICENSE file
//(https://bitbucket.org/bubbles.way/yangbuilder/src/LICENSE)

package org.bitbucket.novakmi.yangbuilder

import groovy.transform.CompileStatic
import org.bitbucket.novakmi.nodebuilder.BuilderException
import org.bitbucket.novakmi.nodebuilder.BuilderNode
import org.bitbucket.novakmi.nodebuilder.NodeBuilderPlugin
import org.bitbucket.novakmi.nodebuilder.PluginResult

/**
 * The plugin which resolves groupings withing one builder.
 */

class GroupingResolverPlugin extends NodeBuilderPlugin {

        def groupingMap = [:]

        @Override
        /**
         * create maps with groupings
         */ protected PluginResult processNodeAfter(BuilderNode node, Object opaque, Map pluginMap) throws BuilderException {
                PluginResult retVal = PluginResult.NOT_ACCEPTED

                if (node.name == 'grouping') {
                        if (!groupingMap[node.value]) {
                                groupingMap[node.value] = node.children
                        } else {
                                groupingMap[node.value] += node.children
                        }
                        retVal = PluginResult.PROCESSED_CONTINUE
                }
                return retVal
        }

        /**
         * TODO move to BuilderNode?
         * @return
         */
        static private BuilderNode shallowCopyNode(BuilderNode node) {
                return new BuilderNode(name: node.name, value: node.value, attributes: node.attributes, parent: node.parent)
        }

        /**
         * Resolve groupings in subnodes for given node
         * @param node
         * @return
         */
        private def resolveGroupingsInSubnodes(BuilderNode node) throws BuilderException {
                if (node.name == 'grouping') {
                        throw new BuilderException("Cannot resolve ${node.name} ${node.value} node!")
                }

                def children = []
                if (node.name in ['leaf', 'leaf-list', 'prefix', 'namespace']) {
                        children = node.children
                } else {
                        def nodeChildren =  node.children
                        if (node.name == 'uses') {
                                nodeChildren = groupingMap[node.value]
                        }
                        nodeChildren.each { c ->
                                // these subnodes cannot have groupings // todo add more?
                                if (c.name != 'grouping') { // skip all groupings
                                        if (c.name == 'uses') {
                                                def usesChildren = groupingMap[c.value]
                                                usesChildren.each { uC ->
                                                        if (uC.name == 'uses') {
                                                                children += resolveGroupingsInSubnodes(uC)
                                                        } else {
                                                                children += resolveGroupings(uC)
                                                        }
                                                }
                                        } else {
                                                children += resolveGroupings(c)
                                        }
                                }
                        }
                }

                return children
        }

        /**
         * Resolve groupings for given node
         * @param node
         * @return
         * @throws BuilderException
         */
        BuilderNode resolveGroupings(BuilderNode node) throws BuilderException {
                if (node.name == 'uses') {
                        throw new BuilderException("Cannot resolve ${node.name} ${node.value} node!")
                }

                def newNode = shallowCopyNode(node)
                newNode.parent = null
                newNode.children = resolveGroupingsInSubnodes(node)
                newNode.children.each { ch ->
                        ((BuilderNode) ch).parent = newNode
                }

                return newNode
        }

        public void reset() {
                groupingMap = [:]
        }
}
