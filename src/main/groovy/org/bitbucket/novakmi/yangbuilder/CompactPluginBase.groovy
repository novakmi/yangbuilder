//This is free software licensed under MIT License, see LICENSE file
//(https://bitbucket.org/novakmi/yangbuilder/src/LICENSE)


package org.bitbucket.novakmi.yangbuilder

import org.bitbucket.novakmi.nodebuilder.BuilderException
import org.bitbucket.novakmi.nodebuilder.BuilderNode
import org.bitbucket.novakmi.nodebuilder.NodeBuilderPlugin


abstract class CompactPluginBase extends NodeBuilderPlugin {

        protected compactNodeAttr(BuilderNode node, String attrName, nlAllow = true) {
                def retVal = false
                def nameList = [attrName]
                if (getMyBuilder()) {
                        def aliases = getMyBuilder().getAliases(attrName)
                        if (aliases) {
                                nameList += aliases
                        }
                }
                for (a in nameList) {
                        // if attribute ends with _'nl', add new line to new node, unless forbidden by 'nlAllow'
                        def pnlVariant = nlAllow && (node.attributes['pnl_' + a] != null || node.attributes['pnl_' + a + '_nl'] != null)
                        def nlVariant = nlAllow && (node.attributes[a + '_nl'] != null || node.attributes['pnl_' + a + '_nl'] != null)
                        if (node.attributes[a] != null || nlVariant || pnlVariant) {
                                // do we have the attribute or nl attribute variant?

                                BuilderNode typeNode =
                                        new BuilderNode(name: attrName, value: node.attributes[(pnlVariant ? 'pnl_' : '') + a + (nlVariant ? '_nl' : '')])
                                typeNode.setParent(node)
                                if (pnlVariant) {
                                        typeNode.attributes['pnl'] = true
                                }
                                if (nlVariant) {
                                        typeNode.attributes['nl'] = true
                                }
                                node.children = [typeNode] + node.children // prepend to list
                                retVal = true
                                break;
                        }
                }
                return retVal
        }

        protected compactBooleanAttr(BuilderNode node, String attrName, nlAllow = true) {
                def retVal = false
                def val = node.attributes[attrName]
                if (val != null) {
                        if (val instanceof Boolean) {
                                retVal = compactNodeAttr(node, attrName, nlAllow)
                        } else {
                                throw new BuilderException("node: ${node.name} path: ${BuilderNode.getNodePath(node)}; '${attrName}' attribute has to be 'boolean' ('true', 'false')");
                        }
                }
                return retVal
        }

        /**
         * Declare aliases for the Yang language from passed keyword list.
         *
         * For each keyword an alias is created  where all minus ('-') and
         * colon (':') characters are replaced with underscore ('_').
         *
         * @param aliasList
         */
        protected void declareMinColAliases(ArrayList aliasList) {
                if (getMyBuilder()) {
                        aliasList.each { a ->
                                def al = a.replace('-', '_').replace(':', '_')
                                if (al != a) {
                                        getMyBuilder().declareAlias(a.replace('-', '_').replace(':', '_'), a)
                                }
                        }
                }
        }
}
