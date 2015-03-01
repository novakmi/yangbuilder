//This is free software licensed under MIT License, see LICENSE file
//(https://bitbucket.org/novakmi/yangbuilder/src/LICENSE)

package org.bitbucket.novakmi.yangbuilder

import org.bitbucket.novakmi.nodebuilder.BuilderException
import org.bitbucket.novakmi.nodebuilder.BuilderNode
import org.bitbucket.novakmi.nodebuilder.TextPluginTreeNodeBuilder
import org.bitbucket.novakmi.nodebuilder.TreeNodeBuilder

class YangBuilder extends TextPluginTreeNodeBuilder {

        public static final String _YGN = "_ygn"
        public static final String YANG_ROOT = 'yangroot'
        public static final String YANG_CMD = 'yngcmd'
        public static final reservedCommands = ["cmt", "geninfo", "yngbuild", YANG_ROOT ,YANG_CMD]

        // list of keywords with special quote handling
        private quoteKeywords = []

        /**
         * Create new YangBuilder
         * @param indent number of spaces for indentation (default is 2)
         * @param plugins list of plugins to be added (no plugins by default)
         */
        public YangBuilder(indent = 2, plugins = null) {
                super(indent, plugins)
                quoteKeywords += [
                        'reference',
                        'contact',
                        'description',
                        'presence',
                        'organization',
                        'namespace',
                        'key',
                        'pattern',
                        'prefix',
                        'must',
                        'error-message'
                ]
        }

        /**
         * Get needed quotes for yang string.
         *  If a string contains any space or tab characters, a semicolon (";"), braces ("{" or "}"),*/
        //*  or comment sequences ("//", "/*", or "*/"), then it MUST be enclosed within double or single quotes.
        /* @param txt
         * @return
         * @see  http://www.yang-central.org/twiki/pub/Main/YangDocuments/rfc6020.html#rfc.section.6.1.3
         */

        def getQuotes(txt) {
                def retVal = ''
                while (1) {
                        if (((txt instanceof String) || (txt instanceof GString)) && txt?.size() > 1) {
                                if ((txt[0] == '"') && txt[-1] == '"') { // enclosed with "
                                        break
                                }
                                if ((txt[0] == "'") && txt[-1] == "'") { // enclosed with '
                                        break
                                }
                                if (
                                        txt.contains(' ') || txt.contains('\t')
                                                || txt.contains(';') || txt.contains('{') || txt.contains('}')
                                                || txt.contains('//') || txt.contains('/*') || txt.contains('*/')
                                ) {
                                        retVal = '"'
                                } else {
                                        break
                                }
                                if (txt.contains('"')) { // already contains double quotes, try single quotes
                                        if (txt.contains("'")) { // contains also single quotes, do nothing
                                                retVal = ''
                                                break
                                        }
                                        retVal = "'" // use single quotes instead
                                }
                        }
                        break
                }
                return retVal
        }

