package com.mediever.softworks.androidtest.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

@RealmClass
open class Image(@PrimaryKey open var id: Int? = 0,
                 open var name:String? = "") : RealmObject()