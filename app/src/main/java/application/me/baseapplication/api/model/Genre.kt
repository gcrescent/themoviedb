package application.me.baseapplication.api.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Genre {

    @PrimaryKey
    var id: Int = 0

    var name: String? = null
}