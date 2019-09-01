package jp.kirin3.okhttp

import android.content.Context
import android.support.v4.content.AsyncTaskLoader
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class AsyncLoadingTask(context: Context, internal var url: String) : AsyncTaskLoader<JSONObject>(context) {

    private var result : JSONObject? = null
    private var isStart : Boolean = false

    override fun loadInBackground(): JSONObject? {
        Log.w( "DEBUG_DATA", "loadInBackground");
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .build()
        val response = client.newCall(request).execute()
        val body = response.body?.string()

        return JSONObject(body)
    }

// ---------------------------------------------
// 呼び出し元のActivityが回転などで再作成されるとinitLoaderを再度呼ばなければならない
// initLoaderがよばれるとココに来るが再度forceLoadしてしまうと
// 実行中のloadInBackgroundは破棄されもう一度loadInBackgroundが開始されてしまう
// 実行中のloadInBackgroundが無い時だけ実行を開始し終了している時は直ちに結果を返す

    override fun onStartLoading() {
        if (result != null){
            deliverResult(result)
            return
        }
        if (!isStart || takeContentChanged()) { // forceLoadが複数回呼ばれないようにする
            forceLoad()
        }
    }

    override fun onForceLoad() {
        super.onForceLoad()
        isStart = true
    }

    override fun deliverResult(data: JSONObject?) {
        result = data
        super.deliverResult(data)
    }

}
