package application.me.baseapplication.api.model

class MovieList {

    var page: Int = 0

    var totalResults: Int = 0

    var totalPages: Int = 0

    var results: List<Movie>? = null
}