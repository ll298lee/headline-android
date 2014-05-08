package com.djages.common;



//import com.loopj.android.http.AsyncHttpResponseHandler;
//import com.loopj.android.http.RequestParams;


public abstract class AbstractRESTApi {

//    public interface IApiListener{
//        public void onApiEvent(ApiEvent event, Object obj);
//
//    }
//
//    public enum ApiEvent{
//        PREPARE,
//        FINISHED,
//        SUCCESS,
//        FAILED
//    }
//
//    protected enum Method{
//        POST,
//        GET,
//        DELETE
//    }
//
//    protected RequestParams mParams = null;
//    protected String mUrl = null;
//    protected Method mMethod = Method.GET;
//
//    public void query(final IApiListener listener){
//        if (mParams == null) {
//            Log.e(this, "mParam haven't initialized");
//            return;
//        }
//        if (mUrl == null) {
//            Log.e(this, "mUrl haven't initialized");
//            return;
//        }
//
//
//        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
//            @Override
//            public void onStart(){
//                listener.onApiEvent(ApiEvent.PREPARE, null);
//            }
//
//            @Override
//            public void onFinish(){
//                listener.onApiEvent(ApiEvent.FINISHED, null);
//            }
//
//
//            @Override
//            public void onSuccess(String response) {
//                JsonElement json;
//                try{
//                    json = GsonHelper.getJsonParser().parse(response);
//                }catch (Exception e){
//                    json = null;
//                }
//
//                if(json != null && json.isJsonObject()){
//                    JsonObject jObj = json.getAsJsonObject();
//                    listener.onApiEvent(ApiEvent.SUCCESS, parse(jObj));
//                }else {
//                    listener.onApiEvent(ApiEvent.FAILED,
//                            new Error(this.getClass().getSimpleName(), ErrorCode.API_PARSE_RESPONSE_FAILED, mMethod.name()+ "" + mUrl + ": Api parse response failed", response));
//                }
//            }
//
//            @Override
//            public void onFailure(Throwable error, String content) {
//
//                listener.onApiEvent(ApiEvent.FAILED,
//                        new Error(this.getClass().getSimpleName(), ErrorCode.API_RESPONSE_FAILED, mMethod.name()+ " " + mUrl + ": Api response failed",content));
//            }
//        };
//
//        if (mMethod == Method.POST) {
//            HttpHelper.getAsyncClient().post(mUrl, mParams, handler);
//        } else if (mMethod == Method.GET){
//            HttpHelper.getAsyncClient().get(mUrl, mParams, handler);
//        } else if (mMethod == Method.DELETE){
//            HttpHelper.getAsyncClient().delete(mUrl, handler);
//        } else {
//            Log.e(this, "Unsupported http method");
//        }
//
//
//    }
//
//    protected abstract Object parse(JsonObject jObj);

}