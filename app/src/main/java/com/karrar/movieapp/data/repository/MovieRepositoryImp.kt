package com.karrar.movieapp.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.paging.Pager
import com.karrar.movieapp.data.Constants
import com.karrar.movieapp.data.local.AppConfiguration
import com.karrar.movieapp.data.local.database.daos.ActorDao
import com.karrar.movieapp.data.local.database.daos.MovieDao
import com.karrar.movieapp.data.local.database.entity.ActorEntity
import com.karrar.movieapp.data.local.database.entity.SearchHistoryEntity
import com.karrar.movieapp.data.local.database.entity.WatchHistoryEntity
import com.karrar.movieapp.data.local.database.entity.movie.AdventureMovieEntity
import com.karrar.movieapp.data.local.database.entity.movie.MysteryMovieEntity
import com.karrar.movieapp.data.local.database.entity.movie.NowStreamingMovieEntity
import com.karrar.movieapp.data.local.database.entity.movie.PopularMovieEntity
import com.karrar.movieapp.data.local.database.entity.movie.TrendingMovieEntity
import com.karrar.movieapp.data.local.database.entity.movie.UpcomingMovieEntity
import com.karrar.movieapp.data.local.database.entity.movie.UserMatchingMovieEntity
import com.karrar.movieapp.data.local.mappers.movie.LocalMovieMappersContainer
import com.karrar.movieapp.data.remote.response.AddListResponse
import com.karrar.movieapp.data.remote.response.AddMovieDto
import com.karrar.movieapp.data.remote.response.BaseListResponse
import com.karrar.movieapp.data.remote.response.CreatedListDto
import com.karrar.movieapp.data.remote.response.CreditsDto
import com.karrar.movieapp.data.remote.response.DailyTrendingDto
import com.karrar.movieapp.data.remote.response.DefaultResponse
import com.karrar.movieapp.data.remote.response.MovieDto
import com.karrar.movieapp.data.remote.response.MyListsDto
import com.karrar.movieapp.data.remote.response.RatedMoviesDto
import com.karrar.movieapp.data.remote.response.SavedListDto
import com.karrar.movieapp.data.remote.response.actor.ActorDto
import com.karrar.movieapp.data.remote.response.actor.ActorMoviesDto
import com.karrar.movieapp.data.remote.response.actor.ActorProfileResponse
import com.karrar.movieapp.data.remote.response.actor.ActorSocialMediaResponse
import com.karrar.movieapp.data.remote.response.genre.GenreDto
import com.karrar.movieapp.data.remote.response.movie.MovieDetailsDto
import com.karrar.movieapp.data.remote.response.movie.RatingDto
import com.karrar.movieapp.data.remote.response.review.ReviewsDto
import com.karrar.movieapp.data.remote.response.trailerVideosDto.TrailerDto
import com.karrar.movieapp.data.remote.service.MovieService
import com.karrar.movieapp.data.repository.mediaDataSource.ActorMovieDataSource
import com.karrar.movieapp.data.repository.mediaDataSource.movie.MovieDataSourceContainer
import com.karrar.movieapp.data.repository.serchDataSource.SearchDataSourceContainer
import com.karrar.movieapp.domain.enums.Era
import com.karrar.movieapp.domain.enums.MatchingGenre
import com.karrar.movieapp.domain.enums.Mood
import com.karrar.movieapp.domain.enums.Runtime
import com.karrar.movieapp.domain.mappers.MediaDataSourceContainer
import com.karrar.movieapp.domain.mappers.MovieQueryMapper
import com.karrar.movieapp.domain.mappers.movie.MovieGenreMapper
import com.karrar.movieapp.domain.models.Genre
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject

