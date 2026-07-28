package com.example.journii_version2.core.model

sealed class CopyMode {
    data object Entire : CopyMode()
    data class Sections(val sections: Set<CopySection>) : CopyMode()
    data object ImportAndEdit : CopyMode()
}
