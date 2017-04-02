package com.lineageinc.br0zip

class WinZipFile(title: String, isDirectory: Boolean) {

    var title: String = ""
    var isDirectory: Boolean = false

    init {
        this.title = title
        this.isDirectory = isDirectory
    }
}