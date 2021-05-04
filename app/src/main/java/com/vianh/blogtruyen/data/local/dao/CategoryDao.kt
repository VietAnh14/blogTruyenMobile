package com.vianh.blogtruyen.data.local.dao

import androidx.room.Dao
import com.vianh.blogtruyen.data.local.dao.BaseDao
import com.vianh.blogtruyen.data.local.entity.CategoryEntity

@Dao
abstract class CategoryDao: BaseDao<CategoryEntity>()