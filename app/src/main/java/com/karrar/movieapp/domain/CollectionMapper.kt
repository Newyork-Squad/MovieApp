package com.karrar.movieapp.domain

import com.karrar.movieapp.data.remote.response.CollectionDto
import com.karrar.movieapp.domain.mappers.Mapper
import com.karrar.movieapp.domain.models.Collection
import javax.inject.Inject

class CollectionMapper @Inject constructor() : Mapper<CollectionDto, Collection> {
    override fun map(input: CollectionDto): Collection {
        return Collection(
            name=input.name,
            description = input.description,
            itemCount = input.itemsCount,
            type = input.type.toString())

    }
}