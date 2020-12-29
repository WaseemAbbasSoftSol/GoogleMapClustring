package com.example.mapclustring.map

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import dagger.hilt.android.qualifiers.ApplicationContext

class BaseMapViewModel @ViewModelInject constructor(
    @ApplicationContext application: Context
) : LiveLocationViewModel(application)