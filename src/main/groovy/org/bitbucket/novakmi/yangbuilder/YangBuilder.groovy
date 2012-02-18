/*
Copyright (c) 2011 Michal Novak (bubbles.way@gmail.com)

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/

package org.bitbucket.novakmi.yangbuilder

import org.bitbucket.novakmi.nodebuilder.BuilderException
import org.bitbucket.novakmi.nodebuilder.BuilderNode
import org.bitbucket.novakmi.nodebuilder.TextPluginTreeNodeBuilder

class YangBuilder extends TextPluginTreeNodeBuilder {

        final private String YANG_ROOT = 'yangroot'
        /**
         * Create new YangBuilder
         * @param indent number of spaces for indentation (default is 2)
         * @param plugins list of plugins to be added (no plugins by default)
         */
        public YangBuilder(indent = 2, plugins = null) {
                super(indent, plugins)
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
                        if (txt && txt?.size() > 1) {
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
                def quotes = ''
                switch (node.name) {
                        case YANG_ROOT:
                                if (root == node) {
                                        opaque.setIndentLevel(-1) //do not indent 'yangroot' node
                                        break
                                }
                                throw new BuilderException("Node: ${BuilderNode.getNodePath(node)} must be root node!")
                // this node directly echoes its value with indentation or without indentation (attribute indent is set to false)
                        case 'yngbuild':
                                if (node.children.size()) {
                                        throw new BuilderException("Node: ${BuilderNode.getNodePath(node)} cannot contain child nodes!")
                                }
                                if (node.attributes.indent) {
                                        opaque.printIndent()
                                }
                                opaque.println(node.value)
                                break
                // for following keywords surround value with quotes if needed, prefer double quotes over single quotes
                // see  http://www.yang-central.org/twiki/pub/Main/YangDocuments/rfc6020.html#rfc.section.6.1.3
                // If a string contains any space or tab characters, a semicolon (";"), braces ("{" or "}"),
                // or comment sequences ("//", "/*", or "*/"), then it MUST be enclosed within double or single quotes.
                // in addition 'multiline' attribute is supported for following node types
                        case 'reference':
                        case 'contact':
                        case 'description':
                        case 'presence':
                        case 'organization':
                                if (node.attributes.multiline) {
                                        opaque.printIndent()
                                        opaque.println("$node.name")
                                        quotes = getQuotes(node.value)
                                        def lines = node?.value?.split('\n')
                                        lines?.eachWithIndex {l, i ->
                                                opaque.printIndent()
                                                if (i == 0 && quotes != '') {
                                                        l = quotes + l
                                                } else {
                                                        l = ' ' + l
                                                }
                                                if (i == lines.size() - 1) {
                                                        l = l + quotes + ';'
                                                }
                                                //opaque.print(" ${l}")
                                                //if (i == lines.size() - 1) {
                                                //        opaque.print("${quotes};")
                                                //}
                                                opaque.println(" ${l}") // indent one space after description
                                        }
                                        break
                                }
                // for following keywords surround value with quotes if needed, prefer double quotes over single quotes
                // see  http://www.yang-central.org/twiki/pub/Main/YangDocuments/rfc6020.html#rfc.section.6.1.3
                // If a string contains any space or tab characters, a semicolon (";"), braces ("{" or "}"),
                // or comment sequences ("//", "/*", or "*/"), then it MUST be enclosed within double or single quotes.
                        case 'namespace':
                        case 'key':
                        case 'pattern':
                        case 'prefix':
                                // TODO add other keywords as needed
                                quotes = getQuotes(node.value)
                        default:
                                opaque.printIndent()
                                opaque.print("$node.name")
                                if (node.value) {
                                        opaque.print(" ${quotes}${node.value}${quotes}")
                                }
                                if (!node?.children?.size()) {
                                        opaque.println(";") // yang statements not having children end with semicolon
                                }
                                break
                }
        }

        @Override
        protected void processNodeBeforeChildren(BuilderNode node, Object opaque) throws BuilderException {
                if (node.name != YANG_ROOT) {
                        opaque.println(" {") // block opening bracket
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

}
