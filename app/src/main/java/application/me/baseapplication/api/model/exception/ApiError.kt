package id.paprika.paprika.api.exception

import android.os.Parcel
import android.os.Parcelable

class ApiError() : Exception(), Parcelable {

    private var code: Int = 0
    override var message: String? = null

    constructor(code: Int, message: String?): this() {
        this.code = code
        this.message = message
    }

    constructor(parcel: Parcel) : this() {
        code = parcel.readInt()
        message = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(code)
        parcel.writeString(message)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ApiError> {

        override fun createFromParcel(parcel: Parcel): ApiError {
            return ApiError(parcel)
        }

        override fun newArray(size: Int): Array<ApiError?> {
            return arrayOfNulls(size)
        }
    }
}
