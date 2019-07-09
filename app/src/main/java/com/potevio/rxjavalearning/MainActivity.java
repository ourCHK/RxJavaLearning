package com.potevio.rxjavalearning;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;
import io.reactivex.MaybeObserver;
import io.reactivex.MaybeOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        helloWorld();
        singleRxJavaTest();
        logHelloWorld();
        logHelloWorldNoJust();
        CompletableRxJavaTest();
        ComplatableRxJavaTestNormal();
        MaybeRxJava();
    }

    void helloWorld() {
        Observable.just("HelloWorld")
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.i(TAG,"onSubscribe");
                    }

                    @Override
                    public void onNext(String s) {
                        Log.i(TAG,"onNext:"+s);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG,"onError");
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG,"onComplete");
                    }
                });
    }

    void logHelloWorld() {
        final String tag = TAG+"logHelloWorld1";
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                emitter.onNext("Hello World!");
                emitter.onNext("hello world2"+50/0);
                Log.i(tag,"after Exception");
                emitter.onNext("Hello world3");
                emitter.onComplete();
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.i(tag, s);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Log.i(tag, throwable.getMessage());
            }
        }, new Action() {
            @Override
            public void run() throws Exception {
                Log.i(tag,"on complete!");
            }
        });
    }

    /**
     * 使用just操作符会在数据发送完之后还发送一个onComplete()，而logHelloWorld的那个方法则需要手动发送
     * 注意，Just是没有捕获异常的
     * onComplete，onError二选一，只能触发一个
     */
    void logHelloWorldWithJust() {
        Observable.just("Hello World："+1/0)
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.i(TAG,"onSubscribe");
                    }

                    @Override
                    public void onNext(String s) {
                        Log.i(TAG,"onNext:"+s);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG,"onError:"+e.getMessage());

                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG,"onComplete");

                    }
                });
    }


    void logHelloWorldNoJust() {
        final String tag = TAG+"logHelloWorldNoJust";
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                emitter.onNext("Hello World："+1/0);
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
                Log.i(tag,"onError:"+e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.i(tag,"onComplete:");
            }
        });
    }

    /**
     * do操作符
     */
    void logHelloWorldWithJustAndDo() {
        Observable.just("Hello World")
                .doOnNext(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.i(TAG,"doOnNext:"+s);
                    }
                })
                .doAfterNext(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.i(TAG,"doAfterNext:"+s);
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.i(TAG,"doOnComplete:");
                    }
                })
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        Log.i(TAG,"doOnSubscribe:");
                    }
                })
                .doAfterTerminate(new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.i(TAG,"doAfterTerminate:");
                    }
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.i(TAG,"doFinally:");
                    }
                }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.i(TAG,s);
            }
        });
    }

    void singleRxJavaTest() {
        final String tag = TAG+"singleRxJavaTest";
        Single.create(new SingleOnSubscribe<String>() {
            @Override
            public void subscribe(SingleEmitter<String> emitter) throws Exception {
                emitter.onSuccess("SingleRxJava:");
                emitter.onSuccess("haha");  //这里不会执行
            }
        }).subscribe(new SingleObserver<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.i(tag,"on Subscribe");
            }

            @Override
            public void onSuccess(String s) {
                Log.i(tag,"on Success:"+s);
            }

            @Override
            public void onError(Throwable e) {
                Log.i(tag,"on Error:"+e);
            }
        });
    }

    /**
     * 不发送数据，只接收数据
     */
    void CompletableRxJavaTest() {
        final String tag = TAG+"CompletableRxJavaTest";

        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                Log.i(tag,"complete");
            }
        }).subscribe();
    }

    /**
     * 结合and then操作符
     */
    void ComplatableRxJavaTestNormal() {
        final String tag = TAG + "ComplatableNormal";
        Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) throws Exception {
                emitter.onComplete();   //这里的emitter只有onComplete()和onError()方法
            }
        }).andThen(Observable.range(1,10))
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.i(tag,"integer:"+integer);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        Log.i(tag,"onComplete");
                    }
                });
    }

    /**
     * 只能发送0个或者1个数据，看看做是Single，Completable的结合体
     */
    void MaybeRxJava() {
        final String tag = TAG + "MaybeRxJava";
        Maybe.create(new MaybeOnSubscribe<String>() {
            @Override
            public void subscribe(MaybeEmitter<String> emitter) throws Exception {
                emitter.onSuccess("maybe");
                emitter.onComplete();
            }
        }).subscribe(new MaybeObserver<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(String s) {
                Log.i(tag,"onSuccess:"+s);
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
}
