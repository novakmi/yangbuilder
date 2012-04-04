#!/usr/bin/env groovy

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

//If you have Internet connection, use groovy Grab to get dependencies (may take some time for the first time to download jars)
//Run as ordinary groovy script with command 'groovy <scriptName>.groovy' (or as Linux executable script './<scriptName>.groovy')
//Update nodebuilder, yangbuilder version numbers as needed
@GrabResolver(name = 'bubbleswayrepo', root = 'https://github.com/bubblesway/bubbleswayrepo/raw/master/releases', m2compatible = true)
@Grab(group = 'org.bitbucket.novakmi', module = 'nodebuilder', version = '0.4.0')
@Grab(group = 'org.bitbucket.novakmi', module = 'yangbuilder', version = '0.2.0')

// This script template represents example of usage without any plugin
def builder = new org.bitbucket.novakmi.yangbuilder.YangBuilder() // create new builder, default indent of 2

//name of file to generate
moduleName = "acme-module"   // do not use 'def' for script global variable

def makeModule(builder) {
        builder.module(moduleName) {

                yngbuild('// based on example from Instant YANG tutorial, section modules', indent:true) //yngbuild echoes its value + request indentation
                namespace "http://acme.example.com/module"; // semicolon at the end can be preset (yang style)
                prefix "acme" // or semicolon can be missing (more groovy like style)
                yngbuild('')  //yngbuild('') means new line without indentation

                'import'("yang-types") { // Groovy/Java keywords has to be quoted; if node has sub nodes, value has to be in brackets
                        prefix "yang"
                }
                include "acme-system" // if node does not have sub nodes, brackets are optional
                yngbuild('')

                organization 'ACME Inc.'
                contact 'joe@acme.example.com'
                description('''The module for entities
implementing the ACME products.''', multiline: true) // multiple line description
                yngbuild('')

                revision('2007-06-09') {
                        description "Initial revision."
                }
                yngbuild('')
        }
}

/* if 'yangroot' is used as root node, its value is not echoed;
   use 'yangroot' e.g. if you need to add comments before 'module' or 'submodule'
   Otherwise use directly builder.module or builder.submodule
*/
builder.yangroot {
        yngbuild("/* This yang file was generated with groovy YangBuilder on ${new Date().toString()}")
        yngbuild('   see http://bitbucket.org/bubbles.way/yangbuilder */')
        // one can continue with  module(moduleName) ... or build continue  building yang in separate function
        // (another option is to define closure after builder.yangroot { ...)
        makeModule(builder)
}

builder.writeToFile("${builder.getYangName()}.yang")
//new File("${moduleName}.yang").write(builder.getText()) // another way how to write to file

