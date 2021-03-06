#Yangbuilder change log

* 1.3.0 2017-06-04
    * added RFC 8022 example (`ietf-routing`)
    * updated dependencies (`nodebuilder-1.1.1`)
    * updated documentation
    * updated copyright 
    * `quoteKeywords` and `autoNlKeywords` lists can be extended
    * `autoNl`, `doSplitOnPlus` attributes
    * updated tests to reflect new functionality
    * fix for empty line trailing spaces in generated yang
    * minor indentation fixes
    
* 1.2.0 2016-12-12
    * updated documentation (mainly reuse with closures)
    * updated examples (closures)
    * updated dependencies (`nodebuilder-1.0.0`)
    * updated copyright
    * added `belongs-to` to aliases
    * updated tests to reflect new functionality

* 1.1.0 2015-04-10    
    * updated documentation
    * updated dependencies
    * updated copyright
    * examples updated to the new jcenter repository    
    * Issue #1 - `CompactYangPlugin` - support for more attributes
    * Issue #3 - `CompactYangPlugin` - `error-message` added to default quote keyword
    * Issue #4 - `CompactYangPlugin` - support for `elems` attribute (value is list of strings which are turned directly into the yang child elements)
    * Created `CompactPluginBase` calss
    * Issue #5 - `CompactYangPlugin` - support to declare common aliases (provided plugin is registered to the builder)
    * Issue #2 - minimalistic `TailfCompactYangPlugin` - support for `tailf:info` and `tailf:info-description`
    * refactoring of `CompactYangPluging` - new support for `enums`, `elems`, attributes (almost any attribute is now accepted)
    * `YangBuilder` - nodes with attribute `_ygn` and attributes with prefix `_ygn_` are ignored from processing
    * `CompactYangPluging` - support for `autoPnlLevel` and `autoNlLevel` attributes
    * better handling of multiline 'quoted' elements (indentation, trim)
    * added examples -  RFC 7223  `ietf-interfaces` - 3 variants, `ietf-isis` - 2 variants

* 1.0.0 2013-03-08
    * updated dependencies to groovy 2.1.1, nodebuilder-0.8.0
    * changed repository to https://bitbucket.org/novakmi/nodebuilder
    * updated documentation
    * check for GString when building quotes (txt instanceof GString)
    * GroupingResolver plugin + tests + example
    * updated examples

* 0.5.0 2013-01-01
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



