package com.agrawalsuneet.dotsloader.contracts

import android.util.AttributeSet

interface InitializationContract {
    fun initAttributes(attrs: AttributeSet)
    fun initViews()
}