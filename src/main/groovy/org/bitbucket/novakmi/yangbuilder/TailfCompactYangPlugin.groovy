//This is free software licensed under MIT License, see LICENSE file
//(https://bitbucket.org/novakmi/yangbuilder/src/LICENSE)

package org.bitbucket.novakmi.yangbuilder

import org.bitbucket.novakmi.nodebuilder.BuilderException
import org.bitbucket.novakmi.nodebuilder.BuilderNode
import org.bitbucket.novakmi.nodebuilder.PluginResult

/**
 * The plugin which allows for more compact building of yang for know Tail-f yang extensions.
 * The syntax is more compact, but more different from original yang syntax.
 */
class TailfCompactYangPlugin extends CompactPluginBase {

        /**
         * Process artificial "tailf:info-description" attribute
         * @param node
         * @return
         */
        private boolean processInfoDescription(BuilderNode node) {
                boolean retVal = false
                def descrAdded = false
                def attrInfoDescrInfo = getAtrributeInfo(node, "tailf:info-description")

                if (attrInfoDescrInfo) {
                        Map newAttributes = [:]
                        node.attributes.each { k, v ->
                                def val = splitPnlNameNlNoAlias(k)
                                def isInfoDescr = val.name == attrInfoDescrInfo?.name
                                if (!isInfoDescr) {
                                        newAttributes[getPnlNameNl(val)] = v   // just copy the attribute
                                } else {
                                        def aVal = [pnl: val.pnl, name: "tailf:info", nl: false]
                                        newAttributes[getPnlNameNl(aVal)] = v // convert to 'tailf:info'
                                        def attrDescrInfo = getAtrributeInfo(node, "description")
                                        if (!attrDescrInfo) {
                                                //convert to description only if other description attribute not present
                                                aVal = [pnl: false, name: "description", nl: val.nl]
                                                newAttributes[getPnlNameNl(aVal)] = v
                                                descrAdded = true
                                        }
                                }
                        }
                        node.attributes = newAttributes
                }
                if (descrAdded) {
                        retVal = compactNodeAttr(node, "description")
                }
                return retVal
        }

        @Override
        protected PluginResult processNodeBefore(BuilderNode node, Object opaque, Map pluginOpaque) throws BuilderException {
                PluginResult retVal = PluginResult.NOT_ACCEPTED

                def processed = false

                // description  under 'leaf', 'leaf-list', 'list', 'container', 'revision', 'typedef'
                if (node.name in ['leaf', 'leaf-list', 'list', 'container', 'typedef', "refine", "length", "type", "enum"]) {
                        if (node.name != "type") { // type cannot have description
                                processed |= processInfoDescription(node)
                        }
                        processed |= compactNodeAttr(node, "tailf:info")
                }

                if (processed) {
                        retVal = PluginResult.PROCESSED_CONTINUE
                }

                return retVal
        }

        public void declareCommonAliasesAndQuotes() {
                declareMinColAliases(["tailf:info", "tailf:info-description"])
                if (getMyBuilder()) {
                        getMyBuilder().addQuoteKeywords("tailf:info")
                }
        }
}
