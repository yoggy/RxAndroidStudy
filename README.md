RxAndroidStudy
====
ReactiveX/RxAndroid
    - https://github.com/ReactiveX/RxAndroid

使い方メモ
----
app/build.gradleに次の行を追加。

    --- build.gradle.org    2016-09-18 16:33:16.197491100 +0900
    +++ build.gradle        2016-09-18 16:33:18.464287200 +0900
    @@ -23,4 +23,7 @@
         compile fileTree(dir: 'libs', include: ['*.jar'])
         testCompile 'junit:junit:4.12'
         compile 'com.android.support:appcompat-v7:24.0.0'
    +    compile 'io.reactivex:rxandroid:1.2.1'
    +    compile 'io.reactivex:rxjava:1.1.6'
    +    compile 'com.jakewharton.rxbinding:rxbinding:0.4.0'
     }


Copyright and license
----
Copyright (c) 2016 yoggy

Released under the [MIT license](LICENSE.txt)

