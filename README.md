#Yangbuilder
`yangbuilder` is Groovy builder for the Yang Modeling Language.

The Groovy `yang` model written in the `yangbuilder` can be very similar to the original `yang` syntax.
On the other hand, it can use power of a programming language (Groovy) - loops, conditions, functions with parameters, closures.
The Groovy support in development environments (`Eclipse`, `Intellij Idea`) makes data model editing faster (syntax
highlighting, formatting, navigation).

In addition, there is a plugin for compact yang syntax, which can reduce the data model size. 
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
Test source code is in the `main/tests` directory.
Documentation files and source code is in the `documentation` directory.

Use [gradle][gradle_id] to build, test and package project.
Use [groovy][groovy_id] version 2.0.0 and later.

See `changelog.md`.

Quick examples:

See `templates\scripts\`


Michal Novak (<it.novakmi@gmail.com>)

[gradle_id]: http://www.gradle.org/  "Gradle"
[groovy_id]: http://groovy.codehaus.org/  "Groovy"
