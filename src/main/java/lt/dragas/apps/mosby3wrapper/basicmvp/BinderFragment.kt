package lt.dragas.apps.mosby3wrapper.basicmvp

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle

/**
 * An extension to wrapper [Fragment] class, that uses Databinding
 */
abstract class BinderFragment<M, V : View<M>, P : Presenter<M, V>, VS : ViewState<M, V>, B : ViewDataBinding> : Fragment<M, V, P, VS>()
{
    protected var binder: B? = null

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        binder = DataBindingUtil.bind(view)
    }
}