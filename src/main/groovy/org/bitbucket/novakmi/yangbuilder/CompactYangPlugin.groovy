//This is free software licensed under MIT License, see LICENSE file
//(https://bitbucket.org/novakmi/yangbuilder/src/LICENSE)

package org.bitbucket.novakmi.yangbuilder

import org.bitbucket.novakmi.nodebuilder.BuilderException
import org.bitbucket.novakmi.nodebuilder.BuilderNode
import org.bitbucket.novakmi.nodebuilder.PluginResult

/**
 * The plugin which allows for more compact building of yang.
 * The syntax is more compact, but more different from original yang syntax.
 */
class CompactYangPlugin extends CompactPluginBase {

        private boolean skipAttr(final String name) {
                def retVal = (name in ["pnl", "nl"])
                retVal |=  (name.startsWith("${YangBuilder._YGN}_")) //if attribute name starts with '_ygn_', skip this attribute from processing
                return retVal
        }

        private boolean processEnums(BuilderNode node, enums) {
                def retVal = false
                if (enums) {
                        if (!(enums instanceof List)) {
                                throw new BuilderException("node: ${node.name} path: ${BuilderNode.getNodePath(node)};  'enums' attribute of 'type enumeration' has to be List")
                        }
                        enums.each { e ->
                                if (!(e instanceof String) && !(e instanceof GString)) {
                                        throw new BuilderException("enum value ${e} is not String type!")
                                }
                                node.children += new BuilderNode(name: 'enum', value: e)
                                retVal = true
                        }
                }
                return retVal
        }

        private boolean processElems(BuilderNode node, elems) {
                def retVal = false
                if (elems) {
                        if (!(elems instanceof List)) {
                                throw new BuilderException("node: ${node.name}(${node.value}) path: ${BuilderNode.getNodePath(node)};  'elems' attribute has to be List!")
                        }
                        elems.reverseEach { p ->
                                if (!(p instanceof String) && !(p instanceof GString) && !(p instanceof List)) {
                                        throw new BuilderException("'elems' value ${p} of node ${node.name}(${node.value}) path: ${BuilderNode.getNodePath(node)}; must be String or Map!")
                                }
                                if (p instanceof List) {
                                        if (p.size() != 2) {
                                                throw new BuilderException("'elems' value ${p} of node ${node.name}(${node.value}) path: ${BuilderNode.getNodePath(node)}; must have 2 elements, but has ${p.size()}!")
                                        }
                                        if (!(p[0] instanceof String) && !(p[0] instanceof GString)) {
                                                throw new BuilderException("'elems' value ${p} of node ${node.name}(${node.value}) path: ${BuilderNode.getNodePath(node)}; First element must be String representing element name!")
                                        }
                                        if (!(p[1] instanceof Map)) {
                                                throw new BuilderException("'elems' value ${p} of node ${node.name}(${node.value}) path: ${BuilderNode.getNodePath(node)}; Second element must be Map representing attributes!")
                                        }
                                        retVal = processMapAttribute(node, p[0], p[1])
                                } else {
                                        if (!(p instanceof String) && !(p instanceof GString)) {
                                                throw new BuilderException("'elems' value ${p} of node ${node.name}(${node.value}) path: ${BuilderNode.getNodePath(node)}; is not String type!")
                                        }
                                        def attrInfo = splitAttrPnlNameNlAndResolveAlias(p)
                                        retVal = addNodeFromAttrInfo(node, attrInfo)
                                }
                        }
                }

                return retVal
        }

        private boolean processComplexAttr(BuilderNode node, final String attrName, attrValue) {
                def retVal = false
                if (node.name == "type" && node.value == "enumeration" && attrName == "enums") {
                        retVal = processEnums(node, attrValue)
                } else {
                        if (attrValue instanceof Map) {
                                retVal = processMapAttribute(node, attrName, attrValue)
                        } else {
                                if (attrName == "elems") {
                                        retVal = processElems(node, attrValue)
                                }
                        }
                }
                return retVal
        }

        @Override
        protected PluginResult processNodeBefore(BuilderNode node, Object opaque, Map pluginOpaque) throws BuilderException {
                PluginResult retVal = PluginResult.NOT_ACCEPTED

                def processed = false
                if (node.attributes[YangBuilder._YGN]) { // if '_ygn' attribute is present, skip attribute processing
                        retVal = PluginResult.PROCESSED_CONTINUE
                } else {

                        if (node.attributes['pnl']) {
                                opaque.println('') // new line before processed
                                processed |= true
                        }

                        node.attributes.reverseEach { e ->
                                if (!skipAttr(e.key)) {
                                        def complex = processComplexAttr(node, e.key, e.value)
                                        processed |= complex
                                        if (!complex) {
                                                def attrInfo = splitAttrPnlNameNlAndResolveAlias(e.key)
                                                attrInfo.value = e.value
                                                processed |= addNodeFromAttrInfo(node, attrInfo)
                                        }
                                }
                        }

                        if (processed) {
                                retVal = PluginResult.PROCESSED_CONTINUE
                        }
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

        /**
         * Declare common aliases for the Yang language conflicting with groovy syntax and keywords.
         */
        public void declareCommonAliasesAndQuotes() {
                if (getMyBuilder()) {
                        this.declareMinColAliases([
                                "leaf-list", "if-feature", "min-elements", "max-elements",
                                "error-app-tag", "error-message", "fraction-digits",
                                "ordered-by", "require-instance", "revision-date", "yang-version", "yin-element"
                        ])
                        getMyBuilder().declareAlias("default_", "default")
                        getMyBuilder().declareAlias("import_", "import")
                        getMyBuilder().declareAlias("enum_", "enum")
                }
        }
}
