package jp.kirin3.okhttp

import android.app.ProgressDialog

import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.AsyncTaskLoader
import android.support.v4.content.Loader
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject


class MainActivity : AppCompatActivity() {

    private var mDialog:ProgressDialog ?= null
    private var mLoader:AsyncLoadingTask ?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadButton.setOnClickListener{
            Log.w( "DEBUG_DATA", "setOnClickListener")
            setLoader()
            mDialog?.show()
        }
        setDialog()
    }

    override fun onStop(){
        super.onStop()
        if (mDialog != null) {
            mDialog?.dismiss()
        }
        if (mLoader != null) {
            mLoader?.stopLoading()
            mLoader = null
        }
    }

    fun setLoader(){
        val LOADER_TAG_METADATA = 1
        val QUERY_ARG_URL = "url"

        val bundle = Bundle()
        bundle.putString(QUERY_ARG_URL,QUERY_ARG_URL)
        // getSupportLoaderManager().initLoader(LOADER_TAG_METADATA,bundle,callback)
        LoaderManager.getInstance(this).initLoader(LOADER_TAG_METADATA,bundle,callback)
    }


    fun setDialog(){
        mDialog = ProgressDialog(this)
        mDialog?.setTitle(R.string.app_name)
        mDialog?.setMessage("データを更新しています...")
        mDialog?.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        mDialog?.setOnCancelListener{
            if (mLoader != null) {
                mLoader?.stopLoading()
                mLoader = null
            }
        }
    }

    fun setText(data: JSONObject){
        if(data != null){
            textView.text = data.toString()
        }
        if(mDialog != null ){
            mDialog?.dismiss()
        }
    }

    // MainActivityに埋め込んでもよいがコールバック作成
    private val callback : LoaderManager.LoaderCallbacks<JSONObject> = object : LoaderManager.LoaderCallbacks<JSONObject> {

        override fun onCreateLoader(id: Int, args: Bundle?) : Loader<JSONObject> {
            val url = "https://madefor.github.io/postal-code-api/api/v1/100/0014.json"
            mLoader = AsyncLoadingTask(applicationContext, url)
            return mLoader!!
        }

        override fun onLoadFinished(loader: Loader<JSONObject>, data: JSONObject) {

            loaderManager.destroyLoader(loader.id)
            LoaderManager.getInstance(this@MainActivity).destroyLoader(loader.id)
            setText(data)
        }

        override fun onLoaderReset(loader: Loader<JSONObject>) {}
    }


}
