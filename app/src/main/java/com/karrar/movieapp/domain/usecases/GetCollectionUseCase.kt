package com.karrar.movieapp.domain.usecases

import android.util.Log
import com.karrar.movieapp.data.repository.MovieRepository
import com.karrar.movieapp.domain.CollectionMapper
import com.karrar.movieapp.domain.models.Collection
import javax.inject.Inject

class GetCollectionUseCase @Inject constructor(
    private val movieRepository: MovieRepository,
    private val collectionMapper: CollectionMapper
    ){


    suspend operator fun invoke(movieId:Int):List<Collection>{
        val collection= movieRepository.getCollections(movieId)
        return collection?.let { it.map { collectionMapper.map(it) }
        }?: throw Throwable("Not Success")
    }
}