package com.mediever.softworks.androidtest.models

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.*

@RealmClass
open class PicPageDataModel(
    @Index
    @RealmField(name = "counter")
    open var counter: Int = 0,
    @PrimaryKey
    @RealmField(name = "page")
    open var page: Int = 0,
    @RealmField(name = "type")
    open var type: Boolean = false,
    @RealmField(name = "countOfPages")
    open var countOfPages: Int = 0,
    @RealmField(name = "data")
    open var data: RealmList<Picture> = RealmList()
) : RealmObject()