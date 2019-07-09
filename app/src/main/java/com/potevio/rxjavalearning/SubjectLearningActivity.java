package com.potevio.rxjavalearning;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.AsyncSubject;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.ReplaySubject;
import io.reactivex.subjects.Subject;

public class SubjectLearningActivity extends AppCompatActivity {

    private static final String TAG = SubjectLearningActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_learning);

        asyncSubjectTest();
        behaviorSubject();
        replaySubject();
        publishSubject();
    }

    /**
     * 只接收onComplete前的最后一个
     */
    public void asyncSubjectTest() {
        final String tag = TAG + "asyncSubject";

        AsyncSubject<String> asyncSubject = AsyncSubject.create();
        asyncSubject.onNext("Async1");
        asyncSubject.onNext("Async2");
        asyncSubject.onComplete();

        asyncSubject.subscribe(new Observer<String>() {
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
     * Observer只接收订阅前的最后一个数据和订阅之后的数据
     */
    public void behaviorSubject() {

        final String tag = TAG + "behaviorSubject";

        BehaviorSubject<String> behaviorSubject = BehaviorSubject.createDefault("default value");
        behaviorSubject.onNext("subject1");
        behaviorSubject.onNext("subject2");
        behaviorSubject.onNext("subject3");
        behaviorSubject.subscribe(new Observer<String>() {
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

        behaviorSubject.onNext("afterSubscribe");
    }

    /**
     * Observer接收订阅之前的数据和订阅之后的数据
     */
    public void replaySubject() {
        final String tag = TAG + "replaySubject";
        ReplaySubject<String> replaySubject = ReplaySubject.createWithSize(1);
        replaySubject.onNext("replaySubject1");
        replaySubject.onNext("replaySubject2");

        replaySubject.subscribe(new Observer<String>() {
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
        replaySubject.onNext("afterSubscribe1");
        replaySubject.onNext("afterSubscribe2");

    }

    public void publishSubject() {
        final String tag = TAG + "publishSubject";
        PublishSubject<String> publishSubject = PublishSubject.create();
        publishSubject.onNext("publishSubject1");
        publishSubject.onNext("publishSubject2");
        publishSubject.subscribe(new Observer<String>() {
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
        publishSubject.onNext("publishSubject3");
        publishSubject.onNext("publishSubject4");
    }







}
