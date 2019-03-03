package application.me.baseapplication.api.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import application.me.baseapplication.BaseApplication
import java.util.*
import kotlin.collections.ArrayList

@Entity
class Movie() : ObjectInterface, Parcelable {

    @PrimaryKey
    var id: Int = 0

    var originalTitle: String? = null

    @Ignore
    var voteCount: Int = 0

    @Ignore
    var voteAverage: Double = 0.0

    var genreIds: List<Int>? = null

    var posterPath: String? = null

    @Ignore
    var originalLanguage: String? = null

    @Ignore
    var overview: String? = null

    @Ignore
    private var backdropPath: String? = null

    @Ignore
    var releaseDate: Date? = null

    @Ignore
    var genres: List<Genre>? = null

    var genreNames: String? = null

    var isFavourite: Boolean = false

    fun getPosterUrl(): String? {
        return if (posterPath != null) "https://image.tmdb.org/t/p/w500/$posterPath" else null
    }

    fun getBackdropUrl(): String? {
        return if (backdropPath != null) "https://image.tmdb.org/t/p/w500/$backdropPath" else null
    }

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        originalTitle = parcel.readString()
        voteCount = parcel.readInt()
        voteAverage = parcel.readDouble()
        posterPath = parcel.readString()
        originalLanguage = parcel.readString()
        overview = parcel.readString()
        backdropPath = parcel.readString()
        val date = parcel.readLong()
        releaseDate = if (date == -1L) null else Date(date)
        isFavourite = parcel.readInt() == 1
        genreNames = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(originalTitle)
        parcel.writeInt(voteCount)
        parcel.writeDouble(voteAverage)
        parcel.writeString(posterPath)
        parcel.writeString(originalLanguage)
        parcel.writeString(overview)
        parcel.writeString(backdropPath)
        parcel.writeLong(releaseDate?.time ?: -1L)
        parcel.writeInt(if (isFavourite) 1 else 0)
        parcel.writeString(genreNames)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Movie> {
        override fun createFromParcel(parcel: Parcel): Movie {
            return Movie(parcel)
        }

        override fun newArray(size: Int): Array<Movie?> {
            return arrayOfNulls(size)
        }
    }
}