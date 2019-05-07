package com.hbcx.driver.utils;


import android.app.Activity;
import android.content.Context;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OSSUtil {
    public final String BUCKET_NAME = "foreignteachers";
    public final String ENDPOINT = "http://oss-cn-beijing.aliyuncs.com";
    public final String OSS_AK = "LTAIw6ll8NkAFjbg";
    public final String OSS_AKS = "S9mz3JRowDqLbYJr6EYlgLSZ8ysdAM";

    private String bucketName = BUCKET_NAME;
    private String endpoint = ENDPOINT;
    private String keyID = OSS_AK;
    private String OSS_KEY = OSS_AKS;
    private Activity mActivity;
    private ArrayList<OSSAsyncTask> mTasks = new ArrayList<>();
    private OSS mOss;
    private ArrayList<String> URLs;

    //总计大小
    private Long TOTAL_SIZE = 0l;
    //已传大小
    private Long CURRENT_SIZE = 0l;
    //总计数目
    private int TOTAL_COUNT;
    //已传数目
    private int CURRENT_COUNT;

    public OSSUtil(Activity activity) {
        mActivity = activity;
        URLs = new ArrayList<>();
        initSDK(mActivity);
    }

    private void initSDK(Context context) {
        OSSPlainTextAKSKCredentialProvider provider = new OSSPlainTextAKSKCredentialProvider(keyID, OSS_KEY);
        ClientConfiguration configuration = new ClientConfiguration();
        configuration.setConnectionTimeout(30 * 1000);  // 连接超时，默认15秒
        configuration.setSocketTimeout(30 * 1000);  // socket超时，默认15秒
        configuration.setMaxConcurrentRequest(5);  // 最大并发请求数，默认5个
        configuration.setMaxErrorRetry(2);  // 失败后最大重试次数，默认2次
        mOss = new OSSClient(context, endpoint, provider);
    }

    private void upload(final List<String> urls, final Boolean isSingle, final OSSUploadCallBack callBack) {
        final String objectKey = UUID.randomUUID().toString();
        PutObjectRequest put = new PutObjectRequest(bucketName, objectKey, urls.get(CURRENT_COUNT));
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            long temp = 0;

            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                CURRENT_SIZE += currentSize - temp;
                temp = currentSize;
                callBack.onSizeProgress(CURRENT_SIZE, TOTAL_SIZE);
            }
        });

        OSSAsyncTask<PutObjectResult> task = mOss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        URLs.add(mOss.presignPublicObjectURL(bucketName, objectKey));
                        if (isSingle) {
                            callBack.onFinish(URLs.get(0));
                            callBack.onCountProgress(++CURRENT_COUNT, TOTAL_COUNT);
                            mTasks.clear();
                        } else {
                            ++CURRENT_COUNT;
                            callBack.onCountProgress(CURRENT_COUNT, TOTAL_COUNT);
                            if (CURRENT_COUNT == urls.size()) {
                                mTasks.clear();
                                callBack.onFinish(URLs);
                            } else
                                upload(urls, isSingle, callBack);
                        }
                    }
                });

            }


            @Override
            public void onFailure(PutObjectRequest request, ClientException
                    clientException, ServiceException serviceException) {
                // 请求异常
                if (clientException != null) {
                    clientException.printStackTrace();
                    // 本地异常如网络异常等
                    callBack.onFial("网络异常");
                }
                if (serviceException != null) {
                    serviceException.printStackTrace();
                    // 服务异常
                    callBack.onFial("服务器异常");
                }

            }
        });
        mTasks.add(task);
    }


    /**
     * 单文件上传
     *
     * @param url      the url
     * @param callBack the call back
     */
    public void uploadSingle(String url, OSSUploadCallBack callBack) {
        ArrayList<String> single = new ArrayList<>();
        single.add(url);
        for (String path : single) {
            TOTAL_SIZE += new File(url).length();
        }
        TOTAL_COUNT = single.size();
        upload(single, true, callBack);
    }

    /**
     * 多文件上传
     *
     * @param urls     the urls
     * @param callBack the call back
     */
    public void uploadMulti(List<String> urls, OSSUploadCallBack callBack) {
        for (String path : urls) {
            TOTAL_SIZE += new File(path).length();
        }
        TOTAL_COUNT = urls.size();
        upload(urls, false, callBack);
    }

    public abstract static class OSSUploadCallBack {
        //单张上传成功
        public void onFinish(String url) {

        }

        //批量上传成功
        public void onFinish(ArrayList<String> urls) {
        }

        //成功文件大小回调
        public void onSizeProgress(Long currentSize, Long totalSize) {
        }

        //成功文件数目回调
        public void onCountProgress(int currentCount, int totalCount) {
        }


        public void onFial(String message) {

        }

    }
}



