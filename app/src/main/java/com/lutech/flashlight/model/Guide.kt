package com.lutech.flashlight.model

import java.io.Serializable

data class Guide(
    val title: Int,
    val questions: Int,
    val answers: Int,
    val isGuidSetting: Boolean
) : Serializable
