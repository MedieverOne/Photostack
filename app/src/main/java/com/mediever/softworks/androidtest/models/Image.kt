package com.mediever.softworks.androidtest.models

import io.realm.RealmObject
import io.realm.annotations.RealmClass
import io.realm.annotations.RealmField

@RealmClass
open class Image(
    @RealmField(name = "id")
    open var id: Int? = 0,
    @RealmField(name = "name")
    open var name: String? = ""
) : RealmObject()