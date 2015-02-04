//This is free software licensed under MIT License, see LICENSE file
//(https://bitbucket.org/novakmi/yangbuilder/src/LICENSE)

package org.bitbucket.novakmi.yangbuilder

import com.sun.org.apache.xpath.internal.functions.FuncFalse
import org.bitbucket.novakmi.nodebuilder.BuilderException
import org.bitbucket.novakmi.nodebuilder.BuilderNode
import org.bitbucket.novakmi.nodebuilder.NodeBuilderPlugin
import org.bitbucket.novakmi.nodebuilder.PluginResult

/**
 * The plugin which allows for more compact building of yang for know Tail-f yang extensions.
 * The syntax is more compact, but more different from original yang syntax.
 */
class TailfCompactYangPlugin extends CompactPluginBase {


        private boolean processInfoDescription(BuilderNode node)  {
                boolean retVal = false
                def descrAdded = false
                def attrInfoInfo = getAtrributeInfo(node, "tailf:info")
                def attrInfoDescrInfo  = getAtrributeInfo(node, "tailf:info-description")

                if (attrInfoInfo || attrInfoDescrInfo) {  // at least one present
                        Map newAttributes = [:]
                        node.attributes.each { k,v ->
                                def val = splitPnlNameNlNoAlias(k)
                                def isInfoDescr = val.name == attrInfoDescrInfo?.name
                                def isInfo = val.name == attrInfoInfo?.name
                                if (!isInfoDescr && !isInfo) {
                                        newAttributes[getPnlNameNl(val)] = v   // just copy the attribute
                                } else {
                                        if (isInfo || isInfoDescr && !attrInfoInfo) {
                                                def aVal = [pnl:val.pnl, name: "tailf:info", nl: false]
                                                // and only if info or does not have other info
                                                newAttributes[getPnlNameNl(aVal)] = v // convert to 'tailf:info'
                                        }
                                        if (isInfoDescr) {
                                                def attrDescrInfo = getAtrributeInfo(node, "description")
                                                if (!attrDescrInfo) { //convert to description only if other description attribute not present
                                                        def aVal = [pnl:false, name: "description", nl: val.nl]
                                                        newAttributes[getPnlNameNl(aVal)] = v
                                                        descrAdded = true
                                                }

                                        }
                                }
                        }
                        node.attributes = newAttributes
                }
                if (descrAdded) {
                        processed |= compactNodeAttr(node, "description")
                }
                return retVal
        }

        @Override
        protected PluginResult processNodeBefore(BuilderNode node, Object opaque, Map pluginOpaque) throws BuilderException {
                PluginResult retVal = PluginResult.NOT_ACCEPTED

                def processed = false

                // description  under 'leaf', 'leaf-list', 'list', 'container', 'revision', 'typedef'
                // presence under 'container', 'refine
                if (node.name in ['leaf', 'leaf-list', 'list', 'container', 'typedef', "refine", "length", "type", "enum"]) {

                        processed |= processInfoDescription(node)
                        processed |= compactNodeAttr(node, "tailf:info")
                }

                if (processed) {
                        retVal = PluginResult.PROCESSED_CONTINUE
                }

                return retVal
        }

        /**
         * Declare common aliases for the Yang language conflicting with groovy syntax and keywords.
         * @param builder being used with the plugin
         */
        public void declareCommonAliasesAndQuotes() {
                declareMinColAliases(["tailf:info", "tailf:info-description"])
                if (getMyBuilder()) {
                        getMyBuilder().addQuoteKeywords("tailf:info")
                }
        }
}
