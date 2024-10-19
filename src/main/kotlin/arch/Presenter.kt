package arch

abstract class Presenter<V> {
    private var view:V?=null
    abstract fun onAttach(view: V)
    abstract fun onDetach(view: V)

    fun attach(view:V){
        if(view == null) {
            throw IllegalArgumentException("View must not be null")
        }
        this.view = view
        onAttach(view)
    }

    fun detach(){
        if (view !=null){
            onDetach(view!!)
        }
        view = null
    }

    protected fun withView(block: (V) -> Unit) =
        view?.also(block)

}