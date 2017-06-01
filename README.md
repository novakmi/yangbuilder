Yangbuilder
===========

(c) Michal Novak (<it.novakmi@gmail.com>)

Licensed under MIT License (see LICENSE file).

The `yangbuilder` is Groovy builder for the Yang Modeling Language (https://en.wikipedia.org/wiki/YANG)[https://en.wikipedia.org/wiki/YANG)].
It can be used to generate `yang` data models using programming language (`groovy`) and still keep the
syntax similar to the `yang` syntax. This becomes useful when reusing parts of the data model that cannot be
reused with `yang` features. The `yangbuilder` simplifies maintenance  of different versions of the
`yang` data model (e.g. release or customer specific versions).

For more information and description look at Asciidoc **documentation** (`docs/yangbuilder.ad`).
See also **examples** in the `examples/scripts` directory.

**Example of the `yang` data model**

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

**Example of the groovy `yangbuilder` data model**

```groovy
container timeout, {                                             
   leaf "access-timeout", {                                       
       description "Maximum time without server response"
       type "uint32"
   }
   leaf retry-timer, {
       description "Period to retry operation"                
       type "uint32"
   }
}
```

**Example of the groovy `yangbuilder` data model written in compact syntax**

```groovy
container "timeout", {
    leaf "access-timeout", type: "uint32", description: "Maximum time without server response"
    leaf "retry-timer", type: "uint32", description: "Period to retry operation"
}
```
