#!/usr/bin/env groovy

/*
copyright (c) 2012 michal novak (bubbles.way@gmail.com)
http://bitbucket.org/bubbles.way/yangbuilder

permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "software"), to deal
in the software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the software, and to permit persons to whom the software is
furnished to do so, subject to the following conditions:

the above copyright notice and this permission notice shall be included in
all copies or substantial portions of the software.

the software is provided "as is", without warranty of any kind, express or
implied, including but not limited to the warranties of merchantability,
fitness for a particular purpose and noninfringement. in no event shall the
authors or copyright holders be liable for any claim, damages or other
liability, whether in an action of contract, tort or otherwise, arising from,
out of or in connection with the software or the use or other dealings in
the software.
*/

//if you have internet connection, use groovy grab to get dependencies (may take some time for the first time to download jars)
//run as ordinary groovy script with command 'groovy <scriptname>.groovy' (or as linux executable script './<scriptname>.groovy')
//update nodebuilder, yangbuilder version numbers as needed
@GrabResolver(name = 'bubbleswayrepo', root = 'https://github.com/bubblesway/bubbleswayrepo/raw/master/releases', m2compatible = true)
@Grab(group = 'org.bitbucket.novakmi', module = 'nodebuilder', version = '0.3.0')
@Grab(group = 'org.bitbucket.novakmi', module = 'yangbuilder', version = '0.0.1')

//due to @Grab limitation in script, we have to have def ... after Grab, in our case we can just create builder
// see http://groovy.codehaus.org/Grapes+and+grab()
def builder = new org.bitbucket.novakmi.yangbuilder.YangBuilder(4) // create new builder, indent 4 (default is 2)

if (args) {  // arguments passed can be processed this way
        println "Following arguments have been passed:"
        args.each {a ->
                println(a)
        }
}

/**
 create List of classes that are related to generated yang files
 Each class has to have static function 'getName()' returning name of yang file (without yang extension) and
 static function  'buildYang' accepting empty builder as parameter
*/
def yangs = [
    YangBgp,
    YangBgpSubmodule,
    YangOspf,
]

for (y in yangs) { // one can also use yangs.each {y ->
        builder.reset() // empty builder
        y.buildYang(builder)
        print("Processing ${y.getName()} ...")
        builder.writeToFile("${y.getName()}.yang")
        //new File("${y.getName()}.yang").write(builder.getBuiltText()) //write to file
        println("done")
}


