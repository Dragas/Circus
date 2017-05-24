package lt.dragas.apps.mosby3wrapper.basicmvp

import com.hannesdorfmann.mosby3.mvp.MvpView


interface View<M> : MvpView
{
    var data: M?
    fun loadData(fromDatabase: Boolean)
}