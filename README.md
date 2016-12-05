#Yangbuilder

The `yangbuilder` is Groovy builder for the Yang Modeling Language (https://en.wikipedia.org/wiki/YANG)

(c) Michal Novak (<it.novakmi@gmail.com>)
See LICENSE file

## Benefits and usage scenarios

* output is the `yang` file (no compatibility issues)
* syntax similar to the `yang` syntax (`compact yang` extenmded syntax with `CompactYangPlugin`)
* better reuse than in `yang` (power of programming language)
    * parametrized groupings (e.g. `grouping ipv4 {`  and `grouping ipv6 {` can be written as `grouping ${ipver} {`)
    * reusable functions/closures
    * build directly from content of groovy closures with `delegate << closure` (requires `nodebuilder-1.0.0` and newer) 
    * use variables for common values
    * optionally extend syntax (e.g. add `leaf_string`)
    * `groovy` reuse (closures, functions, etc.) can be alternative option to the `yang` reuse (`grouping`, `augment`, `refine`);
      e.g in cases when `yang` reuse cannot be used (for instance parsing `yang` with `pyang` plugin that does not implement full support 
      for  `yang` reuse -  handling of `grouping`, `augment`, etc.) 
* generate `yang` conditionally 
    * generate different release or customer specific `yang` versions according to the build options or environment variables
    * use conditions where it is not possible to use `if-feature` (`import`, `include`, ...)
    * use negative conditions (not possible with `if-feature`)
* split common parts into several functions/closures/files 
* extend with plugins
* use power of favorite IDE supporting `groovy` 
    * auto completion
    * syntax highlighting
    * formatting
    * syntax checking       

## Description

The Groovy `yang` model syntax, written in the `yangbuilder`,  is similar to the original `yang` syntax.

On the other hand, we can use power of a programming language (Groovy) - loops, conditions, functions with parameters, closures.
With `groovy` closures, one can optionally extend the functionality in a way as if new 
features/keywords are added to the `yang` language (e.g. define `leaf_string` which represents `leaf` with `string` type).
The Groovy support in development environments (`Eclipse`, `Intellij Idea`) makes data model editing faster (syntax
highlighting, formatting, navigation). 
 
With use of `groovy` `Grapes`, the yang can be generated directly from the text script without need to download and install any
`yangbuilder` dependency or create project (no need for `ant`, `maven`, `gradle`, IDE support, etc.).

The `yangbuilder` is useful for creation of the data model specific variants, which share common data model base, but are
different in the final deployment (e.g. customer variants, device variants, different data model versions). 
With this use case, only one source code for the data model is kept and desired variant is generated during build time. 
This increases source code reuse, reduces possible of copy/paste errors, avoids merging between the variants.
  
In addition, there is a `CompactYangPlugin` plugin, which bring even more features. 
One of them is so called `compact yang` syntax, which creates simple sub-elements from attributes (can be freely mixed with `yang` like syntax):
  
Example (from the Yang tutorial):
 
```groovy 
container timeout {                                             
   leaf access-timeout {                                       
       description "Maximum time without server response";
       type uint32;
   }
   leaf retry-timer {
       description "Period to retry operation";                
       type uint32;
   }
}
```

can be written as:

```groovy
container "timeout", {
    leaf "access-timeout", type: uint32, description: "Maximum time without server response"
    leaf "retry-timer", type: uint32, description: "Period to retry operation"
}
```

Implementation source code is in the `main/src` directory.
Test source code is in the `main/tests` directory. (Test source code is good examples of the `yangbuilder` usage.)
Documentation files and source  is in the `documentation` directory. (Documentation is still in progress.)

Use [gradle][gradle_id] to build, test and package project.
Use [groovy][groovy_id] version 2.1.9 and later (`groovy` < 2.1.9 can be used with special builds of `nodebuilder`, e.g. 
`nodebuilder-1.0.0.1`)

See `changelog.md`.

Quick examples:

See `templates\scripts\`

## Build environment

`yangbuilder` and `nodebuilder` are accessible through JCenter maven repository

### Usage with `groovy` Grapes in script

```groovy
//@GrabResolver(name = 'jcenterrepo', root = 'https://jcenter.bintray.com', m2Compatible = true) //needed only with older ver. of groovy
@Grab(group = 'org.bitbucket.novakmi', module = 'nodebuilder', version = '1.0.0')
@Grab(group = 'org.bitbucket.novakmi', module = 'yangbuilder', version = '1.2.0')
```
First run of the `groovy` scripts downloads dependencies into `~/.groovy/grapes` directory (Internet connection required),
next run of the script uses already downloaded dependencies (Internet connection not required).

`~/.groovy/grapes` can be moved to other development machine (Internet connection not required even for first run) of the script.

See  http://docs.groovy-lang.org/latest/html/documentation/grape.html


### Usage with `gradle` build file

```groovy
dependencies {
        compile localGroovy()
        compile group: 'org.bitbucket.novakmi', name: 'nodebuilder', version: '1.0.0'
        compile group: 'org.bitbucket.novakmi', name: 'yangbuilder', version: '1.2.0'
}
```

### Usage with `groovy` and command line (without dependency on external repository)

* download desired (latest) version of the `nodebuilder` and `yangbuilder` jar files from  http://jcenter.bintray.com/org/bitbucket/novakmi/
* run with `groovy` command with classpath pointing to the downloaded `jar` files (e.g. `groovy -cp ./nodebuilder.jar:./yangbuilder.jar yang_script.groovy`) 

[gradle_id]: http://www.gradle.org/  "Gradle"
[groovy_id]: http://groovy-lang.org/ "Groovy"
