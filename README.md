#Yangbuilder

The `yangbuilder` is Groovy builder for the Yang Modeling Language.

The Groovy `yang` model syntax, written in the `yangbuilder`, can be very similar to the original `yang` syntax.

On the other hand, it can use power of a programming language (Groovy) - loops, conditions, functions with parameters, closures.
With `groovy` closures, one can optionally extend the functionality in a way as if new 
features/keywords are added to the `yang` language (e.g. define `leaf_uint23` which represents `leaf` with `uint32` type).
The Groovy support in development environments (`Eclipse`, `Intellij Idea`) makes data model editing faster (syntax
highlighting, formatting, navigation). 
 
With use of `groovy` `Grapes`, the yang can be generated directly from the text script without need to download and install any
`yangbuilder` dependency or create project (no need for `ant`, `maven`, `gradle`, IDE support, etc.).

The `yangbuilder` is very useful for creation of the data model specific variants, which share common data model base, but are
different in the final deployment (e.g. customer variants, device variants, different data model versions). 
With this use case, only one source code for the data model is kept and desired variant is generated. 
This increases source code reuse, reduces possible copy/paste errors, avoids merging between the variants.
  
In addition, there is a `CompactYangPlugin` plugin, which bring even more features. 
One of them is so called `compact yang` syntax:
  
Example (from the Yang tutorial):
 
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

can be written as:

    container "timeout", {
        leaf "access-timeout", type: uint32, description: "Maximum time without server response"
        leaf "retry-timer", type: uint32, description: "Period to retry operation"
    }


Implementation source code is in the `main/src` directory.
Test source code is in the `main/tests` directory. (Test source code is good examples of the `yangbuilder` usage.)
Documentation files and source  is in the `documentation` directory. (Documentation is still in progress.)

Use [gradle][gradle_id] to build, test and package project.
Use [groovy][groovy_id] version 2.0.0 and later.

See `changelog.md`.

Quick examples:

See `templates\scripts\`


Michal Novak (<it.novakmi@gmail.com>)

[gradle_id]: http://www.gradle.org/  "Gradle"
[groovy_id]: http://groovy.codehaus.org/  "Groovy"
