#!/usr/bin/env groovy

//This is free software licensed under MIT License, see LICENSE file
//(https://bitbucket.org/bubbles.way/yangbuilder/src/LICENSE)

//if you have internet connection, use groovy grab to get dependencies (may take some time for the first time to download jars)
//run as ordinary groovy script with command 'groovy <scriptname>.groovy' (or as linux executable script './<scriptname>.groovy')
//update nodebuilder, yangbuilder version numbers as needed
@GrabResolver(name = 'bubbleswayrepo', root = 'https://github.com/bubblesway/bubbleswayrepo/raw/master/releases', m2compatible = true)
@Grab(group = 'org.bitbucket.novakmi', module = 'nodebuilder', version = '0.7.0')
@Grab(group = 'org.bitbucket.novakmi', module = 'yangbuilder', version = '0.5.0')

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
        //new File("${y.getName()}.yang").write(builder.getText()) //write to file
        println("done")
}


