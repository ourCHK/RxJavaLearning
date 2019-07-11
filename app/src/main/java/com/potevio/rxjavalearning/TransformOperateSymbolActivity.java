package com.potevio.rxjavalearning;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.potevio.rxjavalearning.modul.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observables.GroupedObservable;

/**
 * 变换操作符学习
 */
public class TransformOperateSymbolActivity extends AppCompatActivity {

    String TAG = TransformOperateSymbolActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transform_operate_symbol);

        map();
        flatMap();
        groupBy();
        buffer();
        window();
        first();
        last();
        takeAndTakeLast();
        skipAndSkipLast();
        elementAtAndIgnoreElement();
        distinct();
        filter();
    }

    /**
     * 对原数据进行加工后然后发射Observable，
     * 即就是对数据进行加工啦，使用一个Function可以指定输入类型和输出类型
     */
    void map() {
        final String tag = TAG + "map";

        Observable.just("Hello")
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String s) throws Exception {
                        return s.toUpperCase();
                    }
                })
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String s) throws Exception {
                        return s+" WORLD";
                    }
                })
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
     * flat返回的是Observable,所以可以有更多操作空间
     * 比方说1对1,1对多等等
     */
    void flatMap() {

        final String tag = TAG + "flatMap";

        List<Integer> numList = new ArrayList<>();
        numList.add(1);
        numList.add(2);
        numList.add(3);

//        把一个List变换成多个Integer：一对多的情况
        Observable.just(numList)
                .flatMap(new Function<List<Integer>, ObservableSource<Integer>>() {
                    @Override
                    public ObservableSource<Integer> apply(List<Integer> integers) throws Exception {
                        return Observable.fromIterable(integers);
                    }
                }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.i(tag,"integer:"+integer);
            }
        });

        //对字母进行变换：a->{a,b},b->{b,c},c->{c,d}
        String[] stringArray = {"a","b","c"};
        Observable.fromArray(stringArray)
                .flatMap(new Function<String, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(String s) throws Exception {
                        List<String> tempList = new ArrayList<>(2);
                        if (s.equals("a")) {
                            tempList.add("a");
                            tempList.add("b");
                        } else if (s.equals("b")) {
                            tempList.add("b");
                            tempList.add("c");
                        } else {
                            tempList.add("c");
                            tempList.add("d");
                        }
                        return Observable.fromIterable(tempList);
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
     * 这个分组也是怪怪的
     */
    void groupBy() {
        final String tag = TAG + "groupBy";
        Observable.range(3,30)
                .groupBy(new Function<Integer, String>() {
                    @Override
                    public String apply(Integer integer) throws Exception {
                        if (integer % 3 == 0) {
                            return "可被3整除的数";
                        } else if (integer % 4 == 0) {
                            return "可被4整除的数";
                        } else if (integer % 5 == 0) {
                            return "可被5整除的数";
                        } else {
                            return "3,4,5都不能整除";
                        }
                    }
                })
                .subscribe(new Observer<GroupedObservable<String, Integer>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.i(tag,"onSubscribe");
                    }

                    @Override
                    public void onNext(final GroupedObservable<String, Integer> stringIntegerGroupedObservable) {
                        stringIntegerGroupedObservable.subscribe(new Consumer<Integer>() {
                            @Override
                            public void accept(Integer integer) throws Exception {
                                Log.i(tag,"key"+stringIntegerGroupedObservable.getKey()+"value:"+integer);
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(tag,"on Error:"+e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.i(tag,"onComplete");
                    }
                });
    }

    /**
     * 缓存个数，等缓存到对应个数时发送数据
     */
    void buffer() {
        final String tag = TAG + "buffer";
        Observable.range(1,10)
                .buffer(2)
                .flatMap(new Function<List<Integer>, ObservableSource<Integer>>() {
                    @Override
                    public ObservableSource<Integer> apply(List<Integer> integers) throws Exception {
                        return Observable.fromIterable(integers);
                    }
                }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.i(tag,"integer:"+integer);
            }
        });

        Observable.range(1,10)
                .buffer(2)
                .subscribe(new Consumer<List<Integer>>() {
                    @Override
                    public void accept(List<Integer> integers) throws Exception {
                        Log.i(tag,"integers:"+integers);
                    }
                });

        Observable.range(1,10)
                .buffer(3,2)    //先发射count个数据，然后跳过skip个数据，再继续发送前count个数据，后续依次继续，
                //其实buffer(count)就是buffer(n,n)的实现
                .subscribe(new Consumer<List<Integer>>() {
                    @Override
                    public void accept(List<Integer> integers) throws Exception {
                        Log.i(tag,"bufferSkip:"+integers);
                    }
                });
    }

    /**
     * 其实和buffer差不多，只不过发射的数据是Observable，所以需要重新subscribe，另外这个Observable可能会包含有多个数据,取决于window的大小
     */
    void window() {
        final String tag = TAG + "window";
        Observable.range(1,10)
                .window(3)
                .subscribe(new Consumer<Observable<Integer>>() {
                    @Override
                    public void accept(Observable<Integer> integerObservable) throws Exception {

                        Log.i(tag,"onNext");
                        integerObservable.subscribe(new Consumer<Integer>() {
                            @Override
                            public void accept(Integer integer) throws Exception {
                                Log.i(tag,"integer:"+integer);
                            }
                        });
                    }
                });
    }

    /**
     * 返回第一个数据，注意这个Observable是SingleObservable，只有onSuccess和onError
     *
     */
    void first() {
        final String tag = TAG + "first";

        Observable.range(1,10)
                .first(3)   //默认值
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.i(tag,"first:"+integer);
                    }
                });

        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onComplete();   //不发送数据，后面则会发送first的第一个数据
            }
        })
                .first(3)   //默认值
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.i(tag,"first:"+integer);
                    }
                });

        Observable.<String>empty()
                .first("HH")
                .subscribe(new SingleObserver<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.i(tag,"onSubscribe:");
                    }

                    @Override
                    public void onSuccess(String s) {
                        Log.i(tag,"onSuccess:"+s);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(tag,"onError:"+e.getMessage());
                    }
                });
    }

    /**
     * 取最后一个值，用法和first差不多
     */
    void last() {

        final String tag = TAG + "last";

        Observable.range(1,10)
                .last(99)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.i(tag,"integer:"+integer);
                    }
                });
    }

    /**
     * take-获取前N个数据
     *  takeLast-获取后N个数据
     */
    void takeAndTakeLast() {
        final String tag = TAG+"takeAndTakeLast";

        Observable.range(1,10)
                .take(3)    //取前3个数据
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.i(tag,"take_onSubscribe");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.i(tag,"take_onNext:"+integer);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(tag,"take_onSubscribe");
                    }

                    @Override
                    public void onComplete() {
                        Log.i(tag,"take_onSubscribe");
                    }
                });

        Observable.range(1,10)
                .takeLast(3)    //获取最后3个数据
        .subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.i(tag,"takeLast_onSubscribe");

            }

            @Override
            public void onNext(Integer integer) {
                Log.i(tag,"takeLast_onNext:"+integer);

            }

            @Override
            public void onError(Throwable e) {
                Log.i(tag,"takeLast_onError");

            }

            @Override
            public void onComplete() {
                Log.i(tag,"takeLast_onComplete");

            }
        });
    }


    /**
     * skip-跳过前N个数据
     * skipLast-跳过后N个数据
     */
    void skipAndSkipLast() {
        final String tag = TAG + "skipAndSkipLast";
        Observable.just(1,2,3,4,5)
                .skip(2)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.i(tag,"skip_accept:"+integer);
                    }
                });

        Observable.just(1,2,3,4,5)
                .skipLast(2)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.i(tag,"skipLast_accept:"+integer);
                    }
                });
    }


    /**
     * elementAt 挑选某个数据
     * ignoreElements 忽略所有的元素，直接发送onComplete或者onError
     */
    void elementAtAndIgnoreElement() {
        final String tag = TAG+"elementAtIgnoreElement";
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                for (int i=0; i<10; i++) {
                    Log.i(tag,"emitter"+i);
                    emitter.onNext(i);
                }
                emitter.onComplete();
            }
        }).elementAt(4)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.i(tag,"accept:"+integer);
                    }
                });


        //忽略所有的数据，直接发送onComplete或者onError
        Observable.range(1,10)
                .ignoreElements()
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.i(tag,"emitter complete!");
                    }
                });
    }

    /**
     * 只允许发射过从来没有发射过的数据
     */
    void distinct() {

        final String tag = TAG +"distinct";
        List<User> userList = new ArrayList<>();
        for (int i=0; i<5; i++) {
            User user = new User();
            if (i == 3) {
                user.setName("user:"+3);
            } else {
                user.setName("user:"+i);
            }
            userList.add(user);
        }

        Observable.fromIterable(userList)    //这里的第二个2不会发射
            .distinct()
                .subscribe(new Consumer<TransformOperateSymbolActivity.User>() {
                    @Override
                    public void accept(TransformOperateSymbolActivity.User user) throws Exception {
                        Log.i(tag,"userName:"+user.getName());
                    }
                });
    }


    /**
     * 过滤不符合条件的数据
     * predicate-断言、判断
     *
     */
    void filter() {
        final String tag = TAG + "filter";

        Observable.range(0,10)
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        return integer % 2 == 0 ? true : false;
                    }
                })
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.i(tag,"Filter:"+integer);
                    }
                });
    }

    private static class User {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof User) {
                User objUser = (User) obj;
                if (objUser.getName().equals(name))
                    return true;
            }
            return false;
        }
    }

}
