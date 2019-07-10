package com.potevio.rxjavalearning;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TimeUtils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BooleanSupplier;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class OperateSymbolActivity extends AppCompatActivity {

    private String TAG = OperateSymbolActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operate_symbol);

        createSymbol();
        justSymbol();
        fromSymbol();
        repeatSymbol();
        repeatWhen();
        repeatUntil();
        defer();
        interval();
        timer();
    }

    /**
     * 原生方式创建Observable
     */
    void createSymbol () {
        final String tag = TAG + "createSymbol";

        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                if (!emitter.isDisposed()) {    //这里判断是不是被处理了，！表示没有被处理
                    for (int i=0; i<10; i++) {
                        emitter.onNext(i);
                    }
                    emitter.onComplete();
                }
            }
        }).subscribe(new Observer<Integer>() {
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
     * 将数据一个一个发送，注意，就是发送的是数组或者List，也算是一个数据
     */
    void justSymbol() {

        final String tag = TAG + "justSymbol";


        Observable.just(1,2,3,4)
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
     * 将List或者Array等类似集合的方式一个一个的发送
     */
    void fromSymbol() {
        final String tag = TAG + "fromSymbol";
        List<String> stringList = Arrays.asList("String1","String2");
        String[] stringArray = {"123","234"};
        Observable.fromIterable(stringList)
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
                        Log.i(tag,"onError");

                    }

                    @Override
                    public void onComplete() {
                        Log.i(tag,"onComplete");

                    }
                });
    }

    /**
     * repeat重复发送数据
     */
    void repeatSymbol() {
        final String tag = TAG + "repeat Symbol";
        Observable.just("1","2")
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.i(tag,"accept:"+s);
                    }
                });
    }

    /**
     *有疑惑，貌似只能重发一次
     */
    void repeatWhen() {
        final String tag = TAG + "repeatWhen";
        Observable.range(2,4)
                .repeatWhen(new Function<Observable<Object>, ObservableSource<?>>() {

                    private int n = 1;

                    @Override
                    public ObservableSource<?> apply(Observable<Object> objectObservable) throws Exception {
                        if (n < 5) {
                            n++;
                            Log.i(tag,"go repeat when");
                            return Observable.timer(0,TimeUnit.SECONDS);
                        } else {
                            return Observable.empty();
                        }
                    }
                })
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
                        Log.i(tag,"onError:"+e.getMessage());

                    }

                    @Override
                    public void onComplete() {
                        Log.i(tag,"onComplete");

                    }
                });
    }

    /**
     * 一直重复知道repeatUtil返回true，表示已经达到条件了，所以就不重复下去了
     */
    void repeatUntil() {

        final String tag = TAG + "repeatUntil";

        Observable.range(4,2)
                .repeatUntil(new BooleanSupplier() {

                    int i=0;

                    @Override
                    public boolean getAsBoolean() throws Exception {
                        Log.i(tag,"go repeat Until");
                        if (i < 3) {
                            i++;
                            return false;
                        } else {
                            return true;
                        }
                    }
                })
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
     * 直到有观察者订阅时才创建Observable，并且为每个观察者都创建一个新的Observable,所以每次订阅都是会从onSubscribe开始走起
     * 用法：某些情况下，比如最后一分钟才执行订阅，这样可以确保数据书最新的
     */
    void defer() {
        final String tag = TAG + "defer";
        Observable observable1 = Observable.defer(new Callable<ObservableSource<?>>() {
            @Override
            public ObservableSource<?> call() throws Exception {
                return Observable.just("Hello World");
            }
        });

        Observer<String> observer1 =  new Observer<String>() {
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
        };

        Observer<String> observer2 =  new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.i(tag,"onSubscribe2");
            }

            @Override
            public void onNext(String s) {
                Log.i(tag,"onNext2:"+s);

            }

            @Override
            public void onError(Throwable e) {
                Log.i(tag,"onError2");

            }

            @Override
            public void onComplete() {
                Log.i(tag,"onComplete2");
            }
        };
        observable1.subscribe(observer1);
        observable1.subscribe(observer2);
    }

    /**
     * 按指定时间间隔发送一个从0开始不断递增的整数序列
     */
    void interval() {
        final String tag = TAG + "interval";
        Observable.interval(1,TimeUnit.SECONDS)
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        Log.i(tag,"onNext:"+aLong);
                    }
                });
    }

    /**
     * 设置给定时间延迟之后发送一个0L
     */
    void timer() {
        final String tag = TAG + "timer";
        Observable.timer(2,TimeUnit.SECONDS)
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        Log.i(tag, "onNext:" + aLong);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.i(tag, "onError:");
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.i(tag,"onComplete!");
                    }
                });
    }
}
