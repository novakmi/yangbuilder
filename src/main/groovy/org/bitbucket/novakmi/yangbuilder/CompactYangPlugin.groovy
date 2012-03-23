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
package org.bitbucket.novakmi.yangbuilder

import org.bitbucket.novakmi.nodebuilder.PluginResult
import org.bitbucket.novakmi.nodebuilder.NodeBuilderPlugin
import org.bitbucket.novakmi.nodebuilder.BuilderException
import org.bitbucket.novakmi.nodebuilder.BuilderNode

/**
 * The plugin which allows for more compact building of yang.
 * The syntax is more compact, but more different from original yang syntax.
 */
class CompactYangPlugin extends NodeBuilderPlugin {

        private compactNodeAttr(BuilderNode node, String attrName) {
                def retVal = false

                if (node.attributes[attrName]) {  // do we have the node?
                        BuilderNode typeNode = new BuilderNode(name: attrName, value: node.attributes[attrName])
                        typeNode.setParent(node)
                        node.children = [typeNode] + node.children // prepend to list
                        retVal = true
                }

                return retVal
        }

        @Override
        protected PluginResult processNodeBefore(BuilderNode node, Object opaque, Map pluginOpaque) throws BuilderException {
                PluginResult retVal = PluginResult.NOT_ACCEPTED

                def processed = false

                // type
                if (node.name in ['leaf', 'leaf-list']) {
                        processed |= compactNodeAttr(node, 'type')
                }

                // description
                if (node.name in ['leaf', 'leaf-list', 'list', 'container']) {
                        processed |= compactNodeAttr(node, 'description')
                }

                if (processed) {
                        retVal = PluginResult.PROCESSED_CONTINUE
                }

                return retVal
        }

        @Override
        protected PluginResult processNodeAfter(BuilderNode node, Object opaque, Map pluginMap) throws BuilderException {
                PluginResult retVal = PluginResult.NOT_ACCEPTED

                if (node.attributes['nl']) {
                        opaque.println('') // new line after processed
                        retVal = PluginResult.PROCESSED_CONTINUE
                }

                return retVal
        }

}
