package com.vianh.blogtruyen.features.list.filter

import com.vianh.blogtruyen.data.model.Category

data class FilterCategoryItem(
    val category: Category,
    var isSelected: Boolean
)