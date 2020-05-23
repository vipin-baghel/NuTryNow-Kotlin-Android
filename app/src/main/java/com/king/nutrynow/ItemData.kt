package com.king.nutrynow

class ItemData {
    var name: String = ""
    var id: String = ""
    var brand: String = ""
    var serving: Float = 0.toFloat()
        internal set

    constructor() {}//Empty constructor needed

    constructor(name: String, id: String, brand: String, serving: Float) {
        this.name = name
        this.id = id
        this.brand = brand
        this.serving = serving
    }
}