        @Override
        protected void processNode(BuilderNode node, Object opaque) throws BuilderException {
                def quotes = node.attributes.quotes ?: ''
                switch (node.name) {
                        case YANG_ROOT:
                                if (root == node) {
                                        opaque.setIndentLevel(-1) //do not indent 'yangroot' node
                                        break
                                }
                                throw new BuilderException("Node: ${BuilderNode.getNodePath(node)} must be root node!")
                        // this node directly echoes its value with indentation or without indentation (if attribute indent is set to false)
                        case 'yngbuild':
                        case 'cmt':
                                if (node.children.size()) {
                                        throw new BuilderException("Node: ${BuilderNode.getNodePath(node)} cannot contain child nodes!")
                                }

                                if (node.name == 'yngbuild') {
                                        if (node.attributes.indent) {
                                                opaque.printIndent()
                                        }
                                        opaque.println(node.value)
                                } else {
                                        assert (node.name == 'cmt')
                                        def isInline = node.attributes.inline == null || node.attributes.inline
                                        if (!isInline) {
                                                indentIfNeeded(node, opaque)
                                                opaque.println('/*')
                                        }
                                        // process comment line by line
                                        def lines = node?.value?.split('\n')
                                        lines.each {l ->
                                                indentIfNeeded(node, opaque)
                                                if (isInline) {
                                                        opaque.print('// ')
                                                } else {
                                                        if (isIndent(node)) {
                                                               l = l.trim()
                                                        }
                                                }
                                                opaque.println(l)
                                        }
                                        if (!isInline) {
                                                indentIfNeeded(node, opaque)
                                                opaque.println('*/')
                                        }
                                }
                                break
                        case "geninfo":
                                indentIfNeeded(node, opaque)
                                opaque.println("/*")
                                indentIfNeeded(node, opaque)
                                opaque.println("DO NOT EDIT!")
                                indentIfNeeded(node, opaque)
                                opaque.println("This 'yang' file was generated with Groovy 'yangbuilder' (http://bitbucket.org/novakmi/yangbuilder)")
                                def file = node.attributes.file?:null
                                def time = node.attributes.time?:null
                                if (time && !(time instanceof GString)) {
                                        time = new Date().toString()
                                }
                                def extraLine = null
                                if (file) {
                                        extraLine = "Original file is ${file}"
                                }
                                if (time) {
                                        if (!extraLine) {
                                                extraLine = "Generated on ${time}"
                                        } else {
                                                extraLine += ", generated on ${time}"
                                        }
                                }
                                if (extraLine) {
                                        indentIfNeeded(node, opaque)
                                        opaque.println(extraLine)
                                }
                                indentIfNeeded(node, opaque)
                                opaque.println("*/")
                                break;
                        case "yngcmd": //reserved for YangBuilder commands (can be used by plugins)
                                break;
                        default:
                                if (node.attributes[_YGN]) { // do not process ignored node
                                        break;
                                }
                                opaque.printIndent()
                                opaque.print("$node.name")
                                //qoute handling attributes
                                //*************************
                                //quotes - force quotes
                                //noAutoQuotes - no quotes handling
                                if (quotes == '' && node.name in quoteKeywords && (node.value instanceof String || node.value instanceof GString)) {
                                        //if quote Keyword and quotes not disabled
                                        if (!node.attributes.noAutoQuotes) {
                                                quotes = getQuotes(node.value)
                                        }
                                        def quotesFill = ' ' * quotes.size()
                                        def lines = node?.value?.split('\n')
                                        if (lines.size() == 1) {
                                                opaque.print(" ${quotes}${lines[0]}${quotes}")
                                        } else {
                                                opaque.println("")
                                                lines.eachWithIndex { l, i ->
                                                        indentIfNeeded(node, opaque)
                                                        opaque.print("${opaque.indent}") //TODO indent private
                                                        if (i == 0) {
                                                                opaque.print("${quotes}")
                                                        } else {
                                                                opaque.print("${quotesFill}")
                                                        }
                                                        opaque.print("${l.trim()}")
                                                        if (i == lines.size() - 1) {
                                                                opaque.print("${quotes}")
                                                        } else {
                                                                opaque.println("")
                                                        }
                                                }
                                        }
                                }  else {
                                        if (node.value != null) {
                                                opaque.print(" ${quotes}${node.value}${quotes}")
                                        }
                                }
                                if (!node?.children?.size()) {
                                        opaque.print(";") // yang statements not having children end with semicolon
                                        processInlindeComment(node, opaque)
                                }
                                break
                }
        }


        private boolean isIndent(BuilderNode node) {
                return node.attributes.indent == null || node.attributes.indent
        }

        private void indentIfNeeded(BuilderNode node, opaque) {
                if (isIndent(node)) {
                        opaque.printIndent()
                }
        }

        private void processInlindeComment(BuilderNode node, opaque) {
                if (node.attributes.cmt) { // print inline comment
                        opaque.print(" //${node.attributes.cmt}")
                }
                opaque.println()
        }

        @Override
        protected void processNodeBeforeChildren(BuilderNode node, Object opaque) throws BuilderException {
                if (node.name != YANG_ROOT) {
                        if (!(node.attributes[_YGN])) {
                                opaque.print(" {") // block opening bracket
                                processInlindeComment(node, opaque)
                        } else {
                                opaque.decrementIndent()
                        }
                }
                super.processNodeBeforeChildren(node, opaque) //incrementIndent()
        }

        @Override
        protected void processNodeAfterChildren(BuilderNode node, Object opaque) throws BuilderException {
                super.processNodeAfterChildren(node, opaque)  //decrementIndent()
                if (node.name != YANG_ROOT) {
                        if (!(node.attributes[_YGN])) {
                                opaque.printIndent()
                                opaque.println("}") // block closing bracket
                        }
                } else {
                        opaque.setIndentLevel(0) //reset indent
                }
        }

        /**
         * Search through the yang nodes and returns name of first 'module' or 'submodule'
         * @return string representing module or submodule name or null if 'module' or 'submodule' not found
         */
        public String getYangName() {
                String retVal = null

                BuilderNode node = findNode('module')
                if (node == null) {
                        node = findNode('submodule')
                }
                if (node != null) {
                        retVal = node.value
                }

                return retVal
        }

        /**
         * Find prefix under module or submodule node and return its name.
         * @return string representing prefix name or null not found
         */
        public String getPrefixName() {
                String retVal = null

                BuilderNode node = findNode('module')
                if (node == null) {
                        node = findNode('submodule')
                        if (node) {
                                node = findNode('belongs-to')
                        }
                }
                if (node != null) {
                        for (n in node.children) {
                                if (n.name == 'prefix') {
                                        retVal = n.value
                                }
                        }
                }

                return retVal
        }

        public void addQuoteKeywords(keywordList) {
                quoteKeywords += keywordList
        }
}
