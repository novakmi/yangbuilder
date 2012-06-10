#Yangbuilder change log

* 0.3.1 2012-06-10
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



