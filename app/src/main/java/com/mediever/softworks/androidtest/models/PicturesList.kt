package com.mediever.softworks.androidtest.models

data class PicturesList(var totalItems: Int,
                        var itemsPerPage: Int,
                        var countOfPages: Int,
                        var data: List<Picture>)