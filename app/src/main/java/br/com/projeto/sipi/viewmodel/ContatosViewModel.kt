package br.com.projeto.sipi.viewmodel

import android.arch.lifecycle.ViewModel
import android.os.Parcel
import android.os.Parcelable

class ContatosViewModel(var nome : String = "", var numero : String = "", var checked : Boolean = false) : ViewModel(), Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readByte() != 0.toByte())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(nome)
        parcel.writeString(numero)
        parcel.writeByte(if (checked) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ContatosViewModel> {
        override fun createFromParcel(parcel: Parcel): ContatosViewModel {
            return ContatosViewModel(parcel)
        }

        override fun newArray(size: Int): Array<ContatosViewModel?> {
            return arrayOfNulls(size)
        }
    }
}