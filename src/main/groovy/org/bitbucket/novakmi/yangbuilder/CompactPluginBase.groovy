//This is free software licensed under MIT License, see LICENSE file
//(https://bitbucket.org/novakmi/yangbuilder/src/LICENSE)

package org.bitbucket.novakmi.yangbuilder

import org.bitbucket.novakmi.nodebuilder.BuilderException
import org.bitbucket.novakmi.nodebuilder.BuilderNode
import org.bitbucket.novakmi.nodebuilder.NodeBuilderPlugin


abstract class CompactPluginBase extends NodeBuilderPlugin {


        public static final String PNL_ = 'pnl_'
        public static final String _NL = '_nl'

        protected String getPnlNameNl(val) {
                return (val.pnl ? PNL_ : "") + val.name + (val.nl ? _NL : "")
        }

        protected Map splitPnlNameNlNoAlias(String name) {
                def new_name
                def pnl = false
                def nl = false
                def from = 0
                def to = name.length() - 1

                if (name.startsWith(PNL_)) {
                        pnl = true
                        from = 4
                }
                if (name.endsWith(_NL)) {
                        nl = true
                        to -= 3
                }

                new_name = name[from .. to]
                if  (getMyBuilder()) {
                        new_name = getMyBuilder().convertAlias(new_name)
                }
                return ["name": new_name, "pnl":  pnl, "nl" : nl]
        }

        protected getAtrributeInfo(BuilderNode node, attrName) {
                def retVal = null
                def nameList = [attrName]
                if (getMyBuilder()) {
                        def aliases = getMyBuilder().getAliases(attrName)
                        if (aliases) {
                                nameList += aliases
                        }
                }
                for (n in nameList) {
                        for (a in node.attributes) {
                                if (a.key == n) {
                                        retVal = [pnl: false, nl: false, name: attrName, value: a.value]
                                        break;
                                }
                                if (a.key == PNL_ + n) {
                                        retVal = [pnl: true, nl: false, name: attrName, value: a.value]
                                        break;
                                }
                                if (a.key == n + _NL) {
                                        retVal = [pnl: false, nl: true, name: attrName, value: a.value]
                                        break;
                                }
                                if (a.key == PNL_ + n + _NL) {
                                        retVal = [pnl: true, nl: true, name: attrName, value: a.value]
                                        break;
                                }
                        }
                        if (retVal) {
                                break;
                        }
                }
                return retVal
        }

        protected addNodeFromAttrInfo(BuilderNode node, Map attrInfo, boolean nlAllow = true) {
                def retVal = false
                def allow = true
                if (attrInfo) {
                        if (!nlAllow && (attrInfo.pnl || attrInfo.nl)) {
                                allow = false
                        }
                        if (allow) {
                                BuilderNode typeNode =
                                        new BuilderNode(name: attrInfo.name, value: attrInfo.value)
                                typeNode.setParent(node)
                                if (attrInfo.pnl) {
                                        typeNode.attributes['pnl'] = true
                                }
                                if (attrInfo.nl) {
                                        typeNode.attributes['nl'] = true
                                }
                                node.children = [typeNode] + node.children // prepend to list
                                retVal = true
                        }
                }
                return retVal
        }

        protected boolean compactNodeAttr(BuilderNode node, String attrName, nlAllow = true) {
                def attrInfo = getAtrributeInfo(node, attrName)
                def retVal = addNodeFromAttrInfo(node, attrInfo, nlAllow)
                return retVal
        }

        protected boolean compactBooleanAttr(BuilderNode node, String attrName, nlAllow = true) {
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
