package lt.dragas.apps.mosby3wrapper.basicmvp

import android.os.Bundle
import com.hannesdorfmann.mosby3.mvp.viewstate.RestorableViewState


abstract class ViewState<M, V : View<M>> : RestorableViewState<V>
{
    var dataLoaded: Boolean = false
    override fun restoreInstanceState(into: Bundle?): RestorableViewState<V>?
    {
        into ?: return null
        dataLoaded = into.getBoolean(KEY_STATE, false)
        return this
    }

    /**
     * Saves this ViewState to the outgoing bundle.
     * This will typically be called in [android.app.Activity.onSaveInstanceState]
     * or in  [android.app.Fragment.onSaveInstanceState]

     * @param out The bundle where the viewstate should be stored in
     */
    override fun saveInstanceState(out: Bundle)
    {
        out.putBoolean(KEY_STATE, dataLoaded)
    }

    override fun apply(view: V, retained: Boolean)
    {
        view.loadData(dataLoaded)
    }

    protected companion object
    {
        protected val KEY_STATE: String = "KEY_STATE_IN_BUNDLE"
    }
}