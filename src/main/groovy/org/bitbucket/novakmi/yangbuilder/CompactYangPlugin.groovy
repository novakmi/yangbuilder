//This is free software licensed under MIT License, see LICENSE file
//(https://bitbucket.org/bubbles.way/yangbuilder/src/LICENSE)

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

        private compactNodeAttr(BuilderNode node, String attrName, nlAllow = true) {
                def retVal = false
                // if attribute ends with _'nl', add new line to new node, unless forbidden by 'nlAllow'
                def pnlVariant = nlAllow && (node.attributes['pnl_'+ attrName] != null || node.attributes['pnl_'+ attrName + '_nl'] != null)
                def nlVariant = nlAllow && (node.attributes[attrName + '_nl'] != null ||  node.attributes['pnl_'+ attrName + '_nl'] != null)
                if (node.attributes[attrName] != null || nlVariant || pnlVariant) {  // do we have the attribute or nl attribute variant?

                        BuilderNode typeNode =
                                new BuilderNode(name: attrName, value: node.attributes[(pnlVariant ? 'pnl_' : '') + attrName + (nlVariant ? '_nl' : '')])
                        typeNode.setParent(node)
                        if (pnlVariant) {
                                typeNode.attributes['pnl'] = true
                        }
                        if (nlVariant) {
                                typeNode.attributes['nl'] = true
                        }
                        node.children = [typeNode] + node.children // prepend to list
                        retVal = true
                }
                return retVal
        }

        @Override
        protected PluginResult processNodeBefore(BuilderNode node, Object opaque, Map pluginOpaque) throws BuilderException {
                PluginResult retVal = PluginResult.NOT_ACCEPTED

                def processed = false

                if (node.attributes['pnl']) {
                        opaque.println('') // new line before processed
                        processed |= true
                }

                // prefix under 'import', 'belongs-to', 'module'
                if (node.name in ['import', 'belongs-to', 'module']) {
                        processed |= compactNodeAttr(node, 'prefix')
                }

                if (node.name in ['module'])  {
                        processed |= compactNodeAttr(node, 'namespace')
                }

                // type  under 'leaf', 'leaf-list'. 'typedef'
                if (node.name in ['leaf', 'leaf-list', 'typedef']) {
                        processed |= compactNodeAttr(node, 'type')
                }

                // key under 'list'
                if (node.name in ['list']) {
                        processed |= compactNodeAttr(node, 'key')
                }

                // mandatory under 'leaf', 'choice'
                if (node.name in ['leaf', 'choice']) {
                        def man = node.attributes['mandatory']
                        if (man != null) {
                                if (man instanceof Boolean) {
                                        processed |= compactNodeAttr(node, 'mandatory')
                                } else {
                                        throw new BuilderException("'mandatory' attribute of '${node.name} ${node.value}' has to be 'boolean' ('true', 'false')");
                                }
                        }
                }

                // description  under 'leaf', 'leaf-list', 'list', 'container', 'revision', 'typedef'
                if (node.name in ['leaf', 'leaf-list', 'list', 'container', 'choice', 'revision', 'typedef']) {
                        processed |= compactNodeAttr(node, 'description')
                        if (node.name in ['container']) {
                                processed |= compactNodeAttr(node, 'presence')
                        }
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
