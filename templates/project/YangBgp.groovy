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

// This class is related to bgp-module.yang file
class YangBgp {

        static def name = "bgp-module" // groovy makes automatically getName(), setName()

        static def buildYang(builder) {
                builder.module(getName()) {
                        YangCommon.buildHeader(builder, 'bgp')

                        include YangBgpSubmodule.getName()
                        yngbuild('')

                        yngbuild("/* bgp neighbor */", indent: true)
                        container("bgp-neighbor") {
                                YangCommon.buildAddressPort(builder, 'bgp') // as if content of function is written here, yangbuilder reuse (not possible in yang)
                        }
                        yngbuild('')
                        container('next-hop') {
                                uses 'BgpNextHop'
                        }
                }
        }
}
