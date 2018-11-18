package br.com.projeto.sipi.viewmodel

import android.arch.lifecycle.ViewModel
import android.os.Parcel
import android.os.Parcelable

class ProjetoViewModel(var nome : String, var id : String, var situacao : String) : ViewModel(), Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(nome)
        parcel.writeString(id)
        parcel.writeString(situacao)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ProjetoViewModel> {
        override fun createFromParcel(parcel: Parcel): ProjetoViewModel {
            return ProjetoViewModel(parcel)
        }

        override fun newArray(size: Int): Array<ProjetoViewModel?> {
            return arrayOfNulls(size)
        }
    }
}