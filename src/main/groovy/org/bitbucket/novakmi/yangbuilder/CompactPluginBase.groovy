//This is free software licensed under MIT License, see LICENSE file
//(https://bitbucket.org/novakmi/yangbuilder/src/LICENSE)

package org.bitbucket.novakmi.yangbuilder

import org.bitbucket.novakmi.nodebuilder.BuilderException
import org.bitbucket.novakmi.nodebuilder.BuilderNode
import org.bitbucket.novakmi.nodebuilder.NodeBuilderPlugin

/**
 * This class serves as base for CompactPlugins.
 * Provides various reusable methods.
 */
abstract class CompactPluginBase extends NodeBuilderPlugin {

        /**
         * Attribute prefix indicating to place new line on previous line
         */
        protected static final String PNL_ = 'pnl_'

        /**
         * Attribute prefix indicating to place new line on next line
         */
        protected static final String _NL = '_nl'

        /**
         * attribute value map key indicating the value element
         */
        public static final String VAL = "val"

        /**
         * Return attribute name with new line prefix and suffix
         * @param attrInfo map with key 'name' representing attribute name
         * @return attribute name with prefix and suffix
         */
        protected static String getPnlNameNl(final Map attrInfo) {
                return (attrInfo.pnl ? PNL_ : "") + attrInfo.name + (attrInfo.nl ? _NL : "")
        }

        /**
         * Split attribute name to attribute info map with keys 'pnl', 'name' and 'nl'.
         * If the attribute name is alias, it is converted to the correct name.
         * @param name attribute name (also alias) with possible new line prefix ('PNL_') and suffix ('_NL')
         * @return map (attribute info) where key 'name' is attribute name (possible alias converted), key 'pnl' indicates new line prefix in
         *         original name string, key 'nl' indicates new line suffix was present in original name string
         * @see PNL_
         * @see _NL
         */
        protected Map splitPnlNameNlNoAlias(final String name) {
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

        /**
         * Get attribute info (name, new line prefix/suffix) from node attributes  for given attribute name
         * @param node the node to search attributes in
         * @param attrName attribute name (or attribute alias)
         * @return Map representing found attribute info or null (not found)
         */
        protected Map getAtrributeInfo(final BuilderNode node, final String attrName) {
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

        /**
         * Add child node to the given node from attribute info map
         * @param node node to which child node is to be added
         * @param attrInfo attribute info with node name (cannot be alias) a  new line prefix/suffix indication, value and attributes (optional)
         * @param nlAllow indicates if new line prefix/suffix is allowed (default true)
         * @param append append or prepend (default) new child node
         * @return true if node was successfully added
         */
        protected boolean addNodeFromAttrInfo(BuilderNode node, final Map attrInfo, final boolean nlAllow = true, append = false) {
                def retVal = false
                if (attrInfo) {
                        BuilderNode typeNode =
                                new BuilderNode(name: attrInfo.name, value: attrInfo.value)
                        if (attrInfo.attributes) {
                                typeNode.attributes = attrInfo.attributes
                        }
                        typeNode.setParent(node)
                        if (attrInfo.pnl && nlAllow) {
                                typeNode.attributes['pnl'] = true
                        }
                        if (attrInfo.nl && nlAllow) {
                                typeNode.attributes['nl'] = true
                        }
                        if (!append) {
                                node.children = [typeNode] + node.children // prepend to list
                        } else {
                                node.children += typeNode  // append to list
                        }
                        retVal = true
                }
                return retVal
        }

        /**
         * Find attribute in nodes attribute and if found, convert it to child element.
         *
         * In addition handle new line prefix/suffix and alias in nodes attribute.
         * If child element is added, the attribute is removed from the node.
         * @param node to search attribute and to add new elements to
         * @param attrName attribute name (no alias)
         * @param nlAllow allow handling  of new line prefix/suffix
         * @return true if element was added
         */
        protected boolean compactNodeAttr(BuilderNode node, final String attrName, final nlAllow = true) {
                def attrInfo = getAtrributeInfo(node, attrName)
                def retVal = false
                if (attrInfo) {
                        retVal = addNodeFromAttrInfo(node, attrInfo, nlAllow)
                        node.attributes.remove(getPnlNameNl(attrInfo)) //remove added attribute, so it is not caught by other plugin
                }
                return retVal
        }

        /**
         * Find attribute in nodes attribute and if found, convert it to child element. The value of attribute must be boolean (true or false)
         * @param node  o search attribute and to add new elements to
         * @param attrName  attribute name (no alias)
         * @param nlAllow   allow handling  of new line prefix/suffix
         * @return  true if element was added
         * @throw  BuilderException if attribute value is not boolean
         */
        protected boolean compactBooleanAttr(BuilderNode node, final String attrName, final nlAllow = true) {
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
         * Process attribute where  value is Map (attribute Map)
         * The node is create from the map with use of following rules:
         * node name - attribute name
         * node value - the value of attrVal Map with key "val" or null
         * node attributes - attrVal Map (without attribute key "val", if exists)
         * @param node
         * @param attrName - attribute name
         * @param attrValue - a Map  representing the attributes
         * @param nlAllow allow handling  of new line prefix/suffix
         * @return true if processed, false if not processed
         * @throw BuilderException
         */
        protected boolean processMapAttribute(BuilderNode node, attrName, attrValue, final nlAllow = true) {
                def retVal = false
                if (!attrValue instanceof Map) {
                        throw new BuilderException("node: ${node.name} path: ${BuilderNode.getNodePath(node)}; '${attrName}' attribute value has to be 'Map'!");
                }
                def attrInfo = splitPnlNameNlNoAlias(attrName)
                if (((Map) attrValue).containsKey(VAL)) {
                        attrInfo.value = attrValue[VAL]
                        ((Map) attrValue).remove(VAL)
                }
                if (((Map) attrValue).size()) {
                        attrInfo.attributes = (Map) attrValue
                }
                retVal = addNodeFromAttrInfo(node, attrInfo, nlAllow)
                return retVal
        }


        /**
         * Declare aliases for the Yang language from passed keyword list.
         *
         * For each keyword an alias is created  where all minus ('-') and
         * colon (':') characters are replaced with underscore ('_').
         *
         * @param aliasList  list of string keywords for which aliases are declared
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

        /**
         * Declare commonly used aliases and quotes keywords (e.g. conflicting with Groovy syntax) for the given plugin.
         * In CompactPluginBase class this method does nothing and can be overridden
         */
        public void declareCommonAliasesAndQuotes() {
                // empty in base class
        }
}
