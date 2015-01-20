//This is free software licensed under MIT License, see LICENSE file
//(https://bitbucket.org/novakmi/yangbuilder/src/LICENSE)

package org.bitbucket.novakmi.yangbuilder

import org.bitbucket.novakmi.nodebuilder.BuilderException
import org.bitbucket.novakmi.nodebuilder.BuilderNode
import org.bitbucket.novakmi.nodebuilder.TextPluginTreeNodeBuilder

class YangBuilder extends TextPluginTreeNodeBuilder {

        final private String YANG_ROOT = 'yangroot'

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
                // this node directly echoes its value with indentation or without indentation (attribute indent is set to false)
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
                                                }
                                                opaque.println(l)
                                        }
                                        if (!isInline) {
                                                indentIfNeeded(node, opaque)
                                                opaque.println('*/')
                                        }
                                }
                                break
                        default:
                                //qoute handling attributes
                                //*************************
                                //quotes - force quotes
                                //noAutoQuotes - no quotes handling
                                //always - even if not needed
                                //multiline - handle multiline string
                                if (quotes == '' && node.name in quoteKeywords) { //if quote Keyword and quotes not disabled
                                        if (!node.attributes.noAutoQuotes) {
                                                quotes = getQuotes(node.value)
                                        }
                                        if (node.attributes.multiline) {
                                                opaque.printIndent()
                                                opaque.println("$node.name")
                                                def quotesFill = ' ' * quotes.size()
                                                def lines = node?.value?.split('\n')
                                                lines?.eachWithIndex {l, i ->
                                                        if (i == 0) {
                                                                l = quotes + l
                                                        } else {
                                                                l = quotesFill + l
                                                        }
                                                        if (i == lines.size() - 1) {
                                                                l = l + quotes + ';'
                                                        }
                                                        indentIfNeeded(node, opaque)
                                                        def level = opaque.getIndentLevel() // indent only
                                                        opaque.setIndentLevel(1)            // one indent level
                                                        indentIfNeeded(node, opaque)
                                                        opaque.setIndentLevel(level)        //restore indent
                                                        opaque.println("${l}") //print just
                                                }
                                                break
                                        }
                                }
                                opaque.printIndent()
                                opaque.print("$node.name")
                                if (node.value != null) {
                                        opaque.print(" ${quotes}${node.value}${quotes}")
                                }
                                if (!node?.children?.size()) {
                                        opaque.print(";") // yang statements not having children end with semicolon
                                        processInlindeComment(node, opaque)
                                }
                                break
                }
        }

        private void indentIfNeeded(BuilderNode node, opaque) {
                def isIndent = node.attributes.indent == null || node.attributes.indent
                if (isIndent) {
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
                        opaque.print(" {") // block opening bracket
                        processInlindeComment(node, opaque)
                }
                super.processNodeBeforeChildren(node, opaque) //incrementIndent()
        }

        @Override
        protected void processNodeAfterChildren(BuilderNode node, Object opaque) throws BuilderException {
                super.processNodeAfterChildren(node, opaque)  //decrementIndent()
                if (node.name != YANG_ROOT) {
                        opaque.printIndent()
                        opaque.println("}") // block closing bracket
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
