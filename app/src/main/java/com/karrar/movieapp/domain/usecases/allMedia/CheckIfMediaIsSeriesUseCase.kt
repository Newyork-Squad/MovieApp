package com.karrar.movieapp.domain.usecases.allMedia

import com.karrar.movieapp.domain.enums.AllMediaType
import javax.inject.Inject

class CheckIfMediaIsSeriesUseCase @Inject constructor() {
    operator fun invoke(type: AllMediaType): Boolean {
        return (type == AllMediaType.POPULAR
                || type == AllMediaType.TOP_RATED
                || type == AllMediaType.RECENTLY_RELEASED
                )
    }
}