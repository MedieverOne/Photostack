package com.mediever.softworks.androidtest.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.RealmField

@RealmClass
open class Picture(
    @PrimaryKey
    open var id: Int? = 0,
    open var name: String? = "",
    open var dateCreate: String? = "",
    open var description: String? = "",
    open var new: Boolean = true,
    open var popular: Boolean = true,
    open var image: Image? = Image(),
    open var user: String? = ""
) : RealmObject()