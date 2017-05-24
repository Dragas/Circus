package lt.dragas.apps.mosby3wrapper.basicmvp

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter


abstract class Presenter<M, V : View<M>> : MvpBasePresenter<V>()
{
    abstract fun loadFromDatabase()
    abstract fun loadData()
}