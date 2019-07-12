package com.potevio.rxjavalearning;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class FlowableLearningActivity extends AppCompatActivity {

    String TAG  = FlowableLearningActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flowable_learning);

//        testNoBackPressure();
//        backPressureStrategy_ERROR();
//        backPressureStrategy_BUFFER();
        backPressureStrategy_DROP();
        backPressureStrategy_LATEST();
    }

    void testNoBackPressure() {

        final String tag = TAG + "noBackPressure";

        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                for (int i=0;i<20;i++) {
                    emitter.onNext("string:"+i);
                }
            }
        }).subscribeOn(Schedulers.io())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.i(tag,"onSubscribe");
                    }

                    @Override
                    public void onNext(String s) {
                        Log.i(tag,"onNext:"+s);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(tag,"onError:"+e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.i(tag,"onComplete");
                    }
                });
    }

    /**
     * 被压策略-Error
     * 当发射超过129个数据的时候，则会抛出异常
     * flowable默认缓存大小为128
     */
    void backPressureStrategy_ERROR() {
        final String tag = "StrategyError";
//        Flowable.create(new FlowableOnSubscribe<String>() {
//            @Override
//            public void subscribe(FlowableEmitter<String> emitter) throws Exception {
//                for (int i=0; i<12; i++) {     //默认缓存是128
//                    emitter.onNext("emitter:"+i);
//                }
//            }
//        },BackpressureStrategy.ERROR)
//            .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<String>() {     //这里没有接收onError，App直接爆炸
//                    @Override
//                    public void accept(String s) throws Exception {
//                        Log.i(tag,"accept:"+s);
//                    }
//                });

        Flowable.create(new FlowableOnSubscribe<String>() {
            @Override
            public void subscribe(FlowableEmitter<String> emitter) throws Exception {
                for (int i=0; i<129; i++) {     //默认缓存是128,129就报错crash
                    emitter.onNext("emitter:"+i);
                }
            }
        },BackpressureStrategy.ERROR)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {

                    }
                });
    }

    /**
     * 被压策略-BUFFER
     * 和Observalbe一样可以无限缓存
     */
    void backPressureStrategy_BUFFER() {

        final String tag = TAG +"StrategyBuffer";
        Flowable.create(new FlowableOnSubscribe<String>() {
            @Override
            public void subscribe(FlowableEmitter<String> emitter) throws Exception {
                for (int i=0; ;i++) {   //会导致ANR
                    emitter.onNext("OnNext:"+i);
                }
            }
        },BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.i(tag,"accept:"+s);
                    }
                });
    }

    /**
     * 背压策略-DROP
     * 对超出缓存的数据进行抛弃
     */
    void backPressureStrategy_DROP() {
        final String tag = TAG + "StrategyDROP";
        Flowable.create(new FlowableOnSubscribe<String>() {
            @Override
            public void subscribe(FlowableEmitter<String> emitter) throws Exception {
                for (int i=0;i<150; i++) {
                    emitter.onNext("onNext:"+i);
                }
            }
        },BackpressureStrategy.DROP)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.i(tag,"accept:"+s);
                    }
                });
    }

    /**
     * 和Drop一样，但是和DROP不一样的是LATEST会发射所有数据的最后一个
     */
    void backPressureStrategy_LATEST() {
        final String tag = TAG + "StrategyLatest";

        Flowable.create(new FlowableOnSubscribe<String>() {
            @Override
            public void subscribe(FlowableEmitter<String> emitter) throws Exception {
                for (int i=0; i<200; i++) {
                    emitter.onNext("String:"+i);
                }
            }
        },BackpressureStrategy.LATEST)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.i(tag,"accept:"+s);
                    }
                });

    }












}
