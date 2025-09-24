package com.karrar.movieapp.ui.match.questions

import com.karrar.movieapp.domain.enums.Era
import com.karrar.movieapp.domain.enums.MatchingGenre
import com.karrar.movieapp.domain.enums.Mood


 fun mapChoicesToMoods(choices: List<Choice>): List<Mood> {
    return choices.filter { it.isSelected }.mapNotNull { choice ->
        when (choice.name.lowercase()) {
            "chill" -> Mood.CHILL
            "excited" -> Mood.EXCITED
            "emotional" -> Mood.EMOTIONAL
            "curious" -> Mood.CURIOUS
            else -> null
        }
    }
}

 fun mapChoicesToGenres(choices: List<Choice>): List<MatchingGenre> {
    return choices.filter { it.isSelected }.mapNotNull { choice ->
        when (choice.name.lowercase()) {
            "action" -> MatchingGenre.ACTION
            "comedy" -> MatchingGenre.COMEDY
            "drama" -> MatchingGenre.DRAMA
            "romance" -> MatchingGenre.ROMANCE
            "sci-fi" -> MatchingGenre.SCI_FI
            "thriller" -> MatchingGenre.THRILLER
            "animation" -> MatchingGenre.ANIMATION
            "mystery" -> MatchingGenre.MYSTERY
            else -> null
        }
    }
}

 fun mapChoicesToRuntime(choices: List<Choice>): com.karrar.movieapp.domain.enums.Runtime? {
    return choices.firstOrNull { it.isSelected }?.let { choice ->
        when (choice.name.lowercase()) {
            "short" -> com.karrar.movieapp.domain.enums.Runtime.SHORT
            "medium" -> com.karrar.movieapp.domain.enums.Runtime.MEDIUM
            "long" -> com.karrar.movieapp.domain.enums.Runtime.LONG
            else -> null
        }
    }
}

 fun mapChoicesToEra(choices: List<Choice>): Era? {
    return choices.firstOrNull { it.isSelected }?.let { choice ->
        when (choice.name.lowercase()) {
            "recent" -> Era.RECENT
            "classic" -> Era.CLASSIC
            "both" -> Era.BOTH
            else -> null
        }
    }
}