class MovieRepositoryImp @Inject constructor(
    private val movieService: MovieService,
    private val movieDao: MovieDao,
    private val actorDao: ActorDao,
    private val appConfiguration: AppConfiguration,
    private val actorDataSource: ActorDataSource,
    private val dataMappers: LocalMovieMappersContainer,
    private val mediaDataSourceContainer: MediaDataSourceContainer,
    private val searchDataSourceContainer: SearchDataSourceContainer,
    private val movieDataSource: MovieDataSourceContainer,
    private val actorMovieDataSource: ActorMovieDataSource,
    private val queryMapper: MovieQueryMapper,
    private val movieGenreMapper: MovieGenreMapper
) : BaseRepository(), MovieRepository {

    override suspend fun getMovieGenreList(): List<GenreDto>? {
        val genres = movieService.getGenreList().body()?.genres
        return genres?.onEach { genre ->
            movieDao.insertGenre(
                dataMappers.movieGenreMapper.map(genre)
            )
        }
    }

    override suspend fun increaseMovieGenreVisitCount(genreId: Int) {
        movieDao.increaseGenreVisitCount(genreId)
    }

    override suspend fun getTopVisitedMovieGenre(): Flow<Genre> {
        return movieDao.getTopVisitedMovieGenre().map { movieGenreMapper.map(it) }
    }

    override suspend fun getDailyTrending(): BaseListResponse<DailyTrendingDto> {
        return movieService.getDailyTrending().body()!!
    }


    override suspend fun getRatedMovie(): List<RatedMoviesDto>? {
        return movieService.getRatedMovie().body()?.items
    }

    override suspend fun setRating(movieId: Int, value: Float): RatingDto? {
        return movieService.postRating(movieId, value).body()
    }

    /**
     * Actor.
     * */

    override suspend fun getActorDetails(actorId: Int): ActorDto? {
        return movieService.getActorDetails(actorId = actorId).body()
    }

    override suspend fun getActorSocialMediaIDs(actorId: Int): ActorSocialMediaResponse? {
        return movieService.getActorExternalIds(actorId).body()
    }

    override suspend fun getActorImages(actorId: Int): ActorProfileResponse? {
        return movieService.getActorImages(actorId).body()
    }

    override suspend fun getActorMovies(actorId: Int): ActorMoviesDto? {
        return movieService.getActorMovies(actorId = actorId).body()
    }

    /**
     * My list
     * */

    override suspend fun getAllLists(
        sessionId: String,
    ): List<CreatedListDto>? {
        return movieService.getCreatedLists(sessionId = sessionId).body()?.items
    }

    override suspend fun getListDetails(listId: Int): MyListsDto? {
        return movieService.getList(listId).body()
    }

    override suspend fun getSavedListDetails(listId: Int): List<SavedListDto>? {
        return movieService.getList(listId).body()?.items
    }

    override suspend fun createList(
        sessionId: String,
        name: String,
    ): AddListResponse? {
        return movieService.createList(sessionId, name).body()
    }

    override suspend fun addMovieToList(
        sessionId: String,
        listId: Int,
        movieId: Int,
    ): AddMovieDto? {
        return movieService.addMovieToList(listId, sessionId, movieId).body()
    }

    override suspend fun clearWatchHistory() {
        return movieDao.deleteAllWatchedMovies()
    }

    override suspend fun insertSearchItem(item: SearchHistoryEntity) {
        return movieDao.insert(item)
    }

    override suspend fun deleteSearchItem(item: SearchHistoryEntity) {
        return movieDao.delete(item)
    }

    override suspend fun clearSearchHistory() {
        movieDao.clearSearchHistory()
    }

    override suspend fun insertMovie(movie: WatchHistoryEntity) {
        return movieDao.insert(movie)
    }

    override suspend fun deleteMovieFromHistory(movie: WatchHistoryEntity) {
        movieDao.delete(movie)
    }

    override fun getAllWatchedMovies(): Flow<List<WatchHistoryEntity>> {
        return movieDao.getAllWatchedMovies()
    }


    override suspend fun getActorData(): Pager<Int, ActorDto> {
        return Pager(config = config, pagingSourceFactory = { actorDataSource })
    }

    override suspend fun getAllMovies(): Pager<Int, MovieDto> {
        return Pager(
            config = config,
            pagingSourceFactory = { mediaDataSourceContainer.movieDataSource })
    }

    override suspend fun getMovieByGenre(genreID: Int): Pager<Int, MovieDto> {
        return Pager(
            config = config,
            pagingSourceFactory = {
                val dataSource = mediaDataSourceContainer.movieByGenreDataSource
                dataSource.setGenre(genreID)
                dataSource
            }
        )
    }


    /**
     * Caching
     * */

    override suspend fun getPopularMovies(): Flow<List<PopularMovieEntity>> {
        refreshOneTimePerDay(
            appConfiguration.getRequestDate(Constants.POPULAR_MOVIE_REQUEST_DATE_KEY),
            ::refreshPopularMovies
        )
        return movieDao.getPopularMovies()
    }

    override suspend fun getTrendingMovies(): Flow<List<TrendingMovieEntity>> {
        refreshOneTimePerDay(
            appConfiguration.getRequestDate(Constants.TRENDING_MOVIE_REQUEST_DATE_KEY),
            ::refreshTrendingMovies
        )
        return movieDao.getTrendingMovies()
    }

    override suspend fun getNowStreamingMovies(): Flow<List<NowStreamingMovieEntity>> {
        refreshOneTimePerDay(
            appConfiguration.getRequestDate(Constants.NOW_STREAMING_MOVIE_REQUEST_DATE_KEY),
            ::refreshNowPlayingMovies
        )
        return movieDao.getNowStreamingMovies()
    }

    override suspend fun getAdventureMovies(): Flow<List<AdventureMovieEntity>> {
        refreshOneTimePerDay(
            appConfiguration.getRequestDate(Constants.ADVENTURE_MOVIE_REQUEST_DATE_KEY),
            ::refreshAdventureMovies
        )
        return movieDao.getAdventureMovies()
    }

    override suspend fun getMysteryMovies(): Flow<List<MysteryMovieEntity>> {
        refreshOneTimePerDay(
            appConfiguration.getRequestDate(Constants.MYSTERY_MOVIE_REQUEST_DATE_KEY),
            ::refreshMysteryMovies
        )
        return movieDao.getMysteryMovies()
    }

    override suspend fun getTrendingActors(): Flow<List<ActorEntity>> {
        refreshOneTimePerDay(
            appConfiguration.getRequestDate(Constants.ACTOR_REQUEST_DATE_KEY),
            ::refreshTrendingActors
        )
        return actorDao.getActors()
    }


    override suspend fun getUpcomingMovies(): Flow<List<UpcomingMovieEntity>> {
        refreshOneTimePerDay(
            appConfiguration.getRequestDate(Constants.UPCOMING_MOVIE_REQUEST_DATE_KEY),
            ::refreshUpcomingMovies
        )
        return movieDao.getUpcomingMovies()
    }

    override suspend fun getUserMatchingMovies(): Flow<List<UserMatchingMovieEntity>> {
        refreshOneTimePerDay(
            appConfiguration.getRequestDate(Constants.UPCOMING_MOVIE_REQUEST_DATE_KEY),
            ::refreshUserMatchingMovies
        )
        return movieDao.getUserMatchingMovies()
    }

    override suspend fun getTrendingMoviesPager(): Pager<Int, MovieDto> {
        return Pager(
            config = config,
            pagingSourceFactory = { movieDataSource.trendingMovieDataSource })
    }

    override suspend fun getNowPlayingMoviesPager(): Pager<Int, MovieDto> {
        return Pager(
            config = config,
            pagingSourceFactory = { movieDataSource.nowStreamingMovieMovieDataSource })
    }

    override suspend fun getUpcomingMoviesPager(): Pager<Int, MovieDto> {
        return Pager(
            config = config,
            pagingSourceFactory = { movieDataSource.upcomingMovieMovieDataSource })
    }

    override suspend fun getAdventureMoviesPager(): Pager<Int, MovieDto> {
        val dataSource = movieDataSource.movieByGenreDataSource
        dataSource.setGenre(Constants.MYSTERY_ID)
        return Pager(config = config, pagingSourceFactory = { dataSource })
    }

    override suspend fun getMysteryMoviesPager(): Pager<Int, MovieDto> {
        val dataSource = movieDataSource.movieByGenreDataSource
        dataSource.setGenre(Constants.ADVENTURE_ID)
        return Pager(config = config, pagingSourceFactory = { dataSource })
    }

    override suspend fun getActorMoviesPager(actorId: Int): Pager<Int, MovieDto> {
        val dataSource = actorMovieDataSource
        dataSource.setMovieActorID(actorId)
        return Pager(config = config, pagingSourceFactory = { dataSource })
    }


    private suspend fun refreshPopularMovies(currentDate: Date) {
        val genres = getMovieGenreList() ?: emptyList()
        refreshWrapper(
            { movieService.getPopularMovies() },
            { items ->
                items?.map { dataMappers.popularMovieMapper.map(it, genres) }
            },
            {
                movieDao.deletePopularMovies()
                movieDao.insertPopularMovies(it)
                appConfiguration.saveRequestDate(
                    Constants.POPULAR_MOVIE_REQUEST_DATE_KEY,
                    currentDate.time
                )
            },
        )
    }

    private suspend fun refreshTrendingMovies(currentDate: Date) {
        refreshWrapper(
            { movieService.getTrendingMovies() },
            { list ->
                list?.map { dataMappers.trendingMovieMapper.map(it) }
            },
            {
                movieDao.deleteAllTrendingMovies()
                movieDao.insertTrendingMovie(it)
                appConfiguration.saveRequestDate(
                    Constants.TRENDING_MOVIE_REQUEST_DATE_KEY,
                    currentDate.time
                )
            },
        )
    }

    private suspend fun refreshNowPlayingMovies(currentDate: Date) {
        refreshWrapper(
            { movieService.getNowPlayingMovies() },
            { list ->
                list?.map { dataMappers.nowStreamingMovieMapper.map(it) }
            },
            {
                movieDao.deleteAllNowStreamingMovies()
                movieDao.insertNowStreamingMovie(it)
                appConfiguration.saveRequestDate(
                    Constants.NOW_STREAMING_MOVIE_REQUEST_DATE_KEY,
                    currentDate.time
                )
            },
        )
    }

    private suspend fun refreshUpcomingMovies(currentDate: Date) {
        refreshWrapper(
            { movieService.getUpcomingMovies() },
            { list ->
                list?.map { dataMappers.upcomingMovieMapper.map(it) }
            },
            {
                movieDao.deleteAllUpcomingMovies()
                movieDao.insertUpcomingMovie(it)
                appConfiguration.saveRequestDate(
                    Constants.UPCOMING_MOVIE_REQUEST_DATE_KEY,
                    currentDate.time
                )
            },
        )
    }

    private suspend fun refreshUserMatchingMovies(currentDate: Date) {
        val topGenre = getTopVisitedMovieGenre().last()
        refreshWrapper(
            { movieService.getMovieListByGenre(topGenre.genreID) },
            { list ->
                list?.map { dataMappers.userMatchingMovieMapper.map(it) }
            },
            {
                movieDao.deleteAllUserMatchingMovies()
                movieDao.insertUserMatchingMovie(it)
                appConfiguration.saveRequestDate(
                    Constants.USER_MATCHING_MOVIE_REQUEST_DATE_KEY,
                    currentDate.time
                )
            },
        )
    }

    private suspend fun refreshAdventureMovies(currentDate: Date) {
        refreshWrapper(
            { movieService.getMovieListByGenre(genreID = Constants.ADVENTURE_ID) },
            { list ->
                list?.map { dataMappers.adventureMovieMapper.map(it) }
            },
            {
                movieDao.deleteAllAdventureMovies()
                movieDao.insertAdventureMovie(it)
                appConfiguration.saveRequestDate(
                    Constants.ADVENTURE_MOVIE_REQUEST_DATE_KEY,
                    currentDate.time
                )
            },
        )
    }

    private suspend fun refreshMysteryMovies(currentDate: Date) {
        refreshWrapper(
            { movieService.getMovieListByGenre(genreID = Constants.MYSTERY_ID) },
            { list ->
                list?.map { dataMappers.mysteryMovieMapper.map(it) }
            },
            {
                movieDao.deleteAllMysteryMovies()
                movieDao.insertMysteryMovie(it)
                appConfiguration.saveRequestDate(
                    Constants.MYSTERY_MOVIE_REQUEST_DATE_KEY,
                    currentDate.time
                )
            },
        )
    }

    private suspend fun refreshTrendingActors(currentDate: Date) {
        refreshWrapper(
            { movieService.getTrendingActors() }, { items ->
                items?.map { dataMappers.actorMapper.map(it) }
            }, {
                actorDao.deleteActors()
                actorDao.insertActors(it)
                appConfiguration.saveRequestDate(Constants.ACTOR_REQUEST_DATE_KEY, currentDate.time)
            }
        )
    }


    /**
     * searching
     * */

    override suspend fun searchForMoviePager(query: String): Pager<Int, MovieDto> {
        val dataSource = searchDataSourceContainer.movieSearchDataSource
        dataSource.setSearchText(query)
        return Pager(config = config, pagingSourceFactory = { dataSource })
    }

    override suspend fun searchForActorPager(query: String): Pager<Int, ActorDto> {
        val dataSource = searchDataSourceContainer.actorSearchDataSource
        dataSource.setSearchText(query)
        return Pager(config = config, pagingSourceFactory = { dataSource })
    }


    override suspend fun getAllSearchHistory(): Flow<List<SearchHistoryEntity>> {
        return movieDao.getAllSearchHistory()
    }

    /**
     * movie details
     * */

    override suspend fun getMovieDetails(movieId: Int): MovieDetailsDto? {
        return movieService.getMovieDetails(movieId).body()
    }

    override suspend fun getMovieCastAndCrew(movieId: Int): CreditsDto? {
        return movieService.getMovieCastAndCrew(movieId).body()
    }

    override suspend fun getSimilarMovie(movieId: Int): List<MovieDto>? {
        return movieService.getSimilarMovie(movieId).body()?.items
    }

    override suspend fun getMovieReviews(movieId: Int): List<ReviewsDto>? {
        return movieService.getMovieReviews(movieId).body()?.items
    }

    override suspend fun deleteRating(movieId: Int): RatingDto? {
        return movieService.deleteRating(movieId).body()
    }

    override suspend fun getMovieTrailer(movieId: Int): TrailerDto? {
        return movieService.getMovieTrailer(movieId).body()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getMatchingMovies(
        moods: List<Mood>,
        genres: List<MatchingGenre>,
        runtime: Runtime,
        era: Era,
    ): List<MovieDto>? {
        val keyword = queryMapper.mapMoods(moods)
        val genreIds = queryMapper.mapGenres(genres)
        val (minRuntime, maxRuntime) = queryMapper.mapRuntime(runtime)
        val (fromDate, toDate) = queryMapper.mapEra(era)

        val response = movieService.getMatchingMovies(
            moodId = keyword,
            genreIds = genreIds,
            minRuntime = minRuntime,
            maxRuntime = maxRuntime,
            earliestDate = fromDate,
            latestDate = toDate
        )
        return response.body()?.items
    }

    override suspend fun removeMovieFromCollection(
        sessionId: String,
        collectionId: String,
        movieId: Int,
    ): DefaultResponse? {
        return movieService.removeMovieFromCollection(
            collectionId = collectionId,
            sessionId = sessionId,
            movieId = movieId
        ).body()
    }

    override suspend fun clearCache() {
        movieDao.deletePopularMovies()
        movieDao.deleteAllTrendingMovies()
        movieDao.deleteAllNowStreamingMovies()
        movieDao.deleteAllUpcomingMovies()
        movieDao.deleteAllMysteryMovies()
        movieDao.deleteAllAdventureMovies()
        actorDao.deleteActors()
        appConfiguration.clearRequestDates()

    }

}