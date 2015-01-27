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

        private compactBooleanAttr(BuilderNode node, String attrName, nlAllow = true) {
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

                // default  under 'leaf' - #1
                if (node.name in ["leaf", "typedef", "deviate", "refine"]) {
                        processed |= compactNodeAttr(node, "default")
                }

                // type  under 'leaf', 'leaf-list'. 'typedef'
                if (node.name in ['leaf', 'leaf-list', 'typedef']) {
                        processed |= compactNodeAttr(node, 'type')
                }


                // key under 'list'
                if (node.name in ['list']) {
                        processed |= compactNodeAttr(node, 'key')
                }

                // min-elements, max-elements  - #1
                if (node.name in ["list", "leaf-list", "deviate", "refine"]) {
                        processed |= compactNodeAttr(node, "min-elements")
                        processed |= compactNodeAttr(node, "max-elements")
                }

                // config  under 'leaf' - #1
                if (node.name in ["container", "leaf", "list", "leaf-list", "choice", "deviate", "refine"]) {
                        processed |= compactBooleanAttr(node, "config")
                }


                // mandatory under 'leaf', 'choice', 'refine'
                if (node.name in ['leaf', 'choice', "refine"]) {
                        processed |= compactBooleanAttr(node, "mandatory")
                }

                // description  under 'leaf', 'leaf-list', 'list', 'container', 'revision', 'typedef'
                // presence under 'container', 'refine
                if (node.name in ['leaf', 'leaf-list', 'list', 'container', 'choice', 'revision', 'typedef', "refine"]) {
                        processed |= compactNodeAttr(node, 'description')
                        if (node.name in ['container', "refine"]) {
                                processed |= compactNodeAttr(node, 'presence')
                        }
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

                // support for plains
                def plains = node.attributes["elems"]
                if (plains) {
                        if (!(plains instanceof List)) {
                                throw new BuilderException("node: ${node.name}(${node.value}) path: ${BuilderNode.getNodePath(node)};  'elems' attribute has to be List!")
                        }
                        plains.each { p ->
                                if (!(p instanceof String)) {
                                        throw new BuilderException("'elems' value ${p} of node ${node.name}(${node.value}) path: ${BuilderNode.getNodePath(node)}; is not String type!")
                                }
                                PluginTreeNodeBuilder myBuilder =  this.getMyBuilder()
                                if (myBuilder) {
                                        p = myBuilder.convertAlias(p)
                                }
                                node.children += new BuilderNode(name: p)
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

        /**
         * Declare aliases for the Yang language from passed keyword list.
         *
         * For each keyword an alias is created  where all minus ('-') and
         * colon (':') characters are replaced with underscore ('_').
         *
         * @param aliasList
         */
        public void declareMinColAliases(ArrayList aliasList) {
                if (getMyBuilder()) {
                        aliasList.each { a ->
                                def al = a.replace('-', '_').replace(':', '_')
                                if (al != a) {
                                        getMyBuilder().declareAlias(a.replace('-', '_').replace(':', '_'), a)
                                }
                        }
                }
        }

        /**
         * Declare common aliases for the Yang language conflicting with groovy syntax and keywords.
         *
         * Calling this function on the plugin allows to use Yang keywords in slightly different syntax and it is not
         * needed to surround them in the quotation marks. Mainly minus '-' is replaced with '_' and Groovy keywords
         * are suffixed with '_'. E.g. 'default' -> 'default_', 'leaf-list' -> 'leaf_list'
         * @param builder being used with the plugin
         */
        public void declareCommonAliases() {
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
