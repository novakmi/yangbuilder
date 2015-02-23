//This is free software licensed under MIT License, see LICENSE file
//(https://bitbucket.org/novakmi/yangbuilder/src/LICENSE)

package org.bitbucket.novakmi.yangbuilder

import org.bitbucket.novakmi.nodebuilder.PluginResult
import org.bitbucket.novakmi.nodebuilder.NodeBuilderPlugin
import org.bitbucket.novakmi.nodebuilder.BuilderException
import org.bitbucket.novakmi.nodebuilder.BuilderNode
import org.bitbucket.novakmi.nodebuilder.PluginTreeNodeBuilder

/**
 * The plugin which allows for more compact building of yang.
 * The syntax is more compact, but more different from original yang syntax.
 */
class CompactYangPlugin extends CompactPluginBase {

        @Override
        protected PluginResult processNodeBefore(BuilderNode node, Object opaque, Map pluginOpaque) throws BuilderException {
                PluginResult retVal = PluginResult.NOT_ACCEPTED

                def processed = false

                Map attributesValMap = [:]
                Map attributesMapMap = [:]

                for (e in node.attributes) {
                        if (e.value instanceof Map) {
                                attributesMapMap.put(e.key, e.value)
                        } else {
                                attributesValMap.put(e.key, e.value)
                        }
                }
                node.attributes = attributesValMap

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

                // default  under 'leaf' - #1
                if (node.name in ["leaf", "typedef", "deviate", "refine"]) {
                        processed |= compactNodeAttr(node, "default")
                }

                // type  under 'leaf', 'leaf-list'. 'typedef'
                if (node.name in ['leaf', 'leaf-list', 'typedef']) {
                        processed |= compactNodeAttr(node, 'type')
                }


                // min-elements, max-elements  - #1
                if (node.name in ["list", "leaf-list", "deviate", "refine"]) {
                        processed |= compactNodeAttr(node, "max-elements")
                        processed |= compactNodeAttr(node, "min-elements")
                }

                // key under 'list'
                if (node.name in ['list']) {
                        processed |= compactNodeAttr(node, 'key')
                }

                // config  under 'leaf' - #1
                if (node.name in ["container", "leaf", "list", "leaf-list", "choice", "deviate", "refine"]) {
                        processed |= compactBooleanAttr(node, "config")
                }


                // mandatory under 'leaf', 'choice', 'refine'
                if (node.name in ['leaf', 'choice', "refine"]) {
                        processed |= compactBooleanAttr(node, "mandatory")
                }

                //description and reference allowed under (almost) any node
                processed |= compactNodeAttr(node, 'description')
                processed |= compactNodeAttr(node, 'reference')

                // presence under 'container', 'refine
                if (node.name in ['container', "refine"]) {
                        processed |= compactNodeAttr(node, 'presence')
                }


                // enumerations in type
                if (node.name  == 'type' && node.value == 'enumeration') {
                        def enums = node.attributes['enums']
                        if (enums) {
                                if (!(enums instanceof List)) {
                                        throw new BuilderException("node: ${node.name} path: ${BuilderNode.getNodePath(node)};  'enums' attribute of 'type enumeration' has to be List")
                                }
                                enums.each { e ->
                                        if (!(e instanceof String)) {
                                                throw new BuilderException("enum value ${e} is not String type!")
                                        }
                                        node.children += new BuilderNode(name: 'enum', value: e)
                                }
                        }
                }

                // support for elems
                def elems = node.attributes["elems"]
                if (elems) {
                        if (!(elems instanceof List)) {
                                throw new BuilderException("node: ${node.name}(${node.value}) path: ${BuilderNode.getNodePath(node)};  'elems' attribute has to be List!")
                        }
                        elems.each { p ->
                                if (!(p instanceof String) && !(p instanceof List)) {
                                        //TODO error
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
                                        processed != processMapAttribute(node, p[0], p[1])
                                } else {
                                        def attrInfo = [:]
                                        if (!(p instanceof String) && !(p instanceof GString)) {
                                                throw new BuilderException("'elems' value ${p} of node ${node.name}(${node.value}) path: ${BuilderNode.getNodePath(node)}; is not String type!")
                                        }
                                        attrInfo = splitPnlNameNlNoAlias(p)
                                        processed |= addNodeFromAttrInfo(node, attrInfo, true, true)
                                }
                        }
                }

                for (e in attributesMapMap) {
                        processed != processMapAttribute(node, e.key, e.value)
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
