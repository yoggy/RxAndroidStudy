package net.sabamiso.android.rxandroidstudy;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();

    Subscription timer_subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //
        // case 1 : insted of OnClickListener
        //
        RxView.clicks(findViewById(R.id.buttonButton1))
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        log("BUTTON1を押したとき");
                    }
                });

        // case 2 : timer task
        timer_subscription = Observable.timer(10000, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long t) {
                        log("case2: Observable.timer()を使ったタイマー t=" + t);
                    }
                });
        // timerを止めるときは、timer_subscription.unsubscribe()する

        // case 3 : delay task
        RxView.clicks(findViewById(R.id.buttonDelayTask))
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        log("case3: DELAY_TASKボタンを押したとき");
                        Observable.just(null)
                                .delay(3000, TimeUnit.MILLISECONDS)
                                .subscribe(new Action1<Object>() {
                                    @Override
                                    public void call(Object object) {
                                        log("case3: DELAY_TASKボタンを押してから3秒後に実行されるタスク");
                                    }
                                });
                    }
                });

        // case 4 : cancel timeout
        RxView.clicks(findViewById(R.id.buttonStartTimeout))
                .observeOn(Schedulers.io())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        log("START_TIMEOUTを押したとき");
                        cancelTimeout();
                        startTimeout();
                    }
                });

        RxView.clicks(findViewById(R.id.buttonCancelTimeout))
                .observeOn(Schedulers.io())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        log("CANCEL_TIMEOUTを押したとき");
                        cancelTimeout();
                    }
                });

        // case 5 : message queue ?
        setupOriginalObservable();
        RxView.clicks(findViewById(R.id.buttonEmit))
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        log("EMITを押したとき");
                        emitMessage("EMITボタンから送られてきたメッセージ。t=" + System.currentTimeMillis());
                    }
                });
    }

    @Override
    protected void onDestroy() {
        timer_subscription.unsubscribe(); // 無限Observableの時はunsubscribeしておく。
        cancelTimeout();
        finishOriginalObservable();
        super.onDestroy();
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    //
    // case 4:
    //     press start2 button ---+---> timeout
    //                            +---> press cancel2 button
    //
    Subscription timeout_subscription;

    void cancelTimeout() {
        if (timeout_subscription != null) {
            timeout_subscription.unsubscribe();
            timeout_subscription = null;
        }
    }

    void startTimeout() {
        timeout_subscription = Observable.just(null)
                .observeOn(Schedulers.io())
                .delay(3000, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        log("START_TIMEOUTを押してから3秒後");
                    }
                });
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    //
    // case 5 : message queue ?
    //
    interface MessageEmitter {
        void emit(String msg);
    }

    Observable<String> message_observable;
    MessageEmitter message_emitter;
    Subscription message_subscription;

    void setupOriginalObservable() {
        message_observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(final Subscriber<? super String> subscriber) {
                final MessageEmitter emitter = new MessageEmitter() {
                    @Override
                    public void emit(String msg) {
                        subscriber.onNext(msg);
                    }
                };
                message_emitter = emitter;
            }
        })
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .delay(3000, TimeUnit.MILLISECONDS);

        message_subscription = message_observable
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String msg) {
                        log("3秒後にemitされたメッセージが届きました。msg=" + msg);
                    }
                });
    }

    void emitMessage(String msg) {
        if (message_emitter != null) {
            message_emitter.emit(msg);
        }
    }

    void finishOriginalObservable() {
        if (message_subscription != null) {
            message_subscription.unsubscribe();
        }
        message_emitter = null;
        message_observable = null;
    }

    ////////////////////////////////////////////////////////////////////////////////////////

    Handler handler = new Handler();

    void log(final String msg) {
        Log.d(TAG, msg);

        // UI Thread
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
