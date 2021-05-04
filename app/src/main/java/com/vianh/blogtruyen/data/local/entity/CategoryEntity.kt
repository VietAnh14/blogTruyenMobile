package com.vianh.blogtruyen.data.local.entity

import androidx.room.Entity
import com.vianh.blogtruyen.data.model.Category

@Entity(
    tableName = "categories",
    primaryKeys = ["categoryId"]
)
data class CategoryEntity(
    val categoryId: Int,
    val displayName: String,
    val link: String
) {
    fun toCategory(): Category {
        return Category(
            name = displayName,
            query = link
        )
    }

    companion object {
        fun fromCategory(category: Category): CategoryEntity {
            return CategoryEntity(
                displayName = category.name,
                link = category.query,
                categoryId = category.query.hashCode()
            )
        }
    }
}