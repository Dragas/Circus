package lt.dragas.apps.mosby3wrapper.basicmvp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.hannesdorfmann.mosby3.mvp.viewstate.MvpViewStateFragment

/**
 * Wrapper class for Mosby3 MVP fragments
 */
abstract class Fragment<M, V : View<M>, P : Presenter<M, V>, VS : ViewState<M, V>> : MvpViewStateFragment<V, P, VS>(), View<M>
{
    abstract val layoutRes: Int
    override var data: M? = null
        set(value)
        {
            field = value
            viewState.dataLoaded = value != null
        }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        //retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): android.view.View?
    {
        val view = inflater.inflate(layoutRes, container, false)

        return view
    }

    override fun loadData(fromDatabase: Boolean)
    {
        if (fromDatabase)
            presenter.loadFromDatabase()
        else
            presenter.loadData()
    }

    /*override fun getMvpDelegate(): FragmentMvpDelegate<V, P>
    {
        return mMvpDelegate
    }*/
}