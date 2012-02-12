/*
Copyright (c) 2012 Michal Novak (bubbles.way@gmail.com)
http://bitbucket.org/bubbles.way/yangbuilder

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

// This class contains reusable parts, it is not related to specific yang file
class YangCommon {

        static def buildHeader(builder, prefixName = null) {
                builder.yngbuild("/* This yang file was generated with groovy YangBuilder on ${new Date().toString()}", indent: true)
                builder.yngbuild('   see http://bitbucket.org/bubbles.way/yangbuilder */', indent: true)
                builder.yngbuild('') // new line

                if (prefixName) { // do not generate prefix for and namespace for submodules
                        builder.namespace "http://bitbucket.org/bubbles.way/yangbuilder"
                        builder.prefix prefixName
                        builder.yngbuild('')
                }

                builder.'import'('ietf-inet-types') {
                        prefix 'inet'
                }
                builder.yngbuild('')
        }

        static def buildAddressPort(builder, kind = null) { //this is example how function can be used by the builder, parameters can be used
                // in function all nodes have to be prefixed with 'builder.', except for child nodes
                builder.yngbuild("// IPv4 or IPv6 address", indent: true)
                builder.leaf("${kind ? kind + '-' : ''}address") { //output depends on parameters, not possible in yang
                        type('inet:ip-address')
                }
                builder.yngbuild("// IP port", indent: true)
                builder.leaf("${kind ? kind + '-' : ''}port") {
                        type('uint16')
                }
        }
}
