package com.example.stride.domain

import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.Executors

abstract class BaseUseCase {
    protected val disposables = CompositeDisposable()

    fun dispose(){
        disposables.dispose()
    }

}