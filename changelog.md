#Yangbuilder change log

* 0.5.0 2013-0.-01
    * updated dependencies to groovy 2.0.6, nodebuilder-0.7.0 (testng 6.8, logback 1.0.9, slf4j 1.7.2)
    * tests - updated reading of sout, serr for pyang process
    * CompactYangPlugin now supports  'type' and 'description' attributes for 'typedef'
    * CompactYangPlugin now supports  'presence' attribute for 'container'
    * CompactYangPlugin now supports  'mandatory' (true or false) attribute for 'leaf', 'choice'
    * CompactYangPlugin now supports  'description' attribute for 'choice'
    * tests - used Slf4j groovy annotation used for logging
    * yangbuilder dependency in documentation and examples updated to 0.5.0


* 0.4.0 2012-07-14
    * dependencies updated to groovy 2.0.0, nodebuilder 0.6.0
    * CompactYangPlugin now supports description attribute for revision
    * 'cmt' keyword is now inline and indented by default

* 0.3.3 2012-06-12
    * fixed indent in multiline quoted keywords

* 0.3.2, 0.3.1 2012-06-10
    * fixed error in addQuoteKeyword (infinite loop)

* 0.3.0 2012-06-10
     * `README.md`, `changelog.md` and `LICENSE` added
     * Updated `CompactYangPlugin`
        * `prefix` attribute in `belongs-to`
        * `namespace` attribute in `module`
        * `pnl_` prefix and `_nl` suffix for attributes
    * updated documentation
    * fixed `getPrefixName` for submodules (`returns `prefix` value under `belongs-to`)
    * support for `quoted` keywords (`addQuoteKeywords`)
    * support for `cmt` keyword and `cmt` attribute (for comments)
    * `build.gradle` updated for `gradle 1.0 rc3`



