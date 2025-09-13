package com.karrar.movieapp.domain.mappers

import android.os.Build
import androidx.annotation.RequiresApi
import com.karrar.movieapp.domain.enums.Era
import com.karrar.movieapp.domain.enums.MatchingGenre
import com.karrar.movieapp.domain.enums.Mood
import com.karrar.movieapp.domain.enums.Runtime
import javax.inject.Inject

class MovieQueryMapper @Inject constructor(){

    fun mapMoods(moods: List<Mood>): String {
        return moods.mapNotNull { mood ->
            when (mood) {
                Mood.CHILL -> MOOD_CHILL_ID
                Mood.EXCITED -> MOOD_EXCITED_ID
                Mood.EMOTIONAL -> MOOD_EMOTIONAL_ID
                Mood.CURIOUS -> MOOD_CURIOUS_ID
            }
        }.joinToString(" AND ")}
    fun mapGenres(genres: List<MatchingGenre>): String =
        genres.mapNotNull { genre ->
            when (genre) {
                MatchingGenre.ACTION -> GENRE_ACTION_ID
                MatchingGenre.COMEDY -> GENRE_COMEDY_ID
                MatchingGenre.DRAMA -> GENRE_DRAMA_ID
                MatchingGenre.ROMANCE -> GENRE_ROMANCE_ID
                MatchingGenre.SCI_FI -> GENRE_SCIFI_ID
                MatchingGenre.THRILLER -> GENRE_THRILLER_ID
                MatchingGenre.ANIMATION -> GENRE_ANIMATION_ID
                MatchingGenre.MYSTERY -> GENRE_MYSTERY_ID
            }
        }.joinToString(" AND ")

    fun mapRuntime(runtime: Runtime): Pair<Int?, Int?> = when (runtime) {
        Runtime.SHORT -> RUNTIME_SHORT_MIN to RUNTIME_SHORT_MAX
        Runtime.MEDIUM -> RUNTIME_MEDIUM_MIN to RUNTIME_MEDIUM_MAX
        Runtime.LONG -> RUNTIME_LONG_MIN to null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun mapEra(era: Era): Pair<String?, String?> {
        return when (era) {
            Era.RECENT -> {
                val from = (java.time.Year.now().value - RECENT_YEARS_RANGE).toString() + "-01-01"
                val to = java.time.Year.now().toString() + "-12-31"
                from to to
            }

            Era.CLASSIC -> null to CLASSIC_END_DATE
            Era.BOTH -> null to null
        }
    }

    companion object {

        const val RECENT_YEARS_RANGE = 5
        const val CLASSIC_END_DATE = "2000-01-01"


        const val RUNTIME_SHORT_MIN = 0
        const val RUNTIME_SHORT_MAX = 90
        const val RUNTIME_MEDIUM_MIN = 90
        const val RUNTIME_MEDIUM_MAX = 120
        const val RUNTIME_LONG_MIN = 120


        const val MOOD_CHILL_ID = "267871"
        const val MOOD_EXCITED_ID = "325811"
        const val MOOD_EMOTIONAL_ID = "351091"
        const val MOOD_CURIOUS_ID = "194998"


        const val GENRE_ACTION_ID = "28"
        const val GENRE_COMEDY_ID = "35"
        const val GENRE_DRAMA_ID = "18"
        const val GENRE_ROMANCE_ID = "10749"
        const val GENRE_SCIFI_ID = "878"
        const val GENRE_THRILLER_ID = "53"
        const val GENRE_ANIMATION_ID = "16"
        const val GENRE_MYSTERY_ID = "9648"
    }
}
