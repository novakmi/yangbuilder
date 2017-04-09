//This is free software licensed under MIT License, see LICENSE file
//(https://bitbucket.org/novakmi/yangbuilder/src/LICENSE)

package org.bitbucket.novakmi.yangbuilder

import org.bitbucket.novakmi.nodebuilder.BuilderException
import org.bitbucket.novakmi.nodebuilder.BuilderNode
import org.bitbucket.novakmi.nodebuilder.TextPluginTreeNodeBuilder

class YangBuilder extends TextPluginTreeNodeBuilder {

        public static final String _YGN = "_ygn"
        public static final String YANG_ROOT = 'yangroot'
        public static final String YANG_CMD = 'yngcmd'
        public static final reservedCommands = ["cmt", "geninfo", "yngbuild", YANG_ROOT ,YANG_CMD]
        public static final reservedAttributes = ["autoNl", "noAutoQuotes", "indent", "quotes", "cmt", "inline",
                                                  "splitOnPlus"]

        // list of keywords with special quote handling
        private quoteKeywords = []

        /**
         * Create new YangBuilder
         * @param indent number of spaces for indentation (default is 2)
         * @param plugins list of plugins to be added (no plugins by default)
         */
        public YangBuilder(indent = 2, plugins = null, configMap = null) {
                super(indent, plugins, configMap)
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
                        'error-message',
                        'when'
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

        private static def encloseStrings = [" ", "\t", ";", "{", "}", "//", "/*", "*/"]
        private static def qouteStrings = ['"', "'"]
        private static def getMyQuotes(txt) {
                def retVal = TextPluginTreeNodeBuilder.getQuotes(txt, qouteStrings, encloseStrings)
                if (retVal == '"') {
                        if (txt.contains('"')) { // already contains double quotes, try single quotes
                                if (txt.contains("'")) { // contains also single quotes, do nothing
                                        retVal = ''
                                } else {
                                        retVal = "'" // use single quotes instead
                                }
                        }
                }
                return retVal
        }

        private def static dropFirstElemIfEq(elems, equal='') {
                if (elems.size()) {
                        if (elems[0] == equal) {
                                elems = elems.drop(1)
                        }
                }
                return elems
        }

        private splitLineOnPlus(line) {
            def linesTmp = line.split('\\+')
            def lines = []
            int cur = 0

            for (int i = 0; i < linesTmp.size(); i++) {
                if (i != 0 && lines[cur - 1].endsWith('\\')) {
                    lines[cur - 1] = lines[cur - 1][0..-2] + linesTmp[i]
                    continue
                }
                lines += linesTmp[i]
                cur++
            }
            return lines
        }

        @Override
        protected void processNode(BuilderNode node, Object opaque) throws BuilderException {
                def quoteString = node.attributes.quotes ?: ''
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
                                        def lines = node?.value.readLines()
                                        def numLines = lines.size()
                                        lines = dropFirstElemIfEq(lines)
                                        if (!isInline) {
                                                indentIfNeeded(node, opaque)
                                                opaque.print('/*')
                                                if (numLines > 1) {
                                                        opaque.println('')
                                                }
                                        }
                                        // process comment line by line
                                        lines.each {l ->
                                                if ((numLines > 1) || isInline) {
                                                        indentIfNeeded(node, opaque)
                                                } else {
                                                        opaque.print(' ')
                                                }
                                                if (isInline) {
                                                        opaque.print('// ')
                                                } else {
                                                        if (isIndent(node)) {
                                                               l = l.trim()
                                                        }
                                                }
                                                opaque.print(l)
                                                if (numLines > 1 || isInline) {
                                                        opaque.println('')
                                                }
                                        }
                                        if (!isInline) {
                                                if (numLines > 1) {
                                                        indentIfNeeded(node, opaque)
                                                }  else {
                                                        opaque.print(' ')
                                                }
                                                opaque.println('*/')
                                        }
                                }
                                break
                        case "geninfo":
                                indentIfNeeded(node, opaque)
                                opaque.println("/*")
                                indentIfNeeded(node, opaque)
                                opaque.println("***** DO NOT EDIT THIS FILE! *****")
                                indentIfNeeded(node, opaque)
                                opaque.println("This 'yang' file was generated with Groovy 'yangbuilder'")
                                indentIfNeeded(node, opaque)
                                opaque.println("(http://bitbucket.org/novakmi/yangbuilder)")
                                def file = node.attributes?.file
                                def time = node.attributes?.time
                                if (time && !(time instanceof GString)) {
                                        time = new Date().toString()
                                }
                                def extraLine = null
                                if (file) {
                                        indentIfNeeded(node, opaque)
                                        opaque.println("Original file is ${file}")
                                }
                                if (time) {
                                        indentIfNeeded(node, opaque)
                                        opaque.println("Generated on ${time}")
                                }
                                if (node?.attributes?.cmt) {
                                        def lines = node.attributes.cmt.readLines()
                                        lines.each {l ->
                                                def out = l.trim()
                                                if (out != "") {
                                                        indentIfNeeded(node, opaque)
                                                }
                                                opaque.println(out)
                                        }
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
                                opaque.print(node.name)
                                //qoute handling attributes
                                //*************************
                                //quotes - force quotes
                                //noAutoQuotes - no quotes handling
                                if (quoteString == '' && node.name in quoteKeywords /*&& (node.value instanceof String || node.value instanceof GString)*/) {
                                        //if quote Keyword and quotes not disabled
                                        if (!node.attributes.noAutoQuotes) {
                                                quoteString = getMyQuotes(node.value)
                                        }
                                }
                                if (node.value instanceof String || node.value instanceof GString) {
                                        def lines = node?.value.readLines()
                                        def autoNl = false
                                        if (lines.size() == 1) {
                                                if (isKeyInConfigOrNodeAttr(node, "autoNl") &&
                                                    node.name in ["description", "reference", // newline after these nodes
                                                                  "organization", "contact"]) {
                                                        opaque.println("")
                                                        opaque.incrementIndent()
                                                        indentIfNeeded(node, opaque)
                                                        autoNl = true
                                                } else {
                                                        opaque.print(" ")
                                                }
                                                def splitOnPlus = isKeyInConfigOrNodeAttr(node, "splitOnPlus")
                                                if (splitOnPlus) {
                                                        lines = splitLineOnPlus(lines[0])
                                                        if(quoteString == '') {
                                                            quoteString = getMyQuotes(node.value)
                                                        }
                                                    if(quoteString == '') {
                                                        quoteString = this.qouteStrings[0]
                                                    }
                                                }
                                                splitOnPlus = lines.size() > 1
                                                for (int i = 0; i < lines.size(); i++) {
                                                    opaque.print("${quoteString}${lines[i]}${quoteString}")
                                                    if (i != lines.size() - 1 && splitOnPlus) {
                                                        opaque.println("")
                                                        indentIfNeeded(node, opaque)
                                                        def spaces = node?.name?.size() -1
                                                        opaque.print("${" "?.multiply(spaces)}+ ")
                                                    }
                                                }                                             
                                                if (autoNl) {
                                                        opaque.decrementIndent()
                                                }
                                        } else {
                                                lines = dropFirstElemIfEq(lines)
                                                lines = TextPluginTreeNodeBuilder.trimAndQuoteLines(lines, quoteString)
                                                lines.each { l->
                                                        opaque.println("")
                                                        if (!l.trim().equals("")) {
                                                                indentIfNeeded(node, opaque)
                                                                if (isIndent(node)) {
                                                                        opaque.print("${opaque.indent}")
                                                                        //TODO indent private
                                                                }
                                                                opaque.print(l)
                                                        }
                                                }
                                        }
                                }  else {
                                        if (node.value != null) {
                                                opaque.print(" ${quoteString}${node.value}${quoteString}")
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
                        } else {
                            opaque.incrementIndent()
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
