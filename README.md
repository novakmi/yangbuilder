#Yangbuilder
Ynagbuilder is groovy builder for Yang Modeling Lanuguage.

Implementation source code is in  `main/src` directory.
Test source code is in  `main/tests` directory.
Documentation files and source code is in  `documentation` directory.

Use [gradle][gradle_id] to build, test and package project.

See `changelog.txt`.

TODO:

* support for `enumeration` in CompactYangPlugin (`type(enumeration, enums: [....]`)
* support for `children` in CompactYangPlugin (container, list, leaf, eg. `leaf(my_leaf, childern ['my:annotation'(), 'other:annotation'()])`)

DONE:

* support for `presence` in CompactYangPlugin (`container`)
* support for `type` and `description` in CompactYangPlugin (`typedef(type 'string', description:'my type')`)
* support for `mandatory` in CompactYangPlugin (`choice`, `leaf`)

Michal Novak (<bubbles.way@gmail.com>)

[gradle_id]: http://www.gradle.org/  "Gradle"
