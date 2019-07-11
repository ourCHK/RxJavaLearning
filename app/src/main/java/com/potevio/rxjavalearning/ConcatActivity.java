package com.potevio.rxjavalearning;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import javax.security.auth.login.LoginException;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.observables.ConnectableObservable;

/**
 * 连接操作符的学习
 */
public class ConcatActivity extends AppCompatActivity {

    String TAG = ConcatActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_concat);

        merge();
        zip();
        startWithAndConcat();
        intervalTake();
        connectPublish();
        refCount();
        replay();
    }

    /**
     * 合并Observable，zip
     * 若其中有Observable出现onError，则中断发送数据并发送onError
     * 另一个操作符mergeDelayError则是出现error时会延迟发送onError，而优先发送其他Observable的数据
     */
    void merge() {
        final String tag = TAG +"merge";
        Observable<Integer> observable1 = Observable.just(1,3);
        Observable<Integer> observable2 = Observable.just(2,4);
        Observable.merge(observable1,observable2)
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.i(tag,"onSubscribe");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.i(tag,"onNext:"+integer);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(tag,"onError");
                    }

                    @Override
                    public void onComplete() {
                        Log.i(tag,"onComplete");
                    }
                });
    }

    /**
     * 压缩数据，将2个Observable的数据一对一对取出并进行变换
     * 注意返回数据的个数和最小Observable的数据个数相同
     * 可以看出下面只输出了3个数据
     */
    void zip() {
        final String tag = TAG + "zip";

        Observable<Integer> observable1 = Observable.just(1,2,3,4);
        Observable<Integer> observable2 = Observable.just(4,5,6);
        Observable.zip(observable1, observable2, new BiFunction<Integer, Integer, String>() {
            @Override
            public String apply(Integer integer, Integer integer2) throws Exception {
                return String.valueOf(integer + integer2);  //求和并返回求和之后的字符串
            }
        }).subscribe(new Observer<String>() {
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
                Log.i(tag,"onError");

            }

            @Override
            public void onComplete() {
                Log.i(tag,"onComplete");
            }
        });
    }


    /**
     * startWith在发射数据前插入数据至最前面
     */
    void startWithAndConcat() {
        final String tag = TAG  + "startWithAndConcat";
        Observable.just(1,2,3)
                .startWith(0)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.i(tag,"integer:"+integer);
                    }
                });
    }

    void intervalTake() {
        final String tag = TAG + "intervalTake";
        Observable.interval(1,TimeUnit.SECONDS)
                .take(5)
                .delaySubscription(3,TimeUnit.SECONDS)
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        Log.i(tag, "onNext:" + aLong);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.i(tag, "onError");
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.i(tag,"on Complete");
                    }
                });
    }

    /**
     * Observable使用publish转化为connectableObservable
     * connectableObservable调用connect之后才会开始发送数据
     * 但是订阅其实是马上执行的，只是执行onNext被延迟到Connect才执行
     */
    void connectPublish() {

        final String tag = TAG + "connectPublish";

        Observable<Long> observable = Observable.interval(1,TimeUnit.SECONDS).take(6);   //取前2秒的数据
        ConnectableObservable<Long> connectableObservable = observable.publish();
        connectableObservable.subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.i(tag,"obSubscribe");
            }

            @Override
            public void onNext(Long aLong) {
                Log.i(tag,"onNext:"+aLong);

            }

            @Override
            public void onError(Throwable e) {
                Log.i(tag,"orError");

            }

            @Override
            public void onComplete() {
                Log.i(tag,"complete");
            }
        });
        connectableObservable.delaySubscription(3,TimeUnit.SECONDS) //延迟3秒订阅,从3秒后开始onNext，说明是hot observable
                .subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.i(tag,"obSubscribe2");
            }

            @Override
            public void onNext(Long aLong) {
                Log.i(tag,"onNext2:"+aLong);

            }

            @Override
            public void onError(Throwable e) {
                Log.i(tag,"orError2");

            }

            @Override
            public void onComplete() {
                Log.i(tag,"complete2");
            }
        });
        connectableObservable.connect();    //不加这一句只会有onSubscribe打印，后面的onNext不会打印Log
    }

    /**
     * refCount将ConnectableObservable转化为Observable，并使Observable带有hot Observable的特性
     */
    void refCount() {

        final String tag = TAG + "refCount";

        Observable<Long> observable = Observable.interval(1,TimeUnit.SECONDS).take(6);
        ConnectableObservable<Long> connectableObservable = observable.publish();    //先转化为ConnectableObservable
        Observable<Long> refObservable = connectableObservable.refCount();   //转化为Observable,并保持hot Observable特性

        observable.subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.i(tag,"observable1 subscribe");
            }

            @Override
            public void onNext(Long aLong) {
                Log.i(tag,"onNext1:"+aLong);
            }

            @Override
            public void onError(Throwable e) {
                Log.i(tag,"onError1");
            }

            @Override
            public void onComplete() {
                Log.i(tag,"onComplete1");
            }
        });

        observable.delaySubscription(3,TimeUnit.SECONDS)    //延迟3秒订阅，我们发现数据还是一样会从头开始的，因为是coldObservable
            .subscribe(new Observer<Long>() {
                @Override
                public void onSubscribe(Disposable d) {
                    Log.i(tag,"observable2 Subscribe");
                }

                @Override
                public void onNext(Long aLong) {
                    Log.i(tag,"onNext2:"+aLong);
                }

                @Override
                public void onError(Throwable e) {
                    Log.i(tag,"onError2:"+e.getMessage());

                }

                @Override
                public void onComplete() {
                    Log.i(tag,"onComplete2");

                }
            });

        refObservable.subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.i(tag,"onSubscribe3");
            }

            @Override
            public void onNext(Long aLong) {
                Log.i(tag,"onNext3:"+aLong);

            }

            @Override
            public void onError(Throwable e) {
                Log.i(tag,"onError3:"+e.getMessage());

            }

            @Override
            public void onComplete() {
                Log.i(tag,"onComplete3");

            }
        });
        refObservable.delaySubscription(3,TimeUnit.SECONDS) //延迟3秒订阅
                .subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.i(tag,"onSubscribe4");
            }

            @Override
            public void onNext(Long aLong) {
                Log.i(tag,"onNext4:"+aLong);
            }

            @Override
            public void onError(Throwable e) {
                Log.i(tag,"onError4:"+e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.i(tag,"onComplete4");
            }
        });
    }

    void replay() {
        final String tag = TAG + "replay";

        //转化成cold Observable并使其保有ConnectableObservable的特性（调用Connect才开始发送数据）
        //保持同步的同时是瞬间发射前面累计的数据，而不会按照时间间隔一个个发送，在下一次数据时多个Observer机会是同时收到数据的
        ConnectableObservable<Long> connectableObservable =  Observable.interval(1,TimeUnit.SECONDS).take(6).replay();
        connectableObservable
                .connect();
        connectableObservable.subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.i(tag,"onSubscribe1");
            }

            @Override
            public void onNext(Long aLong) {
                Log.i(tag,"onNext1:"+aLong);
            }

            @Override
            public void onError(Throwable e) {
                Log.i(tag,"onError1:"+e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.i(tag,"onComplete1");
            }
        });

        connectableObservable.delaySubscription(3,TimeUnit.SECONDS)
                .subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.i(tag,"onSubscribe2");
            }

            @Override
            public void onNext(Long aLong) {
                Log.i(tag,"onNext2:"+aLong);
            }

            @Override
            public void onError(Throwable e) {
                Log.i(tag,"onError2:"+e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.i(tag,"onComplete2");
            }
        });
    }

}
